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
    ArrayList<Thread> threads;
    ArrayList<handleClient> clients;
    ServerSocket server;
    
    
    
    public InitRainingServer(){
        this.threads = new ArrayList<Thread>();
        this.clients = new ArrayList<handleClient>();
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
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    }

    
    public static void main(String[] args){
    InitRainingServer server = new InitRainingServer();
    
}
}




