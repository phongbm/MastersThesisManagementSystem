package models;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailManager {

    private static final String EMAIL = "giaovu.uet@gmail.com";
    private static final String PASSWORD = "NamPhong1995";

    public MailManager() {
    }

    public void sendMail(String to, String password) {
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator(EMAIL, PASSWORD));
            email.setSSLOnConnect(true);
            email.setFrom(EMAIL, "University of Engineering and Technology - VNU Hanoi");
            email.setSubject("UET Account");
            email.setMsg("Website:\n" +
                    "Your account\n" +
                    "Email: " + to + "\n" +
                    "Password: " + password);
            email.addTo(to);
            email.send();
        } catch (EmailException emailException) {
        }
    }

}