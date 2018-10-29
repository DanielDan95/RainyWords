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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    	JPanel logoPane = new JPanel(new GridBagLayout());
    	JPanel menuPane = new JPanel(new GridBagLayout());
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

        JLabel statusLabel = new JLabel("Waiting for opponent...");
        statusLabel.setVisible(false);
        
        
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;  
        gbc.weighty = 0; 
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPane.add(playBtn, gbc);
        gbc.gridy = 1;
        menuPane.add(statusLabel, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
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
        spacerPane.setPreferredSize(new Dimension(300, 100));
        panel.add(spacerPane, gbc);

        frame.setContentPane(panel);
		frame.revalidate();
        
        playBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setVisible(true);
				client = new connectClient(frame, username);
	            Thread thread = new Thread(client);
	            thread.start();
			}
        });
    }
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		clientManager asd = new clientManager();
	}
}
