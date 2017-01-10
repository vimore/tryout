package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSADGUIDConstraint implements MefFieldConstrait<String> {

  private Logger logger = LoggerFactory.getLogger(MSADGUIDConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  static String AddLeadingZero(int k) {
    return (k < 0xF) ? "0" + Integer.toHexString(k) : Integer.toHexString(k);
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    if (logger.isDebugEnabled()) {
      logger.debug("MSADGUIDConstraint :" + field.getPrettyName() + " : " + value);
    }
    //X'0b818fd05ca3a849a4cdc6923dfc031e'
    //X'0b818fd0 5ca3 a849 a4cd c6923dfc031e'
     //X'0b 81 8f d0 5c a3 a8 49 a4 cd c6 92 3d fc 03 1e'
    //   0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15

    String strGUID = "";
    String byteGUID = "";
    //Convert the GUID into string using the byte format
    List<String> GUID = new ArrayList<String>();
    for (int c = 2; c < value.length()-2; c=c+2) {
      String b = value.substring(c,c+2);
      GUID.add( AddLeadingZero((int) Integer.parseInt(b,16) & 0xFF));
    }
    //convert the GUID into string format
    strGUID = "";
    strGUID = strGUID + GUID.get(3);
    strGUID = strGUID + GUID.get(2);
    strGUID = strGUID + GUID.get(1);
    strGUID = strGUID + GUID.get(0);
    strGUID = strGUID + "-";
    strGUID = strGUID + GUID.get(5);
    strGUID = strGUID + GUID.get(4);
    strGUID = strGUID + "-";
    strGUID = strGUID + GUID.get(7);
    strGUID = strGUID + GUID.get(6);
    strGUID = strGUID + "-";
    strGUID = strGUID + GUID.get(8);
    strGUID = strGUID + GUID.get(9);
    strGUID = strGUID + "-";
    strGUID = strGUID + GUID.get(10);
    strGUID = strGUID + GUID.get(11);
    strGUID = strGUID + GUID.get(12);
    strGUID = strGUID + GUID.get(13);
    strGUID = strGUID + GUID.get(14);
    strGUID = strGUID + GUID.get(15);

    if (logger.isDebugEnabled()) {
      logger.debug("MSADGUIDConstraint :" + strGUID);
    }
    results.put(field, String.valueOf(strGUID));
    return results;
  }

}
