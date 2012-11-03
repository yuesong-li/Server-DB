
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
 * Class functionality : 	MultiThreadedServer class is the heart of the house.
 * 							All communication passes through here and gets sent to the
 * 							right place.
 *  
 * 							For a full overview of the class, we refer to the design documents.							
 * 							
 * ********************************************************************************************
 */
public class MultiThreadedServer extends Thread {

    /*
     * Declaration of variables used for this class.
     */
    ServerSocket deviceServerSocket = null;
    ServerSocket unitServerSocket = null;
    Socket unitClientSocket = null;
    Socket deviceClientSocket = null;
    PrintWriter deviceWriter = null;
    BufferedReader deviceReader = null;
    ArrayList<Server> threadList = null;
    DatabaseQuery dbq = new DatabaseQuery();
    //to control whether or not the system has initiated
    boolean initiated = false;

    public MultiThreadedServer() {
        threadList = new ArrayList<Server>();
    }

    public void run() {
        try {
            /*
             * Following segment of code covers the creation of the server
             * socket for deviceOut.
             */
            deviceServerSocket = new ServerSocket(7777);
            System.out.print("Server running device-socket on "
                    + InetAddress.getLocalHost()
                    + "\nAwaiting device-client connection.. .. ..");

        } catch (IOException e) {
            System.out.println("Could not listen on port: 7777");
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
                    System.out.println("Connection with house established. Running on : " + deviceClientSocket.getRemoteSocketAddress());
                    createDeviceStreams();
                    while (!initiated) {
                        // Here we must accept information from the House.
                        // Devices and all of their states must be updated in the
                        // Database
                        receiveInitialDevices();
                    }
                    /*
                     * Following segment of code covers the creation of the
                     * server socket for units.
                     */
                    unitServerSocket = new ServerSocket(8888);
                    System.out.println("\nServer running on "
                            + InetAddress.getLocalHost()
                            + "\nAwaiting unit-client connection.. .. ..");
                } /*
                 * Following segment of code covers event : Unit connects to
                 * server. Note that this is only made available whenever a
                 * connection has been made with the house.
                 */ else {
                    unitClientSocket = unitServerSocket.accept();
                    System.out.println("Client accepted. Client : "
                            + unitClientSocket.getRemoteSocketAddress());
                    Server server = new Server(unitClientSocket, this);
                    server.start();
                    threadList.add(server);
                }
            } catch (IOException e) {
                System.out.println("Accept failed: 8888");
                System.exit(-1);
            }
        }

    }

    /*
     * Receiving the information on the current devices from the deviceOut-client.
     * Also overwrites the database with the newest information/states.
     */
    private void receiveInitialDevices() {
        try {
            String deviceMessage, device, state;
            deviceMessage = deviceReader.readLine();
            System.out.println("Received from deviceClient : " + deviceMessage);
            String[] deviceMessageArray = deviceMessage.split(":");
            try {
                device = deviceMessageArray[0];
                state = deviceMessageArray[1];
                //update database
                dbq.updateDataBase(device, state);
                initiated = true;
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println("ERROR info incomplete");
            }
        }catch (IOException e) {
            System.out.println("Failed to read from devices.");
            System.out.println(e.getMessage());
        }
    }

    /*
     * Creating streams for the deviceOut-client.
     */
    private void createDeviceStreams() {
        try {
            deviceReader = new BufferedReader(new InputStreamReader(deviceClientSocket.getInputStream()));
            deviceWriter = new PrintWriter(deviceClientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Failed in creating streams.");
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
        System.out.println(":::DEV::: Msg from server-thread : " + unitRequest + ". :::");
        deviceWriter.println(unitRequest);
        System.out.println("command sent to device");
        try {
            String deviceAnswer = deviceReader.readLine();
            System.out.println("Confirmed by device, answer: " + deviceAnswer);
            if (deviceAnswer != null && deviceAnswer.contains(unitRequest)) {
                /*
                 * The command was successfully executed on the deviceOut.
                 */
                sendToAllServerThreads(deviceAnswer);
                String[] answer = deviceAnswer.split(":");
                dbq.updateDataBase(answer[0], answer[1]);
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllServerThreads(String deviceAnswer) {
        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).receiveAndSendResponseFromDevice(deviceAnswer);
        }
    }
}
