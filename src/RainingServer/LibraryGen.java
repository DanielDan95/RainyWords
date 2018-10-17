package RainingServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class LibraryGen extends JFrame implements ActionListener{
     final String nameOfFile = "swedish";
     PrintWriter writer;
     JTextField text;
     boolean exitLoop = false;
     
     public LibraryGen() throws IOException{
         this.setSize(700,500);
         
         this.setDefaultCloseOperation(EXIT_ON_CLOSE);
         text = new JTextField(20);
         this.setLayout(null);
         text.setBounds(50, 370, 600, 30);
         text.addActionListener(this);
         this.add(text);
         this.setVisible(true);
         try {
             writer = new PrintWriter(new FileWriter("src/RainingServer/Library/"+ nameOfFile +".txt", true));
         } catch (FileNotFoundException ex) {
             Logger.getLogger(LibraryGen.class.getName()).log(Level.SEVERE, null, ex);
         } catch (UnsupportedEncodingException ex) {
             Logger.getLogger(LibraryGen.class.getName()).log(Level.SEVERE, null, ex);
         }
        
     }
     
     
    
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        LibraryGen gen = new LibraryGen();
        
        
        
        
    }

    public void saveWord(String ord){
        writer.println(ord );
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        exitLoop = false;
        String message = text.getText();
        text.setText("");
        try {
            int i = Integer.parseInt(message);
            if(i == 0){
                this.writer.close();
                System.exit(0);
            }
        } catch (NumberFormatException ex) {
            
        }
        
        while(!exitLoop){
            String word;
            if(message.contains(" ")){
                word = message.substring(0,message.indexOf(" "));
                message = message.substring(message.indexOf(" ") +1 , message.length());
                this.saveWord(word);
                System.out.println(word);
            }
            else if(message.length() > 0){
                this.saveWord(message);
                System.out.println(message);
                message = "";
            }
            else{
                this.exitLoop = true;
                System.out.println("Exit Loop");
            }
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LibraryGen.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
            
        }
        
        
    }
}
