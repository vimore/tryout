#from pig_util import outputSchema
import re

import httpagentparser
import ipaddress


#@outputSchema("os_browser:tuple(os:chararray, browser:chararray)")
def os_browser(useragent):
   """
   Given an HTTP User-Agent string, return: os, browser
   """
   os, browser = httpagentparser.simple_detect(useragent)
   return (os, browser)

#@outputSchema("content_type:chararray")
def bluecoat_normalize_content_type(content_type):
   """
   Normalize Bluecoat log content_type strings.
   """
   # Fields with unique values that we want to delete.
   BADFIELD = r';(%(20|09))?(filename|name|boundary|x-mac-type|x-unix-mode|x-mac-creator)\='
   BADFIELD_RE = re.compile(BADFIELD, re.IGNORECASE)
   while BADFIELD_RE.search(content_type):
     # Bad field type, remove.
     content_type = re.sub(BADFIELD + r'[^;]+', r'', content_type)
   if re.search(r'(;%(20|09))?(charset)\=', content_type, flags=re.IGNORECASE):
     # Force lower case.
     charset = re.sub(r'.*(charset=[^;]+).*', r'\1', content_type,
                      flags=re.IGNORECASE).lower()
     content_type = re.sub(r'(%(20|09))?(charset=[^;]+)', charset, content_type,
                           flags=re.IGNORECASE)
   return content_type

#@outputSchema("is_reserved:boolean")
def is_reserved(ip_string):
   """
   Given an IPv4 string, check whether it is a reserved IP, i.e. an
   internal vs. public IP address.
   """
   reserved_spaces = [
     ipaddress.IPv4Network("0.0.0.0/8"),          # "this" network
     ipaddress.IPv4Network("10.0.0.0/8"),         # RFC1918 private space
     ipaddress.IPv4Network("100.64.0.0/10"),      # RFC6598 private space
     ipaddress.IPv4Network("127.0.0.0/8"),        # Loopback/localhost
     ipaddress.IPv4Network("169.254.0.0/16"),     # link local block
     ipaddress.IPv4Network("172.16.0.0/12"),      # RFC1918 private space
     ipaddress.IPv4Network("192.0.0.0/24"),       # IETF protocol assignments
     ipaddress.IPv4Network("192.0.2.0/24"),       # example IP space
     ipaddress.IPv4Network("192.168.0.0/16"),     # RFC1918 private space
     ipaddress.IPv4Network("198.18.0.0/15"),      # benchmark testing IP space 
     ipaddress.IPv4Network("198.51.100.0/24"),    # example IP space
     ipaddress.IPv4Network("203.0.113.0/24"),     # example IP space
     ipaddress.IPv4Network("224.0.0.0/4"),        # Multicast IP space
     ipaddress.IPv4Network("240.0.0.0/4"),        # reserved for future use
     ipaddress.IPv4Network("255.255.255.255/32")  # Local broadcast
   ]

   ip = ipaddress.IPv4Address(ip_string)

   # Iterate explicitly because 'ip in reserved_spaces' doesn't work
   # for lists of IPv4Network objects.
   for net in reserved_spaces:
     if ip in net:
       return True
   return False
