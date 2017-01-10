package com.securityx.model.mef.field.constraint;

import com.securityx.model.mef.field.api.CertMefField;
import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.ValidationLogger;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Created by jyria on 10/04/15.
 */
public class CertMefIsEpochTimestampConstraintTest extends TestCase {
    CertMefIsEpochTimestampConstraint constraint;

    public void setUp() throws Exception {
        super.setUp();
        constraint = new CertMefIsEpochTimestampConstraint();

    }

    public void testValidate() throws Exception {
        // Mon, 27 Aug 2012 20:40:40 GMT
        String value = "120827204040Z";
        Map<SupportedFormat, Object> res = constraint.validate(new ValidationLogger(), CertMefField.certNoValidAfter, value, null);
        assertEquals(true, res.containsKey(CertMefField.certNoValidAfter));
        assertEquals(1346100040000L, res.get(CertMefField.certNoValidAfter));
    }
    public void testLongDateValidate() throws Exception {
        //Sat, 27 Aug 2050 20:40:40 GMT
        String value = "20500827204040Z";
        Map<SupportedFormat, Object> res = constraint.validate(new ValidationLogger(), CertMefField.certNoValidAfter, value, null);
        assertEquals(true, res.containsKey(CertMefField.certNoValidAfter));
        assertEquals(2545245640000L, res.get(CertMefField.certNoValidAfter));
    }
}