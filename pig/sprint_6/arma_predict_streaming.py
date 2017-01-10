#!/usr/local/bin/python

import sys

import arma_model
import pig_parsing


# Usage:
#
# Input is:
#   identifier:chararray,
#   series:tuple,
#   params:tuple,
#   order:tuple,
#   resid_mean:double,
#   resid_std:double,
#   test_points:tuple
#
# Output is:
#   identifier:chararray,
#   series:tuple,
#   test_points:tuple,
#   predictions:tuple,
#   residuals:tuple,
#   pvalues:tuple

if __name__ == '__main__':
  a = arma_model.ArmaModel()
  p = pig_parsing.PigParsing()
  for line in sys.stdin:
    line = line.rstrip('\n')
    fields = line.split('\t')

    # Parse the input fields
    identifier = fields[0]
    series = [float(x) for x in p.str2strlist(fields[1])]
    params = [float(x) for x in p.str2strlist(fields[2])]
    order = [int(x) for x in p.str2strlist(fields[3])]
    resid_mean = float(fields[4])
    resid_std = float(fields[5])
    test_points = [float(x) for x in p.str2strlist(fields[6])]

    # Apply the model, predict values and compare to test_points.
    predictions, residuals, pvalues = a.predict_residuals(
        series, params, order, resid_mean, resid_std, test_points)

    # Format lists into tuples that Pig understands.
    series = p.list2tuple(series)
    test_points = p.list2tuple(test_points)
    predictions = p.list2tuple(predictions)
    residuals = p.list2tuple(residuals)
    pvalues = p.list2tuple(pvalues)

    # Print the output line.
    print "{0}\t{1}\t{2}\t{3}\t{4}\t{5}".format(
        identifier, series, test_points, predictions, residuals, pvalues)
