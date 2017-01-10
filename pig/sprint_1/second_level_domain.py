#!/usr/bin/python
import sys

import tldextract


#@outputSchema("sld:chararray")
def extract_sld(site):
    """
    get Second Level Domains
    """
    if site is None:
        return ""
    arr = tldextract.extract(site)
    if (arr.suffix == ''):
        sld = arr.domain
    else:
        sld = arr.domain + '.' + arr.suffix
    return(sld)

for line in sys.stdin:
    fields = line.split('\t')
    sld = extract_sld(fields[1])
    if(fields[0] != None):
        print "%s\t%s\t%s" % (fields[0], sld, fields[3])
