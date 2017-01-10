REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

set default_parallel 6
rmf /securityx/netflow/$year/$month/$day/inbound_to_dark_ips.txt
rmf /securityx/netflow/$year/$month/$day/external_ips.txt
rmf /securityx/netflow/$year/$month/$day/all_bps.txt
rmf /securityx/netflow/$year/$month/$day/all_entropies.txt
rmf /securityx/netflow/tmp

-- HDFS flow_mef
flow_events = LOAD '/securityx/flow_mef/$year/$month/$day/*/*.avro' USING AvroStorage();   /* */
trimmed_flow_events = FOREACH flow_events GENERATE
    sourceAddress,
    destinationAddress,
    uuid,
    externalLogSourceType,
    startTime,
    startTimeISO,
    transportProtocol,
    destinationPort,
    CONCAT(CONCAT(transportProtocol, (chararray)'/'), destinationPort) AS proto_dport:chararray,
    sourcePort,
    CONCAT(CONCAT(transportProtocol, (chararray)'/'), sourcePort) AS proto_sport:chararray,
    destinationAutonousSystem,
    sourceAutonousSystem,
    tcpFlags,
    totalBytesExp,
    totalFlowsExp,
    totalPacketsExp;

DEFINE home_net_command `home_net_streaming.py` SHIP('home_net_streaming.py', 'home_net.py', 'home_net.cfg');
labeled_flows = STREAM trimmed_flow_events THROUGH home_net_command AS
   (sourceAddress:chararray,
    src_internal:int,
    destinationAddress:chararray,
    dst_internal:int,
    uuid:chararray,
    externalLogSourceType:chararray,
    startTime:long,
    startTimeISO:chararray,
    transportProtocol:int,
    destinationPort:chararray,
    proto_dport:chararray,
    sourcePort:chararray,
    proto_sport:chararray,
    tcpFlags:int,
    totalBytesExp:long,
    totalFlowsExp:long,
    totalPacketsExp:long);

SPLIT labeled_flows INTO
    int2int IF (src_internal==1 AND dst_internal==1),
    int2ext IF (src_internal==1 AND dst_internal==0),
    ext2int IF (src_internal==0 AND dst_internal==1),
    ext2ext IF (src_internal==0 AND dst_internal==0);


-- Find all active hosts inside the network.
internal_sources = FILTER labeled_flows BY src_internal == 1;
internal_sources = GROUP internal_sources BY sourceAddress;
internal_sources = FOREACH internal_sources GENERATE group AS active_ip;
inbound_to_dark_ips = FILTER labeled_flows BY dst_internal == 1;
-- Join to active IPs, null results indicate a non-existent (dark) IP.
inbound_to_dark_ips = JOIN labeled_flows BY destinationAddress LEFT OUTER,
                           internal_sources BY active_ip;
inbound_to_dark_ips = FOREACH inbound_to_dark_ips GENERATE
    labeled_flows::sourceAddress AS sourceAddress,
    labeled_flows::destinationAddress AS destinationAddress,
    internal_sources::active_ip AS active_ip;
inbound_to_dark_ips = FILTER inbound_to_dark_ips BY active_ip IS NULL;
inbound_to_dark_ips = FOREACH inbound_to_dark_ips GENERATE sourceAddress, destinationAddress;
pair = GROUP inbound_to_dark_ips BY (sourceAddress, destinationAddress);
pair = FOREACH pair GENERATE FLATTEN(group) AS (sourceAddress, destinationAddress);
inbound_to_dark_ips = FOREACH (GROUP pair BY sourceAddress) GENERATE
        group AS sourceAddress,
        COUNT(pair) AS dark_ip_count;
STORE inbound_to_dark_ips
    INTO '/securityx/netflow/$year/$month/$day/inbound_to_dark_ips.txt';

-- Find all external IPs to be annotated with whois.
external_destinations = FILTER labeled_flows BY dst_internal == 0;
external_destinations = GROUP external_destinations BY destinationAddress;
external_destinations = FOREACH external_destinations GENERATE group as external_ip;
external_sources = FILTER labeled_flows BY src_internal == 0;
external_sources = GROUP external_sources BY sourceAddress;
external_sources = FOREACH external_sources GENERATE group as external_ip;
external_ips = UNION external_destinations, external_sources;
external_ips = FOREACH external_ips GENERATE external_ip;
-- These will need to be annotated with whois information to be useful,
-- but also will form a catalog of all external IPs the
-- network communicated with.
STORE external_ips
    INTO '/securityx/netflow/$year/$month/$day/external_ips.txt' USING PigStorage();

DEFINE bps(in_relation, group_field, date_spec, bin_size)
    RETURNS out_relation {
  nonnull = FILTER $in_relation BY totalBytesExp IS NOT NULL;
  grouped = GROUP nonnull BY ($group_field);
  $out_relation = FOREACH grouped GENERATE
      (chararray)'$out_relation' as type:chararray,
      (chararray)'$date_spec' as date_time:chararray,
      FLATTEN(group) AS group_field:chararray,
      (double)(SUM(nonnull.totalBytesExp)*8)/(double)$bin_size AS bits_per_second:double;
}

-- Bandwidth features
in_sport_bps = bps(ext2int, proto_sport, '$year-$month-$day', $bin_size);
in_dport_bps = bps(ext2int, proto_dport, '$year-$month-$day', $bin_size);
out_sport_bps = bps(int2ext, proto_sport, '$year-$month-$day', $bin_size);
out_dport_bps = bps(int2ext, proto_dport, '$year-$month-$day', $bin_size);
out_sip_bps = bps(int2ext, sourceAddress, '$year-$month-$day', $bin_size);

-- Per-sip features
smtp_out = FILTER int2ext BY transportProtocol==6 AND destinationPort=='25';
out_sip_smtp_bps = bps(smtp_out, sourceAddress, '$year-$month-$day', $bin_size);

all_bps = UNION
  in_sport_bps,
  in_dport_bps,
  out_sport_bps,
  out_dport_bps,
  out_sip_bps,
  out_sip_smtp_bps;

STORE all_bps INTO '/securityx/netflow/$year/$month/$day/all_bps.txt' USING JsonStorage();


DEFINE calculate_entropy_command `entropy.py` SHIP('entropy.py');

DEFINE entropy(in_relation, bin_field, sum_field) RETURNS out_relation {
  nonnull = FILTER $in_relation BY $sum_field IS NOT NULL;
  sums = FOREACH (GROUP nonnull BY ($bin_field)) GENERATE
      (chararray)'$out_relation' AS type:chararray,
      group AS $bin_field:chararray,
      (long)SUM(nonnull.$sum_field) AS total:long;
  bin_bags = GROUP sums BY type;
  bin_bags = FOREACH bin_bags GENERATE
      group AS type,
      sums.(total) AS totals;
  entropies = STREAM bin_bags THROUGH calculate_entropy_command AS
      (type:chararray,
       bin_count:int,
       entropy:double);
  $out_relation = FOREACH entropies GENERATE
      (chararray)type AS type:chararray,
      (chararray)'all' AS group_field:chararray,
      (int)bin_count AS bin_count:int,
      (double)entropy AS entropy:double;
}

DEFINE entropy_per(group_field, in_relation, bin_field, sum_field) RETURNS out_relation {
  nonnull = FILTER $in_relation BY $sum_field IS NOT NULL;
  sums = FOREACH (GROUP nonnull BY ($group_field, $bin_field)) GENERATE
      (chararray)'$out_relation' AS type:chararray,
      FLATTEN(group) as ($group_field, $bin_field),
      SUM(nonnull.$sum_field) as total:long;
  bin_bags = GROUP sums BY (type, $group_field);
  bin_bags = FOREACH bin_bags GENERATE
      FLATTEN(group) AS (type, group_field),
      sums.(total);
  $out_relation = STREAM bin_bags THROUGH calculate_entropy_command AS
      (type:chararray,
       group_field:chararray,
       bin_count:int,
       entropy:double);
}

in_sport_entropy_pkts = entropy(ext2int, proto_sport, totalPacketsExp);
in_dport_entropy_pkts = entropy(ext2int, proto_dport, totalPacketsExp);
in_sip_entropy_pkts = entropy(ext2int, sourceAddress, totalPacketsExp);
in_dip_entropy_pkts = entropy(ext2int, destinationAddress, totalPacketsExp);
-- TODO(mdeshon): asn_in_dport_entropy_pkts = entropy_per(asn, ext2int, proto_dport, totalPacketsExp);
-- TODO(mdeshon): interface_in_dport_entropy_pkts = entropy_per(interface, ext2int, proto_dport, totalPacketsExp);
out_sport_entropy_pkts = entropy(int2ext, proto_sport, totalPacketsExp);
out_sport_entropy_bytes = entropy(int2ext, proto_sport, totalBytesExp);
out_dport_entropy_pkts = entropy(int2ext, proto_dport, totalPacketsExp);
out_dport_entropy_bytes = entropy(int2ext, proto_dport, totalBytesExp);
out_sip_entropy_pkts = entropy(int2ext, sourceAddress, totalPacketsExp);
out_sip_entropy_bytes = entropy(int2ext, sourceAddress, totalBytesExp);
out_dip_entropy_pkts = entropy(int2ext, destinationAddress, totalPacketsExp);
out_dip_entropy_bytes = entropy(int2ext, destinationAddress, totalBytesExp);

-- Per-sip entropies
sip_out_dip_entropy_pkts = entropy_per(sourceAddress, int2ext, destinationAddress, totalPacketsExp);
sip_out_dport_entropy_pkts = entropy_per(sourceAddress, int2ext, proto_dport, totalPacketsExp);
sip_out_sport_entropy_pkts = entropy_per(sourceAddress, int2ext, proto_sport, totalPacketsExp);
-- TODO(mdeshon): sip_out_asn_entropy_pkts(sourceAddress, int2ext, asn, totalPacketsExp);

all_entropies = UNION
    in_sport_entropy_pkts,
    in_dport_entropy_pkts,
    in_sip_entropy_pkts,
    in_dip_entropy_pkts,
    out_sport_entropy_pkts,
    out_sport_entropy_bytes,
    out_dport_entropy_pkts,
    out_dport_entropy_bytes,
    out_sip_entropy_pkts,
    out_sip_entropy_bytes,
    out_dip_entropy_pkts,
    out_dip_entropy_bytes,
    sip_out_dip_entropy_pkts,
    sip_out_dport_entropy_pkts,
    sip_out_sport_entropy_pkts;

all_entropies = FOREACH all_entropies GENERATE
    (chararray)type AS type:chararray,
    (chararray)'$year-$month-$day' AS date_time:chararray,
    (chararray)group_field AS group_field,
    (int)bin_count AS bin_count:int,
    (double)entropy AS entropy:double;
STORE all_entropies
    INTO '/securityx/netflow/$year/$month/$day/all_entropies.txt'
    USING PigStorage();
