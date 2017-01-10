package com.securityx.model.mef.field.api;

public interface GenericFormat {
  /**
   * return field name (as identified in raw log format)
   * @return
   */
  String getPrettyName();

  GenericFormat getByPrettyName(String prettyName);


}


