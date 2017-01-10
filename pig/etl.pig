REGISTER /opt/cloudera/parcels/CDH-4.4.0-1.cdh4.4.0.p0.39/lib/pig/piggybank.jar
REGISTER /opt/cloudera/parcels/CDH-4.4.0-1.cdh4.4.0.p0.39/lib/pig/lib/avro-1.7.4.jar
REGISTER /opt/cloudera/parcels/CDH-4.4.0-1.cdh4.4.0.p0.39/lib/pig/lib/json-simple-1.1.jar

DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

REGISTER 'udfs.py' USING jython AS udfs;

-- firewall = LOAD '/securityx/flume/livestream/2014/01/13/13/hivedev1*' USING AvroStorage();
firewall = LOAD 'firewall.avro' USING AvroStorage();
DESCRIBE firewall
-- firewall: {headers: map[],body: bytearray}
-- DUMP firewall
/* ([
    timestamp#1389650234075,
    hostname#hivedev1.labs.lan,
    category#firewall,
    Severity#6,
    Facility#16
    ],
    id=firewall 
    sn=0006B129195C 
    time="2014-01-13 13:57:13" 
    fw=71.6.1.234 
    pri=6 
    c=262144 
    m=98 
    msg="Connection Opened" 
    n=10084260 
    src=10.10.30.213:123:X0: 
    dst=216.66.0.142:123:X1: 
    proto=udp/ntp ) */
firewall = FOREACH firewall GENERATE headers, udfs.parse_body(body) AS body:map[];
DESCRIBE firewall

syslog = LOAD '/securityx/flume/livestream/2014/01/13/13/security*' USING AvroStorage();
DESCRIBE syslog
-- syslog: {headers: map[],body: bytearray}
DUMP syslog
-- ([timestamp#1389649560022,hostname#security1,category#syslog,Severity#0,flume.syslog.status#Invalid,Facility#0],01/13/2014 21:45:59 22 45.14.4.21 200 TCP_REFRESH_MISS 469 882 GET http www.microsoft.com /library/gallery/templates/MNP2.Common/images/office_r_corner.gif - - DIRECT www.microsoft.com image/gif "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)" PROXIED Computers/Internet - 192.16.170.42 SG-HTTP-Service - none -)
