package RainingServer;

import java.net.*;

public class handleClient implements Runnable{
    Socket client;
    
    public handleClient(Socket client){
        this.client = client;
    }
   
    public void run() {
        System.out.println("Client Connected!");
    }
    
}
