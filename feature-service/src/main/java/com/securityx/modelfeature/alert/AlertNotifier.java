package com.securityx.modelfeature.alert;

public interface AlertNotifier {

    public void register(AlertListener listener);
    public void unregister(int listenerId);

    public void notifyObservers();
}
