package com.securityx.modelfeature.alert;

import com.google.common.base.Objects;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.utils.CEF;
import org.productivity.java.syslog4j.*;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SplunkAlertListener extends SyslogAlertListener {

    private final Logger LOGGER = LoggerFactory.getLogger(SplunkAlertListener.class);

    private String hostName;
    private int port;
    private int listenerId;
    private String transportProtocol;
    private AlertAuditLogger alertLogger;

    public SplunkAlertListener(int listenerId, String hostName, int port, String transportProtocol, AlertAuditLogger alertLogger) {
        super("splunk", transportProtocol, hostName, port);
        this.hostName = hostName;
        this.port = port;
        this.listenerId = listenerId;
        this.transportProtocol = transportProtocol;
        this.alertLogger = alertLogger;
        LOGGER.info("SplunkAlertListener initialized");

    }

    @Override
    public void sendAlert(Object obj) throws  Exception{
        LOGGER.info("Sending to Splunk...");

        if (obj instanceof AlertNotificationMessage) {
            AlertNotificationMessage notificationMessage = (AlertNotificationMessage) obj;
            List<CEF> cefs = notificationMessage.getCefs();
            if(cefs == null)
                return;

            
            try {
                for(CEF cef : cefs) {
                    String message = CEF.getLogsForArcSight(cef);
                    if(message != null) {
                        syslogSender.info(message);
                        LOGGER.debug("CEF logged to Splunk("+this.transportProtocol+" "+this.hostName+":"+this.port+") => " + message);
                        LOGGER.debug("CEF: "+message);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while logging to Splunk => ", e);
                throw new RuntimeException("Error occurred while logging to Splunk => " + e);
            }
            syslogSender.flush();

        }
    }

    @Override
    public int getListenerId() {
        return listenerId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("hostName", hostName)
                .add("port", port)
                .add("transportProtocol", transportProtocol)
                .toString();
    }
}
