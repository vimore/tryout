#!/usr/local/bin/python

import datetime
import math
import re
import sys

import iso8601


class Timestamps():
  def __init__(self):
    '''Perform operations on timestamps.'''

  def get_intervals(self, timestamps, min_interval=5.0):
    '''Calculate time intervals between ordered timestamps >= a minimum length.

    Accepts:
      timestamps: A list of ordered datetime objects.
      min_interval: A minimum interval length in seconds (default: 5.0)'''

    intervals = []
    indexes = []
    for index in range(len(timestamps)):
      if index > 0:
        interval = timestamps[index] - timestamps[index - 1]
        try:
          secs = interval.total_seconds()
        except AttributeError:
          # This is Python < 2.7, so do a workaround.
          secs = float(interval.days * 86400 + interval.seconds) + float(interval.microseconds) * 1e-6
        if secs >= min_interval:
          indexes.append((index - 1, index))
          intervals.append(secs)
    return indexes, intervals

  def parse_timestamps(self, iso_timestamps):
    '''Robustly parse ISO timestamps and return as sorted lists.
    Accepts:
      iso_timestamps: a list of ISO timestamp strings,
          e.g. 2014-01-01T12:00:00.000000Z
    Returns:
      timestamps: a list of ordered datetime.datetime objects
      selected_timestamps: the original timestamps that were parseable,
          in the same order as the list 'timestamps'
    '''
    timestamps = []
    selected_timestamps = []
    for index in range(len(iso_timestamps)):
      try:
        timestamps.append(iso8601.parse_date(iso_timestamps[index]))
        selected_timestamps.append(iso_timestamps[index])
      except iso8601.iso8601.ParseError:
        # A null or some malformed date. Just count it and skip it.
        sys.stderr.write("ERROR: Could not parse date: " + str(iso_timestamps[index]) + "\n")
        sys.stderr.write("reporter:counter:streaming,Parse Errors,Timestamp,1\n")
        continue
    if len(timestamps) == 0 or len(selected_timestamps) == 0:
      return None, None
    # Sort the two lists the same way.
    timestamps, selected_timestamps = zip(*sorted(
        zip(timestamps, selected_timestamps)))
    # Convert the tuples returned by the above back to lists.
    timestamps = [t for t in timestamps]
    selected_timestamps = [t for t in selected_timestamps]
    return timestamps, selected_timestamps

class TimestampBinner():

  def __init__(self, bin_spec, last_date=None, first_date=None, first_weekday=None):
    '''Truncate ISO timestamps to a specific bin size.

    Accepts:
      bin_spec: A string specifying a bin size, of the
        format "%d%s" where the integer indicates the
        number of units, while the "%s" is a single
        letter indicating the length of time for binning.

        Allowed values:
        's' = seconds, 'm' = minutes, 'h' = hours, 'd' = days.

        The bin size should be an integer fraction of the
        next larger unit, e.g. '15s' or '10s' but not '23s'.

      For bin_spec values in days, specify ONE of the following options:

      last_date: (default=None) A string indicating the last date of
        a multi-day time bin, in ISO date format (e.g. 2014-01-01).
        This argument is only valid or needed for multi-day bins,
        where the truncation strategy is otherwise ambiguous.

      first_date: (default=None) Same as last_date except specifying the
        first date of a multi-day time bin.

      first_weekday: (default=None) A string indicating the first weekday
        of weekly time bins. This option is only valid or allowed for
        bin_spec='7d'.'''
    self.spec_format = re.compile(r'^(?P<multiple>[0-9]+)(?P<unit>[smhd])$')
    self.multiple, self.bin_function = self._parse_spec(bin_spec)
    if self.bin_function == self._multiday_bin:
      if last_date:
        self._options_are_not_set([first_date, first_weekday])
        date_to_use = last_date
      elif first_date:
        self._options_are_not_set([last_date, first_weekday])
        # Use a date of first_date - 1 day to specify the end of a multiday period.
        date_to_use = (iso8601.iso8601.parse_date(first_date)
                       + datetime.timedelta(days=-1)).strftime('%Y-%m-%d')
      elif first_weekday:
        # Find a date that corresponds to the nearest weekday that matches 'first_weekday',
        # and set date_to_use to the day before that as the last day of a period.
        self._options_are_not_set([last_date, first_date])
        try:
          assert(self.multiple == 7)
        except AssertionError:
          sys.stderr.write("ERROR: The option 'first_weekday' can only be used"
                           " with bin_spec='7d'.\n")
          raise TypeError
        weekdays = {
            'Monday': 0, 'Tuesday': 1, 'Wednesday': 2, 'Thursday': 3,
            'Friday': 4, 'Saturday': 5, 'Sunday': 6,
            'Mon': 0, 'Tue': 1, 'Wed': 2, 'Thu': 3, 'Fri': 4, 'Sat': 5, 'Sun': 6 }
        today = datetime.datetime.utcnow().replace(
            hour=0, minute=0, second=0, microsecond=0)
        # We want the day before first_weekday.
        adjustment = datetime.timedelta(
            days=(weekdays[first_weekday] - today.weekday() - 1))
        date_to_use = (today + adjustment).strftime("%Y-%m-%d")
      self.last_date = iso8601.iso8601.parse_date(date_to_use)
      self.last_time = iso8601.iso8601.parse_date(date_to_use + 'T23:59:59.999999Z')
    elif last_date or first_date or first_weekday:
      sys.stderr.write("ERROR: Cannot define last_date, first_date or first_weekday "
                       "for bin size <= 1 day.\n")
      raise TypeError


  def _options_are_not_set(self, list_of_options):
    '''Require that a list of options only values of 'None', or raise an error'''
    if all([x is None for x in list_of_options]):
      return
    else:
      sys.stderr.write("ERROR: Only set one of the options "
                       "[last_date, first_date, first_weekday].\n")
      raise TypeError

  def iso_bin(self, iso_timestamp):
    '''Given an ISO timestamp, return binned version.

    Accepts:
      iso_timestamp: An ISO timestamp string.

    Returns:
      An ISO timestamp string truncated to the beginning of a time
      bin that was defined when this TimestampBinner() object was
      instantiated.
    '''
    return self.bin_function(iso_timestamp)

  #### Private functions. You should need to call these directly.

  def _outformat(self, dt):
    # Produce UTC timestamps in Java format, otherwise ISO with UTC offset.
    if dt.tzinfo.tzname(None) == 'UTC':
      return dt.strftime('%Y-%m-%dT%H:%M:%S.%fZ')
    else:
      return dt.isoformat()

  def _second_bin(self, iso_timestamp):
    dt = iso8601.parse_date(iso_timestamp)
    bucket = self.multiple * math.floor(dt.second/self.multiple)
    dt = dt.replace(second = int(bucket), microsecond = 0)
    iso_format = self._outformat(dt)
    return iso_format

  def _minute_bin(self, iso_timestamp):
    dt = iso8601.parse_date(iso_timestamp)
    bucket = self.multiple * math.floor(dt.minute/self.multiple)
    dt = dt.replace(minute = int(bucket), second = 0, microsecond = 0)
    iso_format = self._outformat(dt)
    return iso_format

  def _hour_bin(self, iso_timestamp):
    dt = iso8601.parse_date(iso_timestamp)
    bucket = self.multiple * math.floor(dt.hour/self.multiple)
    dt = dt.replace(hour = int(bucket), minute = 0, second = 0, microsecond = 0)
    iso_format = self._outformat(dt)
    return iso_format

  def _day_bin(self, iso_timestamp):
    assert(self.multiple == 1)
    dt = iso8601.parse_date(iso_timestamp)
    dt = dt.replace(hour = 0, minute = 0, second = 0, microsecond = 0)
    iso_format = self._outformat(dt)
    return iso_format

  def _multiday_bin(self, iso_timestamp):
    dt = iso8601.parse_date(iso_timestamp)
    diff = self.last_date - dt
    intervals = diff.days / self.multiple  # How many whole time intervals.
    begin_date = (self.last_date
                  - datetime.timedelta(days=self.multiple-1)
                  - datetime.timedelta(days=self.multiple * intervals))
    return self._outformat(begin_date)

  def _parse_spec(self, bin_spec):
    mo = self.spec_format.match(bin_spec)
    if not mo:
      sys.stderr.write("ERROR: bin_spec parameter malformed.\n")
      raise TypeError
    multiple = int(mo.group('multiple'))
    unit = mo.group('unit')
    if multiple == 0:
      sys.stderr.write("ERROR: bin_spec does not allow a zero multiple.\n")
      raise TypeError
    if unit == 's':
      if multiple > 30:
        raise TypeError
        sys.stderr.write("ERROR: Can't bin on seconds > 30\n")
      if 60 % multiple > 0:
        raise TypeError
        sys.stderr.write("ERROR: Second bins must divide evenly into 60\n")
      return multiple, self._second_bin
    elif unit == 'm':
      if multiple > 30:
        raise TypeError
        sys.stderr.write("ERROR: Can't bin on minutes > 30\n")
      if 60 % multiple > 0:
        raise TypeError
        sys.stderr.write("ERROR: Minute bins must divide evenly into 60\n")
      return multiple, self._minute_bin
    elif unit == 'h':
      if multiple > 12:
        raise TypeError
        sys.stderr.write("ERROR: Can't bin on hours > 12\n")
      if 24 % multiple > 0:
        raise TypeError
        sys.stderr.write("ERROR: Hour bins must divide evenly into 24\n")
      return multiple, self._hour_bin
    elif unit == 'd':
      if multiple > 1:
        return multiple, self._multiday_bin
      else:
        return multiple, self._day_bin
    else:
      sys.stderr.write("ERROR: bin_spec bad unit supplied\n")
      raise TypeError


if __name__ == '__main__':
  import pydoc
  t = Timestamps()
  print pydoc.render_doc(t, 'Help on %s')
  
  tb = TimestampBinner('1d')
  print pydoc.render_doc(tb, 'Help on %s')
