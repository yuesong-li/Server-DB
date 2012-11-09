import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseQuery {
 
     /*
     * Simplified method for Creating database, 
     * This method will check for database on localhost. if its find the database will work normally. 
     * otherwise it will create a database with table and data.
     */
    public void createDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=";
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/?user=root&password=");
            ResultSet resultSet = con.getMetaData().getCatalogs();
            boolean check = false;
            while (resultSet.next()) {
                // Get the database name                
                //databaseName.add(resultSet.getString(1));
                if (resultSet.getString(1).equals("interactive_house")) {
                    System.out.println("Database exists");
                    check = true;
                }

            }
            if (check == false) {
                Statement st = con.createStatement();
                String query = "CREATE DATABASE  IF NOT EXISTS interactive_house";
                st.executeUpdate(query);
                System.out.println("Database Created");
                con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/interactive_house?user=root&password=");
                st = con.createStatement();
                query = "CREATE TABLE IF NOT EXISTS `devices` (`deviceId` int(20) NOT NULL,`deviceName` varchar(40) NOT NULL,`deviceState` varchar(40) NOT NULL, PRIMARY KEY (`deviceId`))";
                st.executeUpdate(query);
                System.out.println("Table Created");
                query = "SELECT * FROM devices";
                ResultSet rs = st.executeQuery(query);
                if (!rs.next()) {
                    query = "INSERT INTO `devices` VALUES (1,'lightIn','on'),(2,'lightOut','on'),(3,'fan','off'),(4,'heating','off'),(5,'door','open'),(6,'stove','off'),(7,'coffee','off'),(8,'bath','on')";
                    st.executeUpdate(query);
                    System.out.println("Data Inserted");
                }
            }

            resultSet.close();
            con.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

       /*
     * Simplified method for updating database, only works in the current state
     * of the project. Requires updates for further development of the project.
     */
    public void updateDataBase(String device, String state) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=";
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
		ArrayList<String> dbResponseArray = new ArrayList<String>();
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
