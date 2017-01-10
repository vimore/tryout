#!/usr/local/bin/python

import uaparser
import Orange

class Anomaly():
	'''
		s <- a f(x) + b g(x)
		this sript cacluate g(x) = p(x) + q(x)
		p(x) : what special token a user-agent has
		q(x) : what necessary information is missing (use assocation rule)
	'''
	def __init__(self):
		self.tokens = {}
		self.count = 0

	def load(self, filename):
		'''
			load a user-agent family of customer data, e.g. cronus
			format: 
				number_of_users  \t user-agent string
		'''
		try:
			uas = open(filename, 'r').read().splitlines()
		
			for r_ua in uas:
				count, ua = r_ua.split('\t')
				self.count += int(count)
				tokens = uaparser.breakua(ua)
				comments = tokens[2::3]
				for comment in comments:
					ctokens = uaparser.breakcomment(comment)
					tokens = tokens + ctokens

				for token in tokens[0::3]:
					if not self.tokens.has_key(token):
						self.tokens[token] = 0
					self.tokens[token] += int(count)
			#print self.tokens
		except Exception as e:
			print 'Fail to load ', filename
			print e

	def px(self, ua):
		
		supp = []

		try: 
			tokens = uaparser.breakua(ua)
			comments = tokens[2::3]
			for comment in comments:
				ctokens = uaparser.breakcomment(comment)
				tokens = tokens + ctokens
		
			#print tokens
			utokens = list(set(tokens[0::3]))
			for token in utokens:
				print token, ': ', self.tokens[token]
				supp.append(self.tokens[token])
	
		except Exception as e:
			print e

		return 1.0 - (1.0 * min(supp)) / self.count 
	
	def mining(basket_file):
		''' 
			basket_file: filename, comma separated items
			Assume each line is one transaction
		'''
		data = Orange.data.Table(basket_file)
		rules = Orange.associate.AssociationRulesSparseInducer(data, support = 0.01)
		return rules
        
	def qxgrouping(rules):
		
		groups = {}

		new_rules = filter(lambda x: len(x.right.get_metas()) == 1, rules)
		for r in new_rules:
			left = ','.join(map(lambda x: x.variable.name, r.left.get_metas().values()))
			right = r.right.get_metas().values()[0].variable.name
			tokens = uaparser.breaksegment(right, [' ', '\t', ',']) # Get the token only
			
			left = left + ',$,' + tokens[0] # concatenate as the group key
			if not groups.has_key(left):
				groups[left] = []
				
			groups[left].append(r)

		return groups
 
	def qx(self, ua):
		pass	

def main():
	#ua = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; BTRS100200; GTB7.5; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; Microsoft Outlook 14.0.6025; ms-office; MSOffice 14)"
	#ua = 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/6.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; InfoPath.3; .NET4.0C; .NET4.0E; Microsoft Outlook 14.0.7113; ms-office; MSOffice 14)'
	#ua = 'Mozilla/4.0 (compatible; MSIE 5.01; Windows CE)'
	#ua = 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Site=63806A; Workstation=63806AN0060; Site=56227A; .NET CLR 2    .0.50727)'
	ua = 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Site=90173A; Workstation=372560SP020; .NET CLR 2.0.50727; .NET CLR     3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C; .NET4.0E; IDCRL 6.500.3165.0; IDCRL-cfg 6.500.12320.0; IDCRL-ui 6.500.3165.0; App W    LIDSVC.EXE, 6.500.3165.0, {DF60E2DF-88AD-4526-AE21-83D130EF0F68})'
	A = Anomaly()
	A.load('uafamily/UF00576.txt') # MSIE family
	s = A.px(ua)
	print 'p-Score:', s

if __name__ == '__main__':
	main()
