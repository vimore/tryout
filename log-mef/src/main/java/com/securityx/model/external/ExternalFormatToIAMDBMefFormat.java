package com.securityx.model.external;

import com.securityx.model.external.msad.MsADToIAMDbMefMappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum ExternalFormatToIAMDBMefFormat {

    csvde(MsADToIAMDbMefMappings.values());
    
    
    private final Collection<ExternalFieldsToIAMDBMefFields> externalFieldsToIAMDBMefFields;
    private ExternalFormatToIAMDBMefFormat(ExternalFieldsToIAMDBMefFields[] externalFieldsToMefFields) {
        Map<Class, ExternalFieldsToIAMDBMefFields> keepOnlyOnePerEnumClass = new HashMap<Class, ExternalFieldsToIAMDBMefFields>();
        for (ExternalFieldsToIAMDBMefFields eftmf : externalFieldsToMefFields) {
            if (eftmf.getClass().isEnum()) {
                keepOnlyOnePerEnumClass.put(eftmf.getClass(), eftmf);
            } else {
                throw new IllegalArgumentException("ExternalFieldsToMefFields must be an enum implementation.");
            }
        }
        this.externalFieldsToIAMDBMefFields = keepOnlyOnePerEnumClass.values();
    }

    public Collection<ExternalFieldsToIAMDBMefFields> getExternalFieldsToMefFields() {
        return externalFieldsToIAMDBMefFields;
    }
}
