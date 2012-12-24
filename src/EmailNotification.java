
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mathias Olsson, Hanna Persson, Zakir Hossain In here we send email to
 * the admin, when an alarm has accured
 *
 */
public class EmailNotification {

    String to = "flightreservationsystemhkr@gmail.com";
    String from = "flightreservationsystemhkr@gmail.com";
    String host = "localhost";
    String password = "frs@mark&li";
    //Admins email, and senders email ae the same since it doesnt really matter

    public void send() throws AddressException, MessagingException {

        try {
            Properties props = new Properties();
            props.setProperty("mail.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");

            InternetAddress fromAddress = new InternetAddress(from);
            InternetAddress ToAddress = new InternetAddress(to);

            Authenticator auth = new EmailNotification.SMTPAuthenticator(to, password);

            Session session = Session.getInstance(props, auth);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(fromAddress);
            message.setRecipient(RecipientType.TO,
                    ToAddress);
            message.setSubject("Alarm");
            message.setText("Dear admin,"
                    + "\n\n The alarm started and it can mean many things but you should be worried");
            Transport.send(message);

        } catch (AuthenticationFailedException ex) {
            ex.printStackTrace();
        } catch (AddressException aex) {
            aex.printStackTrace();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

//    public static void main(String args[]) {
//        EmailNotification se = new EmailNotification();
//        try {
//            se.send();
//        } catch (AddressException ex) {
//            Logger.getLogger(EmailNotification.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MessagingException ex) {
//            Logger.getLogger(EmailNotification.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private class SMTPAuthenticator extends Authenticator {

        private PasswordAuthentication authentication;

        public SMTPAuthenticator(String login, String password) {
            authentication = new PasswordAuthentication(login, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }
}
