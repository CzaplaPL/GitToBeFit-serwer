package pl.umk.mat.git2befit.messaging.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * The {@code EmailMessage} class allow sending emails.
 *
 * @implNote To create {@code EmailMessage} object you have to use Builder class.
 * @author Kacper
 */
public class EmailMessage {
    private Email email;

    /**
     * Builder class allows to create new EmailMessage object.
     * You have to execute all subject, message and address methods.
     */
    public static final class Builder {
        private String subject;
        private String message;
        private String address;

        private final static String HOST_NAME = "smtp.gmail.com";
        private final static DefaultAuthenticator AUTHENTICATOR = new DefaultAuthenticator("gittobefit",
                                                                                            "ds_6304/2");
        private final static String SERVER_EMAIL = "gittobefit@gmail.com";
        private final static int SMTP_PORT = 465;

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public EmailMessage build() throws EmailException {
            if (subject == null || message == null || address == null)
                throw new IllegalCallerException("All methods were not called");

            Email tmp = new SimpleEmail();
            tmp.setSubject(this.subject);
            tmp.setMsg(this.message);
            tmp.addTo(this.address);

            tmp.setHostName(HOST_NAME);
            tmp.setSmtpPort(SMTP_PORT);
            tmp.setAuthenticator(AUTHENTICATOR);
            tmp.setSSLOnConnect(true);
            tmp.setFrom(SERVER_EMAIL);
            

            EmailMessage msg = new EmailMessage();
            msg.email = tmp;
            return msg;
        }
    }

    /**
     * Send initialized email.
     * @throws EmailException If message could not be sent, {@code EmailException} will be thrown.
     */
    public void sendEmail() throws EmailException {
        if (email != null) {
            email.send();
        } else {
            throw new IllegalStateException("Not initalized email can't be sent");
        }
    }
}
