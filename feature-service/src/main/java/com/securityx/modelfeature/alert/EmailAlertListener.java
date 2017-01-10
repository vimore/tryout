package com.securityx.modelfeature.alert;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.common.ThresholdDefinition;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.common.inputs.AlertDestination;
import com.securityx.modelfeature.config.AlertConfiguration;
import com.securityx.modelfeature.utils.CEF;
import com.securityx.modelfeature.utils.EmailSender;
import com.securityx.modelfeature.utils.MathUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import javax.mail.Transport;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAccumulator;

public class EmailAlertListener implements AlertListener{

    private final Logger LOGGER = LoggerFactory.getLogger(EmailAlertListener.class);
    private AlertDestination alertDestination;
    private AlertConfiguration.EmailSenderConfiguration emailSenderConfiguration;
    private AlertAuditLogger alertLogger;
    private final String EMAIL_SUBJECT = "E8 Entity Analytics Alert: Suspicious activity";

    public EmailAlertListener(AlertDestination alertDestination,
                              AlertConfiguration.EmailSenderConfiguration emailSenderConfiguration, AlertAuditLogger alertLogger) {
        this.alertDestination = alertDestination;
        this.emailSenderConfiguration = emailSenderConfiguration;
        this.alertLogger = alertLogger;
    }

    @Override
    public void sendAlert(Object obj)  throws Exception{
        LOGGER.info("SENDING EMAIL...");
        if (obj instanceof AlertNotificationMessage) {
            AlertNotificationMessage notificationMessage = (AlertNotificationMessage) obj;
            List<CEF> cefs = notificationMessage.getCefs();
            AlertDefinition alert = notificationMessage.getAlert();
            try {
                sendEmail(alert, cefs);
            } catch (Exception e) {
                LOGGER.error("Error sending alert via email => " + e);
                throw new RuntimeException("Error sending alert via email => " + e);
            }
        }

    }

    @Override
    public int getListenerId() {
        return alertDestination.getAlertDestinationId();
    }

    public String toString(){
        return Objects.toStringHelper(this)
                .add("name", alertDestination.getAlertDestinationName())
                .add("id", alertDestination.getAlertDestinationId())
                .add("hostname", alertDestination.getHostName())
                .add("port", alertDestination.getPort())
                .toString();
    }

    /**
     * Uses Velocity-template to form the email message body and sends the alert via email
     * @param cefs List of CEF
     *
     * @return String email message body
     */
    public void sendEmail(AlertDefinition alert, List<CEF> cefs) throws Exception {

        /*  organize our data  */
        List<Map<String, String>> entityList = Lists.newArrayList();
        for (CEF cef : cefs) {
            populateListOfAlerts(cef, entityList);
        }
        sortListOfAlerts(entityList);

        //check if we need to send one email per entity
        if (emailSenderConfiguration.isOneEmailPerEntity()) {
            Transport t = null;
            try {
                Session session = EmailSender.getSession(alertDestination.getHostName(), alertDestination.getPort(),
                        alertDestination.getAuthUserName(), alertDestination.getAuthPassword());
                t = EmailSender.getTransport(session,
                        alertDestination.getHostName(), alertDestination.getAuthUserName(), alertDestination.getAuthPassword());
                for(Map<String,String> entityMap : entityList){
                    List<Map<String, String>> list = Lists.newArrayList();
                    list.add(entityMap);
                    String message = getEmailMessageFromTemplate(list, alert);
                    EmailSender.sendEmail(session, t, message, EMAIL_SUBJECT,
                            alertDestination.getEmailFrom(), alertDestination.getEmailTo(),
                            alertDestination.getEmailCc(), alertDestination.getEmailBcc());
                    LOGGER.info("Email Sent for entity => " + entityMap.toString());
                }
            }catch(Exception e){
                throw  new RuntimeException(e);
            }finally {
                EmailSender.closeTransport(t);
            }
        } else {
            String message = getEmailMessageFromTemplate(entityList, alert);
            //ToDO: make subject configurable
            EmailSender.sendEmail(message, EMAIL_SUBJECT, alertDestination.getHostName(),
                    alertDestination.getPort(),
                    alertDestination.getAuthUserName(), alertDestination.getAuthPassword(),
                    alertDestination.getEmailFrom(), alertDestination.getEmailTo(),
                    alertDestination.getEmailCc(), alertDestination.getEmailBcc());
            LOGGER.info("Email sent for all entities");

        }
    }

    public void sendEmail(String message) throws Exception {
        EmailSender.sendEmail(message, EMAIL_SUBJECT, alertDestination.getHostName(),
                alertDestination.getPort(),
                alertDestination.getAuthUserName(), alertDestination.getAuthPassword(),
                alertDestination.getEmailFrom(), alertDestination.getEmailTo(),
                alertDestination.getEmailCc(), alertDestination.getEmailBcc());
    }

    /**
     * Sorts the list of alerts in descending order of Risk. High risk first and low risk last
     * @param list
     * @return
     */
    public List<Map<String, String>> sortListOfAlerts(List<Map<String, String>> list){
        Collections.sort(list, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                double x = Double.valueOf(o1.get("risk"));
                double y = Double.valueOf(o2.get("risk"));
                return (x > y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
        return list;
    }
    /**
     * This method parses the CEF and
     * @param cef
     * @param list
     */
    public void populateListOfAlerts(CEF cef, List<Map<String, String>> list){
        Map<String, String> map = cef.getExtension().getExtensionFields();
        Map<String, String> emailMap = Maps.newLinkedHashMap();
        if(map.get("dateTime") != null) {
            emailMap.put("dateTime", map.get("dateTime"));
        }

        String ipAddress = map.get("ipAddress") != null? map.get("ipAddress") : "";
        emailMap.put("ipAddress", ipAddress);

        String macAddress = map.get("macAddress") != null? map.get("macAddress") : "";
        emailMap.put("macAddress", macAddress);

        String hostName = map.get("hostName") != null? map.get("hostName") : "";
        emailMap.put("hostName", hostName);

        String userName = map.get("userName") != null? map.get("userName") : "";
        emailMap.put("userName", userName);

        String behavior = cef.getName();
        emailMap.put("behavior", behavior);

        Double risk = null;
        try {
            risk = MathUtils.formatDecimal(cef.getSeverity() * 10.0);
        } catch (Exception e) {
            LOGGER.error("Error rouding risk score => " + e);
        }
        emailMap.put("risk", risk.toString());

        //add to the list
        list.add(emailMap);
    }

    /**
     * Forms the message body for sending the AlertDefinition. It uses the email.vm template in the api-config
     * @param alertList list of rows(entities) that will be a part of 1 email to be sent via SMTP
     * @param alert AlertDefinition
     *
     * @return String body of the email
     */
    public String getEmailMessageFromTemplate(List<Map<String, String>> alertList, AlertDefinition alert){
        /*  first, get and initialize the Velocity engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        ve.setProperty("classpath.resource.loader.class", FileResourceLoader.class.getName());

        ve.init();

        //logging the number of entities that will be sent out in the email
        LOGGER.debug("Number of entities(rows) to be sent in the email alert: " + alertList.size());

        /*  add that list to a VelocityContext  */
        VelocityContext context = new VelocityContext();
        context.put("entityInfoList", alertList);
        context.put("ip", "IP Address");
        context.put("userName", "Username");
        context.put("hostName", "Hostname");
        context.put("behavior", "Behavior");
        context.put("risk", "Risk");

        //adding $riskScore
        List<ThresholdDefinition> thresholds = alert.getThreshold();
        for(ThresholdDefinition threshold : thresholds){
            if(threshold.getField().equalsIgnoreCase("risk")){
                context.put("riskScore", Double.parseDouble(threshold.getValues().get(0).toString()) * 10);
            }
        }

        /*  get the Template  */
        Template t = ve.getTemplate(emailSenderConfiguration.getEmailTemplateFilePath());

        /*  now render the template into a Writer  */
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

         /* use the output in your email body */
        return writer.toString();
    }

}
