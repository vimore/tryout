package com.securityx.health.agent.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Event {
    private HashMap<String, String> table  = new HashMap<>();
    private StringBuffer format = new StringBuffer();
    public Event(HashMap<String, String> data){
        table.putAll(data);
        for(int i=0;i<data.entrySet().size(); i++){
            if(i>0){
                format.append(",");
            }
            format.append("%s");
        }
    }
    public Set<String> getHeaderLabels(){
        return table.keySet();
    }
    public String getLineFormat(){
        return format.toString();
    }
    public Collection<String> getValues(){
        return table.values();
    }
}
