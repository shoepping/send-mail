package org.shoepping.sendgrid;

// using SendGrid's Java Library
// https://github.com/sendgrid/sendgrid-java
// for inspiration
// https://github.com/sendgrid/sendgrid-java/blob/master/examples/helpers/mail/Example.java
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendMail {

    private static final Logger LOGGER = Logger.getLogger(SendMail.class.getName());
    private static SendGrid SEND_GRID;

    // "from_address" "recipient_comma_separated" "subject" "message" "send_grid_api_key"
    public static void main(String[] args) {
        if(args == null || args.length < 5 ) {
            LOGGER.info("Usage: from-email recipient-email subject message send-grid-api-key");
            System.exit(0);
        }
        String from = args[0];
        Collection<String> recipientList = new ArrayList<>(Arrays.asList(args[1].split(",")));
        String subject = args[2];
        String message = args[3];
        String apiKey = args[4];
        SEND_GRID = new SendGrid(apiKey);
        // SendMail.sendMail(from, subject, recipientList,  message, apiKey);
        SendMail.sendMailOneByOne(from, subject, recipientList, message);
    }

    public static void sendMail(String fromString, String subject,
                                Collection<String> recipientList,
                                String message, String apiKey) {

        String toString = recipientList.iterator().next();
        LOGGER.log(Level.INFO,"from: {0}, \nsubject: {1}, \nto: {2}, \nrecipientList: {3}, \nmessage: {4}",
                new Object[] {fromString, subject, toString, recipientList, message});
        Email from = new Email(fromString);
        Email to = new Email(toString);
        recipientList.remove(toString);
        Content content = new Content("text/plain", message);
        Mail mail = new Mail(from, subject, to, content);
        for(String toEmail : recipientList) {
            LOGGER.log(Level.INFO, "additional recipient: {0}", toEmail);
            mail.getPersonalization().get(0).addTo(new Email(toEmail));
        }

        sendMail(mail);
    }

    public static void sendMailOneByOne(String fromString, String subject,
                                Collection<String> recipientList,
                                String message) {
        LOGGER.log(Level.INFO,"from: {0}, \nsubject: {1}, \nrecipientList: {3}, \nmessage: {4}",
                new Object[] {fromString, subject, recipientList, message});
        for(String toEmail : recipientList) {
            Mail mail = buildEmail(fromString, subject, toEmail, message);
            sendMail(mail);
        }
    }


    private static Mail buildEmail(String fromString, String subject,
                                   String recipient, String message) {
        Email from = new Email(fromString);
        Email to = new Email(recipient);
        Content content = new Content("text/plain", message);
        return new Mail(from, subject, to, content);
    }

    private static void sendMail(Mail mail) {

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = SEND_GRID.api(request);
            LOGGER.log(Level.INFO, ""+response.getStatusCode());
            LOGGER.log(Level.INFO, response.getBody());
            LOGGER.log(Level.INFO, response.getHeaders().toString());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,"Error encountered while sending email", ex);
        }
    }
}
