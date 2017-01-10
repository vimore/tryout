package com.securityx.model.external;

import com.securityx.model.external.flow.NProbeToFlowMefMappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum ExternalFormatToFlowMefFormat {

    nProbe(NProbeToFlowMefMappings.values());
    
    
    private final Collection<ExternalFieldsToFlowMefFields> externalFieldsToMefFields;
    ExternalFormatToFlowMefFormat(ExternalFieldsToFlowMefFields[] externalFieldsToMefFields) {
        Map<Class, ExternalFieldsToFlowMefFields> keepOnlyOnePerEnumClass = new HashMap<Class, ExternalFieldsToFlowMefFields>();
        for (ExternalFieldsToFlowMefFields eftmf : externalFieldsToMefFields) {
            if (eftmf.getClass().isEnum()) {
                keepOnlyOnePerEnumClass.put(eftmf.getClass(), eftmf);
            } else {
                throw new IllegalArgumentException("ExternalFieldsToMefFields must be an enum implementation.");
            }
        }
        this.externalFieldsToMefFields = keepOnlyOnePerEnumClass.values();
    }

    public Collection<ExternalFieldsToFlowMefFields> getExternalFieldsToMefFields() {
        return externalFieldsToMefFields;
    }
}
