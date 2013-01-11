
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class DeviceClient {

    static int portNumber = 7777;
    static Socket client = null;
    String msg;
    static DeviceInputHandler deviceHandler = null;
    static PrintWriter pw = null;
    
    public static void main(String[] args) {

        try {
            client = new Socket(InetAddress.getLocalHost(), portNumber);
            System.out.println("Client-socket is created "
                    + client.getRemoteSocketAddress());

            OutputStream clientOut = client.getOutputStream();
            pw = new PrintWriter(clientOut, true);
            InputStream clientIn = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    clientIn));
            pw.println("lightIn:on,lightOut:on,fan:off,heaterRoom:off,heaterLoft:on,tempRoom:15,tempLoft:10,door:unlocked,coffee:off,bath:on,wash:off,media:off,alarm:off");
//            for (int i = 0; i < 100000; i++) {
//                String s = i + "";
//            }
//            pw.println("Alarm");
            //System.out.println("Device is sending: alarm");
            while (true) {
                if (br.ready()) {
                    String msgFromServer = br.readLine();
                    System.out.println("Received from MultiClientServer"
                            + msgFromServer
                            + "\nSending it back to simulate successful command.");
                    pw.println(msgFromServer);
                }else{
                    if (deviceHandler == null) {
                        deviceHandler = new DeviceInputHandler();
                        deviceHandler.start();
                    }
                }

            }


        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }

    }
    static class DeviceInputHandler extends Thread {

        Scanner sc = new Scanner(System.in);

        @Override
        public void run() {
            while (true) {
                System.out.println("Device handler, input: ");
                if (sc.hasNext()) {
                    pw.println(sc.next());
                }
            }
        }
    }
}
