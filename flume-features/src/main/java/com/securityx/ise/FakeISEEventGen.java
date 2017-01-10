package com.securityx.ise;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class FakeISEEventGen {
	
	public static final Logger logger = LoggerFactory.getLogger(FakeISEEventGen.class);
	
	public static Map<String,String> getfakeSessionData(String iseEventAsStr) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		Map<String,String> sessionProps = new HashMap<String,String>();
		try {
			//System.out.println("Parsing: "+iseEventAsStr);
			builder = factory.newDocumentBuilder();
			//doc = builder.parse("employees.xml");
			InputSource is = new InputSource(new StringReader(iseEventAsStr));
			doc = builder.parse(is);

			// Create XPathFactory object
			XPathFactory xpathFactory = XPathFactory.newInstance();

			// Create XPath object
			XPath xpath = xpathFactory.newXPath();
			xpath.setNamespaceContext(new NamespaceContext() {
			    public String getNamespaceURI(String prefix) {
			        if (prefix == null) throw new NullPointerException("Null prefix");
			        else if ("n".equals(prefix)) return "http://www.cisco.com/identity";
			        else if ("ginet".equals(prefix)) return "http://www.cisco.com/xgridNet";
			        else if ("gi".equals(prefix)) return "http://www.cisco.com/xgrid";
			        return XMLConstants.NULL_NS_URI;
			    }
			    
			    // This method isn't necessary for XPath processing.
			    public String getPrefix(String uri) {
			        throw new UnsupportedOperationException();
			    }

			    // This method isn't necessary for XPath processing either.
			    public Iterator getPrefixes(String uri) {
			        throw new UnsupportedOperationException();
			    }
			});
			String ipAddr = getIPAddress(doc, xpath);
			String macAddr = getMacAddress(doc, xpath);
			String gid = getGid(doc, xpath);
			String user = getUser(doc,xpath);
			String host = getHostName(doc, xpath);
			long lastUpdateTimeSecs = getLastUpdateTime(doc, xpath);
			logger.debug("getfakeSessionData: IP Addr: " + ipAddr+"  MAC Addr: "+macAddr+"  User: "+user+
					" hostName: "+host + " Last Update Time: " + lastUpdateTimeSecs);
			sessionProps.put("ipAddr", ipAddr);
			sessionProps.put("domainName", host);
			sessionProps.put("macAddr", macAddr);
			sessionProps.put("gid",gid);
			sessionProps.put("lastUpdateTime", (new Long(lastUpdateTimeSecs)).toString());
			sessionProps.put("userName", user);
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return sessionProps;
	}	

	private static String getIPAddress(Document doc, XPath xpath) {
		String ip=null;
		try {
			XPathExpression expr =
					xpath.compile("/n:sessionNotification/n:sessions/n:session/ginet:interface/ginet:ipIntfID/gi:ipAddress/text()");			 							
			ip = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	private static String getHostName(Document doc, XPath xpath) {
		String host=null;
		try {
			XPathExpression expr =
					xpath.compile("/n:sessionNotification/n:sessions/n:session/ginet:interface/ginet:ipIntfID/gi:domainName/text()");					
			host = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return host;
	}
	
	private static String getMacAddress(Document doc, XPath xpath) {
		String mac=null;
		try {
			XPathExpression expr =
					xpath.compile("/n:sessionNotification/n:sessions/n:session/ginet:interface/ginet:macAddress/text()");			 							
			mac = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return mac;
	}
	
	private static String getGid(Document doc, XPath xpath) {
		String gid=null;
		try {
			XPathExpression expr =
					xpath.compile("/n:sessionNotification/n:sessions/n:session/gi:gid/text()");			 							
			gid = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return gid;
	}
	static int offset=0;
	private static long getLastUpdateTime(Document doc, XPath xpath) {
		long startTime=0;
		try {
			XPathExpression expr =
					xpath.compile("/n:sessionNotification/n:sessions/n:session/gi:lastUpdateTime/text()");			 							
			String lastUpdateTime = (String) expr.evaluate(doc, XPathConstants.STRING);
			//1697-02-01T00:00:00Z
			/*
			if (lastUpdateTime != null) {
				final Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(lastUpdateTime);
				startTime = calendar.getTimeInMillis();
			}
			*/
			final Calendar calendar = Calendar.getInstance();
			startTime = calendar.getTimeInMillis();
			offset += (int)(Math.random() * 10000 + 1);
			startTime += offset;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return startTime;
	}
	
	private static String getUser(Document doc, XPath xpath) {
		String user=null;
		try {
			XPathExpression expr =
					xpath.compile("/n:sessionNotification/n:sessions/n:session/ginet:user/gi:name/text()");			 							
			user = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return user;
	}	

}
