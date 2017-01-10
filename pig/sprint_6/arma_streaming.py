#!/usr/local/bin/python

import sys

import arma_model
import pig_parsing


# Usage:
#   Input is 
#     identifier:chararray,
#     series:tuple
#
#   Output is
#     identifier:chararray,
#     series:tuple,
#     params:tuple,
#     order: tuple,
#     resid_mean:double,
#     resid_std:double
#
# These outputs are used as inputs to arma_predict_streaming.py,
# along with one or more data points to be predicted. So, these
# outputs should be stored as defining the model for that timeseries.

if __name__ == '__main__':
  a = arma_model.ArmaModel()
  p = pig_parsing.PigParsing()
  for line in sys.stdin:
    line = line.rstrip('\n')
    fields = line.split('\t')

    # Parse the input fields.
    identifier = fields[0]
    series = [float(x) for x in p.str2strlist(fields[1])]

    # Fit an ARMA(1,1) model.
    [params, order, resid, resid_mean, resid_std] = a.fit(series, 1)
    if order is None:
      [params, order, resid, resid_mean, resid_std] = a.fit(series, 2)

    # Convert lists to tuple strings that Pig understands.
    series = p.list2tuple(series)
    params = p.list2tuple(params)
    order = p.list2tuple(order)

    # Print the output line.
    print "{0}\t{1}\t{2}\t{3}\t{4}\t{5}".format(
        identifier, series, params, order, resid_mean, resid_std)
