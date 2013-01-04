
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;


/*
 *************************************************************************
 * Authors : Mathias Olsson, Hanna Persson, Zakir Hossain
 * 
 * Class : Server
 * 
 * Class functionality :     The server class's function is to communicate 
 * 							with the Units that connect with the server.
 * 							
 * ***********************************************************************
 */
public class Server extends Thread {

    private final String TAG = "SERVER: ";
    Socket clientSocket = null;
    ObjectInputStream serverInput = null;
    MultiThreadedServer mts = null;
    BufferedReader br = null;
    PrintWriter pw = null;
    String[] unitRequest = null;
    String device, command, dbResponse;
    String userAndPass = null;
    readOrWriteFromFile row = new readOrWriteFromFile();
    /*
     * Take the socket that was connected to the multi-threaded server and save
     * it so that we can use it. And save a reference to the multi-threaded
     * server so that each thread can reach its methods.
     */

    public Server(Socket clientSocket, MultiThreadedServer mts,
            String dbResponse) {
        this.clientSocket = clientSocket;
        this.mts = mts;
        this.dbResponse = dbResponse;
    }

    public void run() {
        /*
         * Create the streams for input/output so we can communicate with the
         * Units.
         */
        try {
            br = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            pw = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println(TAG + "sending to UNIT : " + dbResponse);
            //UserAndPass is the received username&password from the client.
            userAndPass = br.readLine();
            validateUser(userAndPass);
            //dbResponse is the current deviceInformation in the database
            pw.println(dbResponse);
            while (true) {
                String unitRequest = br.readLine();
                if (unitRequest.contains("getStatus")) {
                    //System.out.println("Server - Sending the status to unit");
                    String deviceStates = mts.getAllState();
                    pw.println(deviceStates);
                } else if (unitRequest.equals("!")) {
                    //TEST CASE: received when android is disconnected
                    System.out.println(TAG + "Throwing IO exception");
                    throw new IOException();
//                    mts.threadList.remove(this);
//                    System.out.println(TAG + "Number of unit conns: " + mts.threadList.size());
//                    System.out.println(TAG + "Client disconnected...");
                } else {
                    verifyRequest(unitRequest);
                    row.writeToFile(userAndPass, unitRequest);
                }
            }
        } catch (IOException e) {
            mts.threadList.remove(this);
            System.out.println(TAG + "Number of unit conns: " + mts.threadList.size());
            System.out.println(TAG + "Client disconnected...");
            System.out.println(e.getMessage());
        }
    }

    private void validateUser(String userAndPass) {
        /*
         * Here, we try to use a synchronized method in MultiThreadedServer
         * that will try to find the username and pass in the database.
         */
        String validation = mts.validateUserAndPass(userAndPass);
        if (validation.equals("Fail")) {
            try {
                pw.println("login:fail");
                clientSocket.close();
            } catch (IOException e) {

                System.out.println(TAG + "method: validateUser : " + e.getMessage());

            }
        } else {
            pw.println("login:" + validation);
        }
    }
    /*
     * I m not sure we r going to use this method or not 
     * I am just puting it on comments now. 
     * And adding my new method instead of it.
     */

    /*	private void identifyRequest(String unitRequest) {
     if (unitRequest.contains("Light") || unitRequest.contains("LIGHT")
     || unitRequest.contains("light")) {
			
     * Now we know which device we are supposed to send the command to.
			 
     // this.unitRequest = unitRequest.split(":");
     // device = this.unitRequest[0];
     // command = this.unitRequest[1];
     System.out.println("Unit received following from server : "
     + unitRequest);
     mts.sendToDevice(unitRequest);

     } else if (unitRequest.contains("Otherdevice")
     || unitRequest.contains("OTHERDEVICE")
     || unitRequest.contains("otherdevice")) {
     // do something
     }
     } */
    /*
     * 
     * This method will verify the unit request is  necessary or not
     * get the request from unit-client  
     * compare it with database 
     * if same data found send a error message to unit and discard request else send request to device
     */
    private void verifyRequest(String unitRequest) {
        /*
         * Now we know which device we are supposed to send the command to.
         * if the request contains temp, send it directly to device, without checking with DB
         * Otherwise, verify it first with DB.
         */
        if (unitRequest != null) {
            if (unitRequest.contains("temp")) {
                mts.sendToDevice(unitRequest);
                System.out.println(TAG + "sending the following from server to device : " + unitRequest);
            } else if (unitRequest.contains(":")) {
                this.unitRequest = unitRequest.split(":");
                device = this.unitRequest[0].trim();
                command = this.unitRequest[1].trim();
                DatabaseQuery dbq = new DatabaseQuery();
                for (int i = 0; i < dbq.readFromDatabase().size(); i++) {
                    String[] deviceinfo = ((String) dbq.readFromDatabase().get(i)).split(":");
                    if (device.equals(deviceinfo[0].trim()) && command.equals(deviceinfo[1].trim())) {
                        System.out.println(TAG + "This command is already executed on devices");
                        //reply with the newest device status
                        String status = mts.getAllState();
                        pw.println(status);
                    } else if (device.equals(deviceinfo[0].trim()) && !command.equals(deviceinfo[1].trim())) {
                        System.out.println(TAG + "sending the following from server to device : " + unitRequest);
                        row.writeToFile(userAndPass, unitRequest);
                        mts.sendToDevice(unitRequest);
                    }
                }
            }else{
                //ignore the invalid requests
            }
        }
    }

    public void receiveAndSendResponseFromDevice(String deviceAnswer) {
        System.out.println(TAG + "received multicast message from mts - " + deviceAnswer);
        pw.println(deviceAnswer);
    }
}