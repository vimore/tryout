package com.securityx.model.external;

import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.SIEMIncidentMefField;

import java.util.Set;

public interface ExternalFieldsToSIEMIncidentMefFields extends GenericFormat {

    Set<String> getExternalFieldNames();

    SIEMIncidentMefField getMefField(String externalFieldName);
}
