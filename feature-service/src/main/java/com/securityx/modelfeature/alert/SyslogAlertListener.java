package com.securityx.modelfeature.alert;

import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SyslogAlertListener implements AlertListener {
    protected SyslogIF syslogSender;
    private final Logger LOGGER = LoggerFactory.getLogger(SyslogAlertListener.class);


    public   SyslogAlertListener(String id, String transportProtocol, String hostName, int port){
        SyslogConfigIF syslogConfig = null;
        LOGGER.info("SyslogAlertListener initialing : "+transportProtocol+"://"+hostName+":"+port);

        if (SyslogConstants.UDP.equals(transportProtocol.toLowerCase())){
            LOGGER.info("SyslogAlertListener initialing UDP config:");
            UDPNetSyslogConfig udpConfig = new UDPNetSyslogConfig();
            syslogConfig = udpConfig;

        }   else if (SyslogConstants.TCP.equals(transportProtocol.toLowerCase() )){
            LOGGER.info("SyslogAlertListener initialing TCP config:");
            TCPNetSyslogConfig tcpConfig = new TCPNetSyslogConfig();
            tcpConfig.setSoLingerSeconds(10);
            tcpConfig.setSoLinger(true);
            tcpConfig.setMaxShutdownWait(10);
            tcpConfig.setWriteRetries(1);
            syslogConfig = tcpConfig;

        }
        if (syslogConfig != null) {
            try {
                Class syslogClass = syslogConfig.getSyslogClass();

                SyslogIF syslog = null;
                try {
                    syslog = (SyslogIF) syslogClass.newInstance();
                } catch (InstantiationException e) {
                    LOGGER.error("InstantiationException raised while creating syslog instance ", e);
                } catch (IllegalAccessException e) {
                    LOGGER.error("IllegalAccessException raised while creating syslog instance ", e);
                }
                syslog.initialize(id, syslogConfig);
                //SyslogIF instance = Syslog.createInstance("customUdp", config);
                syslog.getConfig().setThrowExceptionOnInitialize(true);
                syslog.getConfig().setThrowExceptionOnWrite(true);
                syslog.getConfig().setHost(hostName);
                syslog.getConfig().setPort(514);
                syslogSender = syslog;
            } catch (SyslogRuntimeException e) {
                LOGGER.error("exception raised while creating syslog instance ", e);
            }
        }else{
            throw new IllegalArgumentException("unable to init config from parameters currently supported protocol : UDP or TCP") ;
        }
    }
}
