package com.securityx.datacapture;

import com.securityx.datacapture.E8BatchFileUploadInterceptor.Builder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.interceptor.Interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class E8BatchFileUploadInterceptorTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public E8BatchFileUploadInterceptorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( E8BatchFileUploadInterceptorTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testE8BatchFileUploadInterceptor()
    {
      Builder builder = new E8BatchFileUploadInterceptor.Builder();
      Interceptor interceptor = builder.build();
      Event evt = new SimpleEvent();
      Map<String, String> headers = new HashMap<String, String>();
      headers.put("file", "/bla/bli/blu/logCollectionHost#logCollectionCategory#UploadJobId#yyyy-mm-dd-hh");
      evt.setHeaders(headers);
      Event res = interceptor.intercept(evt);
      
      assertEquals(E8BatchFileUploadInterceptor.LOGCOLLECTIONHOST_KEY, 
              "logCollectionHost", 
              res.getHeaders().get(E8BatchFileUploadInterceptor.LOGCOLLECTIONHOST_KEY));
      assertEquals(E8BatchFileUploadInterceptor.LOGCOLLECTIONCATEGORY_KEY, 
              "logCollectionCategory", 
              res.getHeaders().get(E8BatchFileUploadInterceptor.LOGCOLLECTIONCATEGORY_KEY));
      assertEquals(E8BatchFileUploadInterceptor.LOGCOLLECTIONSPOOLDIRJOBID_KEY, 
              "UploadJobId", 
              res.getHeaders().get(E8BatchFileUploadInterceptor.LOGCOLLECTIONSPOOLDIRJOBID_KEY));
      assertEquals(E8BatchFileUploadInterceptor.LOGCOLLECTIONSPOOLDIRJOBTIME_KEY, 
              "yyyy/mm/dd/hh", 
              res.getHeaders().get(E8BatchFileUploadInterceptor.LOGCOLLECTIONSPOOLDIRJOBTIME_KEY));
        assertTrue( true );
    }
}
