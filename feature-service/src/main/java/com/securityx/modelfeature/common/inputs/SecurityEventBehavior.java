package com.securityx.modelfeature.common.inputs;

import java.util.List;

public class SecurityEventBehavior {
    private int modelId;
    private List<Integer> securityEventId;

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public List<Integer> getSecurityEventId() {
        return securityEventId;
    }

    public void setSecurityEventId(List<Integer> securityEventId) {
        this.securityEventId = securityEventId;
    }

    public static boolean showsBehavior(List<SecurityEventBehavior> securityEventBehaviorList, int securityEventId, int modelId){

        if(securityEventBehaviorList == null || securityEventBehaviorList.isEmpty())
        {
            //this would mean, no Behavior is specified by the user. So we consider "all" the behaviors.
            return  true;
        }

        for(SecurityEventBehavior securityEventBehavior : securityEventBehaviorList){
            if(securityEventBehavior.getModelId() == modelId && securityEventBehavior.getSecurityEventId().contains(securityEventId))
                return true;
        }
        return false;
    }
}