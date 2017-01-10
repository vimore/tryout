package com.securityx.model.mef.field.constraint;

import com.securityx.model.mef.field.api.HostProcessMefField;
import com.securityx.model.mef.field.api.SupportedFormat;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.util.Map;

public class MefMd5StringConstraintTest extends TestCase {

    @Test
    public void testMd5Constraint(){
         MefMd5StringConstraint c = new MefMd5StringConstraint();

        Map<SupportedFormat, Object> out = c.validate(null, HostProcessMefField.processFileMd5, "ABCDEF00000", null);
        this.assertEquals("md5", true, out.containsKey(HostProcessMefField.processFileMd5));
        this.assertEquals("md5", "ABCDEF00000", out.get(HostProcessMefField.processFileMd5));

        out = c.validate(null, HostProcessMefField.processFileMd5, "ABCDEF00000", null);
        this.assertEquals("md5", true, out.containsKey(HostProcessMefField.processFileMd5));
        this.assertEquals("md5", "ABCDEF00000", out.get(HostProcessMefField.processFileMd5));

        out = c.validate(null, HostProcessMefField.processFileMd5, "abcdef00000", null);
        this.assertEquals("md5", true, out.containsKey(HostProcessMefField.processFileMd5));
        this.assertEquals("md5", "abcdef00000", out.get(HostProcessMefField.processFileMd5));


        out = c.validate(null, HostProcessMefField.processFileMd5, "ABCDEFABCDEFABCDEFABCDEFABCDEF00", null);
        this.assertEquals("md5", true, out.containsKey(HostProcessMefField.processFileMd5));
        this.assertEquals("md5", "ABCDEFABCDEFABCDEFABCDEFABCDEF00", out.get(HostProcessMefField.processFileMd5));

        out = c.validate(null, HostProcessMefField.processFileMd5, "ABCDEFJBCDEFABCDEFABCDEFABCDEF00", null);
        this.assertEquals("md5", false, out.containsKey(HostProcessMefField.processFileMd5));

    }

}
