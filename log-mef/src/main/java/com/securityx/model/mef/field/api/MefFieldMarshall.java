package com.securityx.model.mef.field.api;

import java.nio.ByteBuffer;
import java.util.List;

public interface MefFieldMarshall {

    List<ByteBuffer> marshallToBytes(List<Object> values);
    List<Object> marshallFromBytes(List<ByteBuffer> valuesAsBytes);
}
