#!/usr/local/bin/python

import csv
import StringIO
import sys

class PigParsing():
  def __init__(self):
    pass

  def str2strlist(self, input_string):
    # Strip outer characters.
    input_string = input_string.rstrip(')"}\'').lstrip('("{\'')
    file_handle = StringIO.StringIO(input_string)
    try:
      field_list =  csv.reader(file_handle).next()
    except:
      sys.stderr.write("ERROR: " + input_string + '\n')
      raise
    field_list = [x.lstrip().lstrip('{("\'').rstrip('})"\'') for x in field_list]
    return field_list

  def dict2map(self, input_dict):
    output_string = '['
    for key, value in input_dict.iteritems():
      output_string = output_string + str(key) + '#' + str(value) + ', '
    output_string = output_string.rstrip(', ')
    output_string = output_string + ']'
    return output_string

  def list2tuple(self, input_list):
    output_string = '('
    for value in input_list:
      output_string = output_string + str(value) + ", "
    output_string = output_string.rstrip(', ')
    output_string = output_string + ')'
    return output_string

  def list2map(self, input_list):
    # Maps aren't really appropriate for lists, but since tuples can't
    # have arbitrary length sometimes this format is useful. The value is
    # the number of times that the key appears in the list, so there's
    # no requirement for uniqueness of list entries.
    output_dict = {}
    for key in input_list:
      if output_dict.has_key(key):
        output_dict[key] = output_dict[key] + 1
      else:
        output_dict[key] = 1
    return self.dict2map(output_dict)
