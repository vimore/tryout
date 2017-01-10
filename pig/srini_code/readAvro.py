import avro

#LOAD '../../data/bluecoat_datetime.avro' USING AvroStorage();
#Optional key to print

#fileName = '../../data/bluecoat_datetime.avro'
fileName = '../../data/bluecoat_datetime.avro/part-m-00001.avro'
fileName = 'anavrofile'
field_id = None
rec_reader = avro.io.DatumReader()
df_reader = avro.datafile.DataFileReader(open(fileName), rec_reader)



