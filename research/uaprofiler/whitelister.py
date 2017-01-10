#!/usr/local/bin/python

import uaparser

from fuzzywuzzy import process

"""
	Whitelisting tokens Experimental Code


	Shoufu Luo, August 14, 2014
"""

FUZZY_THRESHOLD = 80

"""
	Whitelist for Exact Matching
"""
whitelist_tokens_1 = { 
	'MAM3' : 'OEM:MSI', 
	'MAMI' : 'OEM:MSI', 
	'TAJB' : 'OEM:Toshiba', 
	'TNJB' : 'OEM:Toshiba', 
	'LCJB' : 'OEM:Lenovo', 
	'UAUC' : 'OEM:FAKE',
	'MAAU' : 'OEM:Asus', 
	'MAAR' : 'OEM:Acer', 
	'MDDR' : 'OEM:Dell', 
	'U' : 'Feature:Strong Encryption',
	'HPCMHP' : 'OEM:HP', 
	'WOW64' : 'Feature:64-bit OS running 32-bit machine',
	'SLCC2' : 'Feature'
}

"""
	Whitelist for Fuzzy Matching
"""
whitelist_tokens_2 = {
	'GTB' : 'EXT:Google ToolBar',
	'Windows NT': 'OS', 
	'Intel Mac OS X' : 'OS', 
	'Linux' : 'OS', 
	'Ubuntu' : 'OS', 
	'SunOS' : 'OS', 
	'iOS' : 'OS:Mobile', 
	'X8664' : 'Platform', 
	'iPhone' : 'Platform:Mobile', 
	'Nexus' : 'Platform:Mobile', 
	'Spider' : 'Bot', 
	'Crawler' : 'Bot', 
	'Android'  : 'OS', 
	'HTC One' : 'Platform',
	'Macintosh' : 'Platform',
	'MacBookPro' : 'Platform', 
	'iPad' : 'Platfrom:Tablet',
	'iMac':'Platform', 
	'MacBookAir' : 'Platform', 
	'.NET CLR' : 'Feature',
	'.NET':'Feature', 
}

def token_mapping(prod):
	'''
		map tokens
		@param prod = (token, version, '')
	'''
	profile = {}

	token = prod[0]
	version = prod[1]

	if token in whitelist_tokens_1.keys():
		matched = (token, whitelist_tokens_1[token], 100)
	else:
		result = process.extract(token, whitelist_tokens_2, limit=1)[0]
		matched = (result[0], whitelist_tokens_2[result[0]], result[1])

	#print matched
	if matched[2] > FUZZY_THRESHOLD:

		segs = matched[1].split(':')
		#print segs
		if not profile.has_key(segs[0]):
			profile[segs[0]] = []
		if len(segs) > 1:
			name = segs[1] + ' (' + token + ')' # Append the original token 
		else:
			name = matched[0];
			if matched[2] != 100:
				name += ' (' + token + ')' # Append the Original token as it is not exactly mathed
		if version != '':
			name = name + ' ' + version	
		profile[segs[0]].append(name)
	else:
		#print 'Not recognized token [', token, ']'
		pass

	if profile:
		print profile

def main():
	
	uas = [
		'Mozilla/5.0 (Windows NT 5.2; rv:26.0) Gecko/20100101 Firefox/26.',
		'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Site=00055A; Workstation=011080AP002; ASUPPRIMER; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C; .NET4.0E)', 
		'Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; MAAU)',
		'Mozilla/5.0 (iPad; U; CPU like Mac OS X; fr) AppleWebKit/420+ (KHTML, like Gecko) Zara/App/v1.10.1',
		'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_5_8) AppleWebKit/534.50.2 (KHTML, like Gecko) Version/5.0.6 Safari/533.22.3',
		'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB7.1)'
	]
	for ua in uas:
		print ua
		tokens = uaparser.breakua(ua)
		x = len(tokens)
		for i, comment in enumerate(tokens[2::3]):
			if comment == '':
				continue
			ctokens = uaparser.breakcomment(comment.strip())
			tokens[i*3 + 2] = ''
			if len(ctokens) > 1:
				tokens = tokens + ctokens
		print tokens
		map(token_mapping, zip(tokens[x::3], tokens[x+1::3], tokens[x+2::3]))
		print '---'

if __name__ == '__main__':
	main()
