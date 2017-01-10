package com.securityx.modelfeature.alert;

import com.securityx.modelfeature.utils.CEF;

import java.util.List;

public interface AlertListener {

    public abstract void sendAlert(Object object) throws Exception;

    public int getListenerId();
}
