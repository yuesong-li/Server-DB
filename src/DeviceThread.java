
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

    private final String TAG = "DEVICE_THREAD: ";
    Socket deviceSocket = null;
    BufferedReader buffer = null;
    InputStreamReader input = null;
    MultiThreadedServer mts = null;
    readOrWriteFromFile r = new readOrWriteFromFile();
    DatabaseQuery dbq = new DatabaseQuery();

    public DeviceThread(Socket deviceSocket, MultiThreadedServer mts) {
        this.mts = mts;
        this.deviceSocket = deviceSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println(TAG + "Device threaded created");
            //cease this delay at start-up so it won't mess up with the main thread
            Thread.sleep(2000);

            input = new InputStreamReader(deviceSocket.getInputStream());
            buffer = new BufferedReader(input);

            while (true) {
                System.out.println(TAG + "Ready to listen");
                String msgFromDevice = buffer.readLine();
                if (msgFromDevice.contains("alarm")) {
                    System.out.println(TAG + "Alarm -  " + msgFromDevice);
                    EmailNotification email = new EmailNotification();
                    email.send();
                    r.writeToFile("House", "alarm");
                    mts.sendToAllServerThreads(msgFromDevice);
                } else {
                    String[] deviceMessageArray = msgFromDevice.split(":");
                    String device = deviceMessageArray[0];
                    String state = deviceMessageArray[1];
                    dbq.updateDataBase(device, state);
                    String allStatus = mts.getAllState();
                    mts.sendToAllServerThreads(allStatus);
                }
                
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
