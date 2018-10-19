package RainingServer;

import java.io.*;
import java.util.ArrayList;

public class Library {
    
    ArrayList<String> wordlist = new ArrayList<String>();
    public Library(String lang){
        
        try (BufferedReader br = new BufferedReader(new FileReader("src/RainingServer/Library/"+lang+".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordlist.add(line);
            }
        }catch(Exception ex){
        
        }
    
    }
    public String getRandomWord(){
        int number = (int) (Math.random()*this.wordlist.size());
        
        return wordlist.get(number);
    }
    
    
}
