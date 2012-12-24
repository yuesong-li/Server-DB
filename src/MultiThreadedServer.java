
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 **********************************************************************************************
 * Authors : Mathias Olsson, Hanna Persson, Zakir Hossain
 * 
 * Class : MultiThreadedServer
 * 
 * Class functionality :     MultiThreadedServer class is the heart of the house.
 * All communication passes through here and gets sent to the
 * right place.
 *  
 * For a full overview of the class, we refer to the design documents.							
 * 							
 * ********************************************************************************************
 */
public class MultiThreadedServer extends Thread {

    /*
     * Declaration of variables used for this class.
     */
    private final String TAG = "MTS: ";
    ServerSocket deviceServerSocket = null;
    ServerSocket unitServerSocket = null;
    Socket unitClientSocket = null;
    Socket deviceClientSocket = null;
    PrintWriter deviceWriter = null;
    BufferedReader deviceReader = null;
    ArrayList<Server> threadList = null;
    DatabaseQuery dbq = new DatabaseQuery();

    public MultiThreadedServer() {
        threadList = new ArrayList<Server>();
        dbq.createDatabase();
    }

    @Override
    public void run() {
        try {
            /*
             * Following segment of code covers the creation of the server
             * socket for device.
             */
            deviceServerSocket = new ServerSocket(7777);
            System.out.print(TAG + "running device-socket on "
                    + InetAddress.getLocalHost()
                    + "\nAwaiting device-client connection.. .. ..");

        } catch (IOException e) {
            System.out.println(TAG + "Could not listen on port: 7777");
            System.exit(-1);
        }

        while (true) {
            try {
                /*
                 * Following segment of code covers event : Device connects to
                 * server. Note that this is only available, whenever there
                 * isn't a connection already made with the house.
                 */
                if (deviceClientSocket == null) {
                    deviceClientSocket = deviceServerSocket.accept();
                    System.out
                            .println("\n"+TAG + "Connection with house established. Running on : "
                            + deviceClientSocket
                            .getRemoteSocketAddress());
                    DeviceThread device = new DeviceThread(deviceClientSocket, this);
                    device.start();
                    /*
                     * Here we must accept information from the House.
                     * Devices and all of their states must be updated in the
                     * DataBase
                     */
                    createDeviceStreams();
                    receiveInitialDevices();
                    /*
                     * Following segment of code covers the creation of the
                     * server socket for units.
                     */
                    unitServerSocket = new ServerSocket(8888);
                    System.out.println(TAG + "\nServer running on "
                            + InetAddress.getLocalHost()
                            + "\nAwaiting unit-client connection.. .. ..");

                } /*
                 * Following segment of code covers event : Unit connects to
                 * server. Note that this is only made available whenever a
                 * connection has been made with the house.
                 */ else {
                    unitClientSocket = unitServerSocket.accept();
                    System.out.println(TAG + "Client accepted. Client : "
                            + unitClientSocket.getRemoteSocketAddress());

                    String devicesStatus = getAllState();
                    Server server = new Server(unitClientSocket, this, devicesStatus);
                    server.start();
                    threadList.add(server);
                }
            } catch (IOException e) {
                System.out.println(TAG + "Accept failed: 1234");
                System.exit(-1);
            }
        }

    }

    /*
     * 
     * Receiving the information on the current devices from the device-client.
     * Also overwrites the database with the newest information/states.
     */
    private void receiveInitialDevices() {
        try {
            String deviceMessage, device, state;
            deviceMessage = deviceReader.readLine();
            System.out.println(TAG + "received from device : " + deviceMessage);
            String[] allDevicesStatus = null;
            allDevicesStatus = deviceMessage.split(",");
            for (int i = 0; i < allDevicesStatus.length; i++) {
                //String[] deviceMessageArray = deviceMessage.split(":");
                String deviceStatus = allDevicesStatus[i];
                String[] deviceMessageArray = deviceStatus.split(":");
                device = deviceMessageArray[0];
                state = deviceMessageArray[1];
                dbq.updateDataBase(device, state);
            }

        } catch (IOException e) {
            System.out.println(TAG + "Failed to read from devices.");
            System.out.println(e.getMessage());
        }
    }

    /*
     * Creating streams for the device-client.
     */
    private void createDeviceStreams() {
        try {
            deviceReader = new BufferedReader(new InputStreamReader(
                    deviceClientSocket.getInputStream()));
            deviceWriter = new PrintWriter(
                    deviceClientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(TAG + "failed in creating device streams.");
            System.out.println(e.getMessage());
        }
    }
    /*
     * Synchronized method that the server-threads invoke when they have a
     * command issued. We only want one client at a time to be able to to send
     * at one time.
     * 
     * OPTIONAL REQUIREMENT : Priority queue (Desirable)
     */

    public synchronized void sendToDevice(String unitRequest) {
        System.out.println(TAG + "request from server-thread - " + unitRequest);
        deviceWriter.println(unitRequest);
        
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(MultiThreadedServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        String deviceAnswer = getAllState();
//        //TODO: handle situation where deviceAnswer doesn't contain unitRequest
//        if (deviceAnswer != null) {
//            if (deviceAnswer.contains(unitRequest)) {
//                /*
//                 * The command was successfully executed on the device.
//                 * SO server should update new state of devices to database its read the message and split it and 
//                 * update it to database. 
//                 * 
//                 */
//                System.out.println(TAG + "Sending to all servers.");
//                //TODO: whether or not to keep this?
//                sendToAllServerThreads(deviceAnswer);
//            } else {
//                //handle situation where deviceAnswer doesn't contain unitRequest
//            }
//        }
    }

    public synchronized String validateUserAndPass(String userAndPass) {
        System.out.println(TAG + "login info from server-thread : " + userAndPass);
        String[] usernameAndPass = userAndPass.split(":");
        String user = usernameAndPass[0];
        String pass = usernameAndPass[1];
        String validation = dbq.validateUser(user, pass);
        return validation;
    }

    public void sendToAllServerThreads(String deviceAnswer) {
        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).receiveAndSendResponseFromDevice(deviceAnswer);
        }
    }

    /*
     * To retrieve the all devices status from arrayList and store it on a String
     *  dbq.readFromDatabase() return a arraylist we have to add it on a string 
     */
    public String getAllState() {
        String devicesstatus = (String) dbq.readFromDatabase().get(0);
        for (int i = 1; i < dbq.readFromDatabase().size(); i++) {
            devicesstatus = devicesstatus + "," + (String) dbq.readFromDatabase().get(i);
        }
        return devicesstatus;
    }
}
