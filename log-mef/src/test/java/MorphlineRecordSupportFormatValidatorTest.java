import com.securityx.model.external.bluecoat.BluecoatMainToMefMappings;
import com.securityx.model.mef.MorphlineRecordSupportFormatValidator;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.field.api.WebProxyMefField;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jyrialhon
 */
public class MorphlineRecordSupportFormatValidatorTest extends TestCase{
  @Test
  public void testLogMef(){
    MorphlineRecordSupportFormatValidator validator = new MorphlineRecordSupportFormatValidator(SupportedFormats.WebProxyMef, false);
    ValidationLogger validatorLogger = new ValidationLogger();
    Record r =  new Record();
    r.put(WebProxyMefField.destinationAddress.getPrettyName(), "192.168.12.15");
    r.put(WebProxyMefField.destinationPort.getPrettyName(), "80");
    Boolean result = validator.validate(validatorLogger, r);
    Assert.assertEquals(WebProxyMefField.destinationPort.getPrettyName(), new Integer(80), r.get(WebProxyMefField.destinationPort.getPrettyName()).get(0));
  }

    @Test
  public void testBlueCoat(){
    MorphlineRecordSupportFormatValidator validator = new MorphlineRecordSupportFormatValidator(SupportedFormats.BlueCoat, false);
    ValidationLogger validatorLogger = new ValidationLogger();
    Record r =  new Record();
    r.put(BluecoatMainToMefMappings.cs_ip.getPrettyName(), "192.168.12.15");
    r.put(BluecoatMainToMefMappings.cs_bodylength.getPrettyName(), "80");
    Boolean result = validator.validate(validatorLogger, r);
    Assert.assertEquals(BluecoatMainToMefMappings.cs_bodylength.getPrettyName(), new Integer(80), r.get(BluecoatMainToMefMappings.cs_bodylength.getPrettyName()).get(0));
  }

}
