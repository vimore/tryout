#!/usr/bin/python

# Read Bluecoat data in the format found in Srini's bluecoat_parsed.log
# data.

class Bluecoat:
  def __init__(self, filename):
    self.fh = open(filename, 'r')
    self.fields = self.fh.readline().rstrip('\n').split()

  def __iter__(self):
    return self

  def next(self):
    row = {}
    fieldlist = self.fh.readline().split()
    if len(fieldlist) == 0:
      # end of file
      raise StopIteration
    for i in range(len(fieldlist)):
      row[self.fields[i]] = fieldlist[i]
    return row

if __name__ == '__main__':
  for row in Bluecoat('../bluecoat_parsed.log'):
    print row['c-ip']
