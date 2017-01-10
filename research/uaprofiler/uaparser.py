#!/usr/local/bin/python

"""
	Experiment script for http-user-agent parsing by heuristics

	Shoufu,  Aug. 12, 2014
"""

import re

#import editdist
#from editdist import *
#def getPatterns(ualist):
#	patterns = []
#	E = EditDist()
#	G = E.getConnectedComponentBuckets(ualist, 0.9)
#	for g in G:
#		for ua in g.nodes():
#			tokens = breakua(ua)
#
#	return patterns				

def builduatree(uas):
	'''
		Epxerimental function:
		build a product tree of user-agents after clustering
	'''
	uatree = {}
	E = EditDist()
	G = E.getConnectedComponentBuckets(uas, 0.8)
	for i,g in enumerate(G):
		proddict = buildprodtree(g.nodes())
		if len(proddict)  == 1:
			if 'singleton' not in uatree:
				uatree['singleton'] = {}
			uatree['singleton']['cluster_'+str(i)] = proddict			
		else:
			uatree['cluster_' + str(i)] = proddict

	return uatree	

def buildprodtree(uas):
	'''
		Build the product tree of an organization for UA exploration
		based on the convention of RFC, the order of product depends on the significance 

		plus, it groups mainstream browsers into marketing name
	'''
	ualist = {}
	prodtree = {} 
	for ua in uas:
		try:
			tokens = breakua(ua)

			# Dictionary of mainstream browsers
			# IN reality, this should be read from a configuration file
			# and feed into a matching engine
			proddict = {
				'Mozilla/Gecko/Firefox' : 'Firefox',
				'Mozilla/AppleWebKit/Version/Safari' : 'Apple Safari',
				'Mozilla/AppleWebKit/Chrome/Safari' : 'Google Chrome',
				'Mozilla/AppleWebKit/Chrome/Mobile Safari/OPR' : 'Opera (Mobile)',
				'Mozilla/AppleWebKit/Chrome/Safari/OPR' : 'Opera',
				'SEP/MID/SID' : 'SEP AV Client',
				'SEP/MID/SID/LUE' : 'SEP AV Client (LiveUpdate Engine)',
				'NIS/MID/SID/LUE' : 'Norton AV Client',
				'Symantec/MID/SID/LUE' : 'Hosted SEP AV Client',
				'SEP/MID/SID/SEQ' : 'SEP AV Client (SEQ) ',
			}
			tokenseq = '/'.join(tokens[0::3]) 
			if tokenseq in proddict:
				tokens[0] = proddict[tokenseq]

			prodtree = addProductToTree(prodtree, tokens)

			
			'''
				It might be useful to attach the raw user-agent strings
				to its corresponding tree trunk
			'''
			#if tokens[0] not in ualist:
			#	ualist[tokens[0] = []
			#ualist[tokens[0]].append(ua)
			#if 'raw' not in prodtree[tokens[0]]:
			#	prodtree[tokens[0]]['raw'] = []
			#prodtree[tokens[0]]['raw'].append(ua)
		except:
			pass
			#print 'xError'

	return prodtree

def addProductToTree(prodtree, tokens):  
	'''
		Worker: building the tree of product for UAs (Dictionary)
	
			product | subproduct = {version, count, annotation, subproduct)

		e.g. Mozilla/5.0 (Windows NT 6.1; rv:26.0) Gecko/20100101 Firefox/26.0
			 Mozilla/4.0 (Windows NT 5.0; rv:12.0; .NET CRL 1.2.30.0) Gecko/20090101 Firefox/12.0
	
	 	Output:
			Mozilla: {
				version: [4.0 5.0]
				count: 2
				annotation: {
					Windows NT: {
						version: [5.0 6.1]
						count: 2
						annotation: nil
					},
					rv: {
						version: [12.0 26.0]
						count: 2
						annotation: nil
					},
					.NET CRL: {
						version: [1.2.30.0]
						count: 1
						annotation: nil
					}
				}
				subproduct: {
					Gecko: {
						version: [20100101 20090101]
						count: 2
						annotation: nil
						subproduct: {
							Firefox: {
								version: [12.0 26.0]
								count: 2
								annotation: nil
							}
						}
					}
				}
			}
	'''
	#print token
	try:
		token = tokens[0]
		if token not in prodtree:
			prodtree[token] = {}
			prodtree[token]['count'] = 0

		prodtree[token]['count'] += 1

		version = tokens[1]
		if version != '':
			if 'version' not in prodtree[token]:
				prodtree[token]['version'] = {}
			if version not in prodtree[token]['version']:
				prodtree[token]['version'][version] = 0 
			prodtree[token]['version'][version] += 1

		comment = tokens[2]
		if comment != '':
			if 'annotation' not in prodtree[token]:
				prodtree[token]['annotation'] = {}

			ctokens = breakcomment(comment)
			for i, (ctoken,cversion,ccomment) in enumerate(zip(ctokens[0::3], ctokens[1::3], ctokens[2::3])):
				if cversion == '' and ccomment == '':

					if 'Keywords' not in prodtree[token]['annotation']:
						prodtree[token]['annotation']['Keywords'] = {}
				
					if ctoken not in prodtree[token]['annotation']['Keywords']:
						prodtree[token]['annotation']['Keywords'][ctoken] = 0
					prodtree[token]['annotation']['Keywords'][ctoken] += 1
						
					continue
				
				if ctoken not in prodtree[token]['annotation']:
					prodtree[token]['annotation'][ctoken] = {}
					prodtree[token]['annotation'][ctoken]['count'] = 0 
				prodtree[token]['annotation'][ctoken]['count'] += 1

				if cversion != '':
					if 'version' not in prodtree[token]['annotation'][ctoken]:
						prodtree[token]['annotation'][ctoken]['version'] = {}
					if cversion not in prodtree[token]['annotation'][ctoken]['version']:
						prodtree[token]['annotation'][ctoken]['version'][cversion] = 0 
					prodtree[token]['annotation'][ctoken]['version'][cversion] += 1
				if ccomment != '':
					if 'comment' not in prodtree[token]['annotation'][ctoken]:
						prodtree[token]['annotation'][ctoken]['comment'] = {}
					if ccomment not in prodtree[token]['annotation'][ctoken]['comment']:
						prodtree[token]['annotation'][ctoken]['comment'][ccomment] = 0 
					prodtree[token]['annotation'][ctoken]['comment'][ccomment] += 1

		if len(tokens) > 3 and tokens[3] != '':
			if 'component' not in prodtree[token]:
				prodtree[token]['component'] = {}
			prodtree[token]['component'] = addProductToTree(prodtree[token]['component'], tokens[3:])
	except Exception as e:
		#print 'yError:', e
		pass

	return prodtree

def breaktoken(tokenstr):
	'''
		Extract version from product
		e.g. Windows NT 6.1 -->  ['Windows NT', '6.1']

		[Note] This function might not be necessary anymore
		because we have considered this case in breaksegment
		when we are parsing the user-agent string
		including ':', '=', '-', '_' as separator of version
	'''
	if not tokenstr:   
		return (tokenstr, '')
	#if '=' in tokenstr:
	#	x = tokenstr.split('=', 1)
	#elif ':' in tokenstr:
	#	x = tokenstr.split(':', 1)
	x = tokenstr.rsplit(' ', 1)
	if len(x) > 1 and re.search('\d', x[1]):
	   return x[0], x[1] 
	else:
	   return tokenstr, ''

def breakcomment(comment):
	'''
		Break a comment from user-agent to tokens as ['token', 'version', 'comment', ...]
		e.g. '(Windows NT 6.1; rv:26.0)'
			--> ['Windows NT', '6.1', '', 'rv', '26.0', '']
	'''
	delimiter = [';', ',']
	tokens = breaksegment(comment.strip('(').strip(')'), delimiter)
	for i,(token,version,comment) in enumerate(zip(tokens[0::3], tokens[1::3], tokens[2::3])):
		if version == '':
			token, version = breaktoken(token.strip())
		tokens[i*3] = token
		tokens[i*3+1] = version
	return tokens;

def breakua(uastr):
	'''
		Break user-agent string to tokens as ['token', 'version', 'comment', ...]

		e.g. Mozilla/5.0 (Windows NT 6.1; rv:26.0) Gecko/20100101 Firefox/26.0
			
		-->  ['Mozilla', '5.0', '(Windows NT 6.1; rv:26.0)', 
				'Gecko', '20100101', '', 'Firefox', '26.0', '']

		It will not break down the comment, however.
		To do that, you need to call breakcomment
	'''
	delimiter = [';', ' ', '\t', ',']
	tokens = breaksegment(uastr, delimiter)
	for i,(token,version,comment) in enumerate(zip(tokens[0::3], tokens[1::3], tokens[2::3])):
		if version == '':
			token, version = breaktoken(token.strip())
		tokens[i*3] = token
		tokens[i*3+1] = version
	return tokens;
	
def breaksegment(segstr, delimiter):
	'''
		Heuristically break down the user-agent string:
		Imagine an arbitray user-agent consists of a list of product with <name, version> 

		The first step of breaking down an user-agent is to extract the outer product list
		for example, 'Mozilla/5.0 (Windows NT 6.1; rv:26.0) Gecko/20100101 Firefox/26.0', 
		we extract ['Mozilla', '5.0', '(Windows NT 6.1; rv:26.0)', 'Gecko', '20100101', '', 'Firefox', '26.0', '']

		By Segment, we mean a product segment <name, version, comment>
		a user-agent has many product segments; a comment also has many product segments
		@param delimiter: delimiter for segment
		
		Although we can extract another list of products from each comment block, 
		we rather leave it for another process to keep the integrity of one product by <name, version, comment>
		
		************
		The format of User-agent varies many way
		We use a state-machine to track our parsing
		elem_index : 
			0 - we are absorbing char for product token (name)
			1 - reconstrusting product version 
			2 - reconstructing product comment
	'''
	openblocks = ['(', '[', '<', '{']
	closeblocks = [')', ']', '>', '}']

	tokens = []

	# track the openblocks/closeblocks 
	# enable read nested blocks
	# e.g. ( ()) 
	stack = [] 
	
	# Are we in a block, like '()', '{}', '[]' etc.
	# if yes, we will blindly skip paring
	inablock = False

	# State-Machine
	elem_index = 0

	# place-holder for absorbing char 
	word = '' 
	for i, c in enumerate(segstr):
		if inablock:
			if c in openblocks:
				stack.append(c)
			elif c in closeblocks:
				o = stack.pop()
				if openblocks.index(o) != closeblocks.index(c):
					tokens.append(word + segstr[i:])
					print 'error'
					word = ''
					break
				if len(stack) == 0: # no blocks in the stack, we complete a block
					inablock = False 
					#continue
			else:
				pass
			word = word + c
		else:
			'''
				Heuristically, we consider '/', ':', '='
				as strong delimiters of prod_name and prod_version
				e.g. Mozilla/5.0, Site=D930F25693
			'''
			if c == '/' or c == ':' or c == '=':
				if elem_index == 0:
					tokens.append(word)
					word = ''
					elem_index = 1 # we start to reconstruct version  
				else:
					word  = word + c
			elif c in delimiter: 
				if elem_index == 0:
					'''
						when we are absorbing char for constructing <name>,
						we continuouly check whether the name involves any number
						e.g.
						"Adobe Application Manager 2.0"
							--> ['Adobe Application Manager', '2.0']
						Or
						"LiveUpdateEngine-2.2.0.102"
							--> ['LiveUpdateEngine', [2.2.0.102']

					'''
					x = word.strip().rsplit(' ', 1) 
					parts = re.split('-|_', x[-1].strip(), 1)
					has_number = re.search('\d', x[-1]) # check the seond part of name
					# if has_number, then we had the version and move to comment (state = 2)
					if len(x) == 1 and len(parts) == 2 and has_number:
						tokens.append(parts[0])
						tokens.append(parts[1])
						word = ''
						elem_index = 2
					elif len(x) == 2 and has_number:
						if len(parts) == 2:
							tokens.append(x[0] + parts[0])
							tokens.append(parts[1])
						else:
							tokens.append(x[0])
							tokens.append(parts[0])
						word = ''
						elem_index = 2  # expecting a comment 
					elif c == ' ' or c == '\t':
						word = word + c
					else: # but if it is ';' or ',', strong delimiter for segments
						tokens.append(word)
						tokens.append('')
						tokens.append('')
						word = ''
						elem_index = 0
				elif elem_index == 1: # end of version, we start for a comment 
					tokens.append(word)
					word = ''
					elem_index = 2
				else:
					word = word + c
					'''
						Keep absorbing, we are not done yet (state = 2)
						
						After we read (x86_64), we see SP, but we do not finalize the comment
						until we see another char which is not a weak delimiter, like SP
					    e.g. 'ocspd (unknown version) CFNetwork/520.5.1 Darwin/11.4.2 (x86_64) (MacBookPro8%2C1)',
					'''
					pass
			elif c in openblocks:
				inablock = True
				if elem_index == 0:
					'''
						We do not have a version indicator, but we are run into a comment block 
						e.g. "Windows Phone Search (Windows Phone OS 7.10;SAMSUNG;GT-I8350;7.10;8773)"
					'''
					tokens.append(word) # push token into queue
					tokens.append('') # version
					elem_index = 2
					word = ''
				elif elem_index == 1 and (c == '('):
					'''
						We only consider () as the comment block
						!!!NOTE: we might need to consider [] as comment block as well

						It seems not usual to use '{}' as block comment
						but, possibly used for version identifier (which seems to be a convention, e.g. UUID)
						e.g. "SEP/12.1.2015.2015, MID/{69012EDC-228A-DAA2-EF2D-406C4136130F}, SID/908"
					'''
					tokens.append(word)
					elem_index = 2
					word = ''
				else:
					'''
						state = 2, we have special case like 
					    e.g. 'Darwin/11.4.2 (x86_64) (MacBookPro8%2C1)',
						continue to read & append the comment to place-holder
					'''
					pass
				stack.append(c)
				word = word + c 
			elif c in closeblocks:
				'''
					In normal case, it should only appear when we are in-a-block
				'''
				tokens.append(word + segstr[i:]) #malformat
				word = ''
				break
			else:
				'''
					Got a ordinary char (not delimiters or openblocks/closeblocks),
					Our state is '2', (contrusting a comment)
					but we are outside a block, which means we had absorb a complete comment block (state = 2)
					thus, we should push it into tokens, 
					and reset our state-machine (state = 0) for a new product
				'''
				if elem_index == 2:
					tokens.append(word)
					elem_index = 0
					word = ''
				word = word + c
	
	# end of the uastr 

	'''
		Make sure we have a complete segment for every product (name, version, comment)
		so we have tokens=[token, version, coment, token, version, comment, ...] 
	'''
	if elem_index == 0:
		if word != '':
			#########patch for - and _
			parts = re.split('-|_', word, 1)
			if len(parts) == 2 and re.search('\d', parts[1]):
				  tokens.append(parts[0])
				  tokens.append(parts[1])
				  tokens.append('')
			else:
				tokens.append(word)
				tokens.append('')
				tokens.append('')
		else:
			tokens.append('')
			tokens.append('')
	elif elem_index == 1:
		if word != '':
			tokens.append(word)
			tokens.append('')
		else:
			tokens.append('')
			tokens.append('')
	else:
		if word != '':
			tokens.append(word)
		else:
			tokens.append('')

	return tokens		

def extracttokens(ua):
	'''
		extract only product names
	'''
	tokens = []
	ptokens = breakua(ua)
	tokens += ptokens[0::3]
	for comments in ptokens[2::3]:
		if comments != '':
			ctokens = breakcomment(comments)
			tokens += ctokens[0::3]
	return tokens

def printtokens(uas):
	for ua in uas:
		if ua.startswith('"Mozilla'):
			continue
		tokens = breakua(ua.strip('"'))
		print ua
		print tokens
		for comment in tokens[2::3]:
			if comment != '':
				print breakcomment(comment)
		print '\n'

def test(filename=''):
	if not filename == '':
		fp = open(filename, 'r')
		uas = map(lambda x: x.strip('"'), list(set(fp.read().splitlines())))
	else:
		uas = [

		# Malformated
		#'Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; User-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; http://bsalsa.com) (Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)); User-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; http://bsalsa.com) (Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)); SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)',

		# Nested Blocks
		'YahooFantasyHockey/1.0(Android Fantasy Hockey; 1.1.1 (GT-I9100P; GT-I9100P; 4.1.2/JZO54K))',
		'iTunes/11.1.2 (Windows; Microsoft Windows 7 x64 Enterprise Edition Service Pack 1 (Build 7601)) AppleWebKit/536.30.1',
		# Two blocks
		'ocspd (unknown version) CFNetwork/520.5.1 Darwin/11.4.2 (x86_64) (MacBookPro8%2C1)',
		# Normal cases
		'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.132 Moible Safari/537.36 (Edition Campaign 21)',
		'Mozilla/5.0 (Windows NT 5.2; rv:26.0) Gecko/20100101 Firefox/26.',
		# ':' as separator for version
		'SkyNet/1.5.0-0000(android:2.3.5;package:com.halfbrick.jetpackjoyride;lang:zh_CN;app_version:null;channel:;device_brand:samsung;device_model:GT-I9100P;resolution:480X800;udid:ffffffff-bada-a272-3d05-435100000000;cpu_freq:1200000;google_account:guy.fabre@mfacteur.fr;phone_number:unknown;game_name:Jetpack+Joyride;encoded:true;sdk_version:1.5.0;imei:358488043925804;location:[44.91082121666667,-0.6508202666666668])',
		# {} in version (value)
		'NIS/19.9.1.14 MID/{hJbaefXjtoMMi/N1ExwW8gJr9uw} SID/y8t0UwAAAAA LUE/1.8.2.10 (Windows;6.1;SP1.0;X64;FRA)',
		'SEP/12.1.4013.4013, MID/{CF2C3137-771C-EE16-82CA-3ED3AF1BE336}, SID/102 SEQ/140514034',
		'FBAN/FB4A;FBAV/9.0.0.26.28;FBBV/2403124;FBDM/{density=1.5,width=480,height=800};FBLC/fr_FR;FBCR/BouyguesTelecom;FBPN/com.facebook.katana;FBDV/GT-I9100P;FBSV/2.3.5;FBOP/1;FBCA/armeabi-v7a:armeabi;',
		# '=' as separator for version
		'AVGMOBILE-DROP17 202FREE BUILD=191274 LIC=PUTYX-HBX6U-GSHXC-QTC7H-TYTXW-6 LNG=fr_FR PROD=CLN',
		'Mozilla/5.0 (compatible; XmarksFetch/1.0; +http://www.xmarks.com/about/crawler; info@xmarks.com)',
		"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Site=92469A; Workstation=757560SP085; BTRS106558; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C)",
		# these cases, we cannot actually make sense of it
		"OrangeTVPlayer_Android_3.3h.2n_HTC_HTC One SV_4.0.4_OrangeAppliTV/3.3h.2n",
		"OrangeTVPlayer_Android_5.0f.0b_HTC_HTC One SV_4.0.4_OrangeAppliTV/5.0f.0b",
		"OrangeTVPlayer_Android_3.3h.2p_samsung_GT-I9100P_4.1.2_OrangeAppliTV/3.3h.2p",
		"OrangeTVPlayer_Android_4.0h.0f_HTC_HTC One SV_4.1.2_OrangeAppliTV/4.0h.0f",
		# special information	
		"WordPress/3.5; http://perso.fr/wordpress/", 
		# We have space within a product name
		"SonyC5303 Build/12.1.A.1.201 stagefright/1.2 (Linux;Android 4.3)",
		"CaptiveNetworkSupport-209.39 wispr"
		'gPvJNkVEFn8rSORzhbKIUaiuVMEv/B0UwAAAAA"']

	# Dump the tree to JSON format
	# print json.dumps(builduatree(uas), sort_keys=True)
			
	for ua in uas:
		print ua
		tokens = breakua(ua)
		print tokens
		for comment in tokens[2::3]:
			if comment != '':
				print breakcomment(comment)

if __name__ == '__main__':
	#test('laposte_ua.txt')
	#test('ua_1_user.txt')
	test()
		
	
