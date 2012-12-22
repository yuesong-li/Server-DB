
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
public class DeviceThread extends Thread {

    Socket deviceSocket = null;
    BufferedReader buffer = null;
    InputStreamReader input = null;
    MultiThreadedServer multi = null;
    readOrWriteFromFile r = new readOrWriteFromFile();
    DatabaseQuery dbq = new DatabaseQuery();

    public DeviceThread(Socket deviceSocket, MultiThreadedServer mts) {
        this.multi = mts;
        this.deviceSocket = deviceSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Device threaded created");
            //cease this thread at start-up so it won't mess up with the 
            Thread.sleep(2000);
            while (true) {
                input = new InputStreamReader(deviceSocket.getInputStream());
                buffer = new BufferedReader(input);
                //Here we listen for the Alaram message from the device machine
                System.out.println("Device Thread: ready to listen");
                String msgFromDevice = buffer.readLine();
                if (msgFromDevice.contains("alarm")) {
                    System.out.println("Device Thread: Alarm " + msgFromDevice);
                    SendEmail send = new SendEmail();
                    send.sending();
                    r.writeToFile("House", "alarm");
                    
                } else {
                    String[] deviceMessageArray = msgFromDevice.split(":");
                    String device = deviceMessageArray[0];
                    String state = deviceMessageArray[1];
                    dbq.updateDataBase(device, state);
                }
                multi.sendToAllServerThreads(msgFromDevice);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AddressException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeviceThread.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
