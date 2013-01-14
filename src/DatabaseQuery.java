
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseQuery {

    private final String TAG = "DB: ";

    /*
     * Simplified method for Creating database, 
     * This method will check for database on localhost. if its find the database will work normally. 
     * otherwise it will create a database with table and data.
     */
    public void createDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=";
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/?user=root&password=root");
            ResultSet resultSet = con.getMetaData().getCatalogs();
            Statement st = con.createStatement();
            String query;
            boolean check = false;
            while (resultSet.next()) {
                // Get the database name                
                //databaseName.add(resultSet.getString(1));
                if (resultSet.getString(1).equals("interactive_house")) {
                    System.out.println(TAG + "Database exists");
                    check = true;
                }

            }
            if (check == false) {
                query = "CREATE DATABASE  IF NOT EXISTS interactive_house";
                st.executeUpdate(query);
                System.out.println(TAG + "Database Created");
            }
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/interactive_house?user=root&password=root");
            st = con.createStatement();
            query = "CREATE TABLE IF NOT EXISTS `devices` (`deviceId` int(20) NOT NULL,`deviceName` varchar(40) NOT NULL,`deviceState` varchar(40) NOT NULL, PRIMARY KEY (`deviceId`))";
            st.executeUpdate(query);
            //System.out.println("Devices Table Created");
            query = "SELECT * FROM devices";
            ResultSet rs = st.executeQuery(query);
            if (!rs.next()) {
                query = "INSERT INTO `devices` VALUES (1,'lightIn','on'),(2,'lightOut','on'),(3,'fan','off'),(4,'heaterRoom','off'),(5,'heaterLoft','off'),(6,'tempRoom','10'),(7,'tempLoft','12'),(8,'door','unlocked'),(9,'coffee','off'),(10,'bath','on'),(11,'wash','off'),(12,'media','off'),(13,'alarm','off')";
                st.executeUpdate(query);
                System.out.println(TAG + "Data Inserted in Device Table");
            }
            query = "CREATE TABLE IF NOT EXISTS `users` (`userid` int(10) NOT NULL AUTO_INCREMENT,`username` varchar(25) DEFAULT NULL,`password` varchar(25) DEFAULT NULL, `access` varchar(25) DEFAULT NULL,PRIMARY KEY (`userid`))";
            st.executeUpdate(query);
            //System.out.println("User Table Created");
            query = "SELECT * FROM users";
            rs = st.executeQuery(query);
            if (!rs.next()) {
                String houseMaster = cryptPassword("HouseMaster");
                String housePerson = cryptPassword("HousePerson");
                String anotherPerson = cryptPassword("AnotherPerson");
                query = "INSERT INTO `userss` (`userid`, `username`, `password`, `access`) VALUES (1, 'HouseMaster','"+houseMaster+"','admin'),(2, 'HousePerson','"+housePerson+"', 'low'), (3, 'AnotherPerson','"+anotherPerson+"', 'high')";
                st.executeUpdate(query);
                System.out.println(TAG + "Data Inserted in users Table");
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
            String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=root";
            Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();
            String query = "UPDATE devices SET deviceState='" + state
                    + "' WHERE deviceName='" + device + "'";
            st.executeUpdate(query);
            System.out.println(TAG + "Database updated : " + device + ":" + state);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(TAG + "Error encountered. : " + e.getMessage());
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
            String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=root";
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
            System.out.println(TAG + "Error encountered. : " + e.getMessage());
        }
        return dbResponseArray;
    }

    public String validateUser(String user, String pass) {
        String dbUser = null, dbPass = null, dbAccess = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1/interactive_house?user=root&password=root";
            Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();

            String query = "SELECT username, password, access FROM users WHERE username='" + user + "'";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                dbUser = rs.getString("username");
                dbPass = rs.getString("password");
                dbAccess = rs.getString("access");
            }
            System.out.println(TAG + "Result from db : " + dbUser + " " + dbPass);
            if (user.equals(dbUser) && check(pass,dbPass)) {
                return dbAccess;
            }
        } catch (Exception e) {
            System.out.println(TAG + "Error encountered. : " + e.getMessage());
        }
        return "Fail";
    }
    
     private String cryptPassword(String password) {
        String hashed = PasswordCrypt.hashpw(password, PasswordCrypt.gensalt(12));
        return hashed;
    }

    private boolean check(String candi, String hash) {
        boolean status = false;
        if (PasswordCrypt.checkpw(candi, hash)) {           
            status=true;
        } else {            
            status=false;
        }
        return status;
    }
}
