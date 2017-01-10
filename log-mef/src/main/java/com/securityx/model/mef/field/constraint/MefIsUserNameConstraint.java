package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;

import java.util.HashMap;
import java.util.Map;

public class MefIsUserNameConstraint implements MefFieldConstrait<String> {

    public static int USERNAMELENGTH = 1023;

    @Override
    public InputTuplizer<String> getInputTuplizer() {
        return new StringTuplizer();
    }

    @Override
    public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value, ListMultimap<String, Object> context) {
        // respects CEF source or destinationUserName max length
        Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
        if (value.length() < MefIsUserNameConstraint.USERNAMELENGTH) {
            results.put(field, value);
        } else {
            results.put(field, value.substring(0, USERNAMELENGTH));
        }
        return results;
    }
}
