#!/usr/local/bin/python

import sparse_hist

sh = sparse_hist.SparseHistogram()
assert(sh.count([3600.0, 3605.0, 3599.0, 3700.0, 5.0, 86400.0], 10.0, 3602.5)
       == {86400.0: 1, 3602.5: 3, 3700.0: 1, 5.0: 1})
assert(sh.count([20.0, 19.9, 20.1, 21.0, 32.0, 3600.0], 1.0)
       == {3600.0: 1, 20.25: 4, 32.0: 1})  # No desired_key set.
assert(sh.count([20.0, 19.9, 20.1, 21.0, 3600.0], 1.0)
       == {20.25: 4, 3600.0: 1})  # Odd number of intervals.
print "PASS"
