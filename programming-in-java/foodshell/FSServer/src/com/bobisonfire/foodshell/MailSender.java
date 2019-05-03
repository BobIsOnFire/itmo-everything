package com.bobisonfire.foodshell;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class MailSender {
    private final static String PROPERTIES_PATH = "mail.properties";
    private Session mailSession;
    private final String sender;

    public MailSender() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(PROPERTIES_PATH))) {
            props.load(in);
        } catch (IOException exc) {
            System.out.println("Cannot read mail properties.");
        }

        this.sender = props.getProperty("mail.smtp.username");
        mailSession = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, props.getProperty("mail.smtp.password"));
            }
        });

    }

    public void sendMessage(String to, String subject, String text) {
        try {
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException exc) {
            System.out.println("Cannot send message.");
        }
    }
}
