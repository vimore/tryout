solrctl --zk hiveapp1:2181/solr instancedir --create web_proxy_mef $HOME/solr_configs3
solrctl --zk hiveapp1:2181/solr collection --create web_proxy_mef -s 2

sudo -u hdfs hadoop --config /etc/hadoop/conf.cloudera.mapreduce1 jar /opt/cloudera/parcels/SOLR-1.1.0-1.cdh4.3.0.p0.21/lib/solr/contrib/mr/search-mr-1.1.0-job.jar org.apache.solr.hadoop.MapReduceIndexerTool -D 'mapred.child.java.opts=-Xmx500m' --log4j /opt/cloudera/parcels/SOLR-1.1.0-1.cdh4.3.0.p0.21/share/doc/search-1.1.0/examples/solr-nrt/log4j.properties --morphline-file ~/readAvroContainer.conf --output-dir hdfs://hiveapp1:8020/user/$USER/outdir --verbose --go-live --zk-host hiveapp1:2181/solr --collection web_proxy_mef hdfs://hivecluster2:8020/securityx/web_proxy_mef/sample/part-m-00000.avro

solrctl --zk hiveapp1:2181/solr instancedir --update web_proxy_mef $HOME/solr_configs3

solrctl --zk hiveapp1:2181/solr instancedir --create flow_mef /home/hivedata/solr_configs3
solrctl --zk hiveapp1:2181/solr collection --create flow_mef -s 2

solrctl --zk hiveapp1:2181/solr instancedir --create dns_mef /home/hivedata/solr_configs3
solrctl --zk hiveapp1:2181/solr collection --create dns_mef -s 2

solrctl --zk hiveapp1:2181/solr instancedir --create iam_mef /home/hivedata/solr_configs3
solrctl --zk hiveapp1:2181/solr collection --create iam_mef -s 2