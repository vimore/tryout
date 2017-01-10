/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.field.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * describe supported formats such as Master Event Format, bluecoat SGOS, sonicOs or cef
 * @author jyrialhon
 */
public interface SupportedFormat extends GenericFormat {
  /**
   * 
   * @return 
   *   conversion constraints for the field
   */
  List<MefFieldConstrait> getContraints();
  /**
   *   field description
   * @return 
   *   field description as a field
   */
  String getDescription();

  /**
   * return avroName (field name for avro container)
   * @return 
   */
  String getAvroName();

  /**
   * return avroObjectClass (container of the field)
   * @param prefix
   * @return 
   */
  String getAvroObjectClass(SupportedFormats prefix);


  /**
   * are null accepted 
   * @return 
   */
  int getNumberOfValues();
  /**
   * is the field nullable ?
   * @return 
   * 
   */
  boolean isNullable();
  
  String getSetter();
  Class getArgClass();
  
  Collection<SupportedFormat> getSupportedFormatfields();
  
  String getNamespace()  ;
  /**
   * build stack
   * @param prefix
   *    supportedFormat name
   * @return 
   */
  Map<String, String> getStack(String prefix);

}
