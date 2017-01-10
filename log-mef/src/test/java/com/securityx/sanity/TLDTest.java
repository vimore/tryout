package com.securityx.sanity;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.ibm.icu.text.MessageFormat;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.testng.annotations.Test;

public class TLDTest extends TestCase {
    @Test
    public void testTLD(){
        InternetDomainName owner = InternetDomainName.from("google.com").topPrivateDomain();
        OutUtils.printOut(owner.toString());
        owner = InternetDomainName.from("google.com").topPrivateDomain();
        OutUtils.printOut(owner.toString());

        String hostname = "www.bluelotussoftware.com";
        hostname = "google.com";

        boolean result = InternetDomainName.from(hostname).hasParent();
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
        OutUtils.printOut(MessageFormat.format("parent : {0}", InternetDomainName.from(hostname).parent().toString()));

        result = InternetDomainName.isValid(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
        hostname = "bluelotussoftware.com";
        result = InternetDomainName.isValid(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
        hostname = "bluelotussoftware";
        result = InternetDomainName.isValid(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
        hostname = "256.0.0.0";
        result = InternetDomainName.isValid(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
        result = InetAddresses.isInetAddress(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Address: {0} valid? {1}", hostname, result));
        hostname = "255.255.255.0";
        result = InternetDomainName.isValid(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
        result = InetAddresses.isInetAddress(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Address: {0} valid? {1}", hostname, result));
        hostname = "_xyz.infor.com";
        result = InternetDomainName.isValid(hostname);
        OutUtils.printOut(MessageFormat.format("Is Internet Domain Name: {0} valid? {1}", hostname, result));
    }
}
