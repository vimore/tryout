/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.field.marshaller;

import com.securityx.model.mef.field.api.MefFieldMarshall;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jyrialhon
 */
public class StringMarshall implements MefFieldMarshall{
  @Override
  public List<ByteBuffer> marshallToBytes(List<Object> values) {
    List<ByteBuffer> out = new ArrayList<ByteBuffer>();
    for (Object value : values){
      String str = (String) value;
      byte[] bytes;
      try {
        bytes = str.getBytes("UTF-8");
        out.add(ByteBuffer.wrap(bytes));
      } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(StringMarshall.class.getName()).log(Level.SEVERE, "failed to convert "+str, ex);
      }
    }
    return out;
  }

  @Override
  public List<Object> marshallFromBytes(List<ByteBuffer> valuesAsBytes) {
   List<Object> out = new ArrayList<Object>();
   for (ByteBuffer value: valuesAsBytes){
     try {
       String str = new String(value.array(), "UTF-8");
       out.add(str);
     } catch (UnsupportedEncodingException ex) {
       Logger.getLogger(StringMarshall.class.getName()).log(Level.SEVERE, "failed to convert from bytes", ex);
     }
   }
   return out;
  }
  
  
}
