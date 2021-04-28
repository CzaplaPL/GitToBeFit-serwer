package pl.umk.mat.git2befit.user.service.messaging.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;


public class EmailMessageFacade {
    private final static String HOST_NAME = "smtp.gmail.com";
    private final static DefaultAuthenticator AUTHENTICATOR = new DefaultAuthenticator("gittobefit","ds_6304/2");
    private final static String SERVER_EMAIL = "gittobefit@gmail.com";
    private final static int SMTP_PORT = 465;

    private final Email email;

    public EmailMessageFacade(String subject, String message, String address) throws EmailException {
        this.email = new SimpleEmail();
        this.email.setSubject(subject);
        this.email.setMsg(message);
        this.email.addTo(address);

        this.email.setHostName(HOST_NAME);
        this.email.setSmtpPort(SMTP_PORT);
        this.email.setAuthenticator(AUTHENTICATOR);
        this.email.setSSLOnConnect(true);
        this.email.setFrom(SERVER_EMAIL);
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
