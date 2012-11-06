import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseQuery {
    /*
     * Simplified method for updating database, only works in the current state
     * of the project. Requires updates for further development of the project.
     */

    String test = "test for first commit";
    public void updateDataBase(String device, String state) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=root";
            Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();
            String query = "UPDATE devices SET deviceState='" + state
                    + "' WHERE deviceName='" + device + "'";
            st.executeUpdate(query);
            System.out.println("Database updated : " + device + " : " + state);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error encountered. : " + e.getMessage());
        }
    }
    /*
     * read all data from database and return an arraylist 
     * 
     * Now this method will return an ArrayList instead of String 
     */
    public ArrayList readFromDatabase() {
		String dbResponse = null, name = null, state = null;
		ArrayList<String> dbResponseArray = new ArrayList<>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=";
			Connection con = DriverManager.getConnection(url);
			Statement st = con.createStatement();

			String query = "SELECT * FROM devices";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				name = rs.getString("deviceName");
				state = rs.getString("deviceState");
				dbResponse = name + ":" + state;
				dbResponseArray.add(dbResponse);
			}
			//System.out.println("Retrieved from database : " + dbResponseArray);
			con.close();
		} catch (Exception e) {
			System.out.println("Error encountered. : " + e.getMessage());
		}
		return dbResponseArray;
	}

    public String validateUser(String user, String pass) {
		String dbUser = null, dbPass = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=";
			Connection con = DriverManager.getConnection(url);
			Statement st = con.createStatement();
			
			String query = "SELECT username, password FROM users WHERE username='"+ user +"'";
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				dbUser = rs.getString("username");
				dbPass = rs.getString("password");
			}
			System.out.println("Result from db : " + dbUser + " " + dbPass);
			if(user.equals(dbUser) && pass.equals(dbPass)) {
				return "Pass";
			}
		} catch (Exception e) {
			System.out.println("Error encountered. : " + e.getMessage());
		}
		return "Fail";
	}
}
