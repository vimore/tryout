/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.field.marshaller;

import com.securityx.model.mef.field.api.MefFieldMarshall;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jyrialhon
 */
public class LongMarshall implements MefFieldMarshall {

  @Override
  public List<ByteBuffer> marshallToBytes(List<Object> values) {
    List<ByteBuffer> out = new ArrayList<ByteBuffer>();
    for (Object value : values) {
      long longValue;
      if (value instanceof String){
        longValue = Long.parseLong((String)value);
      }else{
        longValue = (Long)value;
      }
      ByteBuffer b = ByteBuffer.allocate(Long.SIZE/8);
      b.putLong(longValue);
      out.add(b);
    }
    return out;
  }

  @Override
  public List<Object> marshallFromBytes(List<ByteBuffer> valuesAsBytes) {
    List<Object> out = new ArrayList<Object>();
    for (ByteBuffer value : valuesAsBytes) {
      long i = value.getLong();
      out.add(i);
    }
    return out;
  }

}
