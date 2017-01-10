#####################################################################################
# Crawler using Tor Network for myip.ms                                             #
# First install and run Vidalia v0.2.21 to connect to Torself.                      #
# Ensure that socks settings, controlport and passphrase matches Vidalia conf file  #
# Run ua_spider_myip.py                                                             #
# Author: Jason Ng                                                                  # 
#####################################################################################

import socket

import socks


def create_connection(address, timeout=None, source_address=None):
    sock = socks.socksocket()
    sock.connect(address)
    return sock

socks.setdefaultproxy(socks.PROXY_TYPE_SOCKS5, "127.0.0.1", 9050)

# patch the socket module
originalSocket = socket.socket
socket.socket = socks.socksocket
socket.create_connection = create_connection

import urllib
import urllib2
import cookielib
from StringIO import StringIO
import gzip
from TorCtl import TorCtl

class Ajax:
    count = 0

    def myIP (self):
		myip_url = 'http://icanhazip.com/'
		headers = {'User-Agent':'Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/20100101 Firefox/17.0','Accept-Language':'en-us,en;q=0.5','Accept-Encoding':'gzip, deflate','Accept':'image/png,image/*;q=0.8,*/*;q=0.5'}
		myip_request=urllib2.Request(myip_url, None, headers)
		myip = urllib2.urlopen(myip_request).read()
		print 'IP address: ' + myip,
		open('logging','a').write('IP address: ' + myip + ' ')    

    def newId(self, originalSocket):
    	socket.socket = originalSocket
    	conn = TorCtl.connect(controlAddr="127.0.0.1", controlPort=9151, passphrase='e8security')
    	TorCtl.Connection.send_signal(conn, "NEWNYM")
    	#time.sleep(3)
        conn.close()
    	socket.socket = socks.socksocket

    def getHTML(self,pagenum):
    	url = 'http://myip.ms/browse/comp_browseragents/' + str(pagenum)
    	headers = {'User-Agent':'Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/20100101 Firefox/17.0','Host':'myip.ms','Connection':'keep-alive','Accept-Language':'en-us,en;q=0.5','Accept-Encoding':'gzip, deflate','Accept':'image/png,image/*;q=0.8,*/*;q=0.5'}
    	values = {'getpage':'yes', 'lang' : 'en'}
    	data = urllib.urlencode(values)

    	request = urllib2.Request(url, None, headers)
    	cookies = cookielib.MozillaCookieJar()
    	ck_opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cookies))	#Creating an opener handler to get the Cookie Jar
    	try:
    		response = ck_opener.open(request)
    		if response.info().get('Content-Encoding') == 'gzip':
    			buf = StringIO(response.read())
    			f = gzip.GzipFile(fileobj=buf)
    			data = f.read()
    			print 'File size: ' + str(len(data)),
    			open('logging','a').write('File size: ' + str(len(data)) + ' ')   
    			if len(data) < 70000:
    				print 'Incorrect response, pagenum: ' + str(pagenum)
    				open('logging','a').write('Incorrect response, pagenum: ' + str(pagenum) + ' ')
    				self.newId(originalSocket)
    				self.myIP()
    				return self.getHTML(pagenum)
    			else:
    				print 'Correct response, pagenum: ' + str(pagenum)
    				open('logging','a').write('Correct response, pagenum: ' + str(pagenum) + ' ')
    				filename = str(pagenum) + '_my-ip.html'
    				print filename + ' stored successfully'
    				open('logging','a').write(filename + ' stored successfully' + ' ')
    				open(filename, 'wb').write(data)
    				pagenum += 1
    				self.myIP()
    				return pagenum
    	except Exception as e:
    		print "Failed open URL"
    		print e
    		open('logging','a').write("Failed open URL. " + e + ' ')
    		return pagenum

    def ajax(self):
		nxt_page_to_store = 274                               #Configure this before running (Start page)
		pagenum = self.getHTML(nxt_page_to_store)
		while pagenum < 275:                                  #Configure this before running (End page)
			pagenum = self.getHTML(pagenum)
			if pagenum == None:
				break

if __name__ == '__main__':
    a = Ajax()
    a.ajax()
