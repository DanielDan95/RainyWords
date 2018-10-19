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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;

import RainingServer.Message;

public class connectClient implements Runnable{
	
    //Settings UI
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    
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
    
    int counter = 0;
    
    public connectClient(JFrame frame, String username){
    	this.frame = frame;
    	this.old = (JPanel) frame.getContentPane();
    	setupUI();
    	try {
            this.socket = new Socket(HOST, SERVERPORT);
            this.writer = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
            this.game.setWriter(writer);
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
                int x = 5;
                try {
                    //Ta bort detta sen
                    /////////////////////////////////////////////////////////
                    final JFXPanel fxPanel = new JFXPanel();
                    String bip = "src\\RainingClient\\assets\\music.mp3";
                    Media hit = new Media(new File(bip).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(hit);
                    
                    mediaPlayer.play();
                    /////////////////////////////////////////////////////////
                    for(int i = 0; i < 5; i++)  {
                        JOptionPane jop = new JOptionPane("Matched with: " + message.getMessage() + "\n" + "game starts in: " + x
                                , JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                        frame.add(jop);
                        JDialog dialog = new JDialog();
                        dialog.setModal(true);
                        dialog.setContentPane(jop);
                        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                        dialog.pack();
                        dialog.setLocationRelativeTo(frame);
                        Timer timer = new Timer(1000, new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                dialog.dispose();
                            }
                        });
                        timer.start();
                        dialog.setVisible(true);
                        x--;
                    }
                    sendDataToServer(2, "Has entered game");

                }
                catch (Exception e) {
                    System.out.println("timer fault");
                }
            //incoming settings
            case 20:
                //handleIncomingSettings();
                break;
            case 100:
                incomingWord(message.getMessage());
                break;
            case 101:
                removeWord(message.getMessage());
            default:
                System.out.println("Can not understand Status from server: " + message.getStatus());
        }
        
    }
    private void removeWord(String wordRemove){
        this.game.removeWord(wordRemove);
    
    }
    private void incomingWord(String word){
        this.game.addWord(word);
    }
    public void setupUI(){
    	JPanel panel = new JPanel(new GridBagLayout());
    	JPanel headerPane = new JPanel(new GridBagLayout());
    	JPanel gamePane = new JPanel();
    	JPanel inputPane = new JPanel(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	
    	JLabel scoreLabel = new JLabel("Score: 999");
    	JLabel timeLabel = new JLabel("Time: 23 sec");
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
        
        frame.setContentPane(panel);
		frame.revalidate();
		
    	game = new clientGame(gamePane, scoreLabel);
        Thread thread = new Thread(game);
        thread.start();
        
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
		disconnectBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutdownSequence();
				//game.addWord("test" + counter);
				//counter++;
			}
        });
    }
    public void shutdownSequence(){
        try {
        	sendDataToServer(-1, "DISCONNECT");
            this.close = true;
            this.input.close();
            this.writer.flush();
            this.writer.close();
            try {
                Thread.sleep(100);
                this.socket.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(connectClient.class.getName()).log(Level.SEVERE, null, ex);
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