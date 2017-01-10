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
    print "%s\t%s\t%s\t%f\t%f\t%s" % (fields[0], fields[1], fields[2], float(fields[3]), float(fields[4]), sld)
