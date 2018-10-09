package RainingClient;

import java.io.Serializable;

public class Message implements Serializable{

    private int status;
    private String message;

    public Message() {
    
    }
    public Message(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
    
    

   



    
}
