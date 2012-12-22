
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
public class SendEmail {

    String to = "flightreservationsystemhkr@gmail.com";
    String from = "flightreservationsystemhkr@gmail.com";
    String host = "localhost";
    String password = "frs@mark&li";
    //Admins email, and senders email ae the same since it doesnt really matter

    public static void main(String args[]) {
        SendEmail se = new SendEmail();
        try {
            se.sending();
        } catch (AddressException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sending() throws AddressException, MessagingException {

        try {
            Properties props = new Properties();
            //props.put("mail.host", "smtp.gmail.com");
//        props.("mail.smtp.port", "465");

            props.setProperty("mail.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");


            //Session mailSession = Session.getDefaultInstance(props);


            InternetAddress fromAddress = new InternetAddress(from);
            InternetAddress ToAddress = new InternetAddress(to);


//        Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//                    @Override
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(host, password);
//                    }
//                });



            Authenticator auth = new SendEmail.SMTPAuthenticator(to, password);

            Session session = Session.getInstance(props, auth);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(fromAddress);
            message.setRecipient(RecipientType.TO,
                    ToAddress);
            message.setSubject("Alarm");
            message.setText("Dear admin,"
                    + "\n\n The alarm started and it can mean many things but you should be worried");
            System.out.println("Before sending");
            Transport.send(message);

            System.out.println("Done");
        } catch (AuthenticationFailedException ex) {
            ex.printStackTrace();
        } catch (AddressException aex) {
            aex.printStackTrace();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

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
