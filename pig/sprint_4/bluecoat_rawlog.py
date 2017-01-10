#!/usr/local/bin/python

import json
import md5
import re
import sys

unescaped_backslash = re.compile(r'\d{4}-\d{2}-\d{2} \d{2}:.*[^\\]\\[^"\\]')
unescaped_quote = re.compile(r'\d{4}-\d{2}-\d{2} \d{2}:.*[^\\]"[^}]')

for line in sys.stdin:
   line = line.rstrip('\n')
   fields = line.split('\t')
   raw_log = '\t'.join(fields[6:])
   # Escape quote characters and tabs.
   while unescaped_backslash.search(raw_log):
     print raw_log
     raw_log = re.sub(r'(\d{4}-\d{2}-\d{2} \d{2}:.*[^\\])\\([^"\\])', r'\1\\\\\2', raw_log)
   while unescaped_quote.search(raw_log):
     raw_log = re.sub(r'(\d{4}-\d{2}-\d{2} \d{2}:.*[^\\])"([^}])', r'\1\\"\2', raw_log)
   raw_log = re.sub(r'\t', r'\\\\t', raw_log)
   # Decode the json and extract the original syslog.
   try:
     log_line = unicode(json.loads(raw_log)['body']['bytes']).encode('utf-8')
     print '{0}\t{1}\t{2}'.format(
         unicode('\t'.join(fields[0:6]), 'utf-8').encode('utf-8'),
         md5.new(raw_log).hexdigest(),
         log_line)
   except:
     sys.stderr.write("ERROR: " + line + '\n')
     raise
