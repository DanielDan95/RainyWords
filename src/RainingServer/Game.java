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
    private int timer;
    
    //Ready Checks
    private boolean ready1 = false;
    private boolean ready2 = false;
    
    //Start Game Check
    private boolean gameStarter = false;
    
    //Word List
    ArrayList<String> wordList = new ArrayList<String>();
    
    
    public Game(handleClient player1, handleClient player2){
        this.player1 = player1;
        this.player2 = player2;
        this.settings = new GameSettings();
        this.id = (int) (Math.random()*1000)+1;
        
        this.player1.setGame(this);
        this.player2.setGame(this);
    }
    
    
    
    
    //Sending message to both players
    private void broadcast(int status, String message){
        this.player1.sendMessage(status, message);
        this.player2.sendMessage(status, message);
        
    }
    private void handleCommands(Message message){
        switch(message.getStatus()){
            case 20:
                sendSettings();
                break;
            case 21:
                updateSettings(message.getMessage());
                break;
            case 22:
                readyCheck(message.getMessage());
                break;
            case 100:
                break;
            case 101:
                validateWord(message.getMessage());
                
        }
    
    }
    public void validateWord(String message){
        int playerId = Integer.parseInt(message.substring(message.indexOf("="+1), message.indexOf(" ")));
        message = message.substring(message.indexOf(" "+1), message.length());
        String word = message;
        try {
            for(int i = 0; i < this.wordList.size(); i++){
                if(word.equals(wordList.get(i))){
                    wordList.remove(i);
                    int points = word.length();
                    if(playerId == 1){
                        score1 += points;
                    }
                    else if(playerId == 2){
                        score2 += points;
                    }
                    broadcast(102, word);
                }
            }
        } catch (Exception e) {
        }
        
    
    
    }
    
    public void readyCheck(String message){
        int player = Integer.parseInt(message);
        if(player == 1){
            this.ready1 = true;
        }
        else if(player == 2){
            this.ready2 = true;
        }
        if(ready1 && ready2){
            this.gameStarter = true;
        }
    }
    
    
    private void sendSettings(){
        broadcast(20, settings.toString());
        this.ready1 = false;
        this.ready2 = false;
    }
    private void updateSettings(String message){
        this.settings.updateSettings(message);
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
        while(timer < settings.getTime()){
            try {
                Thread.sleep(1000);
                this.timer++;
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(int t = 0; t < 2*settings.getDifficulty();t++){
                broadcast(100, word);
            }
        }
    
    
    }
    
    
}
