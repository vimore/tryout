package com.securityx.mef.log.mapreduce;

import junit.framework.TestCase;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import com.securityx.log.parsed.avro.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParserOutputConverterTest extends TestCase{
            /*      {
         "name":"rawLog",
         "name":"sAMAccountName",
         "name":"samAccountType",
         "name":"misterSAM",
         "name":"rIdSetReferences",
      }*/
    @Test
    /**
     * this unit test has been created to document a behavior met with avro IAMDBMEF
     * It appeared during test on demo data that fields sAMAccountName or sAMAccountType where not dumped into
     * avro format despite being extracted by parser.
     * by renaming these fields to samAccountName and samAccountType we can get the Mef field taken in account.
     * issue faced while working on E8-2430
     *
     */
    public void testCopyProperties(){
        Map<CharSequence, CharSequence> map = new HashMap<CharSequence, CharSequence>();
        // failing field name
        map.put("sAMAccountName", "name");
        // valid fieldname
        map.put("samAccountType", "type");
        // valid fieldname
        map.put("rawLog", "rawLog");
        // valid fieldname
        map.put("misterSAM", "misterSAM");
        // failing fieldname
        map.put("rIdSetReferences", "rIdSetReferences");
        Object mef=new TestAvroMef();
        ParsedOutput output = new ParsedOutput("rawlog", "iamdbmef", map);
        try {
            BeanUtils.copyProperties(mef, output.getValues());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        TestAvroMef iam = (TestAvroMef) mef;
        assertEquals("rawLog", "rawLog", iam.getRawLog());
        assertEquals("missing sAMAccountName", null, iam.getSAMAccountName());
        assertEquals("samAccountType", "type", iam.getSamAccountType());
        assertEquals("misterSAM", "misterSAM", iam.getMisterSAM());
        assertEquals("missing rIdSetReferences", null, iam.getRIDSetReferences());
        System.out.println(mef.toString()) ;


    }
}
