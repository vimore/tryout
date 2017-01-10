
import profile

p = profile.AnomalyProfile()
# Mixed profile.
assert(p.profile(
           ['FieldA', 'FieldB', 'FieldC'],
           [1.0, 0.0, -1.0],
           [0.9, 0.5, 1.0]) == 'FieldA+ FieldC-')
# Only one thing anomalous, don't return a string.
assert(p.profile(
           ['FieldA', 'FieldB', 'FieldC'],
           [1.0, 0.0, -1.0],
           [1.0, 0.5, 0.0]) == '')
# Nothing anomalous.
assert(p.profile(
           ['FieldA', 'FieldB', 'FieldC'],
           [1.0, 0.0, -1.0],
           [0.7, 0.5, 0.0]) == '')
print "PASS"
