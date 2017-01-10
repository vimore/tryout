#!/usr/local/bin/python

import sys

class AnomalyProfile():
  def __init__(self):
    pass

  def profile(self, names, residuals, anomaly_scores):
    '''Generate an anomaly profile string.

    The dimension of each input list must be identical.
    Only returns a profile string if there is more than one anomaly.

    Accepts:
      names: A list of strings, one for each model.
      residuals: A list of floats, the residuals for each model.
      anomaly_scores: A list of floats, the anomaly score for each model.

    Returns:
      profile_string: The generated anomaly profile identifier.'''

    profile_string = ''
    count = 0
    for i in range(len(names)):
      if anomaly_scores[i] >= 0.9:
        count += 1
        if len(profile_string) > 0:
          profile_string += ' '
        profile_string += names[i]
        if residuals[i] > 0.0:
          profile_string += '+'
        elif residuals[i] < 0.0:
          profile_string += '-'
        else:
          sys.stderr.write("ERROR: Zero residual with high anomaly score.\n")
    if count > 1:
      return profile_string
    else:
      return ''
