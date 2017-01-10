package com.securityx.tds;
/**
 * 
 */

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.securityx.tds.taniumsoap.Auth;
import com.securityx.tds.taniumsoap.ObjectList;
import com.securityx.tds.taniumsoap.SavedQuestion;
import com.securityx.tds.taniumsoap.TaniumSOAPPort;
import com.securityx.tds.taniumsoap.TaniumSOAPRequest;
import com.securityx.tds.taniumsoap.TaniumSOAPResult;
import com.securityx.tds.taniumsoap.TaniumSOAPService;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.*;

import javax.xml.namespace.QName;

/**
 * Flume source to collect data from Tanium Console using SOAP API.
 */
public class TaniumDS extends AbstractSource implements Configurable, PollableSource {

	// WORKAROUND to ignore hostname missmatch
	// http://stackoverflow.com/questions/10258101/sslhandshakeexception-no-subject-alternative-names-present
	static {
	    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
	        {
	            public boolean verify(String hostname, SSLSession session)
	            {
	                // ip address of the service URL(like.23.28.244.244)
	                if (hostname.equals("54.235.91.187"))
	                    return true;
	                return false;
	            }
	        });
	}
	
    public static final Logger LOGGER = LoggerFactory.getLogger(TaniumDS.class);

    static final int MINIMUM_POLLING_INTERVAL_SECS = 10;
    static final int DEFULT_SOAP_REQUEST_RETRIES = 3;
    static final int DEFULT_SOAP_REQUEST_RETRY_INTERVAL_SEC = 5;
    static final int DEFULT_CHANNEL_REQUEST_RETRIES = 3;
    static final int DEFULT_CHANNEL_REQUEST_RETRY_INTERVAL_SEC = 5;

    TaniumSOAPService _tss = null;
    TaniumSOAPPort _tsp = null;
    TaniumSOAPResult _qRes = null;
    TaniumSOAPRequest _qReq = null;
    ObjectList _objectList = null;
    String _savedQuestionsString = null;
	String _wsdlLocation = null;
	Auth _credentials = new Auth();
	long _poll_interval_secs = MINIMUM_POLLING_INTERVAL_SECS;
    long _last_poll_time_ms = 0;
    SourceCounter _sc = null;

    /*
     * Class to hold Result w/ timestamp for a saved question
     */
	class QuestionResult {
		String result_set = null;
		String now = null;   // now is a timestamp when result_set for saved qustion was queries w/ Tanium server

		public String getResult_set() {
			return result_set;
		}

		public String getNow() {
			return now;
		}

		public void setResult_set(String result_set) {
			this.result_set = result_set;
		}

		public void setNow(String now) {
			this.now = now;
		}
	}
	
    public void configure(Context context) throws FlumeException {

        this._sc = new SourceCounter(getName());
    	
    	// Get Tanium data source properties 
    	this._savedQuestionsString = context.getString("saved_questions").trim();
		this._wsdlLocation = context.getString("wsdl_location").trim();
		this._credentials.setUsername(context.getString("user").trim());
		this._credentials.setPassword(context.getString("passwd").trim());

        // Check for correctness
        if (this._savedQuestionsString.isEmpty() || this._wsdlLocation.isEmpty() ||
                context.getString("poll_interval_secs").isEmpty()) {
            LOGGER.error("Agent configuration for TaniumDS is not correct : \n");
            throw new
            FlumeException("SavedQuestionsString or Wsdl location or Polling interval is empty in agent configuration");
        }

        // Check if WSDL location local file and if so is it accessible
        // TODO: if HTTP or HTTPS then make different check for accessiblility
        if (this._wsdlLocation.startsWith("file://")) {
            File wsdlF = new File(this._wsdlLocation.substring(7));
            if (!wsdlF.isFile() || !wsdlF.exists() || !wsdlF.canRead()) {
                LOGGER.error("The wsdl location file path is not readable [" + this._wsdlLocation + "]");
                throw new FlumeException("The wsdl location file path is not readable [" + this._wsdlLocation + "]");
            }
        }

        // Minimum is 1 min not to overwhelm Tanium server
        this._poll_interval_secs = context.getLong("poll_interval_secs").longValue();
        if (this._poll_interval_secs < MINIMUM_POLLING_INTERVAL_SECS) {
            this._poll_interval_secs = MINIMUM_POLLING_INTERVAL_SECS;
        }

        // Set Object list
		this._objectList = new ObjectList();
		List<String> questionStrings = Arrays.asList(this._savedQuestionsString.split(","));
        //int p = 0;
		for (String q : questionStrings) {
			SavedQuestion x = new SavedQuestion(); 
			x.setName(q.trim());
			this._objectList.getSavedQuestion().add(x);
            //System.out.println ("questions ["+this._objectList.getSavedQuestion().get(p).getName()+"]");
            //p++;
		}


        // Set Request object
		this._qReq = new TaniumSOAPRequest();
		this._qReq.setCommand("GetResultData"); 
		this._qReq.setAuth(this._credentials);
		this._qReq.setObjectList(this._objectList);
        try {
            this._tss = new TaniumSOAPService(
                    new URL(this._wsdlLocation),
                    new QName("urn:TaniumSOAP", "TaniumSOAPService"));
            this._tsp = this._tss.getTaniumSOAPPort();
        } catch (MalformedURLException mfu) {
            // TODO Auto-generated catch block
            LOGGER.error("Tanium WSDL URL Malformed \n", mfu);
            throw new FlumeException("Tanium WSDL URL Malformed");
        } catch (Exception e) {
            LOGGER.error("Unknown exception\n"+e);
            throw new FlumeException("Unknown exception\n"+e);
        }
    }

    @Override
    public synchronized void start() {  
    	LOGGER.info ("Tanium DataSource is starting");
        super.start();
        this._sc.start();
    }

    @Override
    public synchronized void stop() {
    	LOGGER.info ("Tanium DataSource is stopping");
        super.stop();
        this._sc.stop();
    }

    public Status process() throws EventDeliveryException {
        Status status = Status.BACKOFF;
        try {
            if(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this._last_poll_time_ms) >
                    this._poll_interval_secs)
            {
                int count = DEFULT_SOAP_REQUEST_RETRIES;
                this._last_poll_time_ms = System.currentTimeMillis();
                long next_half_poll_time_ms = this._last_poll_time_ms + this._poll_interval_secs * 1000 / 2;
                do {
                    this._qRes = this._tsp.request(this._qReq);
                    try {
                        Thread.sleep(DEFULT_SOAP_REQUEST_RETRY_INTERVAL_SEC * 1000);
                    } catch (InterruptedException ie) {
                        LOGGER.warn("TaniumDS source (" + getName() + ") interrupted during retry.. continuig with next try");
                    }
                } while (this._qRes == null &&
                        (--count > 0 || System.currentTimeMillis() >= next_half_poll_time_ms));
                if (this._qRes == null) {
                    LOGGER.error("SOAP request to Tanium server returned null result even after retries: scheduled at: " + System.currentTimeMillis());
                    return Status.BACKOFF;
                }

                // LOGGER.info(result);
                String result = this._qRes.getResultXML();
                //System.out.println(result);
                List<QuestionResult> xmlQueryResultSetList = extractXMLQueryResults(result);
                List<Event> events = new ArrayList<Event>();
                for (int i = 0; i < this._objectList.getSavedQuestion().size(); i++) {
                    // NOTE: Assumption: result_sets are in same order as
                    // order of saved questions in ArrayList (this._savedQuestions)
                    String name = this._qRes.getObjectList().getSavedQuestion().get(i).getName();
                    Integer id = this._qRes.getObjectList().getSavedQuestion().get(i).getId();
                    String result_set_timestamp = xmlQueryResultSetList.get(i).getNow();
                    Map<String, String> header = new HashMap<String, String>();
                    header.put("saved_question_name", name);
                    header.put("result_set_timestamp", result_set_timestamp);
                    System.out.println("name=["+name+"], time ["+result_set_timestamp+"]");
                    Event event = EventBuilder.withBody(
                            xmlQueryResultSetList.get(i).getResult_set().getBytes(Charset.forName("UTF-8")),
                            header);
                    events.add(event);
                }
                //System.out.println(events.size()+" : " +Arrays.toString(events.toArray()));

                // Add the batch and events received to source counter
                int size = events.size();
                this._sc.incrementAppendBatchReceivedCount();
                this._sc.addToEventReceivedCount(size);
                Boolean channelExp = false;
                count = DEFULT_CHANNEL_REQUEST_RETRIES;
                do {
                    try {
                        getChannelProcessor().processEventBatch(events);
                    } catch (ChannelException ce) {
                        channelExp = true;
                        LOGGER.warn("Error appending event to channel. "
                                + "Channel might be full. Consider increasing the channel "
                                + "capacity or make sure the sinks perform faster.\n", ce);
                        try {
                            Thread.sleep(DEFULT_CHANNEL_REQUEST_RETRY_INTERVAL_SEC * 1000);
                        } catch (InterruptedException ie) {
                            LOGGER.warn("TaniumDS source (" + getName() + ") interrupted during channel retry.. continuig with next try");
                        }
                    }
                } while (channelExp &&
                        (--count > 0 || System.currentTimeMillis() >= next_half_poll_time_ms));
                if (channelExp) {
                    LOGGER.error("Channel request to add events returned exception even after retries: query scheduled at: " + System.currentTimeMillis());
                    return Status.BACKOFF;
                }

                // Add successful batch and events to source counter
                this._sc.addToEventAcceptedCount(size);
                this._sc.incrementAppendBatchAcceptedCount();

                // Sleep for scheduled poll interval
                try {
                    Thread.sleep(this._poll_interval_secs * 1000);
                } catch (InterruptedException ie) {
                    LOGGER.warn("TaniumDS source (" + getName() + ") interrupted during polling interval sleep, continuing with next try");
                    return Status.BACKOFF;
                }
                return Status.READY;
            }
        } catch (Exception e) {
            LOGGER.warn("TaniumDS source (" + getName() + "), Unknown exception encountered \n"+e);
			status = Status.BACKOFF;
			throw new EventDeliveryException(e);
		}
        // If polling query interval period is not passed then backoff
        return Status.BACKOFF;
    }

	/** 
    // * @param
    // * Get individual result for each query.
     */
    
    private List<QuestionResult> extractXMLQueryResults(String result)
                              throws SAXException, ParserConfigurationException, IOException {
		// Parse the XML string and extract individual results
    	ArrayList<QuestionResult> list = new ArrayList<QuestionResult>();
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(result)));
        Element root = doc.getDocumentElement();
        NodeList nl = root.getElementsByTagName("result_set");
		NodeList nl1 = root.getElementsByTagName("now");
        for (int i = 0; i<nl1.getLength(); i++) {
            //nl.item(i).appendChild(nl1.item(i)); already added to header
			QuestionResult qr = new QuestionResult();
            qr.setResult_set(getStringFromDoc(nl.item(i), doc));
            qr.setNow(nl1.item(i).getFirstChild().getTextContent());
        	list.add(qr);
        }
		return list;
	}

    public static String getStringFromDoc(org.w3c.dom.Node node, org.w3c.dom.Document doc)    {
    	//DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        return lsSerializer.writeToString(node);   
    }
    
    /*
    public static Document nodeToDocument(Node node) throws ParserConfigurationException {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	//factory.setNamespaceAware(true);
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	Document newDocument = builder.newDocument();
    	Node importedNode = newDocument.importNode(node, true);
    	newDocument.appendChild(importedNode);
    	return newDocument;
    }*/

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		    File fXmlFile = new File("/Users/suhasg/yy.xml");
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(fXmlFile);
		    String result = getStringFromDoc(doc.getFirstChild(), doc);
		    System.out.print(result);
            TaniumDS tds = new TaniumDS();
		    List<QuestionResult> x = tds.extractXMLQueryResults(result);
		    for (int i=0; i<x.size();i++) {
                System.out.println("\n now:{"+x.get(i).getNow()+"}\n");
		        System.out.println("\n result_set:{"+x.get(i).getResult_set()+"}\n");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
