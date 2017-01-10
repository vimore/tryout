REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

set default_parallel 6

-- Pull daily entropy values.
all_entropies = LOAD '/securityx/netflow/$year/$month/*/all_entropies.txt' USING PigStorage();  /* */
all_entropies = FOREACH all_entropies GENERATE type:chararray,
					       date:chararray,
                                               group_field:chararray,
					       bin_count:int,
					       entropy:double;
all_entropies = FILTER all_entropies BY group_field == 'all';

-- Extract the entropies we need for modeling.
SPLIT all_entropies INTO
    in_sip_entropy IF type == 'in_sip_entropy_pkts',
    in_dip_entropy IF type == 'in_dip_entropy_pkts',
    in_sport_entropy IF type == 'in_sport_entropy_pkts',
    in_dport_entropy IF type == 'in_dport_entropy_pkts';

-- Generate ARMA model parameters.
DEFINE arma_model_command `arma_streaming.py` SHIP('arma_streaming.py', 'arma_model.py', 'pig_parsing.py');
DEFINE calculate_arima_parameters(in_relation) RETURNS out_relation {
  trimmed = ORDER $in_relation BY date;
  trimmed = FOREACH $grouped GENERATE entropy:double;
  grouped = GROUP ALL trimmed;
  grouped = FOREACH grouped GENERATE entropy:tuple;
  $out_relation = STREAM grouped THROUGH arima_model_command AS
      model_params:chararray,
      model_order:chararray;
}

-- Store ARMA model parameters.
