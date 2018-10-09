package RainingClient;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import RainingServer.Message;

public class connectClient extends JFrame{
	
    //Settings UI
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    
    //Settings Connection
    final int SERVERPORT = 23456;
    final String HOST = "localhost";

    Socket socket;
    ObjectOutputStream writer;
    ObjectInputStream input;
    
    boolean close = false;
    
    public connectClient(){
    	setupUI();
    	try {
            this.socket = new Socket(HOST, SERVERPORT);
            this.writer = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
            startProgram();
        }
    	catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        }
    	catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
	}
    }
    
    public void sendDataToServer(int status, String toSend) {
        try {
            Message message = new Message(status,toSend);
            this.writer.writeObject(message);
            System.out.printf("Sent message to server: %s\n", toSend);
        } catch (IOException ex) {
            Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void startProgram(){
        while(!close){
            try {
                Message message = (Message) this.input.readObject();
                handleMessage(message);
            } catch (IOException ex) {
                
            } catch (ClassNotFoundException ex) {
                
            }
         
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
        this.setSize(this.WIDTH, this.HEIGHT);
        addElements();
        this.setVisible(true);
        this.setTitle("Client: Raining Words");
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);      
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            { 
                String ObjButtons[] = {"Yes","No"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit?","Online Examination System",
                		JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==JOptionPane.YES_OPTION)
                {
                	sendDataToServer(-1, "DISCONNECT");
                    shutdownSequence();
                    
                }
            }
        });
    }
    public void shutdownSequence(){
        try {
            this.close = true;
            this.input.close();
            this.writer.flush();
            this.writer.close();
            try {
                Thread.sleep(100);
                this.socket.close();
                System.exit(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addElements(){
    	JPanel panel = new JPanel();
        JButton nameButton = new JButton("OK");
        JTextField usernameField = new JTextField();
        JLabel userLabel = new JLabel("Username:");
        usernameField.setText("");
        usernameField.setPreferredSize(new Dimension(200, 20));
        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(nameButton);
        this.getContentPane().add(panel);
        
        nameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendDataToServer(1, usernameField.getText());
                System.out.println("Username set");
			}
        });
    }
    
    public static void main(String[] args){
    	connectClient client = new connectClient();
    }
}
