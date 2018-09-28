package RainingServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class InitRainingServer extends JFrame{
    
    //Settings UI
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    
    //Settings Connection
    final int PORT = 23456;
    
    //Clients
    ArrayList<Thread> clients;
    ServerSocket server;
    
    
    
    public InitRainingServer(){
        this.clients = new ArrayList<Thread>();
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
                Thread thread = new Thread(new handleClient(this.server.accept()));
                this.clients.add(thread);
                thread.start();
                
            } catch (IOException ex) {
                Logger.getLogger(InitRainingServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }
    

    
    



    public void setupUI(){
        this.setSize(this.WIDTH, this.HEIGHT);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    }

    
    public static void main(String[] args){
    InitRainingServer server = new InitRainingServer();
    
}
}




