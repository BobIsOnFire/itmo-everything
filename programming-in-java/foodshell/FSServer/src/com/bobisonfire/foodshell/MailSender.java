package com.bobisonfire.foodshell;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

class MailSender {
    private final static String PROPERTIES_PATH = "mail.properties";
    private Session mailSession;
    private final String sender;

    MailSender() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(PROPERTIES_PATH))) {
            props.load(in);
        } catch (IOException exc) {
            System.out.println("Cannot read mail properties.");
        }
        this.sender = props.getProperty("mail.smtp.username");

        if (!ServerMain.debug && !ServerMain.mailWorking)
            return;
        mailSession = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, props.getProperty("mail.smtp.password"));
            }
        });

    }

    void sendMessage(String to, String subject, String text) {
        if (!ServerMain.debug && !ServerMain.mailWorking) {
            System.out.println("Похоже, что вы попросили не отправлять ничего с помощью MailAPI.\n" +
                    "Вот сообщение, которое должно было быть отправлено:\n" +
                    "Отправитель: "+ sender + "\nПолучатель: " + to + "\nТема: " + subject + "\n" + text);
            return;
        }

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
