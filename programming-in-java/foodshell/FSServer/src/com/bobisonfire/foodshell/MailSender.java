package com.bobisonfire.foodshell;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Класс, инкапсулирующий логику работы с почтовыми сервисами.<br>
 * Использует классы из Java MailAPI - в classpath или manifest-файле
 * необходимо указать путь к данной библиотеке.<br>
 * Также использует файл со свойствами (mail.properties), который должен
 * содержать следующие свойства:
 * 1. mail.transport.protocol - заданный транспортный протокол для передачи данных<br>
 * 2. mail.host - имя почтового сервера<br>
 * 3. mail.smtp.port - порт почтового сервера<br>
 * 4. Свойства, используемые непосредственно JavaMail:<br>
 * - mail.smtp.socketFactory.class<br>
 * - mail.smtp.socketFactory.port<br>
 * - mail.smtp.socketFactory.fallback<br>
 * 5. mail.smtp.auth - true, если почтовый сервер запрашивает авторизацию пользователей.<br>
 * В таком случае необходимы еще два свойства:<br>
 * - mail.smtp.username - почтовый адрес<br>
 * - mail.smtp.password - пароль<br>
 * 6. mail.debug - true, если MailAPI должен быть запущен в режиме отладки.
 */
class MailSender {
    private final static String PROPERTIES_PATH = "/home/s264443/prog/lab7/mail.properties";
    private Session mailSession;
    private final String sender;

    /**
     * Конструктор, устанавливающий соединение по SMTP относительно заданных свойств.
     */
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

    /**
     * Метод, отправляющий сообщение по почте от указанного в свойствах отправителя.
     * @param to Получатель
     * @param subject Тема
     * @param text Текст сообщения
     */
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
            ServerMain.logException(exc);
        }
    }
}
