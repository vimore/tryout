
# Accepts as input a tab-delimited line of:
#     key:chararray
#     values:tuple of int or double
#
# Returns one tab_delimited line per input line, of:
#   
#     key:chararray
#     minimum:double
#     first_quartile:double
#     median:double
#     third_quartile:double
#     maximum:double

import sys

import numpy as np

import pig_parsing as pp


if __name__ == '__main__':
  for line in sys.stdin:
    line = line.rstrip('\n')
    fields = line.split('\t')
    pp.str2strlist(fields[0]) = 
    np.percentile
