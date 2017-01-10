#!/usr/bin/python

import os
import Queue
import socket
import sys
import threading

import re_pats


class ReverseLookups:
  def __init__(self, infile, num_threads, outfile):
    self.num_threads = int(num_threads)
    self.q = Queue.Queue()
    self.outq = Queue.Queue()
    self.outfile = outfile
    self.knownips = []
    if os.path.exists(outfile):
      self.get_knownips_(outfile)
    self.populate_queue_(infile)

  def populate_queue_(self, infile):
    ifile = open(infile)
    for line in ifile:
      ip = line.rstrip('\n')
      if re_pats.IPV4_ADDRESS.match(ip):
        if ip not in self.knownips:
          self.q.put(ip)
    sys.stderr.write("Looking up " + str(self.q.qsize()) + " IPs\n")
    ifile.close()

  def get_knownips_(self, outfile):
    ofile = open(outfile)
    for line in ofile:
      ip = line.split(' ', 1)[0]
      if ip and re_pats.IPV4_ADDRESS.match(ip):
        self.knownips.append(ip)
    ofile.close()
    if len(self.knownips) > 0:
      sys.stderr.write(
          "Ignoring " + str(len(self.knownips)) + " known ips.\n")

  def do_lookups(self):
    for i in range(self.num_threads):
      sys.stderr.write("Starting worker thread " + str(i) + "\n")
      t = threading.Thread(target=self.worker)
      t.daemon = True
      t.start()
    # Start a single output thread.
    wt = threading.Thread(target=self.writer)
    wt.daemon = True
    wt.start()
    self.q.join()
    self.outq.join()

  def writer(self):
    if os.path.exists(self.outfile):
      ofile = open(self.outfile, 'a')
    else:
      ofile = open(self.outfile, 'r')
    while True:
      ip, response = self.outq.get()
      ofile.write(str(ip) + " " + response + "\n")
      self.outq.task_done()
    
  def worker(self):
    while True:
      ip = self.q.get()
      #response = str(dns.reversename.from_address(ip))
      try:
        response = socket.gethostbyaddr(ip)
      except socket.herror as e:
        if e.errno in [1, 2, 4]:
          # 1 = Host not found
          # 2 = Lookup failure
          # 4 = No address associated with name
          response = ('', [], [])
        else:
          raise
      domains = response[0]
      for other_domain in response[1]:
        domains += " " + other_domain
      self.outq.put((ip, domains))
      self.q.task_done()

if __name__ == '__main__':
  if len(sys.argv) < 4:
    sys.stderr.write("Usage: " + sys.argv[0] + " infile num_threads outfile\n")
    sys.exit(1)
  rl = ReverseLookups(sys.argv[1], sys.argv[2], sys.argv[3])
  rl.do_lookups()
