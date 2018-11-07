package RainingServer;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library {
    
    ArrayList<String> wordlist = new ArrayList<String>();
    ArrayList<String> wordlist2 = new ArrayList<String>();
    ArrayList<String> wordlist3 = new ArrayList<String>();
    
    String[] lang = {"swedish", "english", "number"};
    String currentLang = "english";
    
    ArrayList<String> current;
    boolean debug;
    
    public Library(boolean debug){
        this.debug = debug;
        if(debug){
    
        try (BufferedReader br = new BufferedReader(new FileReader("src/RainingServer/Library/"+lang[0]+".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordlist.add(line);
            }
        }catch(Exception ex){
        
        }
        try (BufferedReader br = new BufferedReader(new FileReader("src/RainingServer/Library/"+lang[1]+".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordlist2.add(line);
            }
        }catch(Exception ex){
        
        }
        try (BufferedReader br = new BufferedReader(new FileReader("src/RainingServer/Library/"+lang[2]+".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordlist3.add(line);
            }
        }catch(Exception ex){
        
        }
        }
        else{
        try{
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("Library/"+lang[0]+".txt");
        try {
            BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(is, "UTF-8")));
            String line;
            while ((line = br.readLine()) != null) {
                wordlist.add(line);
            }
        }catch(Exception ex){
        
        }
        is = this.getClass().getClassLoader().getResourceAsStream("Library/"+lang[1]+".txt");
        try {
            BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(is, "UTF-8")));
            String line;
            while ((line = br.readLine()) != null) {
                wordlist2.add(line);
            }
        }catch(Exception ex){
        
        }
        is = this.getClass().getClassLoader().getResourceAsStream("Library/"+lang[2]+".txt");
        try {
            BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(is, "UTF-8")));
            String line;
            while ((line = br.readLine()) != null) {
                wordlist3.add(line);
            }
        }catch(Exception ex){
        
        }
        }catch(Exception e){
        
        }
        
        
        }
    
    }
    public Library(String lang){
        
        try (BufferedReader br = new BufferedReader(new FileReader("RainingServer/Library/"+lang+".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordlist.add(line);
            }
        }catch(Exception ex){
        
        }
    
    }

    public void setCurrentLang(String currentLang) {
        this.currentLang = currentLang;
    }
    
    private void getCurrentList(){
        if(this.currentLang.equals("swedish")){
            System.out.println("Found Swedish Dict");
            this.current = wordlist;
        }
        else if(this.currentLang.equals("english")){
            this.current = wordlist2;
        }
        else if(this.currentLang.equals("number")){
            this.current = wordlist3;
        }
        
    }
    public String getRandomWord(){
        getCurrentList();
        int number = (int) (Math.random()*this.current.size());
        System.out.println("Number:" + number);
        return current.get(number);
    }
    
    
}
