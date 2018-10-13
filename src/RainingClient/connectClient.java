package RainingClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
    	JPanel panel = new JPanel(new GridBagLayout());
    	JPanel headerPane = new JPanel(new GridBagLayout());
    	JPanel gamePane = new JPanel();
    	JPanel inputPane = new JPanel(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	
    	JLabel scoreLabel = new JLabel("Score: 999");
    	JLabel timeLabel = new JLabel("Time: 23 sec");
        JButton disconnectBtn = new JButton("Disconnect");
        JTextField wordInput = new JTextField();
        
        gamePane.setPreferredSize(new Dimension(300, 300));
        wordInput.setPreferredSize(new Dimension(100, 30));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;
        gbc.weighty = 0;
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        headerPane.add(timeLabel, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.gridx = 1;
        headerPane.add(disconnectBtn, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.gridx = 2;
        headerPane.add(scoreLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.gridx = 0;
        inputPane.add(wordInput, gbc);
        
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0;
        gbc.gridy = 0;
        panel.add(headerPane, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridy = 1;
        panel.add(gamePane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 0;  
        gbc.weighty = 0; 
        gbc.gridy = 2;
        panel.add(inputPane, gbc);
        
        gamePane.setBorder(BorderFactory.createLineBorder(Color.black));
        
        frame.setContentPane(panel);
		frame.revalidate();
		
		disconnectBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutdownSequence();
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
