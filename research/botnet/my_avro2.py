#!/usr/bin/env python

#
# derived from example at http://www.harshj.com/2010/04/25/writing-and-reading-avro-data-files-using-python/
#
import pprint
import sys

from avro import datafile, io


field_id = None
# Optional key to print
if (len(sys.argv) > 2):
  field_id = sys.argv[2]




# Test reading avros


file_name =  'part-r-00003.avro'
filenames = []
file_names.append('../testdemo_jyr5/part-r-00000.avro')
file_names.append('../testdemo_jyr5/part-r-00001.avro')
file_names.append('../testdemo_jyr5/part-r-00002.avro')
file_names.append('../testdemo_jyr5/part-r-00003.avro')
file_names.append('../testdemo_jyr5/part-r-00004.avro')
file_names.append('../testdemo_jyr5/part-r-00005.avro')
file_names.append('../testdemo_jyr5/part-r-00006.avro')
file_names.append('../testdemo_jyr5/part-r-00007.avro')


pp = pprint.PrettyPrinter()



jj = 0
for file_name in file_names:
    print (file_name)
    rec_reader = io.DatumReader()
    df_reader = datafile.DataFileReader( open(file_name),  rec_reader)

    # write _records into a file
    for _record in df_reader:
        _record = str(_record['rawLog'])
        ii = str.find(_record,'\"bytes\":')
        _record = _record[ii+10:-3] + '\n'
        fpOut.write(_record)
        jj += 1


fpOut.close()
print ('num lines', jj)



