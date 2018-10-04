package RainingClient;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class connectClient extends JFrame{
	
    //Settings UI
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    
    //Settings Connection
    final int SERVERPORT = 23456;
    final String HOST = "loclahost";

    Socket socket;
    
    public connectClient(){
    	setupUI();
    	try {
			socket = new Socket(HOST, SERVERPORT);
		}
    	catch (UnknownHostException e) {
			System.out.println("Server not found: " + e.getMessage());
		}
    	catch (IOException e) {
			System.out.println("I/O error: " + e.getMessage());
		}
    }
    
    public void sendDataToServer(String send) {
    	try {
			OutputStream output = socket.getOutputStream();

			byte[] data = send.getBytes();
			output.write(data);

			PrintWriter writer = new PrintWriter(output, true);
			writer.println("This is a message sent to the server");
		} catch (IOException e) {
			System.out.println("I/O error: " + e.getMessage());
		}
    }
    
    public void setupUI(){
        this.setSize(this.WIDTH, this.HEIGHT);
        addElements();
        this.setVisible(true);
        this.setTitle("Client: Raining Words");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);      
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
                	sendDataToServer("Cya lator alligator");
                    System.exit(0);
                }
            }
        });
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
				sendDataToServer(usernameField.getText());
			}
        });
    }
    
    public static void main(String[] args){
    	connectClient client = new connectClient();
    }
}
