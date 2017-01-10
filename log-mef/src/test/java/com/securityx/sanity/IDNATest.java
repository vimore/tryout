package com.securityx.sanity;

import com.ibm.icu.text.IDNA;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.testng.annotations.Test;

public class IDNATest extends TestCase {


    @Test
    public void testSimpleIDNA(){
        IDNA idna = IDNA.getUTS46Instance(IDNA.USE_STD3_RULES);
        IDNA.Info info = new IDNA.Info();
        StringBuilder builder = new StringBuilder();
        OutUtils.printOut(idna.nameToASCII("méxico.icom.museum", builder, info));
        builder.delete(0, builder.capacity());
        OutUtils.printOut(idna.nameToASCII("r4---sn-q4fl6n7e.googlevideo.com", builder, info));
        builder.delete(0, builder.capacity());
        OutUtils.printOut(idna.nameToUnicode("r4---sn-q4fl6n7e.googlevideo.com", builder, info));
    }


    public void testSimpleIDNA2(){
        IDNA idna = IDNA.getUTS46Instance(IDNA.USE_STD3_RULES);
        IDNA.Info info = new IDNA.Info();
        StringBuilder builder = new StringBuilder();
        builder = idna.nameToASCII("méxico.icom.museum", builder, info);
        if (!info.hasErrors()){
            OutUtils.printOut("converted to " + builder);
        }  else {
            OutUtils.printOut("issue converting : "+ info.getErrors());
        }

        builder.delete(0, builder.capacity());
        builder = idna.nameToASCII("r4---sn-q4fl6n7e.googlevideo.com", builder, info);
        if (!info.hasErrors()){
            OutUtils.printOut("converted to " + builder);
        }  else {
            OutUtils.printOut("issue converting : "+ info.getErrors());
        }

    }

}
