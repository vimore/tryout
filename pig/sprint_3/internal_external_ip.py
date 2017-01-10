#!/usr/bin/python
import sys
import getopt

import ipaddress


def is_reserved(ip_string):
   """
   Given an IPv4 string, check whether it is a reserved IP, i.e. an
   internal vs. public IP address.
   """
   reserved_spaces = [
     ipaddress.IPv4Network(u"0.0.0.0/8"),          # "this" network
     ipaddress.IPv4Network(u"10.0.0.0/8"),         # RFC1918 private space
     ipaddress.IPv4Network(u"100.64.0.0/10"),      # RFC6598 private space
     ipaddress.IPv4Network(u"127.0.0.0/8"),        # Loopback/localhost
     ipaddress.IPv4Network(u"169.254.0.0/16"),     # link local block
     ipaddress.IPv4Network(u"172.16.0.0/12"),      # RFC1918 private space
     ipaddress.IPv4Network(u"192.0.0.0/24"),       # IETF protocol assignments
     ipaddress.IPv4Network(u"192.0.2.0/24"),       # example IP space
     ipaddress.IPv4Network(u"192.168.0.0/16"),     # RFC1918 private space
     ipaddress.IPv4Network(u"198.18.0.0/15"),      # benchmark testing IP space
     ipaddress.IPv4Network(u"198.51.100.0/24"),    # example IP space
     ipaddress.IPv4Network(u"203.0.113.0/24"),     # example IP space
     ipaddress.IPv4Network(u"224.0.0.0/4"),        # Multicast IP space
     ipaddress.IPv4Network(u"240.0.0.0/4"),        # reserved for future use
     ipaddress.IPv4Network(u"255.255.255.255/32")  # Local broadcast
   ]
   
   ip = ipaddress.IPv4Address(unicode(ip_string))
   
   # Iterate explicitly because 'ip in reserved_spaces' doesn't work
   # for lists of IPv4Network objects.
   for net in reserved_spaces:
     if ip in net:
       return True
   return False

def main(argv):
    mode = ''
    try:
        opts, args = getopt.getopt(argv,"hm:")
    except getopt.GetoptError:
        print 'second_level_domain.py -m <internal|external>'
        sys.exit(2)
   
    for opt, arg in opts:
        if opt == '-h':
            print 'second_level_domain.py -m <internal|external>'
            sys.exit()
        if opt == '-m':
            mode = arg
    
    for line in sys.stdin:
        fields = line.split('\t')
       
        source_name_or_ip = fields[0]
        destination_name_or_ip = fields[1]
        start_time_iso = fields[2]
        bits_in = fields[3]
        bits_out = fields[4]
       
        if mode == 'internal':
            if(fields[0] != None):
                if(is_reserved(source_name_or_ip)):
                    print "%s\t%s\t%s\t%d\t%d" % (source_name_or_ip, destination_name_or_ip, start_time_iso, int(bits_in), int(bits_out))
        if mode == 'external':
            if(fields[0] != None):
                if(not is_reserved(source_name_or_ip)):
                    print "%s\t%s\t%s\t%d\t%d" % (source_name_or_ip, destination_name_or_ip, start_time_iso, int(bits_in), int(bits_out))


if __name__ == "__main__":
   main(sys.argv[1:])
