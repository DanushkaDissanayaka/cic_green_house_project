import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JSlider;
import com.fazecast.jSerialComm.*;
import java.sql.*;

public class Main {
	
	private static Scanner data;
	private static Scanner scanvalue;


	public static class dbConnect {
	    Connection conn = null;
	    
	    public static Connection connectDb(){
	        try{
	            Class.forName("com.mysql.jdbc.Driver");  
	            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/cic", "root", "");  //db login
	            return conn;
	        }
	        catch (Exception e){
	            System.out.println(e);
	            return null;
	        }
	    }
	    
	    public static void updateData(Connection conn, String chosenport, String data) {
	    	
		    String query = " insert into data (port, data)" + " values (?, ?)";		         
	        PreparedStatement preparedStmt = null;
			try {
				preparedStmt = conn.prepareStatement(query);
			} 
			catch (SQLException e) {
				System.out.println(e);
			}
			
	        try {
	        	preparedStmt.setString (1, chosenport);
				preparedStmt.setString (2, data);
			} 
	        catch (SQLException e) {
				System.out.println(e);
			}
	        
	        try {
				preparedStmt.execute();
			} 
	        catch (SQLException e) {
				System.out.println(e);
			}
	    }
	}


	public static void main(String[] args) {		
	    Connection conn = null;
	    conn = dbConnect.connectDb(); //connect to db

		JFrame window = new JFrame();
		JSlider slider = new JSlider();
		slider.setMaximum(1023);
		window.add(slider);
		window.pack();
		window.setVisible(true);
		SerialPort ports[]= SerialPort.getCommPorts();
		System.out.println("select a port");
		int i =1;
		for(SerialPort port : ports) {			
			System.out.println(i++ + ". " + port.getSystemPortName());
		}
		
		scanvalue = new Scanner(System.in);
			int chosenport = scanvalue.nextInt();
			SerialPort port = ports[chosenport-1];
			if(port.openPort()) {
				System.out.println("Successfuly opened the port.");
			}
			else {
				System.out.println("Unble to open the prot.");
				return;
			}
			
			port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
			data = new Scanner(port.getInputStream());

			while(data.hasNextLine()) {
				String info = data.nextLine();
				System.out.println(info);
				dbConnect.updateData(conn, Integer.toString(chosenport), info);  //insert database
			}
			System.out.println("Data error");
		}
}
