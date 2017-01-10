package com.securityx.model.external;


import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.IAMDBMefField;

import java.util.Set;

public interface ExternalFieldsToIAMDBMefFields extends GenericFormat{

    Set<String> getExternalFieldNames();

    IAMDBMefField getMefField(String externalFieldName);
}
