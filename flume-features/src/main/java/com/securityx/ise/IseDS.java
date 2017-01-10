package com.securityx.ise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.naming.ConfigurationException;

import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.codehaus.jettison.json.JSONObject;

import org.slf4j.LoggerFactory;

import com.cisco.pxgrid.GCLException;
import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.ise.Group;
import com.cisco.pxgrid.model.net.Session;
import com.cisco.pxgrid.model.net.User;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryNotification;

public class IseDS extends AbstractSource implements EventDrivenSource, Configurable {
	
	private GridConnection gridCon=null;
	private ReconnectionManager recon=null;
	private String iseHost;
	private String iseUserName;
	private String keyStorePath;
	private String keystorePassphrase;
	private String truststorePath;
	private String truststorePassphrase;
	private long savedTimeMillis = System.currentTimeMillis();
	
	private static final int DEFAULT_CHAN_REQUEST_RETRIES=3;
	private static final int DEFAULT_CHAN_REQUEST_RETRY_INTERVAL_SEC=10;
	private static final int EVENT_POLLING_INTERVAL_SECS=10;
	private static final int EVENT_AGGREGATION_INTERVAL_SECS=10;
	private static final int EVENT_BURST_SIZE=10;
	
	private final String EXTRACTED_IP_ADDR = "destinationAddress";
	private final String EXTRACTED_MAC_ADDR = "destinationMacAddress"; 
	private final String EXTRACTED_USER_NAME = "destinationUserName";
	private final String EXTRACTED_HOST_NAME = "destinationHostName";
	private final String EXTRACTED_START_TIME = "lastUpdateTime";
	private final String ISEIDENT = "iseIdent";
	private final String ISEIDENTSTR = "CsC0IseDS";
	
	private LinkedBlockingQueue<SessionEvent> iseEvents=new LinkedBlockingQueue<SessionEvent>();
	public static final org.slf4j.Logger logger = LoggerFactory.getLogger(IseDS.class);
	public static enum iseRunMode {FROMFILE, STANDALONE, FROMFLUME}    
	public static iseRunMode runMode = iseRunMode.FROMFLUME;

	private static class SessionEvent {
		List<String> ipAddrList = new ArrayList<String>();
		List<String> macAddrList = new ArrayList<String>();
		final String userName, eventStr, hostName;
		final long startTime;
		
		public SessionEvent(long st, String name, List<String> iplist, List<String> maclist) {
			this(st, name, null, iplist, maclist, null);
		}
		
		public SessionEvent(long st, String name, List<String> iplist, 
				List<String> maclist, String eventString) {
			this(st, name, null, iplist, maclist, eventString);
		}
		
		public SessionEvent(long st, String name, String hName, List<String> iplist, List<String> maclist, 
				String eventString)	{
			startTime = st;
			userName = name;
			hostName = hName;
			ipAddrList = iplist;
			macAddrList = maclist;
			eventStr = eventString;
		}
		
		public String getEventStr(){return eventStr;}
		public String getUserName(){return userName;}
		public String getHostName(){return hostName;}
		public List<String> getIpAddrList(){return ipAddrList;}
		public List<String> getMacAddrList(){return macAddrList;}
		public long getStartTime(){return startTime;}
	}

	public class IseNotificationHandler implements SessionDirectoryNotification	{
		@Override
		public void onChange(Session session) {
			logger.debug("IseNotificationHandler: received session: " + session.getGid());
			// Need to use LinkedBlockingQueue to hold objects. These are thread safe
			String sessionData = session.toString();
			logger.debug("Session Data: "+sessionData);
			List<String> ipList = new ArrayList<String>();
			List<String> domainNameList = new ArrayList<String>();
			List<IPInterfaceIdentifier> intfIDs = session.getInterface().getIpIntfIDs();
			logger.debug("Session State: "+session.getState());
			//logger.info(" # of interfaces: "+intfIDs.size());			
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar lastUpdateCal = session.getLastUpdateTime();
			long startTime=0;
			if (lastUpdateCal != null) {
				logger.debug("Time: "+dateFormat.format(lastUpdateCal.getTime()));
				startTime = lastUpdateCal.getTimeInMillis();
			} else { 
				logger.error("ERROR: lastUpdateCal is null??");
				// We need to take special action here....
			}			
			
			logger.debug("ip=[");
			for (int i = 0; i < intfIDs.size(); i++) {
				String ip_addr = intfIDs.get(i).getIpAddress();
				if (ip_addr != null)
					ipList.add(ip_addr);
				String domainName =  intfIDs.get(i).getDomainName();
				if (domainName != null)
					domainNameList.add(domainName);
				logger.debug("ipaddr: "+intfIDs.get(i).getIpAddress()+"  domainName: "+intfIDs.get(i).getDomainName());
			}
			logger.debug("]");
			logger.debug(", Audit Session Id=" +session.getGid());
			String userName=null;
			User user = session.getUser();
			if (user != null) {				
				logger.debug("AD User Name=" + user.getName());
				logger.debug("AD User DNS Domain=" + user.getADUserDNSDomain());
				logger.debug("AD Host DNS Domain=" + user.getADHostDNSDomain());
				logger.debug("AD User NetBIOS Name=" + user.getADUserNetBIOSName());
				logger.debug("AD Host NETBIOS Name=" + user.getADHostNetBIOSName());
				userName = user.getName();
			}
			List<String> macList = session.getInterface().getMacAddresses();
			for( String mac : macList)
			{
				logger.debug("MAC: "+mac);
			}
			logger.debug("\n\n");
			String hostName=null;
			if(domainNameList.size() > 0)
				hostName = domainNameList.get(0); 
			enqueueSessionObjs(startTime, userName, hostName, ipList, macList, sessionData);
		}	
		
		public void enqueueSessionObjs(long startTime, String uname, 
				String hostName, List<String> ipAddresses,  
				List<String> macAddresses, String sessionData)	{
			SessionEvent sessionEvent = new SessionEvent(startTime, uname, hostName, ipAddresses, 
					macAddresses, sessionData);
			if (!iseEvents.offer(sessionEvent) )
			{
				logger.error("Session Event List is full!!");
			}				
		}
	}
			
	
	public TLSConfiguration authSetup() throws ConfigurationException 	{		
		/*
		if (!SampleUtilities.isValid(truststoreFilename, truststorePassword)) {
			System.err.println("unable to read truststore. please check the truststore filename and truststore password.");
			System.exit(1);
		}
		 */		
		if( iseHost == null || iseUserName == null || keyStorePath==null || keystorePassphrase==null || 
				truststorePath==null || truststorePassphrase==null)
			throw new ConfigurationException("Cisco ISE: Bad TLS Config parameter");
		
		TLSConfiguration config = new TLSConfiguration();
		/*
		config.setHosts(new String[]{"ise.my_server.com"});
		config.setUserName("my_client");
		config.setGroup(Group.SESSION.value());
		config.setKeystorePath("my_keystore.jks");
		config.setKeystorePassphrase("my_password");
		config.setTruststorePath("my_truststore.jks");
		config.setTruststorePassphrase("my_password");
		*/
		config.setHosts(new String[]{iseHost});
		config.setUserName(iseUserName);
		config.setGroup(Group.SESSION.value());
		config.setKeystorePath(keyStorePath);
		config.setKeystorePassphrase(keystorePassphrase);
		config.setTruststorePath(truststorePath);
		config.setTruststorePassphrase(truststorePassphrase);		
		return config;
	}
	
	public void connSetup(TLSConfiguration config) throws GCLException {
		gridCon = new GridConnection(config);
		// We need this next line to handle ISE connection / disconnection issues 
		gridCon.addListener(new ISEConnectionListener());
	}
	
	public void registerHandler() throws GCLException {
		IseNotificationHandler handler = new IseNotificationHandler();
		SessionDirectoryFactory.registerNotification(gridCon, handler);
	}
	
	public boolean setupPxGrid() {
		boolean onErr=false;
		try{
			TLSConfiguration config = authSetup();
			connSetup(config);
		}
		catch( GCLException gcle)
		{
			logger.debug("setupPxGrid: "+gcle.getMessage());
			onErr=true;
		}
		catch( ConfigurationException cfe)
		{
			logger.debug("setupPxGrid: "+cfe.getMessage());
			onErr=true;
		}
		return onErr ? false : true;
	}
	
	public void startPxGridListener()
	{
		recon = new ReconnectionManager(gridCon);
		recon.setRetryMillisecond(2000);
		recon.start();		
	}

	public void startPxGridService() {
		if( setupPxGrid() )
		{
			startPxGridListener();
			try{
				registerHandler();
			}
			catch(GCLException glce)
			{
				logger.error("Registration Failed: "+glce.getMessage());			
			}
		}
		else
			logger.error("startPxGridService: Failed - setup error");
	}
	public void stopPxGridService()
	{
		recon.stop();	
	}
	
	private interface IseSourceConstants{		
		public final String ISE_HOST="ise_host";
		public final String ISE_USER_NAME="ise_user_name";
		public final String ISE_KEY_STORE_PATH="ise_key_store_path";
		public final String ISE_KEY_STORE_PASSPHRASE="ise_key_store_passphrase";
		public final String ISE_TRUST_STORE_PATH="ise_trust_store_path";
		public final String ISE_TRUST_STORE_PASSPHRASE="ise_trust_store_passphrase";
		/*
		public final String ISE_HOST="10.10.30.33";
		public final String ISE_USER_NAME="sb_ise";
		public final String ISE_KEY_STORE_PATH="/Users/santanubhattacharyya/xGrid/pxgrid-sdk-1.0.0.10/samples/certs/iseSample1.jks";
		public final String ISE_KEY_STORE_PASSPHRASE="cisco123";
		public final String ISE_TRUST_STORE_PATH="/Users/santanubhattacharyya/xGrid/pxgrid-sdk-1.0.0.10/samples/certs/rootSample.jks";
		public final String ISE_TRUST_STORE_PASSPHRASE="cisco123";
		*/
	}

	private void configurePxGridConn(String ise_host, String ise_user_name, 
			String ise_key_store_path, String ise_key_store_passphrase,
			String ise_trust_store_path, String ise_trust_store_passphrase) {
		
		logger.info("config: ise_host: "+ise_host+"\nise_username: "
			+ise_user_name+"\nise_key_store_path: "+ise_key_store_path+"\nise_key_store_passphrase: "
			+ise_key_store_passphrase+"\nise_trust_store_passphrase: "+ise_trust_store_passphrase
			+"\nise_trust_store_path: "+ise_trust_store_path);
		iseHost = ise_host;
		iseUserName = ise_user_name;
		keyStorePath = ise_key_store_path;
		keystorePassphrase = ise_key_store_passphrase;
		truststorePath = ise_trust_store_path;
		truststorePassphrase = ise_trust_store_passphrase;
	}
		
	@Override
	public void configure(Context context) {
		configurePxGridConn(
				context.getString(IseSourceConstants.ISE_HOST),
				context.getString(IseSourceConstants.ISE_USER_NAME),
				context.getString(IseSourceConstants.ISE_KEY_STORE_PATH),
				context.getString(IseSourceConstants.ISE_KEY_STORE_PASSPHRASE),
				context.getString(IseSourceConstants.ISE_TRUST_STORE_PATH),
				context.getString(IseSourceConstants.ISE_TRUST_STORE_PASSPHRASE)
				);
	}

	private void dumpReceivedEvents(List<String> ipAddrList, List<String> macAddrList, 
			String userName, String hostName) {
		logger.info("dumpReceivedEvents: IPs: "+ipAddrList.size()+"  MACs: "+macAddrList.size());
		for (String ip: ipAddrList) {
			logger.info("processISEEvents: IP: "+ ip);
		}
		for (String mac : macAddrList) {
			logger.info("processISEEvents: MAC: "+ mac);
		}
		if (userName != null)
			logger.info("processISEEvents: USERNAME: "+ userName);					
		if (hostName != null)
			logger.info("processISEEvents: HOSTNAME: "+ hostName);					
	}
	
	private void processISEEvents()	{
		int event_count=0;
		SessionEvent iseEvent;
		List<org.apache.flume.Event> events = new ArrayList<org.apache.flume.Event>();		
		logger.info("processISEEvents: run mode: "+runMode);		
		
		while (true) {
			try {
				if ((iseEvent = iseEvents.poll(EVENT_POLLING_INTERVAL_SECS, 
						TimeUnit.SECONDS)) != null)	{
					// Write these to Flume as key/value pairs - no reason to send XML.
					logger.debug("processISEEvents: Received something....");

					Map<String, String> bodyMap = new HashMap<String, String>();

					List<String> ipAddrList = iseEvent.getIpAddrList();
					if (ipAddrList != null)
						bodyMap.put(EXTRACTED_IP_ADDR, ipAddrList.get(0));

					List<String> macAddrList = iseEvent.getMacAddrList();					
					if (macAddrList != null)
						bodyMap.put(EXTRACTED_MAC_ADDR,macAddrList.get(0));

					String userName = iseEvent.getUserName();
					if (userName != null)
						bodyMap.put(EXTRACTED_USER_NAME,userName);

					String hostName = iseEvent.getHostName(); 
					if(hostName != null)
						bodyMap.put(EXTRACTED_HOST_NAME, hostName);

					long starttime = iseEvent.getStartTime();
					if (starttime <= 0) {
						starttime = System.currentTimeMillis();
					} 
					bodyMap.put(EXTRACTED_START_TIME, String.valueOf(starttime));
					bodyMap.put(ISEIDENT, ISEIDENTSTR);
					
					if (logger.isDebugEnabled())
						dumpReceivedEvents(ipAddrList, macAddrList, userName, hostName);

					JSONObject bodyObj = new JSONObject(bodyMap);					
					String bodyStr = bodyObj.toString();
					logger.debug("Flume Body: "+bodyStr);

					byte[] iseMsgByteArr = bodyStr.getBytes(StandardCharsets.UTF_8);

					if (iseMsgByteArr != null) {
						org.apache.flume.Event event = EventBuilder.withBody(iseMsgByteArr);
						events.add(event);
					}
				}
			} catch (InterruptedException ie) {
				logger.error("IseDS: start: "+ie.getMessage());
			}		
			long timenow = System.currentTimeMillis();
			if ((events.size() > EVENT_BURST_SIZE) || 
					((timenow - savedTimeMillis > EVENT_AGGREGATION_INTERVAL_SECS*1000) &&
							(events.size() > 0))) {
				savedTimeMillis = timenow;
				logger.debug("Sending "+events.size()+" events to flume");
				event_count += events.size();
				// If channel is timing out something is wrong...should we bail?
				if (sendEventsToFlume(events) == org.apache.flume.Sink.Status.READY
						|| runMode == iseRunMode.FROMFILE
						|| runMode == iseRunMode.STANDALONE)
					events.clear();
				logger.debug("processISEEvents: # of events processed: "+event_count);		
			}
		}
	}

	// Called to dump the headers of an event
	private void debugDumpEventsSentToFlume(List<org.apache.flume.Event> events) {
		for (org.apache.flume.Event event : events) {
			Map<String,String> eHeaders = event.getHeaders();
			logger.info("********* Event Header Dump *************");
			Set<Map.Entry<String,String>> eHeaderSet = eHeaders.entrySet();
			for( Map.Entry<String,String> e : eHeaderSet)
			{
				logger.info("event header key: "+e.getKey()+"\tval: "+e.getValue());
			}
			byte[] bodyArr = event.getBody();
			if (event.getBody() != null) {
				String bodyStr = new String(bodyArr);
				logger.info("event body: "+bodyStr);
			}				
		}
	}
	
	private org.apache.flume.Sink.Status sendEventsToFlume(List<org.apache.flume.Event> events) {
		if (logger.isDebugEnabled())
			debugDumpEventsSentToFlume(events);
		org.apache.flume.Sink.Status channelStatus = org.apache.flume.Sink.Status.BACKOFF; 
		Boolean channelExp = false;
		int count = DEFAULT_CHAN_REQUEST_RETRIES;
		do {
			channelExp = false;
			try {
				// Transaction based (all events or no events processed) - more efficient than processEvent
				org.apache.flume.channel.ChannelProcessor cp = getChannelProcessor();
				if (cp != null) {
					logger.debug("sendEventsToFlume: # events: "+events.size());
					cp.processEventBatch(events);
					channelStatus = org.apache.flume.Sink.Status.READY;
				}
			} catch (ChannelException ce) {
				channelExp = true;
				logger.warn("Error appending event to channel. "
						+ "Channel might be full. Consider increasing the channel "
						+ "capacity or make sure the sinks perform faster.\n", ce);
				try {
					Thread.sleep(DEFAULT_CHAN_REQUEST_RETRY_INTERVAL_SEC * 1000);
				} catch (InterruptedException ie) {
					logger.warn("IseDS source (" + getName() + ") interrupted during channel retry.. continuig with next try");
				}
			}
		} while (channelExp && (--count > 0));
		if (channelExp) {
			logger.error("Channel request to add events returned exception even after retries: query scheduled at: " + System.currentTimeMillis());
			//channelStatus = org.apache.flume.Sink.Status.BACKOFF; 
		}
		return channelStatus;
	}
	
	@Override
	public void start() {
		startPxGridService();
		processISEEvents();
		super.start();
	}
	@Override
	public void stop() {
		logger.debug("Shutting down IseDS Connector...");
		stopPxGridService();	
		super.stop();	
	}
	
    public static void runISEwPxGrid(final IseDS iseDS) {
    	iseDS.configurePxGridConn(
    			"10.10.30.33", 
    			"sb_ise_mac",  
    			"/Users/santanubhattacharyya/xGrid/pxgrid-sdk-1.0.0.10/samples/certs/iseSample1.jks",
    			"cisco123",
    			"/Users/santanubhattacharyya/xGrid/pxgrid-sdk-1.0.0.10/samples/certs/rootSample.jks",
    			"cisco123"
    			);
    	iseDS.startPxGridService();
    	iseDS.processISEEvents();
    }
    
    public static void runISE(final IseDS iseDS)
    {    		    	    	
    	//new Thread(() -> iseDS.testAgainstLocalLog("/opt/e8sec/sessions.txt")).start();
    	new Thread( new Runnable(){ 
    		public void run()
    		{
    			iseDS.testAgainstLocalLog("/opt/e8sec/sessions-new-time.txt");
    		}
    	}).start();
    	iseDS.processISEEvents();
    }
    
    public void testAgainstLocalLog(String fname) {
		StringBuilder sb=new StringBuilder();
		Pattern p = Pattern.compile("^<\\?xml");
		int count=0;
		IseNotificationHandler iseNotificationHandler = new IseNotificationHandler();
		try {
			while (true) {
				File fXmlFile = new File(fname);
				BufferedReader br = new BufferedReader(new FileReader(fXmlFile));
				String line;

				while( (line = br.readLine()) != null)
				{		    	
					line = line.trim();
					Matcher m = p.matcher(line);
					if( m.lookingAt() )
					{
						String xmlISEEvent = sb.toString();
						if( xmlISEEvent.length() > 0)
						{
							Map<String,String> sessionProps = FakeISEEventGen.getfakeSessionData(xmlISEEvent);		    			
							com.cisco.pxgrid.model.net.Session fakeSession = new com.cisco.pxgrid.model.net.Session();
							fakeSession.setGid(sessionProps.get("gid"));
							String lastUpdateTime = sessionProps.get("lastUpdateTime");
							final Calendar cal =  Calendar.getInstance();
							if (lastUpdateTime != null) {
								long lastUpdateTimeMillis = Long.parseLong(lastUpdateTime);								
								cal.setTimeInMillis(lastUpdateTimeMillis);
							}
							fakeSession.setLastUpdateTime(cal);
							com.cisco.pxgrid.model.net.Interface iface = new com.cisco.pxgrid.model.net.Interface();
							List<String> macAddrs = iface.getMacAddresses();
							macAddrs.add(sessionProps.get("macAddr"));
							List<com.cisco.pxgrid.model.core.IPInterfaceIdentifier> ipIntIDs = iface.getIpIntfIDs();		    			
							com.cisco.pxgrid.model.core.IPInterfaceIdentifier ipIfaceIdent = 
									new com.cisco.pxgrid.model.core.IPInterfaceIdentifier();
							String ip_addr = sessionProps.get("ipAddr");
							if( ip_addr != null)
								ipIfaceIdent.setIpAddress(ip_addr);
							String domain_name =  sessionProps.get("domainName");		    			
							if( domain_name != null )
								ipIfaceIdent.setDomainName(domain_name);		    			
							ipIntIDs.add(ipIfaceIdent);
							fakeSession.setInterface(iface);
							String userName = sessionProps.get("userName");
							com.cisco.pxgrid.model.net.User user = new com.cisco.pxgrid.model.net.User();
							user.setName(userName);
							fakeSession.setUser(user);
							iseNotificationHandler.onChange(fakeSession);
							//fakeSession
						}
						++count;
						if (count % 4 == 0) {
							Thread.sleep((2*60)*1000);
							//Thread.sleep(10*1000);
							count=0;
						}
						sb = new StringBuilder();
					}
					sb.append(line);
				}
//				logger.debug("testAgainstLocalLog: # of Events generated = "+count);
				br.close();
			}		    
		} catch (Exception e) {
			e.printStackTrace();
		}     	
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String runMode="fromFile";
		//String runMode="standalone";
		if( runMode.equalsIgnoreCase("fromFile"))
		{
			logger.info("Starting Cisco ISE Connector in (static) fromFile mode");
			final IseDS iseDS = new IseDS();
			iseDS.runMode = iseRunMode.FROMFILE;
			runISE(iseDS);
		}		
		else if( runMode.equalsIgnoreCase("standalone")) 
		{
			logger.info("Starting Cisco ISE Connector in (static) standalon mode");
			final IseDS iseDS = new IseDS();
			iseDS.runMode = iseRunMode.STANDALONE;
			runISEwPxGrid(iseDS);
		}	
		/*
		else
			
		{
		// This the actual implementation - the above is just for initial testing
			logger.info("Starting Cisco ISE Connector in Live mode");
			IseDS iseDS = new IseDS();
			throw new UnsupportedOperationException();
		}
		*/
	}
}

	