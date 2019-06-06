package com.bobisonfire.foodshell.exchange;

import com.bobisonfire.foodshell.ServerException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class MailSender {
    private static final String PROPERTIES_PATH = "mail.properties";
    private static String MAIL_USERNAME;
    private static String MAIL_PASSWORD;

    private Session mailSession;

    public static void setCredentials(String username, String password) {
        MAIL_USERNAME = username;
        MAIL_PASSWORD = password;
    }

    MailSender() throws ServerException {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(PROPERTIES_PATH))) {
            properties.load(in);
        } catch (IOException exc) {
            throw new ServerException("Произошла ошибка чтения свойств SMTP.", exc);
        }

        this.mailSession = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
            }
        });
    }

    void sendMessage(String to, String subject, String text) {
        try {
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(MAIL_USERNAME);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text, "UTF-8");

            Transport.send(message);
        } catch (MessagingException exc) {
            System.out.println("Невозможно отправить сообщение.\n" +
                    "Вот сообщение, которое должно было быть отправлено:" +
                    "Отправитель: "+ MAIL_USERNAME + "\nПолучатель: " + to + "\nТема: " + subject + "\n" + text);
            // todo logging but different
        }
    }
}
