#!/anaconda/bin/python
import sys
#sys.path.append('/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/site-packages/')
sys.path.append('/anaconda/lib/python2.7/site-packages/')
from networkx import *

from org.apache.pig.scripting import Pig
import string as str

P = Pig.compile("""DEFINE test Test();
                   rmf output;
                   raw = load '../../data/bluecoat_datetime.avro' using AvroStorage();
		   result = foreach raw generate c_ip, cs_host;
                   store result into 'output';
                """)


#sys.path.append('/anaconda/lib/python2.7/site-packages/networkx/')

inputData = '../../data/bluecoat_datetime.avro'
Q = P.bind()
results = Q.runSingle()

ii = 0
G = nx.Graph()
fpOut = open('tmpdata','w')
for item in results.result("result").iterator():
    print ('---->   ', item[0], item[1])
    fpOut.write(str(item) + '\n')
    if (ii < 10):
        break
    ii += 1
fpOut.close()
			




	
