#!/usr/bin/python

import csv
import re
import sys

class IPRegistry:
  # Column headings
  PREFIX = 'Prefix'
  DESIGNATION = 'Designation'
  DATE = 'Date'
  WHOIS = 'Whois'
  STATUS = 'Status [1]'
  NOTE = 'Note'

  # Status values
  LEGACY = 'LEGACY'
  RESERVED = 'RESERVED'
  ALLOCATED = 'ALLOCATED'
  STATUS_VALUES = [LEGACY, RESERVED, ALLOCATED]

  # Registry whois values
  ARIN = 'whois.arin.net'
  RIPE = 'whois.ripe.net'
  APNIC = 'whois.apnic.net'
  AFRINIC = 'whois.afrinic.net'
  LACNIC = 'whois.lacnic.net'
  WHOIS_VALUES = [ARIN, RIPE, APNIC, AFRINIC, LACNIC]

  # File is from http://www.iana.org/assignments/ipv4-address-space/ipv4-address-space.csv
  # which should be checked occasionally for changes.
  def __init__(self, filename='Data/ipv4-address-space.csv'):
    self.filename = filename
    self.re_slasheight = re.compile(r'^[0-9]{3}/8$')
    self.data = {}
    self.parse_()

  def get_row(self):
    for row in self.data:
      yield row

  ### Private functions
  def parse_(self):
    csv_reader = csv.DictReader(open(self.filename, 'r'))
    for row in csv_reader:
      self.validate_row_(row)
      first_octet = int(re.sub(r'^0*([0-9]+)/8$', r'\1', row[self.PREFIX]))
      self.data[first_octet] = row

  def validate_row_(self, row):
    # We're assuming that prefixes are all /8's.
    try:
      assert self.re_slasheight.match(row[self.PREFIX])
    except AssertionError:
      sys.stderr.write("ERROR: Row contains a prefix other than /8" + str(row))
      raise
    # We're assuming that all rows have one of the known status values.
    try:
      assert row[self.STATUS] in self.STATUS_VALUES
    except AssertionError:
      sys.stderr.write("ERROR: Row contains unknown status: " + str(row))
      raise
    # We're assuming that all nonempty whois servers are on our list.
    if row[self.WHOIS]:
      try:
        assert row[self.WHOIS] in self.WHOIS_VALUES
      except AssertionError:
        sys.stderr.write("ERROR: Row contains unknown whois: " + str(row))
        raise

if __name__ == '__main__':
  ipr = IPRegistry()
