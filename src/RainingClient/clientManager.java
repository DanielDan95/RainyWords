package RainingClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class clientManager extends JFrame {

	JFrame frame = this;
	connectClient client;
	
	final int WIDTH = 1000;
    final int HEIGHT = 700;
    
    String username = "Guest";
	
	public clientManager(){
		setupUI();
	}
	public void setupUI(){
        this.setSize(this.WIDTH, this.HEIGHT);
        addElements();
        this.setVisible(true);
        this.setTitle("ClientManager: Raining Words");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            { 
                String ObjButtons[] = {"Yes","No"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit?","Online Examination System",
                		JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==JOptionPane.YES_OPTION)
                {
                	try{
                		client.shutdownSequence();
                	} catch (Exception ex){
                		
                	}
                	System.exit(0);
                }
            }
        });
    }
	public void addElements(){
    	JPanel panel = new JPanel(new GridBagLayout());
    	JPanel headerPane = new JPanel(new GridBagLayout());
    	JPanel logoPane = new JPanel(new GridBagLayout());
    	JPanel menuPane = new JPanel(new GridBagLayout());
    	JPanel settingsPane = new JPanel(new GridBagLayout());
    	JPanel spacerPane = new JPanel(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	
    	BufferedImage image = null;
    	try {
    	    image = ImageIO.read(new File("RainingClient/Assets/raining.png"));
    	} catch (IOException ex) {
    	    ex.printStackTrace();
    	}
    	
    	try{
    		JLabel picLabel = new JLabel(new ImageIcon(image));
    	    logoPane.add(picLabel);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	}
        JButton playBtn = new JButton("Play");
        JButton settingsBtn = new JButton("Settings"); 
        JButton saveBtn = new JButton("Save"); 
        JButton closeBtn = new JButton("Close");
        JTextField usernameField = new JTextField();
        JLabel userLabel = new JLabel("User: " + username);
        JLabel settingsLabel = new JLabel("Settings");
        JLabel settingNameLabel = new JLabel("Username:");
        
        usernameField.setPreferredSize(new Dimension(200, 20));
        
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;  
        gbc.weighty = 0; 
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        headerPane.add(userLabel, gbc);
        gbc.gridx = 0;
        menuPane.add(playBtn, gbc);
        gbc.gridy = 1;
        menuPane.add(settingsBtn, gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        settingsPane.add(settingsLabel, gbc);
        gbc.gridy = 1;
        settingsPane.add(settingNameLabel, gbc);
        gbc.gridx = 1;
        settingsPane.add(usernameField, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 2;
        settingsPane.add(closeBtn, gbc);
        gbc.gridx = 0;
        settingsPane.add(saveBtn, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(headerPane, gbc);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 1;
        panel.add(logoPane, gbc);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(menuPane, gbc);
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.gridy = 3;
        panel.add(settingsPane, gbc);
        spacerPane.setPreferredSize(new Dimension(300, 100));
        panel.add(spacerPane, gbc);
        
        settingsPane.setVisible(false);
        
        frame.setContentPane(panel);
		frame.revalidate();
        
        saveBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				username = usernameField.getText();
				userLabel.setText("User: " + username);
		        settingsPane.setVisible(false);
			}
        });
        closeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				usernameField.setText("");
		        settingsPane.setVisible(false);
			}
        });
        playBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				client = new connectClient(frame, username);
	            Thread thread = new Thread(client);
	            thread.start();
			}
        });
        settingsBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				usernameField.setText(username);
				settingsPane.setVisible(true);
			}
        });
    }
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		clientManager asd = new clientManager();
	}
}
