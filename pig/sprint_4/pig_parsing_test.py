#!/usr/local/bin/python

import pig_parsing

p = pig_parsing.PigParsing()
input_string = ('{2014-03-03T16:04:51.000Z,"2014-03-03T16:04:52.000Z",'
                '(2014-03-03T16:04:53.000Z),"(2014-03-03T16:04:54.000Z)"}')

assert(p.str2strlist(input_string) == [
    '2014-03-03T16:04:51.000Z', '2014-03-03T16:04:52.000Z',
    '2014-03-03T16:04:53.000Z', '2014-03-03T16:04:54.000Z'])

assert(p.str2strlist('{(2014-03-18T22:53:27.000Z)}') == ['2014-03-18T22:53:27.000Z'])


input_list = [1, 2, 3, 4, 5, 4, 5]
assert(p.list2tuple(input_list) == '(1, 2, 3, 4, 5, 4, 5)')
assert(p.list2map(input_list) == '[1#1, 2#1, 3#1, 4#2, 5#2]')

input_dict = {'a': 1, 'b': 2, 'c': 3}
assert(p.dict2map(input_dict) == '[a#1, c#3, b#2]')
print "PASS"
