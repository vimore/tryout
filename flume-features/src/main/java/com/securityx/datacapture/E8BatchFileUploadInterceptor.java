package com.securityx.datacapture;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.interceptor.Interceptor;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Batch File Upload relies on Flume-ng spooldir source To make it as a service,
 * the spooldir must be as generic as possible. As a consequence, only context
 * information event extracted from spooldir contains is the file it comes from.
 * This limitation can be bypassed by setting some file naming convention. The
 * E8BatchFileUploadInterceptor provides enrichment capabilities letting events
 * being ingested into E8 processing chain with required parameters.
 *
 * @author jyria <jean-yves@e8security.com>
 */
public class E8BatchFileUploadInterceptor
        implements Interceptor {

  public static final String LOGCOLLECTIONCATEGORY_KEY = "category";
  private static final String LOGCOLLECTIONINGESTOR = "ingestorName";
  public static final String LOGCOLLECTIONHOST_KEY = "hostname";
  public static final String LOGCOLLECTIONSPOOLDIRID_KEY = "spooldirId";
  public static final String LOGCOLLECTIONSPOOLDIRJOBID_KEY = "spooldirJobId";
    public static final String LOGCOLLECTIONSPOOLDIRJOBTIME_KEY = "spooldirJobTime";

  private static final String SPOOLDIR_FILE = "file";
  private static final String FILENAMETOKEN_SEP = "#";

  private String spooldirId;
  private String ingestorName;
  private String[] parts;
  private String file ="";
  private String curfilename;
  Map<String, String> headers;
  

  public E8BatchFileUploadInterceptor(String spooldirId) {
    this.spooldirId = spooldirId;
  }

  @Override
  public void initialize() {
    // At interceptor start up
    try {
      ingestorName
              = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new FlumeException("Cannot get Hostname", e);
    }
  }

  @Override
  public Event intercept(Event event) {

    // These are the event's headers
    headers = event.getHeaders();

    // Enrich header with ingesting context : 
    //  host : from system
    //  spooldirId : from flume's config (statically defined)
    headers.put(LOGCOLLECTIONSPOOLDIRID_KEY, spooldirId);
    headers.put(LOGCOLLECTIONINGESTOR, ingestorName);
    headers.put(LOGCOLLECTIONSPOOLDIRID_KEY, spooldirId);
    
    curfilename = headers.get( SPOOLDIR_FILE);
    // logCollectionHost#logCollectionCategory#UploadJobId#yyyy-mm-dd-hh
    if(! this.file.equals(curfilename)){
      parts = new File(curfilename).getName().split(FILENAMETOKEN_SEP);
      //change yyyy-mm-dd-hh to yyyy/mm/dd/hh
      if (parts.length >=4)
        parts[3]=parts[3].replaceAll("-", "/");
      file=curfilename;
    }
    if (parts.length >= 4){
      headers.put(LOGCOLLECTIONHOST_KEY, parts[0]);
      headers.put(LOGCOLLECTIONCATEGORY_KEY, parts[1]);
      headers.put(LOGCOLLECTIONSPOOLDIRJOBID_KEY, parts[2]);
      String jobSubmissionTime = parts[3];
      
      headers.put(LOGCOLLECTIONSPOOLDIRJOBTIME_KEY, jobSubmissionTime);
      // break through done, no need to keep the SPOOLDIR_FILE
      headers.remove(SPOOLDIR_FILE);
      event.setHeaders(headers);
    }else{
      // wrong format
      throw new FlumeException("Not a valid E8 Batch File Upload filename");
    }
    
    
    
    // Process file name to extract other headers fields
    
  

    // Let the enriched event go
    return event;
  }

  @Override
  public List<Event> intercept(List<Event> events) {

    List<Event> interceptedEvents
            = new ArrayList<Event>(events.size());
    for (Event event : events) {
      // Intercept any event
      Event interceptedEvent = intercept(event);
      interceptedEvents.add(interceptedEvent);
    }

    return interceptedEvents;
  }

  @Override
  public void close() {
    // At interceptor shutdown
  }

  public static class Builder
          implements Interceptor.Builder {

    private String spooldirId;

    @Override
    public void configure(Context context) {
      // Retrieve property from flume conf
      spooldirId = "staticspooldir";//context.getString("spooldirId");
    }

    @Override
    public Interceptor build() {
      return new E8BatchFileUploadInterceptor(spooldirId);
    }
  }
}
