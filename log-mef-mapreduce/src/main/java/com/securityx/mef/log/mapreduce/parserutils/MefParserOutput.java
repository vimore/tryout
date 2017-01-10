package com.securityx.mef.log.mapreduce.parserutils;

import org.apache.avro.specific.SpecificRecord;

public class MefParserOutput {
    private final CharSequence format;
    private final SpecificRecord value;

    public CharSequence getFormat() {
        return format;
    }

    public SpecificRecord getValue() {
        return value;
    }

    public MefParserOutput(CharSequence format, SpecificRecord value){
        this.format = format;
        this.value = value;
    }
}
