
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


/*
 **********************************************************************************************
 * Authors : Hanna Persson, Zakir Hossain, Mathias Olsson
 * 
 * Class : UnitClient 				
 * 							
 * ********************************************************************************************
 */
public class UnitClient {

    static int portnumber = 8888;
    static PrintWriter pw = null;
    static Socket client = null;
    static OutputStream clientOut = null;
    static InputStream clientIn = null;
    static BufferedReader br = null;
    static int count = 0;

    public static void main(String[] args) {

        try {
            client = new Socket(InetAddress.getLocalHost(), portnumber);
            System.out.println("Client socket is created "
                    + client.getRemoteSocketAddress());
            clientOut = client.getOutputStream();
            pw = new PrintWriter(clientOut, true);
            clientIn = client.getInputStream();
            br = new BufferedReader(new InputStreamReader(clientIn));
            sendUserPass();
            receiveStartupDetails();
            sendToServer();

            while (true) {
                if (br.ready()) {
                    String deviceAnswer = br.readLine();
                    System.out.println("Unit received message : " + deviceAnswer);
                } else {
                    /*
                     * Code for running other unit specific code. 
                     * Will only be invoked when the bufferedReader doesnt have anything to read.
                     */
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendToServer() {
        System.out.println("Sending command to server : tempLoft:-10");
        pw.println("tempLoft:-10");
    }

    private static void receiveStartupDetails() {
        try {
            String startup = br.readLine();
            System.out.println("Client knows about following device : " + startup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendUserPass() {
        String sendUser = "HouseMaster";
        String sendPass = "HouseMaster";
        pw.println(sendUser + ":" + sendPass);
        try {
            String answer = br.readLine();
            System.out.println("Result of validation : " + answer);
        } catch (IOException e) {
            System.out.println("Method:sendUserPass : " + e.getMessage());
        }
    }
}
