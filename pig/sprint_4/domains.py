#!/usr/local/bin/python

import tldextract

def get_tld(domain):
  t = tldextract.extract(domain)
  if t.suffix:
    return t.suffix
  else:
    return t.domain

def get_sld(domain):
  t = tldextract.extract(domain)
  if t.suffix:
    return t.domain + '.' + t.suffix
  else:
    return t.domain
