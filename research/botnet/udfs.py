 #from pig_util import outputSchema
 import re

 import ipaddress
 from ipaddress import *


 #@outputSchema("os_browser:tuple(os:chararray, browser:chararray)")
#@@ -28,3 +29,36 @@ 
def bluecoat_normalize_content_type(content_type):
    content_type = re.sub(r'(%(20|09))?(charset=[^;]+)', charset, content_type, flags=re.IGNORECASE)
    return content_type

#@outputSchema("is_reserved:boolean")
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
   ip = ipaddress.IPv4Address(ip_string)

   # Iterate explicitly because 'ip in reserved_spaces' doesn't work
   # for lists of IPv4Network objects.
   for net in reserved_spaces:
       if ip in net:
           return True
   return False


def is_internal(ip_string):
   """
   Given an IPv4 string, check whether it is a reserved IP, i.e. an
   internal vs. public IP address.
   """
   internal_spaces = [
       ipaddress.IPv4Network(u"45.16.0.0/13"),
       ipaddress.IPv4Network(u"45.0.0.0/11"),
       ipaddress.IPv4Network(u"45.96.0.0/11"),
       ipaddress.IPv4Network(u"45.0.0.0/9"),
       ipaddress.IPv4Network(u"192.16.170.0/24")
       ]

   ip = ipaddress.IPv4Address(ip_string)

   # Iterate explicitly because 'ip in reserved_spaces' doesn't work
   # for lists of IPv4Network objects.
   for net in internal_spaces:
       if ip in net:
           return True
   return False

