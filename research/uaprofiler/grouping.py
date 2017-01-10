#!/usr/local/bin/python

import cPickle
import os

import uaparser


def grouping(filename):
	
	uacount = {}
	uadict = {}
	num = 0
	
	uas = open(filename, 'r').read().splitlines()
	for rua in uas:
		count, ua = rua.split('\t')
		uatokens = uaparser.breakua(ua.strip('"'))			

		''' Only if Mozilla, we use token sequence '''
		if uatokens[0] == 'Mozilla':
			seq = '/'.join(uatokens[0::3])
			if len(uatokens) == 3:
				ctokens = uaparser.breakcomment(uatokens[2])
				if 'MSIE' in ctokens:
					seq = 'Mozilla/MSIE'
		else:	
			seq = uatokens[0]
		
		if not uadict.has_key(seq):
			uadict[seq] = []
			uacount[seq] = ('UF%05d' % (num), 0)
			num = num + 1
		uadict[seq].append(rua)
		fname, count = uacount[seq]
		uacount[seq] = (fname, count + 1)
	
	cPickle.dump(uacount, open('uafamily.txt', 'w'));

	if not os.path.exists('./uafamily'):
		os.makedirs('uafamily')
	
	for uaseq in uadict:
		with open('uafamily/' + uacount[uaseq][0] + '.txt', 'w') as fp:
			fp.write('\n'.join(uadict[uaseq]))
			fp.write('\n')
		
def main():
	''' Data file should have format, each line is
	
		num_of_users\tuser-agent string
	
	'''
	grouping('numuser2ua.txt');

if __name__ == '__main__':
	main()
		
