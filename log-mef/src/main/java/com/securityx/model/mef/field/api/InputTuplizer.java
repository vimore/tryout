package com.securityx.model.mef.field.api;

import java.util.List;

public interface InputTuplizer<T> {

    List<T> tuplize(List<Object> inputValues);

}
