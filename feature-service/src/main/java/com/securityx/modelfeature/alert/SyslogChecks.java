package com.securityx.modelfeature.alert;

import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;

public class SyslogChecks {

    public void testSyslogUDP() throws IllegalAccessException, InstantiationException {

        class MyCustomSylogConfig extends UDPNetSyslogConfig {};

        SyslogConfigIF config = new MyCustomSylogConfig();
        Class syslogClass = config.getSyslogClass();

        SyslogIF syslog = (SyslogIF) syslogClass.newInstance();
        syslog.initialize("customudp", config);
        //SyslogIF instance = Syslog.createInstance("customUdp", config);
        syslog.getConfig().setHost("10.10.30.62");
        syslog.getConfig().setPort(514);


// Use Your Custom Syslog Implementation
        syslog.info("Log Message from test class");
        syslog.flush();
        //syslog.shutdown();
    }


    public void testSyslogTCP() throws IllegalAccessException, InstantiationException {

        class MyCustomSylogConfig extends TCPNetSyslogConfig {};

        SyslogConfigIF config = new MyCustomSylogConfig();
        Class syslogClass = config.getSyslogClass();

        SyslogIF syslog = (SyslogIF) syslogClass.newInstance();
        syslog.initialize("customudp", config);
        //SyslogIF instance = Syslog.createInstance("customUdp", config);
        syslog.getConfig().setHost("10.10.30.62");
        syslog.getConfig().setPort(514);


// Use Your Custom Syslog Implementation
        syslog.info("Log Message from test class");
        syslog.flush();
        //syslog.shutdown();
    }

    public void testSyslogTCPArcsight() throws IllegalAccessException, InstantiationException {

        class MyCustomSylogConfig extends TCPNetSyslogConfig {};

        MyCustomSylogConfig config = new MyCustomSylogConfig();
        Class syslogClass = config.getSyslogClass();
        config.setSoLingerSeconds(10);
        config.setSoLinger(true);
        config.setMaxShutdownWait(10);
        config.setWriteRetries(1);


        SyslogIF syslog = (SyslogIF) syslogClass.newInstance();
        syslog.initialize("arcsight", config);
        //SyslogIF instance = Syslog.createInstance("customUdp", config);
        syslog.getConfig().setHost("uvo15nuy45mzl3j6sd3.vm.cld.sr");
        syslog.getConfig().setPort(1702);
        syslog.getConfig().setThrowExceptionOnInitialize(true);
        syslog.getConfig().setThrowExceptionOnWrite(true);


// Use Your Custom Syslog Implementation
        try {
            syslog.info("CEF:0|E8|EntityAnalytics|1.3.1|Command and Control|Command and Control|4|dst=192.168.12.47 rt=1472816757376 dvchost=cluster5-e8.e8sec.com");
        }catch (SyslogRuntimeException e){
            System.err.println(e) ;
        }
        System.out.println("after sending") ;
        try{
            syslog.flush();
        }catch(SyslogRuntimeException e){
            System.err.println(e) ;
        }
        System.out.println("after flush") ;

        //syslog.shutdown();
    }
    public void testSyslogTCPSplunk() throws IllegalAccessException, InstantiationException {

        class MyCustomSylogConfig extends TCPNetSyslogConfig {};

        MyCustomSylogConfig config = new MyCustomSylogConfig();
        Class syslogClass = config.getSyslogClass();
        config.setSoLingerSeconds(10);
        config.setSoLinger(true);
        config.setMaxShutdownWait(10);
        config.setWriteRetries(1);
        SyslogIF syslog = (SyslogIF) syslogClass.newInstance();
        syslog.initialize("customudp", config);
        //SyslogIF instance = Syslog.createInstance("customUdp", config);
        syslog.getConfig().setHost("172.16.1.254");
        syslog.getConfig().setPort(20514);
        syslog.getConfig().setThrowExceptionOnInitialize(true);
        syslog.getConfig().setThrowExceptionOnWrite(true);


// Use Your Custom Syslog Implementation
        try {
            syslog.info("CEF:0|E8|EntityAnalytics|1.3.1|Command and Control|Command and Control|4|dst=192.168.12.47 rt=1472816757376 dvchost=cluster5-e8.e8sec.com");
        }catch (SyslogRuntimeException e){
            System.err.println(e) ;
        }
        System.out.println("after sending") ;
        try{
            syslog.flush();
        }catch(SyslogRuntimeException e){
            System.err.println(e) ;
        }
        System.out.println("after flush") ;

        //syslog.shutdown();
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        SyslogChecks sl = new SyslogChecks();
        System.out.println("arcsight") ;
        sl.testSyslogTCPArcsight();
        System.out.println("splunk") ;
        sl.testSyslogTCPSplunk();
        System.out.println("done") ;
        System.exit(0);

    }
}
