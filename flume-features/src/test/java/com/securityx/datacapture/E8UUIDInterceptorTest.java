package com.securityx.datacapture;

import com.securityx.datacapture.E8UUIDInterceptor.Builder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.interceptor.Interceptor;

/**
 * Unit test for simple App.
 */
public class E8UUIDInterceptorTest
        extends TestCase {
  private final SimpleEvent inputEvt;


  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public E8UUIDInterceptorTest(String testName) {
    super(testName);
    
     MessageDigest mDigest = null;
     
     // init input data
     inputEvt  = new SimpleEvent();
     inputEvt.setBody(THIS_IS_A_SAMPLE_LOG_EXAMPLE.getBytes());
  }
  public static final String HOST_IP = "192.168.1.1";
  public static final String SOMEHOSTNAME = "somehostname";
  public static final String THIS_IS_A_SAMPLE_LOG_EXAMPLE = new String ("this is a sample log example");

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(E8UUIDInterceptorTest.class);
  }

  /**
   * Rigourous Test :-)
   */
  
  public void testE8UUIDInterceptorMD5() {
    E8UUIDInterceptor interceptor;
    Builder builder = new E8UUIDInterceptor.Builder();
    builder.configure(new Context());

   
      interceptor = builder.build();
      Map<String, String> headers = new HashMap<String, String>();
      inputEvt.setHeaders(headers);
      String md5 = interceptor.getUuidGenerator().md5(THIS_IS_A_SAMPLE_LOG_EXAMPLE);
      System.out.println("md5 : "+ md5);
      assertEquals("log", "6ded1ac5fb0038816b50d8e7d024ee3f", md5);

      
      /*
        echo -n "undef" | md5sum
        9bffa0b6d58b13d5029ad4c12ff4bbfa  -
     
        echo -n "this is a sample log example" | md5sum
        6ded1ac5fb0038816b50d8e7d024ee3f  -
      */
  
     
      
    
    assertTrue(true);
  }
  
  
  
  public void testE8UUIDInterceptorUndef() {
    Interceptor interceptor;
    Builder builder = new E8UUIDInterceptor.Builder();
    builder.configure(new Context());

   
      interceptor = builder.build();
      Map<String, String> headers = new HashMap<String, String>();
      inputEvt.setHeaders(headers);
      Event res = interceptor.intercept(inputEvt);
      assertEquals("contains uuid", true, inputEvt.getHeaders().containsKey("uuid"));
      System.out.println(inputEvt.getHeaders().get("uuid"));
      E8RawLogUUID uuid = new E8RawLogUUID(inputEvt.getHeaders().get("uuid"));
      assertEquals("log", "6ded1ac5fb0038816b50d8e7d024ee3f", uuid.getLog());
      assertEquals("src", "f31e", new String(uuid.getSrc().getBytes()));
      /*
        echo -n "undef" | md5sum
        f31ee5e3824f1f5e5d206bdf3029f22b  -
     
        echo -n "this is a sample log example" | md5sum
        6ded1ac5fb0038816b50d8e7d024ee3f  -
      */
  
     
      
    
    assertTrue(true);
  }
  public void testE8UUIDInterceptorWithHostName() {
    Interceptor interceptor;
    Builder builder = new E8UUIDInterceptor.Builder();
    builder.configure(new Context());

   
      interceptor = builder.build();
      Map<String, String> headers = new HashMap<String, String>();;
      headers.put("hostname", SOMEHOSTNAME);
      inputEvt.setHeaders(headers);
      Event res = interceptor.intercept(inputEvt);
      assertEquals("contains uuid", true, inputEvt.getHeaders().containsKey("uuid"));
      System.out.println(inputEvt.getHeaders().get("uuid"));
      E8RawLogUUID uuid = new E8RawLogUUID(inputEvt.getHeaders().get("uuid"));
      assertEquals("log", "6ded1ac5fb0038816b50d8e7d024ee3f", uuid.getLog());
      assertEquals("src", "4cff", uuid.getSrc());
      /*
      echo -n somehostname | md5sum
      4cff68d238349312981c770c94e92131  -
      */
    
    assertTrue(true);
  }
  
  
    public void testE8UUIDInterceptorWithHost() {
    Interceptor interceptor;
    Builder builder = new E8UUIDInterceptor.Builder();
    builder.configure(new Context());

   
      interceptor = builder.build();
      
      Map<String, String> headers = new HashMap<String, String>();
      headers.put("hostname", SOMEHOSTNAME);
      headers.put("host", HOST_IP);
      
      inputEvt.setHeaders(headers);
      Event res = interceptor.intercept(inputEvt);
      assertEquals("contains uuid", true, inputEvt.getHeaders().containsKey("uuid"));
      System.out.println(inputEvt.getHeaders().get("uuid"));
      E8RawLogUUID uuid = new E8RawLogUUID(inputEvt.getHeaders().get("uuid"));
      assertEquals("log", "6ded1ac5fb0038816b50d8e7d024ee3f", uuid.getLog());
      assertEquals("src", "66ef", uuid.getSrc());
      /* 
      echo -n "192.168.1.1" | md5sum
      66efff4c945d3c3b87fc271b47d456db  -
      */
    
  }
    
    
 
}
