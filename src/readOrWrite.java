/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Hanna
 */
public class readOrWrite {
// This class is under construction, will be more modified
    PrintWriter fstream = null;
    FileInputStream freader = null;
    InputStream iss = null;
    BufferedReader brr = null;
    BufferedWriter out = null;
    DataInputStream dis = null;
    Calendar cal = null;
    SimpleDateFormat sdf = null;
    SimpleDateFormat sdf2 = null;
    String currentTime = null;
    String date = null;

    public void writeToFile(String user, String req) {
        try {
            String[] array = user.split(":");
            String users = array[0];
            String passWord = array[1];



            File f = new File("out.txt");
            freader = new FileInputStream(f);
            dis = new DataInputStream(freader);
            brr = new BufferedReader(new InputStreamReader(dis));
            String fromFile = "";
            String save = "";
            while ((fromFile = brr.readLine()) != null) {
                save += fromFile + "#";

            }
            System.out.println(fromFile);

            fstream = new PrintWriter("out.txt");
            out = new BufferedWriter(fstream);
            cal = Calendar.getInstance();
            sdf = new SimpleDateFormat("HH:mm");
            currentTime = sdf.format(cal.getTime());


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String currentDate = dateFormat.format(date);

            String fromUnit = users + " wants to " + req + " Time " + currentTime +" Date "+currentDate;
            String[] textTromFile = save.split("#");
            for (int i = 0; i < textTromFile.length; i++) {
                out.write(textTromFile[i]);
                out.newLine();
            }


            out.write(fromUnit);
            out.close();

        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }
}