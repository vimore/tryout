from my_avro_3 import *

file_names = []
file_names.append('../testdemo_jyr5/part-r-00000.avro')
file_names.append('../testdemo_jyr5/part-r-00001.avro')
file_names.append('../testdemo_jyr5/part-r-00002.avro')
file_names.append('../testdemo_jyr5/part-r-00003.avro')
file_names.append('../testdemo_jyr5/part-r-00004.avro')
file_names.append('../testdemo_jyr5/part-r-00005.avro')
file_names.append('../testdemo_jyr5/part-r-00006.avro')
file_names.append('../testdemo_jyr5/part-r-00007.avro')

output_file = 'raw_data/test_data'
for file_name in file_names:
    readBluecoatAvro(file_name, output_file)

