package RainingServer;
public class GameSettings {
    private int time = 300;
    private String language = "english";
    
    private int difficulty = 1;
    
    public GameSettings(){
    }
    
    
    public void setTime(int time) {
        this.time = time;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setdifficulty(int diff) {
        this.difficulty = diff;
    }
    

    public int getTime() {
        return time;
    }

    public String getLanguage() {
        return language;
    }

    public int getDifficulty() {
        return this.difficulty;
    }
    public void updateSettings(String message){
        String time = message.substring(message.indexOf("=")+1, message.indexOf(" "));
        message = message.substring(message.indexOf(" "), message.length());
        String language = message.substring(message.indexOf("=")+1, message.indexOf(" "));
        message = message.substring(message.indexOf(" "), message.length());
        String diff = message.substring(message.indexOf("=")+1, message.length());
        
        this.time = Integer.parseInt(time);
        this.language = language;
        this.difficulty = Integer.parseInt(diff);
    }

    @Override
    public String toString() {
        return "time=" + time + " language=" + language + " difficulty=" + difficulty;
    }
}
