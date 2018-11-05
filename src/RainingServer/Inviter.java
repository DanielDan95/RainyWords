package RainingServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Inviter implements Runnable{
    private ServerSocket server;
    private ArrayList<handleClient> clients;
    private ArrayList<Thread> threads;
    private ReentrantLock lock;
    
    
    public Inviter(ServerSocket server, ArrayList<handleClient> clients, ArrayList<Thread> threads, ReentrantLock lock){
        this.server = server;
        this.clients = clients;
        this.threads = threads;
        this.lock = lock;
    }
    
    
    public void run() {
        while(true){
        try {
            handleClient client = new handleClient(this.server.accept());
            
            this.clients.add(client);
            Thread thread = new Thread(client);
            this.threads.add(thread);
            thread.start();
            
            System.out.println("Client Coonnected with INV");
        } catch (IOException ex) {
            Logger.getLogger(Inviter.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    
    }
    
}
