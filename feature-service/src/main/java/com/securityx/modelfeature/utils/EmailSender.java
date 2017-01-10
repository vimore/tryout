package com.securityx.modelfeature.utils;



import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class EmailSender {

    public static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    public static void sendEmail(String messageBody, String subject, String hostName, int port,
                                 String userName, String password,
                                 List<String> mailFrom, List<String> mailTo, List<String> mailCc, List<String> mailBcc) throws Exception {
        Transport t = null;
        try {
            Session session = getSession(hostName, port, userName, password);
            t = getTransport(session, hostName, userName, password);
            LOGGER.debug("Mail being sent from: " + mailFrom);
            LOGGER.debug("To Recipients: " + mailTo);
            LOGGER.debug("Cc Recipients: " + mailCc);
            LOGGER.debug("Bcc Recipients: " + mailBcc);
            LOGGER.debug("Sending email");
            sendEmail(session, t, messageBody, subject, mailFrom, mailTo, mailCc, mailBcc);
            LOGGER.debug("Message sent successfully....");
        } catch (Exception e) {
            throw new RuntimeException("Exception Occurred while send the email => " + e);
        } finally {
            t.close();
        }
    }

    public static void sendEmail(Session session, Transport t,String messageBody, String subject,
                             List<String> mailFrom, List<String> mailTo, List<String> mailCc, List<String> mailBcc ) throws Exception {
        Message message = getMimeMessage(session, messageBody, subject, mailFrom, mailTo, mailCc, mailBcc);
        t.sendMessage(message, message.getAllRecipients());
    }

    public static Transport getTransport(Session session, String hostName, String userName, String password) throws Exception {
        SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
        //Password needs to be decrypted
        Optional<String> decryptedPassword = EncryptionUtil.decrypt(password);
        if (!decryptedPassword.isPresent()) {
            //logging null password.
            LOGGER.error("Null password");
        } else {
            password = decryptedPassword.get();
        }
        try {
            //hostName, userName, password
            t.connect(userName, password);
        } catch (MessagingException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                try {
                    // retry on SocketTimeoutException
                    t.connect(hostName, userName, password);
                    LOGGER.info("Email retry on SocketTimeoutException succeeded");
                } catch (MessagingException me) {
                    LOGGER.error("Email retry on SocketTimeoutException failed", me);
                    throw me;
                }
            } else {
                LOGGER.error("Encountered issue while connecting to email server", e);
                throw e;
            }
        }
        return  t;
    }

    public static Message getMimeMessage(Session session, String messageBody, String subject,
                                          List<String> mailFrom, List<String> mailTo, List<String> mailCc, List<String> mailBcc) throws Exception {
        Message message = new MimeMessage(session);
        for(String fromEmail : mailFrom) {
            InternetAddress from = new InternetAddress(fromEmail, false);
            message.setFrom(from);
        }

        addRecipients(message, mailTo, Message.RecipientType.TO);
        addRecipients(message, mailBcc, Message.RecipientType.BCC);
        addRecipients(message, mailCc, Message.RecipientType.CC);

        message.setSubject(subject);
        message.setContent(messageBody, "text/html");
        return  message;
    }

    public static void closeTransport(Transport t){
        try {
            t.close();
        } catch (MessagingException e) {
            LOGGER.error("Failed to close connection => "  + e);
        }

    }
    private static void addRecipients(Message message, List<String> recipients, Message.RecipientType recipientType){
        try{
            for(String emailAddress : recipients) {
                message.addRecipient(recipientType, new InternetAddress(emailAddress, false));
            }
        }catch(Exception e){
            throw  new RuntimeException(e);
        }
    }


    public static Session getSession(String hostName, int port,
                                 String userName, String password) {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");

        LOGGER.debug("mail.smtp.host: " + hostName);
        properties.put("mail.smtp.host", hostName);   //hostName

        LOGGER.debug("mail.smtp.ssl.trust: " + hostName);
        properties.put("mail.smtp.ssl.trust", hostName);

        LOGGER.debug("mail.smtp.port: " + port);
        properties.put("mail.smtp.port", port);              //port

        LOGGER.debug("Username used: " + userName);
        properties.put("mail.user", userName);    //userName

        //Password needs to be decrypted
        Optional<String> decryptedPassword = EncryptionUtil.decrypt(password);
        if (!decryptedPassword.isPresent()) {
            //logging null password.
            LOGGER.error("Null password");
        } else {
            password = decryptedPassword.get();
        }
        properties.put("mail.password", password);    //user password
        properties.put("mail." + "smtp" + ".timeout", 1000000);
        return Session.getInstance(properties, null);
    }

}
