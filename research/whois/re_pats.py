import re

BLANK = re.compile(r'^$')
IPV4_ADDRESS = re.compile(r'^([0-9]{1,3}\.){3}[0-9]{1,3}$')
IPV4_NETBLOCK = re.compile(r'^([0-9]{1,3}\.){3}[0-9]{1,3}/[0-9]{1,2}$')
IPV6_ADDRESS = re.compile(r'^[0-9a-fA-F:]{3,39}$')
IPV6_NETBLOCK = re.compile(r'^[0-9a-fA-F:]{3,39}/[0-9]{1,2}$')
