import java.net.*;


public class Start {
    public static void main(String args []) throws Exception {
        int port = 5000;
        try {
            ServerSocket socket = new ServerSocket(port);

        }
        catch (Exception E) {
            System.exit(1);
        }

    }
}
