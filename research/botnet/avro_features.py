#
# derived from example at http://www.harshj.com/2010/04/25/writing-and-reading-avro-data-files-using-python/
#
import pprint
import sys
import json

from avro import datafile, io


def readBluecoatAvroData(file_name = 'part-r-00003.avro'):
    field_id = None

    # Optional key to print
    if (len(sys.argv) > 2):
        field_id = sys.argv[2]



    # Test reading avros
    rec_reader = io.DatumReader()


    # Create a 'data file' (avro file) reader
    df_reader = datafile.DataFileReader(
        open(file_name),
        rec_reader
        )

    # Read all _records stored inside
    pp = pprint.PrettyPrinter()
    i = 0
    for _record in df_reader:
        if i > 100:
            break
        i += 1
        if field_id:
            pp.pprint(_record[field_id])
        else:
            pp.pprint(_record)

    obj = json.loads(df_reader.meta['avro.schema'])
    print "\nAvro Schema: " + json.dumps(obj)
