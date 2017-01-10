package com.securityx.model.external;

import com.securityx.model.external.bluecoat.BluecoatMainToMefMappings;
import com.securityx.model.external.cef.CEFToMefMappings;
import com.securityx.model.external.cef.E8CustomCEFToMefMappings;
import com.securityx.model.external.leef.LEEFToMefMappings;
import com.securityx.model.external.mcafeewebsec.McAfeeWebSecToMefMappings;
import com.securityx.model.external.msad.MsADToIAMDbMefMappings;
import com.securityx.model.external.sonicos.SonicOsToMefMappings;
import com.securityx.model.mef.field.api.GenericFormat;

import java.util.*;

public enum ExternalFormats  {
    Bluecoat(BluecoatMainToMefMappings.values(), BluecoatMainToMefMappings.class),
    cef(CEFToMefMappings.values(), CEFToMefMappings.class),
    csvde(MsADToIAMDbMefMappings.values(), MsADToIAMDbMefMappings.class),
    SonicOs(SonicOsToMefMappings.values(), SonicOsToMefMappings.class),
    mcafeewebsecurity(McAfeeWebSecToMefMappings.values(), McAfeeWebSecToMefMappings.class),
    e8CustomCef(E8CustomCEFToMefMappings.values(), E8CustomCEFToMefMappings.class),
    leefWebsenseSecurity(LEEFToMefMappings.values(),LEEFToMefMappings.class)


    ;
    private final Collection<GenericFormat> externalFormats;
    private final Class enumClass;

    ExternalFormats(GenericFormat[] formats, Class enumClass) {
        this.enumClass = enumClass;
        Map<Class, GenericFormat> keepOnlyOnePerEnumClass = new HashMap<Class, GenericFormat>();
        for (GenericFormat format : formats) {
            if (format.getClass().isEnum()) {
                keepOnlyOnePerEnumClass.put(format.getClass(), format);
            } else {
                throw new IllegalArgumentException("SupportedFormats must be an enum implementation.");
            }
        }
        this.externalFormats = keepOnlyOnePerEnumClass.values();
    }

    public Class getEnumClass(){
        return this.enumClass;
    }
    public Collection<GenericFormat> getSupportedFormatFields() {
        return externalFormats;
    }


    public static List<ExternalFormats> genExternalFormatList(){
        List<ExternalFormats> out = new ArrayList<ExternalFormats>();

        for (ExternalFormats f : ExternalFormats.values()){
            out.add(f) ;
        }
        return out;
    }

}
