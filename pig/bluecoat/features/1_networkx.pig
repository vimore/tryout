REGISTER /Users/rjurney/Software/datafu/dist/datafu-1.2.1-SNAPSHOT.jar
REGISTER 'udfs.py' using streaming_python AS udfs;

rmf /tmp/network_degrees.avro

DEFINE PadZero datafu.pig.util.PadZero();

bluecoat = LOAD '../../../data/bluecoat_datetime.avro' USING AvroStorage();

/* Trim down the fields to improve processing speed */
edges = FOREACH bluecoat GENERATE cs_host, c_ip;
by_all = GROUP edges ALL;
degrees = FOREACH by_all GENERATE udfs.degree_of_nodes(edges);
STORE degrees INTO '/tmp/network_degrees.avro' USING AvroStorage();
