package RainingServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class InitRainingServer extends JFrame implements ActionListener{
    
    //Settings UI
    final int WIDTH = 530;
    final int HEIGHT = 500;
    
        // Variables declaration - do not modify                     
    private JButton clearButton;
    private JButton stopButton;
    private JTextField inputStopField;
    private JButton onlineButton;
    private JScrollPane jScrollPane1;
    private JLabel companyName;
    private JTextArea textField;
    
    //Settings Connection
    final int PORT = 23456;
    
    //Clients
    ArrayList<Thread> threads;
    ArrayList<handleClient> clients;
    ArrayList<Game> gameThread;
    ServerSocket server;
    
    public InitRainingServer(){
        this.threads = new ArrayList<Thread>();
        this.clients = new ArrayList<handleClient>();
        this.gameThread = new ArrayList<Game>();
        setupUI();
        System.out.println("Server is up and running on port: " + this.PORT);
        
        
        try {
            server = new ServerSocket(this.PORT);
            startServer();
        } catch (IOException ex) {
            Logger.getLogger(InitRainingServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    public void startServer(){
        while(true){
            try {
                handleClient client = new handleClient(this.server.accept()); 
                this.clients.add(client);
                Thread thread = new Thread(client);
                this.threads.add(thread);
                thread.start();
                
            } catch (IOException ex) {
                Logger.getLogger(InitRainingServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }
    

    
    



    public void setupUI(){
        this.setSize(this.WIDTH, this.HEIGHT);
        this.setTitle("Server Monitor");
        this.setName("Raining Words Server");
        this.setResizable(false);
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
                	try{
                		for(handleClient c : clients){
                			c.shutdownSequence(true);
                		}
                	} catch (Exception ex){
                		
                	}
                	System.exit(0);
                }
            }
        });
        
        
         jScrollPane1 = new JScrollPane();
        textField = new JTextArea();
        inputStopField = new JTextField();
        stopButton = new JButton();
        onlineButton = new JButton();
        clearButton = new JButton();
        companyName = new JLabel();

       

        textField.setColumns(20);
        textField.setRows(5);
        jScrollPane1.setViewportView(textField);
      

        stopButton.setText("Stop Game");
        stopButton.addActionListener(this);

        onlineButton.setText("Online Users");
        onlineButton.addActionListener(this);

        clearButton.setText("Clear");
        clearButton.addActionListener(this);
        
        

        companyName.setText("Raining Words");
        companyName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        this.setLayout(null);
        
        this.textField.setBounds(10, 10, 500, 340);
        this.textField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        this.inputStopField.setBounds(10, 370, 100, 20);
        
        this.stopButton.setBounds(10, 410, 100, 25);
        
        this.onlineButton.setBounds(390, 370, 120, 25);
        
        this.clearButton.setBounds(390, 410, 120, 25);
        
        this.add(clearButton);
        this.add(onlineButton);
        this.add(stopButton);
        this.add(inputStopField);
        this.add(textField);
        
        this.setVisible(true);
        
        
        
    }
    private void showLogger(String text){
        this.textField.append(text + "\n");
    
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == stopButton){
            stopGame(inputStopField.getText());
            inputStopField.setText("");
        }
        else if(e.getSource() == onlineButton){
            showAllOnlineUsers();
        }
        else if(e.getSource() == clearButton){
            clearLogger();
        }
    }
    private void stopGame(String nr){
        int number = 0;
        boolean found = false;
        try{
            number = Integer.parseInt(nr);
            found = true;
        }catch(Exception ex){
            showLogger("Cant find that Game ID");
            
        }
        if(found){
            /*
            Här ska Game instansen med id i variable number stängas ner
            Denna del görs när en Game Objekt existerar
            
            
            
            
            */
            showLogger("Stops Game with id: " + number);
        }
        
    }
    
    private void clearLogger(){
        this.textField.setText("");
    }
    private void showAllOnlineUsers(){
        String responseText = "Online \n---------------\n";
        for(int i = 0; i < clients.size(); i++){
            if(!clients.get(i).isShutdown){
                responseText+= clients.get(i).getName() + " -- " + clients.get(i).getUserStatus() + "\n";
            }
        }
        if(clients.size() == 0){
            responseText += "No Active Users";
        }
        showLogger(responseText);
        
    }
    
    public static void main(String[] args){
    InitRainingServer server = new InitRainingServer();
    
}

    
}




