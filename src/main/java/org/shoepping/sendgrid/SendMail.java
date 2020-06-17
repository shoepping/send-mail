package org.shoepping.sendgrid;

// using SendGrid's Java Library
// https://github.com/sendgrid/sendgrid-java
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendMail {

    private static final Logger LOGGER = Logger.getLogger(SendMail.class.getName());

    // "noreply@shoeppingtest.at" "recipient" "subject" "message" "send_grid_api_key" "additional_recipients_semicolon_separated_string"
    public static void main(String[] args) throws IOException {

        if(args == null || args.length < 5 ) {
            LOGGER.info("Usage: from-email to-email subject message send-grid-api-key additional_recipients_semicolon_separated_string");
            System.exit(0);
        }
        String from = args[0];
        String to = args[1];
        String subject = args[2];
        String message = args[3];
        String apiKey = args[4];
        List<String> toList = new ArrayList<String>();
        if(args.length == 6 && !args[5].isEmpty()) {
            toList =  Arrays.asList(args[5].split(";"));
        }
        SendMail.sendMail(from, subject, to, toList,  message, apiKey);
    }

    public static void sendMail(String fromString, String subjectString, String toString, List<String> toList, String message, String apiKey) {
        LOGGER.log(Level.INFO,"from: {0}, \nsubject: {1}, \nto: {2}, \ntoList: {3}, \nmessage: {4}",
                new Object[] {fromString, subjectString, toString, toList, message});
        Email from = new Email(fromString);
        String subject = subjectString;
        Email to = new Email(toString);
        Content content = new Content("text/plain", message);
        Mail mail = new Mail(from, subject, to, content);
        for(String toEmail : toList) {
            LOGGER.log(Level.INFO, "adding to: {0}", toEmail);
            mail.getPersonalization().get(0).addTo(new Email(toEmail));
        }

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,"Error encountered while sending email", ex);
        }
    }
}
