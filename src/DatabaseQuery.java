import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseQuery {
    /*
     * Simplified method for updating database, only works in the current state
     * of the project. Requires updates for further development of the project.
     */

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

    public String readFromDatabase() {
        String dbResponse = null, name = null, state = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=root";
            Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();

            String query = "SELECT deviceName, deviceState FROM devices";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                name = rs.getString("deviceName");
                state = rs.getString("deviceState");
            }
            dbResponse = name + ":" + state;
            System.out.println("Retrieved from database: " + dbResponse);
            con.close();
        } catch (Exception e) {
            System.out.println("Error encountered. : " + e.getMessage());
        }
        return dbResponse;
    }
}
