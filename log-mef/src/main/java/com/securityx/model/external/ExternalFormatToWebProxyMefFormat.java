package com.securityx.model.external;

import com.securityx.model.external.bluecoat.BluecoatMainToMefMappings;
import com.securityx.model.external.cef.CEFToMefMappings;
import com.securityx.model.external.leef.LEEFToMefMappings;
import com.securityx.model.external.mcafeewebsec.McAfeeWebSecToMefMappings;
import com.securityx.model.external.sonicos.SonicOsToMefMappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum ExternalFormatToWebProxyMefFormat {

    Cef(CEFToMefMappings.values()), 
    BlueCoat(BluecoatMainToMefMappings.values()),
    SonicOs(SonicOsToMefMappings.values()),
    mcafeewebsec(McAfeeWebSecToMefMappings.values()),
    leefWebsenseSecurity(LEEFToMefMappings.values())   ;
    
    private final Collection<ExternalFieldsToWebProxyMefFields> externalFieldsToMefFields;
    private ExternalFormatToWebProxyMefFormat(ExternalFieldsToWebProxyMefFields[] externalFieldsToMefFields) {
        Map<Class, ExternalFieldsToWebProxyMefFields> keepOnlyOnePerEnumClass = new HashMap<Class, ExternalFieldsToWebProxyMefFields>();
        for (ExternalFieldsToWebProxyMefFields eftmf : externalFieldsToMefFields) {
            if (eftmf.getClass().isEnum()) {
                keepOnlyOnePerEnumClass.put(eftmf.getClass(), eftmf);
            } else {
                throw new IllegalArgumentException("ExternalFieldsToMefFields must be an enum implementation.");
            }
        }
        this.externalFieldsToMefFields = keepOnlyOnePerEnumClass.values();
    }

    public Collection<ExternalFieldsToWebProxyMefFields> getExternalFieldsToMefFields() {
        return externalFieldsToMefFields;
    }
}
