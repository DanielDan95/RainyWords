package RainingClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;

import RainingServer.Message;

public class connectClient implements Runnable{
	
    //Settings UI
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    JLabel timeLabel;
    JLabel scoreLabel;
    JComboBox<String> languageList;
    JComboBox<String> diffList;
    JSlider slider;
    JTextField usernameField;
    private String username = "Guest";
    JButton settingsPlayBtn;
    JLabel settingsTimeLabel;
    JLabel statLabel;
    
    
    
    //Settings Connection
    final int SERVERPORT = 23456;
    final String HOST = "localhost";

    Socket socket;
    ObjectOutputStream writer;
    ObjectInputStream input;
    
    boolean close = true;
    
    private clientGame game;
    
    JFrame frame;
    JPanel old;
    JPanel panel;
    JPanel settingsPane;
    JPanel gamePane;
    
    int counter = 0;
    
    MediaPlayer mediaPlayer;
    
    
    
    
    public connectClient(JFrame frame, String username, JLabel statusLabel){
        this.game = new clientGame();
        this.frame = frame;
        this.statLabel = statusLabel;
    	this.old = (JPanel) frame.getContentPane();
    	setupUI();
    	try {
            this.socket = new Socket(HOST, SERVERPORT);
            this.writer = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
            
        	close = false;
        	sendDataToServer(1, username);
        }
    	catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
            shutdownSequence();
        }
    	catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            shutdownSequence();
    	}
        
    	
    }
    
    public void sendDataToServer(int status, String toSend) {
        try {
            Message message = new Message(status,toSend);
            
            this.writer.writeObject(message);
            System.out.printf("Sent message to server: %s\n", toSend);
        } catch (Exception ex) {
            System.out.println("CRASHHs");
        }
    }
    public void handleMessage(Message message){
        switch(message.getStatus()){
            case -1:
                shutdownSequence();
                break;
            case 1:
                System.out.println("Found opponent");
		frame.setContentPane(settingsPane);
                this.statLabel.setVisible(false);
		frame.revalidate();
                break;
            case 2:
            	System.out.println("Game starting");
                frame.setContentPane(panel);
		frame.revalidate();
		
                game.initClientGame(gamePane, scoreLabel);
                this.game.setWriter(this.writer);
            	Thread thread = new Thread(game);
                thread.start();
                break;
            case 9:
                game.setId(Integer.parseInt(message.getMessage()));
                break;
            //incoming settings
            case 20:
                removeAllActionListeners();
                importSettings(message.getMessage());
                addAllActionListeners();
                break;
            //call from server that new word incoming
            case 100:
                incomingWord(message.getMessage());
                break;
            //Call from server that Client should delete word
            case 101:
                removeWord(message.getMessage());
                break;
            //Adding score that server calls Client
            case 105:
                this.scoreLabel.setText("Score: " + message.getMessage());
                break;
                
            //Call from server to broadcast current timer countdown    
            case 110:
                setGameTimer(message.getMessage());
                break;
            //Call from server that game is ended and result incoming
            case 200:
                showResultTable(message.getMessage());
                break;
            default:
                System.out.println("Can not understand Status from server: " + message.getStatus());
        }
        
    }
    private void showResultTable(String message){
        int score1 = Integer.parseInt(message.substring(0, message.indexOf(" ")));     
        int score2 = Integer.parseInt(message.substring(message.indexOf(" ")+1, message.length()));
        
        int yourScore = 0;
        int opponentScore = 0;
        if(game.getId() == 1){
            yourScore = score1;
            opponentScore = score2;
        }
        else if(game.getId() == 2){
            yourScore = score2;
            opponentScore = score1;
        }
        if(yourScore > opponentScore){
            JOptionPane.showMessageDialog(frame, "You Won!\nYou: "+ yourScore + " VS Opponent: "+opponentScore );
        }
        else if(yourScore < opponentScore){
            JOptionPane.showMessageDialog(frame, "You Lost!\nYou: "+ yourScore + " VS Opponent: "+opponentScore );
        }
        else if(yourScore == opponentScore){
            JOptionPane.showMessageDialog(frame, "Match is a Tie!\nYou: "+ yourScore + " VS Opponent: "+opponentScore );
        }
        
      
    
    }
    
    private void setGameTimer(String message){
        int time = Integer.parseInt(message);
        this.timeLabel.setText("Time: "+time+" sec left");
    }
    private void removeWord(String wordRemove){
        this.game.removeWord(wordRemove);
    
    }
    private void incomingWord(String word){
        this.game.addWord(word);
    }
    public void setupUI(){
    	panel = new JPanel(new GridBagLayout());
    	JPanel headerPane = new JPanel(new GridBagLayout());
    	gamePane = new JPanel();
    	JPanel inputPane = new JPanel(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	
    	scoreLabel = new JLabel("Score: 0");
    	timeLabel = new JLabel("Time: 300 sec left");
        JButton disconnectBtn = new JButton("Disconnect");
        JTextField wordInput = new JTextField();
        
        gamePane.setPreferredSize(new Dimension(300, 300));
        wordInput.setPreferredSize(new Dimension(100, 30));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;
        gbc.weighty = 0;
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        headerPane.add(timeLabel, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.gridx = 1;
        headerPane.add(disconnectBtn, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.gridx = 2;
        headerPane.add(scoreLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.gridx = 0;
        inputPane.add(wordInput, gbc);
        
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0;
        gbc.gridy = 0;
        panel.add(headerPane, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridy = 1;
        panel.add(gamePane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 0;  
        gbc.weighty = 0; 
        gbc.gridy = 2;
        panel.add(inputPane, gbc);
        
        gamePane.setBorder(BorderFactory.createLineBorder(Color.black));

        usernameField = new JTextField();
        usernameField.setText(this.username);
        usernameField.setPreferredSize(new Dimension(200, 20));
        usernameField.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    sendDataToServer(1, usernameField.getText());
                    username = usernameField.getText();
                }
            });
        settingsPane = new JPanel(new GridBagLayout());
        JLabel settingsLabel = new JLabel("Settings");
        JLabel settingNameLabel = new JLabel("Username");
        settingsPlayBtn = new JButton("Play"); 
        JButton closeBtn = new JButton("Back");
        JLabel languageLabel = new JLabel("Language");
        JLabel diffLabel = new JLabel("Difficulty");
        JLabel timerLabel = new JLabel("Time Limit");
        settingsTimeLabel = new JLabel("300");

        String[] languageChoices = {"English", "Svenska"};
        languageList = new JComboBox<String>(languageChoices);
        languageList.setSelectedIndex(0);
        
        
        String[] diffChoices = {"Easy", "Medium", "Hard"};
        diffList = new JComboBox<String>(diffChoices);
        diffList.setSelectedIndex(0);
        
        
        slider = new JSlider(JSlider.HORIZONTAL,60,600,300);
        
        addAllActionListeners();
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsPane.add(settingsLabel, gbc);
        gbc.gridy = 1;
        settingsPane.add(settingNameLabel, gbc);
        gbc.gridy = 2;
        settingsPane.add(diffLabel, gbc);
        gbc.gridy = 3;
        settingsPane.add(languageLabel, gbc);
        gbc.gridy = 4;
        settingsPane.add(timerLabel, gbc);
        gbc.gridy = 6;
        settingsPane.add(settingsPlayBtn, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        settingsPane.add(usernameField, gbc);
        gbc.gridy = 2;
        settingsPane.add(diffList, gbc);
        gbc.gridy = 3;
        settingsPane.add(languageList, gbc);
        gbc.gridy = 4;
        settingsPane.add(slider, gbc);
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        settingsPane.add(settingsTimeLabel, gbc);
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        settingsPane.add(closeBtn, gbc);
        
        closeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutdownSequence();
			}
        });
        settingsPlayBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                            settingsPlayBtn.setEnabled(false);
                            sendDataToServer(22, ""+game.getId());
			}
        });
		disconnectBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutdownSequence();
			}
        });
        wordInput.addKeyListener(new KeyListener(){
        	@Override
        	public void keyPressed(KeyEvent e) {
        	}

        	@Override
        	public void keyReleased(KeyEvent e) {
        		game.compareWord(wordInput.getText(), wordInput);
        	}

        	@Override
        	public void keyTyped(KeyEvent e) {
        	}
        });
    }
    public void removeAllActionListeners(){
        ChangeListener[] cl = slider.getChangeListeners();
        slider.removeChangeListener(cl[0]);
        
        ActionListener[] al = diffList.getActionListeners();
        diffList.removeActionListener(al[0]);
        
        al = languageList.getActionListeners();
        languageList.removeActionListener(al[0]);
        
        
    
    }
    public void addAllActionListeners(){
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            settingsTimeLabel.setText(""+((JSlider)e.getSource()).getValue());
            System.out.println("exported slider called");
            exportSettings();
            }
	});
        
        diffList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
		exportSettings();
            }
        });
        languageList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				exportSettings();
			}
        });
        
    
    }
    
    public void exportSettings(){
        String resp = ""+this.diffList.getSelectedIndex();
        resp += " " + this.languageList.getSelectedIndex();
        resp += " " + this.slider.getValue();
        
        sendDataToServer(20, resp);
        System.out.println("Clients Settings sended to Server");
        
    }
    public void importSettings(String req){
        
        System.out.println("Inc Settings: " + req);
        this.diffList.setSelectedIndex(Integer.parseInt(req.substring(0, req.indexOf(" "))));
        req = req.substring((req.indexOf(" ")+1), req.length());
        this.languageList.setSelectedIndex(Integer.parseInt(req.substring(0, req.indexOf(" "))));
        req = req.substring((req.indexOf(" ")+1), req.length());
        this.slider.setValue(Integer.parseInt(req));
        this.settingsTimeLabel.setText(req);
        
        this.settingsPlayBtn.setEnabled(true);
        
        frame.revalidate();
        System.out.println("Imported Settings");
        
    }
    
    public void shutdownSequence(){
        try {
            sendDataToServer(-1, game.getId() + " DISCONNECT");
            this.close = true;
            this.input.close();
            this.writer.flush();
            this.writer.close();
            mediaPlayer.stop();
            try {
                Thread.sleep(100);
                this.socket.close();
            } catch (InterruptedException ex) {
            }
        } catch (IOException ex) {
        } catch (NullPointerException ne) {
        }
    }

	@Override
	public void run() {
		while(!close){
            try {
                Message message = (Message) this.input.readObject();
                handleMessage(message);
            } catch (IOException ex) {
                
            } catch (ClassNotFoundException ex) {
                
            }
        }
		frame.setContentPane(old);
		frame.revalidate();
	}
}