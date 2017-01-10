package com.securityx.model.mef.morphline.command.kvp;

import com.securityx.model.external.ExternalFormats;
import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.*;

/**
 * Split key value pairs set 
 * cefKvpSplitter  {
 *     inputFieldName : "message" # optional Defaults to "message"
 *     trim : true, # optional. Calls String trim on each value. Default is 'true'
 * }
 */
public class CefKeyValuePairsCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("cefKvpSplitter");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        String inputFieldName = config.getString("inputFieldName");
        if (inputFieldName == null) {
            inputFieldName = ",";
        }

        String fieldHeaderPattern;
        if (config.hasPath("fieldHeaderPattern")) {
            fieldHeaderPattern= config.getString("fieldHeaderPattern");
        }else{
            fieldHeaderPattern = "(?:\\s+|^|#011|\\|)";
        }

        boolean trim = true;
        if (config.hasPath("trim")) {
            trim = config.getBoolean("trim");
        }
        List<String> formats = null;
        if (config.hasPath("formatKeySet")){
            formats = config.getStringList("formatKeySet");
        }
        Set<String> fields = new HashSet<String>();
        if (formats != null){
            for (String format : formats){
                try{
                    SupportedFormats f = SupportedFormats.valueOf(format);
                    for (SupportedFormat field : f.getSupportedFormatFields()){
                        SupportedFormat[] keys = field.getClass().getEnumConstants();
                        for (SupportedFormat fieldName : keys) {
                            fields.add(fieldName.getPrettyName());
                        }
                    }

                }catch(IllegalArgumentException e){
                    try{
                        ExternalFormats f = ExternalFormats.valueOf(format);
                        for (GenericFormat field : f.getSupportedFormatFields()){
                            GenericFormat[] keys = field.getClass().getEnumConstants();
                            for (GenericFormat fieldName : keys){
                                fields.add( fieldName.getPrettyName());
                            }
                        }
                    }catch (IllegalArgumentException ex){

                        throw ex;
                    }
                }
            }
        }
        List<String> keySet = null;
        if (config.hasPath("keySet")){
            config.getStringList("keySet");
        }
        if (keySet != null && keySet.size()>0){
            for (String field : keySet){
                fields.add(field) ;
            }
        }

        return new CefKeyValuePairsCommand(config,
                context,
                parent,
                child,
                inputFieldName,
                fieldHeaderPattern,
                fields,
                trim);
    }
}
