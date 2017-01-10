package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ArrayListMultimap;
import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.field.api.WebProxyMefField;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.util.Map;

public class MefNameOrIpConstraintTest extends TestCase {
    @Test
    public void testNameOrIpConstraint(){
        MefInetNameOrIpConstraint c = new MefInetNameOrIpConstraint();

        Map<SupportedFormat, Object> out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "toto.google.com", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp", "toto.google.com", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", "google.com", out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationHostName", "toto", out.get(WebProxyMefField.destinationHostName));

        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "google.com", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp", "google.com", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", "google.com", out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationHostName", null, out.get(WebProxyMefField.destinationHostName));

        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "google", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp", "google", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", null, out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationHostName", "google", out.get(WebProxyMefField.destinationHostName));

        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "GOOGLE.COM", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp", "GOOGLE.COM", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", "google.com", out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationHostName", null, out.get(WebProxyMefField.destinationHostName));

        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "paints.méxico.museum.com", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp", "paints.méxico.museum.com", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", "xn--mxico-bsa.museum.com", out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationHostName", "paints", out.get(WebProxyMefField.destinationHostName));


        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "\\\\MYWINDOWS-Host", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp",  "\\\\MYWINDOWS-Host", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", null, out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationHostName", "mywindows-host", out.get(WebProxyMefField.destinationHostName));

        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, "DOMAIN\\MYWINDOWS-Host", ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        this.assertEquals("destinationNameOrIp",  "DOMAIN\\MYWINDOWS-Host", out.get(WebProxyMefField.destinationNameOrIp));
        this.assertEquals("destinationDnsDomain", null, out.get(WebProxyMefField.destinationDnsDomain));
        this.assertEquals("destinationNtDomain", "DOMAIN", out.get(WebProxyMefField.destinationNtDomain));
        this.assertEquals("destinationHostName", "MYWINDOWS-Host", out.get(WebProxyMefField.destinationHostName));


        MefTLDNormalizedConstraint cTLD = new MefTLDNormalizedConstraint();


        String value = "host.totot.mexico.com";
        out = c.validate(new ValidationLogger(), WebProxyMefField.destinationNameOrIp, value, ArrayListMultimap.<String, Object>create());
        OutUtils.printOut("out: "+out);

        out = cTLD.validate(new ValidationLogger(), WebProxyMefField.destinationDnsDomain, (String) out.get(WebProxyMefField.destinationDnsDomain), null);;
        this.assertEquals("check output value", "mexico.com", out.get(WebProxyMefField.destinationDnsDomainTLD));
        OutUtils.printOut(out.get(WebProxyMefField.destinationDnsDomainTLD));

    }
}
