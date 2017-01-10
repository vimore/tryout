package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSADSIDConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MSADSIDConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }
  private String littleToBigIndian(String in){
    String out=null;
    for(int i=0;i<in.length()/2; i++){
      out=(out==null?in.substring(2*i, 2*i+2):in.substring(2*i, 2*i+2).concat(out));
    }
    return out;
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if(logger.isDebugEnabled())
        logger.debug("MSADSIDConstraint :"+field.getPrettyName()+ " : "+ value);
      //X'010500000000000515000000e49ec47102197d0ca6881995e9030000'
      //X'010500000000000515000000A065CF7E784B9B5FE77C8770091C0100'
      String a=value.substring(2, 4);
      String n=value.substring(4, 6);
      String b=value.substring(6,18);
      Integer nn = Integer.parseInt(n,16); //nn = nb of dash in sid - 2
      ///X'01 05 000000000005 15000000 A065CF7E 784B9B5F E77C8770 091C0100'
      List<String> parts = new ArrayList<String>();
      for (int i=0; i< nn;i++){
        parts.add(littleToBigIndian(value.substring(18+8*i, 18+8*i+8)));
      }
      
      Integer na = Integer.parseInt(a,16);
      Long nb = Long.parseLong(b,16);
      List<Long> nparts = new ArrayList<Long>();
      String res = "S-"+na+"-"+nn+"-"+nb;
      for (int i=0; i< nn;i++){
        res = res.concat("-".concat(String.valueOf(Long.parseLong(parts.get(i),16))));
      }
      if(logger.isDebugEnabled())
          logger.debug("MSADSIDConstraint :"+res);
      results.put(field, String.valueOf(res));
      return results;
  }



}
