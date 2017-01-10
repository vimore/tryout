package com.securityx.model.mef.field.constraint;

import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.field.api.WebProxyMefField;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.util.Map;

public class MefTLDNormalizedConstraintTest extends TestCase{

    @Test
    public void testMefTLDNormalizedConstraint(){
        MefTLDNormalizedConstraint c = new MefTLDNormalizedConstraint();


        String value = "totot.mexico.com";
        Map<SupportedFormat, Object> out = c.validate(new ValidationLogger(), WebProxyMefField.destinationDnsDomain, value, null);;
       this.assertEquals("check output value", "mexico.com", out.get(WebProxyMefField.destinationDnsDomainTLD));
    }


    @Test
    public void testMefIDNANormalizedConstraint1(){
        MefTLDNormalizedConstraint c = new MefTLDNormalizedConstraint();


        String value = "google.com";
        Map<SupportedFormat, Object> out = c.validate(new ValidationLogger(), WebProxyMefField.destinationDnsDomain, value, null);;
        this.assertEquals("check output value", "google.com", out.get(WebProxyMefField.destinationDnsDomainTLD));
    }


    @Test
    public void testMefIDNANormalizedConstraint2(){
        MefTLDNormalizedConstraint c = new MefTLDNormalizedConstraint();


        String value = "gouv.co.uk";
        Map<SupportedFormat, Object> out = c.validate(new ValidationLogger(), WebProxyMefField.destinationDnsDomain, value, null);;
        this.assertEquals("check output value", "gouv.co.uk", out.get(WebProxyMefField.destinationDnsDomainTLD));
    }
    @Test
    public void testMefIDNANormalizedConstraint3(){
        MefTLDNormalizedConstraint c = new MefTLDNormalizedConstraint();

        // to short for a tld.
        String value = "co.uk";
        Map<SupportedFormat, Object> out = c.validate(new ValidationLogger(), WebProxyMefField.destinationDnsDomain, value, null);;
        this.assertEquals("check output value", null, out.get(WebProxyMefField.destinationDnsDomainTLD));
    }


}
