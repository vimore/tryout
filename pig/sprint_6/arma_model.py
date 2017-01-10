#!/usr/local/bin/python

import pydoc
import sys

import numpy as np
import scipy
from scipy import *
import scipy.stats

import statsmodels.tsa.arima_model as arima
from statsmodels.tsa.arima_model import *


class ArmaModel:
  '''Fit an ARMA(n, n) model to a supplied time series, and predict future values.

     Usage:
       a = arma_model.ArmaModel()

     Accepts:
       attempts: (default=20) number of times to attempt to fit at each order.
       maxorder: (default=10) maximum order of ARMA model to try.
       maxiter: (default=200) maximum number of iterations when fitting.
       method: (default='bgfs') ARMA fitting method to use (mle=Maximum Likelihood
               Estimation)
       disp: (default=False) Display fitting attempts as they run.
  '''

  def __init__(self, attempts=20, maxorder=10, maxiter=200, method='mle',
               solver='bfgs', disp=False):
    self.attempts = attempts
    self.maxorder = maxorder
    self.maxiter = maxiter
    self.method = method
    self.solver = solver
    self.disp = disp

  def _extractParameters(self, series, order):
    n = len(series)
    model = arima.ARIMA(series, order).fit(method = self.method, maxiter = self.maxiter,
        solver = self.solver, disp = self.disp)
    return model.resid, model.params

  def fit(self, series, order_n=None):
    '''Fit an ARMA(n, n) model to the supplied time series.
    
    Usage:
      try:
        a = arma_model.ArmaModel(series)
        parameters, order, residuals = a.parameters, a.order, a.residuals
      except TypeError:
        # Fitting has failed.
        raise

    Accepts:
       series: The time series to model.
       order_n: (optional) The ARMA(n, n) model order n.

    Returns:
       parameters: the model parameters.
       order: the ARIMA model order used in the form (n, 0, n).
       residuals: the residuals of the model when compared to the supplied series.
       resid_mean: mean of the residuals.
       resid_std: standard deviation of the residuals.
    '''
    success = False
    residuals = None
    for candidate_order in range(1, self.maxorder):
      if order_n:
        # A particular order was requested.
        if candidate_order != order_n:
          # This was not the order requested, so go to the next order.
          continue
      #print "Trying order ", candidate_order
      for attempt in range(self.attempts):
        #print "Attempt", attempt
        order = (candidate_order, 0, candidate_order)
        try:
          residuals, parameters = self._extractParameters(series, order)
          residuals = residuals[:-1]
          success = True
          break
        except:
          # It's OK to fail, keep trying.
          continue
      if success:
        resid_mean = mean(residuals)
        resid_std = std(residuals)
        return parameters, order, residuals, resid_mean, resid_std
    sys.stderr.write('WARNING: ARIMA model not fitted.\n')
    return None, None, None, None, None

  def predict_residuals(self, series, parameters, order, resid_mean,
      resid_std, test_points):
    '''Given some data points, calculate the residuals compared to predicted values.

    Usage:
       a.predict_residuals(series, parameters, order, test_points)

    Accepts:
       series: the data series that as passed to fit().
       parameters: the parameters returned by fit().
       order: the order returned by fit().
       resid_mean: the mean of the residuals of series.
       resid_std: the standard deviation of the residuals of series.
       test_points: the values occurring just after series.

    Returns:
       residuals: the difference between the data points predicted by the
          model and the points provided in test_points.
       residual_pvalues: the p-values of the residuals based on a Gaussian
          model using resid_mean and resid_std.
    '''

    series_length = len(series)
    data = np.concatenate((series, test_points))
    prediction = []
    # There's no way to set the parameters directly, so we do a workaround where
    # we give start_parameters and don't allow the fitting routine to change them
    # (1 iteration only).
    model = arima.ARIMA(data, order).fit(start_params = parameters, maxiter = 1,
        solver = self.solver, disp = self.disp)
    predicted_points = array(model.predict(start = series_length, end = len(data) - 1))
    new_residuals = test_points - predicted_points
    residual_pvalues = [scipy.stats.kstest([(x - resid_mean)/resid_std],
        'norm')[1] for x in new_residuals]
    return predicted_points, new_residuals, residual_pvalues

  def predict(self, series, parameters, order, number_of_values=1):
    '''Given the current series and model, predict some future values.

    Usage:
       a.predict(series, parameters, order, number_of_values)

    Accepts:
       series: the data series that was modeled using it().
       parameters: the parameters returned by fit().
       order: the model order returned by fit().
       number_of_values: integer, default=1. The number of points to predict
         to continue the series under the current model.

    Returns:
       values: A list of the next number_of_values data points based on the model.
    '''
    n = len(series)
    model = arima.ARIMA(series, order).fit(solver=self.solver,
        start_parameters=parameters, maxiter = 0)
    predicted = model.predict(start=len(series), end=len(series) - 1 + number_of_values)
    return predicted

  def model_type(self, order):
    '''Render a string describing the model in use.
    
    Usage:
      a.model_type(order)

    Accepts:
      order: The model order as returned by fit().

    Returns:
      model_string: a string describing the model, e.g. ARMA(1, 1).
    '''
    type = ''
    if order[0] > 0:
      type += 'AR'
    if order[1] > 0:
      type += 'I'
    if order[2] > 0:
      type += 'MA'
    parenthetical = [x for x in order if x > 0]
    parenthetical = '(' + str(parenthetical).rstrip(']').lstrip('[') + ')'
    return type + parenthetical

if __name__ == '__main__':
  a = ArmaModel()
  print pydoc.render_doc(a, 'Help on %s')
