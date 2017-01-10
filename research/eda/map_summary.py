import operator
import sys

class MapSummary():
  '''Given a dictionary, generate descriptive statistics.

  Accepts:
    input_map: A Python dictionary of {key: count} pairs.
  '''
  def __init__(self, input_map):
    # Accept input variables.
    self.input_map = input_map;
    # Initialize output variables.
    self.total_count = None
    self.minimum = None
    self.first_quartile = None
    self.median = None
    self.third_quartile = None
    self.maximum = None
    self.five_number = None
    self.unique = None
    self.top10 = None
    self._process()

  def _process(self):
    '''Decide what the input data is, and calculate stats.'''
    self.unique = self._count_unique()
    if self.unique:
      # Only do anything else if there are counts in the map.
      self.total_count = self._get_total_count()
      if self._convert_keys():
        # Keys are numeric, so calculate numeric stats.
        self._calculate_quantiles()
      else:
        sys.stderr.write("WARNING: Keys not converted to numeric.\n")
      # Also calculate categorical stats, even if keys are numeric,
      # since some numeric fields are actually categorical (e.g.
      # port numbers)
      self.unique = self._count_unique()
      self.top10 = self._topn(10)

  def _count_unique(self):
    if not self.input_map:
      return None
    unique = len(self.input_map.keys())
    if unique > 0:
      return unique
    else:
      return None

  def _topn(self, n):
    '''Given the frequency distribution, find top N.
 
    Accepts:
      n: Number of top entries to return.
 
    Returns:
      A list of up to n tuples
        (key:string/float,
         count:integer,
         percentage of total counts:float,
         cumulative percentage of total counts:float)
    '''
    # Has some nondeterministic behavior in cases where the input
    # data has keys with equal counts. The sort order will then
    # be different depending on how the input dictionary is
    # stored. This is probably fine since there would be no
    # preferred order in that case anyway.
    outlist = []
    cumulative_count = 0
    for (key, count) in sorted(self.input_map.iteritems(),
                               key=operator.itemgetter(1),
			       reverse=True):
      cumulative_count += count
      outlist.append((key, count,
                      float(count)/float(self.total_count),
                      float(cumulative_count)/float(self.total_count)))
      if len(outlist) >= n:
        break
    return outlist

  def _convert_keys(self):
    '''Iterate through keys and try to convert to numeric values.'''
    new_map = {}
    for key in self.input_map.keys():
      try:
        new_key = float(key)
        if new_map.has_key(new_key):
          # If a different key converted to the same numeric value, then
          # instead of creating a new entry in the new map, just add the
          # counts to the matching key.
          new_map[new_key] += self.input_map[key]
        else:
          new_map[new_key] = self.input_map[key]
      except ValueError:
        sys.stderr.write("WARNING: Key '" + str(key) + "' was non-numeric.\n")
        return False
    # We were able to convert all keys to a numeric value, so let's
    # replace self.input_map with the newly assembled one that uses
    # numeric keys.
    self.input_map = new_map
    return True

  def _check_quantile(self, quantile, key, count):
    if not quantile and float(self.current_count) >= count:
      if self.last_count and float(self.last_count + 1) > count:
        return self.last_key + (key - self.last_key) * (count - float(int(count)))
      else:
        return key
    return quantile

  def _get_quantile(self, quantile):
    # Using the NIST recommended procedure.
    return float(quantile) * (float(self.total_count - 1)) / 100.0 + 1.0

  def _get_total_count(self):
    total_count = 0
    for (key, count) in self.input_map.iteritems():
      total_count += count
    if total_count > 0:
      return total_count
    else:
      return None

  def _calculate_quantiles(self):
    # We've already decided that the keys are numeric, so calculate
    # quantiles here.
    first_quartile_count = self._get_quantile(25)
    median_count = self._get_quantile(50)
    third_quartile_count = self._get_quantile(75)
    self.current_count = 0
    self.last_key = None
    self.last_count = None
    for key in sorted(self.input_map.keys()):
      self.current_count += self.input_map[key]  # Add the count of the current key.
      if not self.minimum:
        # This is the lowest-valued key, so it's our minimum.
        self.minimum = key
      self.first_quartile = self._check_quantile(self.first_quartile, key, first_quartile_count)
      self.median = self._check_quantile(self.median, key, median_count)
      self.third_quartile = self._check_quantile(self.third_quartile, key, third_quartile_count)
      self.last_key = key
      self.last_count = self.current_count
    # We reached the highest-valued key, so that's our maximum.
    self.maximum = self.last_key
    self.five_number = (self.minimum, self.first_quartile, self.median, self.third_quartile, self.maximum)
