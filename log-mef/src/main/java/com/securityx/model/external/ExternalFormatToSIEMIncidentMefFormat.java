package com.securityx.model.external;

import com.securityx.model.external.cef.CEFToSIEMIncidentMefMappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum ExternalFormatToSIEMIncidentMefFormat {

    Cef(CEFToSIEMIncidentMefMappings.values()),
    ;

    private final Collection<ExternalFieldsToSIEMIncidentMefFields> externalFieldsToMefFields;
    private ExternalFormatToSIEMIncidentMefFormat(ExternalFieldsToSIEMIncidentMefFields[] externalFieldsToMefFields) {
        Map<Class, ExternalFieldsToSIEMIncidentMefFields> keepOnlyOnePerEnumClass = new HashMap<Class, ExternalFieldsToSIEMIncidentMefFields>();
        for (ExternalFieldsToSIEMIncidentMefFields eftmf : externalFieldsToMefFields) {
            if (eftmf.getClass().isEnum()) {
                keepOnlyOnePerEnumClass.put(eftmf.getClass(), eftmf);
            } else {
                throw new IllegalArgumentException("ExternalFieldsToMefFields must be an enum implementation.");
            }
        }
        this.externalFieldsToMefFields = keepOnlyOnePerEnumClass.values();
    }

    public Collection<ExternalFieldsToSIEMIncidentMefFields> getExternalFieldsToMefFields() {
        return externalFieldsToMefFields;
    }
}
