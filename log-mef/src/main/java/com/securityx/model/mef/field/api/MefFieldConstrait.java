package com.securityx.model.mef.field.api;

import com.google.common.collect.ListMultimap;

import java.util.Map;

public interface MefFieldConstrait<IN> {

    InputTuplizer<IN> getInputTuplizer();

    Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, IN value,
            ListMultimap<String, Object> context);
}
