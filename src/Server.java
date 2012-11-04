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

	Socket clientSocket = null;
	ObjectInputStream serverInput = null;
	MultiThreadedServer mts = null;
	BufferedReader br = null;
	PrintWriter pw = null;
	String[] unitRequest = null;
	String device, command, dbResponse;

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
			System.out.println("Server is sending to UNIT : " + dbResponse);
			//UserAndPass is the received username&password from the client.
			String userAndPass = br.readLine();
			validateUser(userAndPass);
			//dbResponse is the current deviceInformation in the database
			pw.println(dbResponse);
			while (true) {
				String unitRequest = br.readLine();
				identifyRequest(unitRequest);
			}
		} catch (IOException e) {
			System.out.println("Failed in creating streams!");
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
				pw.println("Validation failed.");
				clientSocket.close();
			} catch (IOException e) {
				System.out.println("Method:validateUser : " + e.getMessage());
			}
		} else {
			pw.println("Validation successful.");
		}
	}

	private void identifyRequest(String unitRequest) {
		if (unitRequest.contains("Light") || unitRequest.contains("LIGHT")
				|| unitRequest.contains("light")) {
			/*
			 * Now we know which device we are supposed to send the command to.
			 */
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
	}

	public void receiveAndSendResponseFromDevice(String deviceAnswer) {
		System.out.println("ServerThread received multicast message from mts. "
				+ deviceAnswer);
		pw.println(deviceAnswer);
	}
}
