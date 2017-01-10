package com.securityx.model.external;

import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.WebProxyMefField;

import java.util.Set;

public interface ExternalFieldsToWebProxyMefFields extends GenericFormat {

    Set<String> getExternalFieldNames();

    WebProxyMefField getMefField(String externalFieldName);
}
