package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefInetHostNameConstraint implements MefFieldConstrait<String> {
    private Logger logger = LoggerFactory.getLogger(MefInetHostNameConstraint.class);

    @Override
    public InputTuplizer<String> getInputTuplizer() {
        return new StringTuplizer();
    }

    @Override
    public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
                                                 ListMultimap<String, Object> context) {
        if (logger.isDebugEnabled())
            logger.debug("MefInetHostNameConstraint :" + field.getPrettyName() + " : " + value);
        Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
        if (! value.contains(".") && value.matches("^[a-zA-Z0-9][\\w-]+")){
            results.put(field, value);
        }else{
            if (logger.isDebugEnabled())
                logger.error("Invalid hostname : "+value);
        }
        return results;
    }


}
