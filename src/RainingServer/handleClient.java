package RainingServer;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class handleClient implements Runnable{
    Socket client;
    String name = "Anonymous";
    String userStatus = "Idle";

    
    ObjectOutputStream writer;
    ObjectInputStream input;
    
    boolean close = false;
    
    public handleClient(Socket client){
        this.client = client;
        
        try {
            this.input = new ObjectInputStream(this.client.getInputStream());
            this.writer = new ObjectOutputStream(this.client.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error: Server could not open Out/In Streams to Client");
        }
    }

   
    public void run() {
        System.out.println("Client Connected!");
        while(!this.close){
            try {
                Message message = (Message) this.input.readObject();
                System.out.println("String recieved: " + message.getMessage());
                handleMessage(message);
               
            } catch (IOException ex) {
                Logger.getLogger(handleClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(handleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }
    public void handleMessage(Message message){
        switch(message.getStatus()){
            case -1:
            	System.out.println("HERRO dis");
                shutdownSequence(false);
                break;
            case 1:
            	System.out.println("HERRO name");
                this.name = message.getMessage();
                break;
            
            default:
                System.out.println("Could not handle Message with status: " + message.getStatus());
            
        }
    
    
    }
    
    
    
    //This method create a Message Object and send it to Client
    public void sendMessage(int status, String toSend){
        Message message = new Message(status, toSend);
        try {
            this.writer.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(handleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    //GETTERS
    public String getUserStatus() {
        return userStatus;
    }
    public String getName() {
        return name;
    }
    public void changeState(int state){
        if(state == 1){
            this.userStatus = "Idle";
        }
        else if(state == 2){
            this.userStatus = "Waiting Oppon";
        }
        else if(state == 3){
            this.userStatus = "Playing";
        }
    }
    
    
    //This Method shutdowns all streams and inputs is if its server or client that initialized shutdown
    public void shutdownSequence(boolean isServerCall){
         System.out.println("ShutdownSequence Called");
        if(isServerCall){
            try {
                this.sendMessage(-1, "DISCONNECT");
                this.input.close();
                try {
                    Thread.sleep(100);
                    this.writer.flush();
                    this.writer.close();
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(handleClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(handleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            try {
                System.out.println("ShutdownSeq for Client Called");
                this.close = true;
                this.input.close();
                this.writer.close();
                this.client.close();
            } catch (IOException ex) {
                
            }
        }
    }
    
}
