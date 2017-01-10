#!/usr/local/bin/python

class BeaconConfidence():
  def __init__(self):
    '''Given a relative IQR, return beaconing confidence.

    A lookup table translates a relative inter-quartile range
    (riqr), defined as IQR/median, into a beaconing confidence
    value.

    Usage:
      c = BeaconConfidence()
      c.confidence(1.0)
    '''
    # Table of [low_value, high_value, confidence]
    # Use the riqur to lookup the appropriate confidence value.
    self.confidence_table = []
    self.confidence_table.append([0, 0.001, 1])
    self.confidence_table.append([0.001, 0.005, 0.97])
    self.confidence_table.append([0.005, 0.02, 0.95])
    self.confidence_table.append([0.02, 0.05, 0.92])
    self.confidence_table.append([0.05, 0.075, 0.90])
    self.confidence_table.append([0.075, 0.09, 0.87])
    self.confidence_table.append([0.09, 0.1, 0.85])
    self.confidence_table.append([0.10, 0.125, 0.82])
    self.confidence_table.append([0.125, 0.15, 0.80])
    self.confidence_table.append([0.15, 0.175, 0.70])
    self.confidence_table.append([0.175, 0.20, 0.60])
    self.confidence_table.append([0.20, 0.25, 0.50])
    self.confidence_table.append([0.25, 0.30, 0.40])
    self.confidence_table.append([0.30, 0.35, 0.30])
    self.confidence_table.append([0.35, 0.40, 0.20])
    self.confidence_table.append([0.40, 0.45, 0.10])
    # Confidence for any higher riqr is 0.0
    self.MAX_RIQR = 0.45

  def confidence(self, riqr):
    '''Given riqr of a list of time intervals, return beaconing confidence.
   
    Accepts:
    * riqr: relative inter-quartile range (IQR/median), a real value [0, Infinity)
      Intended to be used for time intervals, which are by definition positive.

    Returns:
    * confidence: a value 0.0-1.0 indicating confidence that this riqr
      describes a set of beacon intervals.
    '''
    confidence = 0.0
    for row in self.confidence_table:
        if (riqr >= row[0] and riqr < row[1]):
            confidence = row[2]
    return confidence

if __name__ == '__main__':
  b = BeaconConfidence()
  assert(b.confidence(0.12) == 0.82)
  # High riqrs have zero confidence.
  assert(b.confidence(1) == 0.0)
  print "All tests pass"
