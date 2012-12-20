
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hanna
 */
public class DeviceThread extends Thread{

    Socket deviceSocket = null;
    BufferedReader buffer =null;
    InputStreamReader input = null;
    MultiThreadedServer multi =null;

    public DeviceThread(Socket deviceSocket) {
        this.deviceSocket = deviceSocket;
    }

    @Override
    public void run() {
        try { 
            input = new InputStreamReader(deviceSocket.getInputStream());
            buffer = new BufferedReader(input);
            multi = new MultiThreadedServer();
            sendEmail send = new sendEmail();
            send.sending();
            //Here we listen for the Alaram message from the device machine
            String alarm = buffer.readLine();
            multi.sendToAllServerThreads(alarm);
        } catch (AddressException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
}
