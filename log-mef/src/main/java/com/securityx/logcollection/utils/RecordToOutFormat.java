package com.securityx.logcollection.utils;

import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.kitesdk.morphline.api.Record;

public interface RecordToOutFormat {
    public Record keepFormat(Record record);
}
