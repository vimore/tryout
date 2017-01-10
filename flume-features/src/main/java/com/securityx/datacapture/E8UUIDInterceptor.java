package com.securityx.datacapture;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

/**
 * Flume Interceptor that sets a universally unique identifier on all events
 * that are intercepted. By default this event header is named "id".
 */
public class E8UUIDInterceptor implements Interceptor {

  private final String headerName;
  private final boolean preserveExisting;
  private E8UUIDGenerator uuidGenerator;

  public static final String HEADER_NAME = "headerName";
  public static final String PRESERVE_EXISTING_NAME = "preserveExisting";
  public static final String NB_OF_REGION = "nbRegion";


  protected E8UUIDInterceptor(Context context) {
    headerName = context.getString(HEADER_NAME, "uuid");
    preserveExisting = context.getBoolean(PRESERVE_EXISTING_NAME, true);
    int nbRegion = context.getInteger(NB_OF_REGION, 3);
    uuidGenerator = new E8UUIDGenerator(nbRegion);
  }

  protected boolean isMatch(Event event) {
    return true;
  }

  public E8UUIDGenerator getUuidGenerator() {
    return uuidGenerator;
  }

  public void initialize() {}

  public Event intercept(Event event) {
    Map<String, String> headers = event.getHeaders();
    if (preserveExisting && headers.containsKey(headerName)) {
      // we must preserve the existing id
    } else if (isMatch(event)) {
      headers.put(headerName, uuidGenerator.generateUUID(event.getHeaders(), new String(event.getBody())));
    }
    return event;
  }

  public List<Event> intercept(List<Event> events) {
    List results = new ArrayList(events.size());
    for (Event event : events) {
      event = intercept(event);
      if (event != null) {
        results.add(event);
      }
    }
    return results;
  }

  public void close() {

  }


  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  /** Builder implementations MUST have a public no-arg constructor */
  public static class Builder implements Interceptor.Builder {

    private Context context;

    public Builder() {
    }

    public E8UUIDInterceptor build() {
      return new E8UUIDInterceptor(context);
    }


    public void configure(Context context) {
      this.context = context;
    }

  }
  

  
  public static void main(String[] args) throws NoSuchAlgorithmException{
    EthernetAddress addr = EthernetAddress.fromInterface();
    TimeBasedGenerator generator = Generators.timeBasedGenerator(addr);
    UUID uuid = generator.generate();
    
    
    
   System.out.println(uuid.toString());
   System.out.println(uuid.timestamp());
   System.out.println(uuid.node());
   System.out.println(uuid.node()%8);

   
    
  }

}