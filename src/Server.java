
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/*
 *************************************************************************
 * Authors : Mathias Olsson, Hanna Persson, Zakir Hossain
 * 
 * Class : Server
 * 
 * Class functionality : 	The server class's function is to communicate 
 * 							with the Units that connect with the server.
 * 							
 * ***********************************************************************
 */
public class Server extends Thread {

    Socket clientSocket = null;
    ObjectInputStream serverInput = null;
    MultiThreadedServer mts = null;
    BufferedReader br = null;
    PrintWriter pw = null;
    String[] unitRequest = null;
    String device, command;
    DatabaseQuery dbq = new DatabaseQuery();
    Scanner sc = null;
    /*
     * Take the socket that was connected to the multi-threaded server and save
     * it so that we can use it. And save a reference to the multi-threaded
     * server so that each thread can reach its methods.
     */

    public Server(Socket clientSocket, MultiThreadedServer mts) {
        this.clientSocket = clientSocket;
        this.mts = mts;
    }

    public void run() {
        /*
         * Create the streams for input/output so we can communicate with the
         * Units.
         */
        try {
            //sc = new Scanner(clientSocket.getInputStream());
            br = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            pw = new PrintWriter(clientSocket.getOutputStream(), true);
            //send the current status of devices to units
            String lightState = dbq.readFromDatabase();
            pw.println(lightState);//"light:on");
        } catch (IOException e) {
            System.out.println("Failed in creating streams!");
            System.out.println(e.getMessage());
        }
        while (true) {

            try {

                //accept command from units
                String request = null;// = "error";
                System.out.println("-----value of request: " + request);
                //sc.next();
                //if (br.ready()) {
                while (request == null) {
                    request = br.readLine();
                    
                    System.out.println("request: "+request);
                }//}
                //int test = br.read();
                System.out.println("command received: " + request);
                identifyRequest(request);

            } catch (IOException e) {
                System.out.println("Failed in creating streams!");
                System.out.println(e.getMessage());
            }
        }
    }

    private void identifyRequest(String unitRequest) {
        if (unitRequest.contains("light") || unitRequest.contains("LIGHT") || unitRequest.contains("Light")) {
            /*
             * Now we know which device we are supposed to send the command to.
             */
            this.unitRequest = unitRequest.split(":");
            device = this.unitRequest[0];
            command = this.unitRequest[1];

            System.out.println(":::DEV:::" + unitRequest + ":::");
            mts.sendToDevice(unitRequest);
            
        } else if (unitRequest.contains("Otherdevice") || unitRequest.contains("OTHERDEVICE") || unitRequest.contains("otherdevice")) {
            //do something
        }
    }

    public void receiveAndSendResponseFromDevice(String deviceAnswer) {
        System.out.println("ServerThread received multicast message from mts. " + deviceAnswer);
        pw.println(deviceAnswer);
    }
}
