package com.securityx.model.external;

import com.securityx.model.mef.field.api.FlowMefField;
import com.securityx.model.mef.field.api.GenericFormat;

import java.util.Set;

public interface ExternalFieldsToFlowMefFields extends GenericFormat{

    Set<String> getExternalFieldNames();

    FlowMefField getMefField(String externalFieldName);
}
