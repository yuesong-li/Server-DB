
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class UnitClient {

    static int portnumber = 8888;
    static PrintWriter pw = null;
    static Socket client = null;
    static OutputStream clientOut = null;
    static InputStream clientIn = null;
    static BufferedReader br = null;

    public static void main(String[] args) {

        try {
            client = new Socket(InetAddress.getLocalHost(), portnumber);
            System.out.println("Client socket is created "
                    + client.getRemoteSocketAddress());
            clientOut = client.getOutputStream();
            pw = new PrintWriter(clientOut, true);
            clientIn = client.getInputStream();
            br = new BufferedReader(new InputStreamReader(clientIn));

            sendToServer();

            while (true) {
                String deviceAnswer = br.readLine();
                System.out.println("Unit received message : " + deviceAnswer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendToServer() {
        pw.println("light:on");
    }
}
