import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class DeviceClient {

    static int portNumber = 7777;
	static Socket client = null;
	String msg;

	public static void main(String[] args) {

		try {
			client = new Socket(InetAddress.getLocalHost(), portNumber);
			System.out.println("Client-socket is created "
					+ client.getRemoteSocketAddress());

			OutputStream clientOut = client.getOutputStream();
			PrintWriter pw = new PrintWriter(clientOut, true);
			InputStream clientIn = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					clientIn));
			pw.println("lightIn:on,lightOut:on,fan:off,heating:off,door:open,stove:off,coffee:off,bath:on"); 
			while (true) {
				String msgFromServer = br.readLine();
				System.out.println("Received from MultiClientServer"
						+ msgFromServer
						+ "\nSending it back to simulate successful command.");
				pw.println(msgFromServer);
			}

		} catch (IOException e) {
			System.out.println();
			e.printStackTrace();
		}

	}
}
