#!/usr/bin/python
import sys
import getopt

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


def main(argv):
    instance = 0
    try:
        opts, args = getopt.getopt(argv,"hi:")
    except getopt.GetoptError:
        print 'second_level_domain.py -i <instance>'
        sys.exit(2)
    
    for opt, arg in opts:
        if opt == '-h':
            print 'second_level_domain.py -i <instance>'
            sys.exit()
        if opt == '-i':
            instance = arg
    
    # We do the same thing twice for two sets of fields, so there's an argument to pick which instance/set of fields
    # c_ip, cs_host, hour, request_method, user_agent
    if(instance == "1"):
        for line in sys.stdin:
            fields = line.split('\t')
            sld = extract_sld(fields[1])
            request_method = fields[3].rstrip()
            if(fields[0] != None):
                print "%s\t%s\t%s\t%s\t%s" % (fields[0], sld, fields[2], request_method, "\t".join(fields[4:]).rstrip())
    if(instance == "2"):
        for line in sys.stdin:
            fields = line.split('\t')
            sld = extract_sld(fields[1])
            print "%s\t%s\t%s\t%s\t%s\t%f\t%f\t%s" % (fields[0], fields[1], fields[2], fields[3], fields[4], float(fields[5]), float(fields[6]), sld)

if __name__ == "__main__":
    main(sys.argv[1:])