
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/*
 **********************************************************************************************
 * Authors : Zakir Hossain, Mathias Olsson, Hanna Persson, 
 * 
 * Class : DeviceThread 				
 * 							
 * ********************************************************************************************
 */
public class DeviceThread extends Thread {

    Socket deviceSocket = null;
    BufferedReader buffer = null;
    InputStreamReader input = null;
    MultiThreadedServer multi = null;
    readOrWriteFromFile r = new readOrWriteFromFile();

    public DeviceThread(Socket deviceSocket, MultiThreadedServer mts) {
        this.multi = mts;
        this.deviceSocket = deviceSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Device threaded created");
            while (true) {
                input = new InputStreamReader(deviceSocket.getInputStream());
                buffer = new BufferedReader(input);
//                multi = new MultiThreadedServer();

                //Here we listen for the Alaram message from the device machine
                String alarm = buffer.readLine();
                System.out.println("******Alarm******" + alarm);
                sendEmail send = new sendEmail();
                send.sending();
                r.writeToFile("House", "alarm");
                multi.sendToAllServerThreads(alarm);
            }
        } catch (AddressException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
