import map_summary

ms = map_summary.MapSummary({'1.0': 1, '2.0': 1, '3.0': 1, '4.0': 1, '5.0': 1, '6.0': 1, '7.0': 1, '8.0': 1})
assert((ms.minimum, ms.first_quartile, ms.median, ms.third_quartile,
        ms.maximum) == (1.0, 2.75, 4.5, 6.25, 8.0))
assert(ms.total_count == 8)
assert(ms.five_number == (1.0, 2.75, 4.5, 6.25, 8.0))
ms = map_summary.MapSummary({'1.0': 1, '2.0': 1, '3.0': 1})
assert((ms.minimum, ms.first_quartile, ms.median, ms.third_quartile,
        ms.maximum) == (1.0, 1.5, 2.0, 2.5, 3.0))
ms = map_summary.MapSummary({'2.0': 6, '1.0': 1, '3.0': 1})
assert((ms.minimum, ms.first_quartile, ms.median, ms.third_quartile,
        ms.maximum) == (1.0, 2.0, 2.0, 2.0, 3.0))
ms = map_summary.MapSummary({'1.0': 1})
assert((ms.minimum, ms.first_quartile, ms.median, ms.third_quartile,
        ms.maximum) == (1.0, 1.0, 1.0, 1.0, 1.0))
# Null map yields all 'None' results.
ms = map_summary.MapSummary({})
assert((ms.minimum, ms.first_quartile, ms.median, ms.third_quartile,
        ms.maximum) == (None, None, None, None, None))
assert(ms.total_count == None)
assert(ms.unique == None)
ms = map_summary.MapSummary(None)
# Test the Top10 function.
ms = map_summary.MapSummary(
    {22.0: 0, 23.0: 0, 1.0: 1, 2.0: 2, 3.0: 3, 4.0: 4, 5.0: 5, 6.0: 6,
     7.0: 7, 8.0: 8, 9.0: 9, 10.0: 10})
assert(ms.top10 == 
    [(10.0, 10, 0.18181818181818182, 0.18181818181818182),
     (9.0, 9, 0.16363636363636364, 0.34545454545454546),
     (8.0, 8, 0.14545454545454545, 0.4909090909090909),
     (7.0, 7, 0.12727272727272726, 0.6181818181818182),
     (6.0, 6, 0.10909090909090909, 0.7272727272727273),
     (5.0, 5, 0.09090909090909091, 0.8181818181818182),
     (4.0, 4, 0.07272727272727272, 0.8909090909090909),
     (3.0, 3, 0.05454545454545454, 0.9454545454545454),
     (2.0, 2, 0.03636363636363636, 0.9818181818181818),
     (1.0, 1, 0.01818181818181818, 1.0)])
print "PASS"
