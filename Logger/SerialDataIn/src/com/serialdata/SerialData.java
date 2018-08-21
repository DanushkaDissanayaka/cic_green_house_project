package com.serialdata;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFrame;
import javax.swing.JLabel;

import gnu.io.*;
import java.sql.*;
import  java.util.*;
 
/*public class test {
 
    public static void  main(String[] args) {
    Enumeration<CommPortIdentifier> ports
    = CommPortIdentifier.getPortIdentifiers();
    while(ports.hasMoreElements()){
        CommPortIdentifier info = ports.nextElement();
        System.out.println("hi this answer  "+info.getName());
    }
 
    }
 
}*/
//import gnu.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
 
 
public class SerialData extends javax.swing.JFrame implements SerialPortEventListener  {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	SerialPort serialPort;
    static Connection conn = null;
    
        /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
            "COM10", // Windows
    };
    /**
    * A BufferedReader which will be fed by a InputStreamReader
    * converting the bytes into characters
    * making the displayed results codepage independent
    */
    private BufferedReader input;
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;
 
    public void initialize() {
                // the next line is for Raspberry Pi and
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
       
               CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
 
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }
 
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);
 
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
 
            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
 
            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
 
    /**
     * 
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
 
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
	    
	    public static void updateData(Connection conn, String data) {
	    	
		    String query = " insert into data (data)" + " values (?)";	
		   // String.format("INSERT INTO DATA (data) %s %s %s", );
	        PreparedStatement preparedStmt = null;
			try {
				preparedStmt = conn.prepareStatement(query);
			} 
			catch (SQLException e) {
				System.out.println(e);
			}
			
	        try {
				preparedStmt.setString (1, data);
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
    
    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
    	
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine(); // inputLine   serial data sotore variable
                System.out.println("Current date time "+inputLine);
                jframe.changeText(inputLine);
                //dbConnect.updateData(conn, inputLine);  //insert database
                writeFile.write(inputLine); // Write data to file
            } catch (Exception e) {
                System.err.println(e.toString());
                
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }
    
 
    public static void main(String[] args) throws Exception {
    	jframe frame = new jframe();
    	writeFile file = new writeFile();//Write to file
    	
        conn = dbConnect.connectDb(); //connect to db
        
        SerialData main = new SerialData();
        main.initialize();
        Thread t=new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
            }
        };
        t.start();
        System.out.println("Started");
        //conn.close();
    }
    
}

class writeFile{

	FileHandler fileHandler;
	static Logger logger = Logger.getLogger("MyLog");
	
	writeFile(){
		try {  
			
	        // This block configure the logger with handler and formatter  
	    	fileHandler = new FileHandler("log.log",true);  
	        logger.addHandler(fileHandler);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fileHandler.setFormatter(formatter);  
	        
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}

	public static void write(String data) {
        // the following statement is used to log any messages 
		logger.info(data);
	}
}

class jframe  extends javax.swing.JFrame{

	private static final long serialVersionUID = 1L;
	
	JFrame frame = new JFrame("Data Logger");
	static JLabel lable1 = new JLabel("Waiting for data to log",JLabel.CENTER);
	JLabel lable2 = new JLabel("Serial data:");
	
	jframe(){	
		frame.setSize(300,300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.add(lable1);
		
	}
	
	public static void changeText(String Data) {
		lable1.setText(Data);
	}

}