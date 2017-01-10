#!/usr/bin/python

import sys

import numpy

import arma_model


if __name__ == '__main__':
    A = arma_model.ArmaModel(disp=True)
    series = numpy.random.poisson(5,200)
    [params, order, resid, resid_mean, resid_std] = A.fit(series)
    sys.stderr.write("params " +  str(params) + "\n")
    sys.stderr.write("order " + str(order) + "\n")
    test_points = numpy.random.poisson(15,5)
    residuals, pvalues = A.predict_residuals(
        series, params, order, resid_mean, resid_std, test_points)
    sys.stderr.write("Residuals: " + str(residuals) + "\n")
    sys.stderr.write("p-values: " + str(pvalues) + "\n")
    sys.stderr.write("Predicted points: " + str(A.predict(series, params, order, 5)) + "\n")
    print "PASS"
