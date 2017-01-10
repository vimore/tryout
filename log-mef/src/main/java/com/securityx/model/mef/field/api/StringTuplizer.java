
package com.securityx.model.mef.field.api;

import java.util.ArrayList;
import java.util.List;

public class StringTuplizer implements InputTuplizer<String> {

    @Override
    public List<String> tuplize(List<Object> inputValues) {
        List<String> tuples = new ArrayList<String>();
        for(Object i:inputValues) {
            if (i != null) {
                if (i instanceof String) {
                    tuples.add((String)i);
                } else {
                    tuples.add(i.toString());
                }
            }
        }
        return tuples;
    }
}
