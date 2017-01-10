#!/usr/bin/env python

#
# derived from example at http://www.harshj.com/2010/04/25/writing-and-reading-avro-data-files-using-python/
#
from avro import datafile, io


def readBluecoatAvro(file_name, output_file):
    field_id = None

    rec_reader = io.DatumReader()


    df_reader = datafile.DataFileReader(
        open(file_name),
        rec_reader
        )


    # write _records into a file
    fpOut = open(output_file, 'a')
    jj = 0
    for _record in df_reader:
        _record = str(_record['rawLog'])
        ii = str.find(_record,'\"bytes\":')
        _record = _record[ii+10:-3] + '\n'
        fpOut.write(_record)
        jj += 1
    fpOut.close()
    print ('num lines', jj)



