#!/usr/bin/python

# TODO(mdeshon): Use package iso8601 instead of time

# Generates domains according to the domain generation algorithm (DGA) used
# by the Torpig botnet.
#
# Adapted from Brett Stone-Gross et al. "Your Botnet is My Botnet: Analysis 
# of a Botnet Takeover"
# http://www.net.t-labs.tu-berlin.de/teaching/ws0910/IS_seminar/papers/torpig.pdf

import sys
import time

suffix = ["anj", "ebf", "arm", "pra", "aym", "unj",
          "ulj", "uag", "esp", "kot", "onv", "edc"]

def generate_daily_domain(t):
  #t = GetLocalTime()
  t = time.localtime(time.time())
  p = 8
  return generate_domain(t, p)

def scramble_date(t, p):
  return (((t.tm_mon ^ t.tm_mday) + t.tm_mday) * p) + t.tm_mday + t.tm_year

def generate_domain(t, p):
  if t.tm_year < 2007:
    year = 2007
  else:
    year = t.tm_year
  s = scramble_date(t, p)
  c1 = (((year >> 2) & 0x3fc0) + s) % 25 + ord('a')
  c2 = (t.tm_mon + s) % 10 + ord('a')
  c3 = ((year & 0xff) + s) % 25 + ord('a')
  if chr(t.tm_mday * 2) < ord('0') or t.tm_mday * 2 > ord('9'):
    c4 = (t.tm_mday * 2) % 25 + ord('a')
  else:
    c4 = t.tm_mday % 10 + ord('1')
  return chr(c1) + 'h' + chr(c2) + chr(c3) + 'x' + chr(c4) + suffix[t.tm_mon - 1]

def CheckDomain(iso_string, domain):
  # Example ISO string: 2014-03-03T20:04:11.000Z
  t = time.strptime(iso_string, "%Y-%m-%dT%H:%M:%S.000Z")
  dga_domain = generate_domain(t, 8)
  dga_domains = [dga_domain + '.com', dga_domain + '.net', dga_domain + '.biz']
  if domain in dga_domains:
    return True
  else:
    return False

if __name__ == '__main__':
  if len(sys.argv) > 1:
    t = time.strptime(sys.argv[1], "%Y-%m-%d")
    print generate_domain(t, 8)
  else:
    for line in sys.stdin:
      line = line.rstrip("\n")
      iso_string, domain = line.split("\t")
      if CheckDomain(iso_string, domain):
        print "{0}\t{1}".format(iso_string, domain)
