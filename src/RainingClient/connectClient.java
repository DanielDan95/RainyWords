package RainingClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import RainingServer.Message;

public class connectClient implements Runnable{
	
    //Settings UI
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    
    //Settings Connection
    final int SERVERPORT = 23456;
    final String HOST = "localhost";

    Socket socket;
    ObjectOutputStream writer;
    ObjectInputStream input;
    
    boolean close = true;
    
    JFrame frame;
    JPanel old;
    
    public connectClient(JFrame frame, String username){
    	this.frame = frame;
    	this.old = (JPanel) frame.getContentPane();
    	setupUI();
    	try {
            this.socket = new Socket(HOST, SERVERPORT);
            this.writer = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
        	close = false;
        	sendDataToServer(1, username);
        }
    	catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
            shutdownSequence();
        }
    	catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            shutdownSequence();
    	}
    }
    
    public void sendDataToServer(int status, String toSend) {
        try {
            Message message = new Message(status,toSend);
            
            this.writer.writeObject(message);
            System.out.printf("Sent message to server: %s\n", toSend);
        } catch (Exception ex) {
            System.out.println("CRASHHs");
        }
    }
    public void handleMessage(Message message){
        switch(message.getStatus()){
            case -1:
                shutdownSequence();
                break;
            
            default:
                System.out.println("Can not understand Status from server: " + message.getStatus());
        }
        
    }
    public void setupUI(){
    	JPanel panel2 = new JPanel();
        JButton disconnect = new JButton("Disconnect");
        JButton test = new JButton("Test");
        panel2.add(disconnect);
        panel2.add(test);
        
        frame.setContentPane(panel2);
		frame.revalidate();
		
		disconnect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutdownSequence();
			}
        });
		test.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("Test Herro");
			}
        });
    }
    public void shutdownSequence(){
        try {
        	sendDataToServer(-1, "DISCONNECT");
            this.close = true;
            this.input.close();
            this.writer.flush();
            this.writer.close();
            try {
                Thread.sleep(100);
                this.socket.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ne) {
        }
    }

	@Override
	public void run() {
		while(!close){
            try {
                Message message = (Message) this.input.readObject();
                handleMessage(message);
            } catch (IOException ex) {
                
            } catch (ClassNotFoundException ex) {
                
            }
        }
		frame.setContentPane(old);
		frame.revalidate();
	}
}
