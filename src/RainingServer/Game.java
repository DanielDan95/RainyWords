package RainingServer;

import static java.lang.Math.random;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game implements Runnable{
    
    //Player
    private handleClient player1, player2;
    private int score1 = 0;
    private int score2 = 0;
    
    
    
    //Game
    private int id;
    private GameSettings settings;
    private Library library;
    private int timer;
    
    //Ready Checks
    private boolean ready1 = false;
    private boolean ready2 = false;
    
    //Start Game Check
    private boolean gameStarter = false;
    boolean gameExit = false;
    
    
    //Word List
    ArrayList<String> wordList = new ArrayList<String>();
    
    
    public Game(handleClient player1, handleClient player2, boolean debug){
        this.player1 = player1;
        this.player2 = player2;
        this.settings = new GameSettings();
        this.id = (int) (Math.random()*1000)+1;
        this.library = new Library(debug);
        this.library.setCurrentLang(settings.getLanguage());
        
        this.player1.setGame(this);
        this.player2.setGame(this);
        this.player1.changeState(3);
        this.player2.changeState(3);
        
        this.publishId();
        
    }
    public void importedSettings(String req){
        this.ready1 = false;
        this.ready2 = false;
        
        broadcast(20, req);
        this.settings.setdifficulty(Integer.parseInt(req.substring(0, req.indexOf(" "))));
        req = req.substring((req.indexOf(" ")+1), req.length());
        
        int lang = Integer.parseInt(req.substring(0, req.indexOf(" ")));
        req = req.substring((req.indexOf(" ")+1), req.length());
        
        if(lang == 0){
            this.settings.setLanguage("english");
        }
        else if(lang == 1){
            this.settings.setLanguage("swedish");
        }
        this.settings.setTime(Integer.parseInt(req));
        
        
        
        System.out.println("Imported Settings");
    }
    
    
    
    
    
    //Sending message to both players
    private void broadcast(int status, String message){
        this.player1.sendMessage(status, message);
        this.player2.sendMessage(status, message);
        
    }
    private void publishId(){
        this.player1.sendMessage(9, "1");
        this.player2.sendMessage(9, "2");
    
    }
    public void handleCommands(Message message){
        switch(message.getStatus()){
            case 20:
                importedSettings(message.getMessage());
                break;
            case 21:
                //updateSettings(message.getMessage());
                break;
            case 22:
                readyCheck(message.getMessage());
                break;
            case 100:
                break;
            case 101:
                validateWord(message.getMessage());
                break;
            
                
        }
    
    }

    public int getId() {
        return this.id;
    }
    
    public void clientShutdownGame(String message){
        /*int playerId = Integer.parseInt(message.substring(0,1));
        if(playerId == 1){
            this.player2.shutdownSequence(true);
        }
        else if(playerId == 2){
            this.player1.shutdownSequence(true);
        }*/
        this.player1.shutdownSequence(true);
        this.player2.shutdownSequence(true);
        this.gameExit = true;
    
    }
    public void validateWord(String message){
        int playerId = Integer.parseInt(message.substring(0,1));
        message = message.substring(2, message.length());
        //int playerId = 1;
        String word = message;
        try {
            for(int i = 0; i < this.wordList.size(); i++){
                if(word.equals(wordList.get(i))){
                    wordList.remove(i);
                    int points = word.length();
                    if(playerId == 1){
                        score1 += points;
                      //  this.player1.sendMessage(105, ""+score1);
                    }
                    else if(playerId == 2){
                        score2 += points;
                    //    this.player2.sendMessage(105, ""+score2);
                    }
                    String scoreLabel = this.player1.name + ": " + score1 + "    " + this.player2.name + ": " + score2;
                    broadcast(105, scoreLabel);
                    broadcast(101, word);
                }
            }
        } catch (Exception e) {
        }
        
    
    
    }
    
    public void readyCheck(String message){
        int player = Integer.parseInt(message);
        System.out.println("Player " + player + " is Ready");
        if(player == 1){
            this.ready1 = true;
        }
        else if(player == 2){
            this.ready2 = true;
        }
        if(ready1 && ready2){
            this.gameStarter = true;
            this.library.setCurrentLang(settings.getLanguage());
            broadcast(2, "");
            String scoreLabel = this.player1.name + ": " + score1 + "    " + this.player2.name + ": " + score2;
            broadcast(105, scoreLabel);
        }
    }
    
    
    
    private void updateSettings(String message){
        this.settings.updateSettings(message);
    }
    
    private void sendResultTable(){
        broadcast(200, ""+score1 + " " + score2);
    
    }
    public void shutdownGame(){
            this.gameExit = true;
            player1.shutdownSequence(true);
            player2.shutdownSequence(true);
    
    }
    
    @Override
    public void run() {
        while(!gameStarter){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Game Initializing");
        System.out.println("Lang: " + settings.getLanguage());
        this.timer = settings.getTime();
        System.out.println("Step 1");
        while(timer >= 0 && this.gameExit == false){
            broadcast(110, ""+this.timer);
            System.out.println("Time: " + this.timer);
            try {
                Thread.sleep(1000);
                this.timer--;
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for(int t = 0; t < 2*settings.getDifficulty();t++){
                String currentWord = this.library.getRandomWord();
                System.out.println("word: " + currentWord);
                this.wordList.add(currentWord);
                broadcast(100, currentWord);
            }
            
        }
        if(!this.gameExit){
            sendResultTable();
            shutdownGame();
            
        }
        
        
    
    
    }
    
    
}
