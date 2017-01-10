package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.logcollection.utils.IpUtils;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * constraint diving into Service name to get relevant information by splitting the field into subpart
 *
 * @see Service Principal Names : https://technet.microsoft.com/en-us/library/cc961723.aspx
 */
public class IAMMefServiceNameConstraint implements MefFieldConstrait<String> {
    private final static Pattern firstdnsletter = Pattern.compile(
            "^[a-z0-9]", Pattern.CASE_INSENSITIVE);
    private Logger logger = LoggerFactory.getLogger(IAMMefServiceNameConstraint.class);

    public InputTuplizer<String> getInputTuplizer() {
        return new StringTuplizer();
    }

    public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
                                                 ListMultimap<String, Object> context) {
        Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
        if (logger.isDebugEnabled())
            logger.debug("IAMMefServiceNameConstraint :" + field.getPrettyName() + " : " + value);
        //user@domain, some other as
        //host$
        //service/host ( sometime service/host/domain)
        String[] parts = value.split("/");
        if (parts.length > 1) {
            results.put(IAMMefField.destinationProcessName, parts[0]);
            if (parts.length >= 2) {
                if (IpUtils.isFqdn(parts[1]))
                    results.put(IAMMefField.destinationNameOrIp, parts[1]);
            }
        } else {
            // either a host or a user
            if (value.endsWith("$")) {
                //host
                results.put(IAMMefField.destinationNameOrIp, value);
            } else {
                parts = value.split("@");
                if (parts.length == 2) {
                    results.put(IAMMefField.destinationUserName, value);
                }
            }

        }

        results.put(field, String.valueOf(value));
        return results;
    }


}
