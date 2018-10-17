package RainingClient;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class clientGame implements Runnable{
	Canvas canvas;
	BufferStrategy bufferStrategy;
	
	ArrayList<word> wordList = new ArrayList<word>();
	ArrayList<word> wordsToAdd = new ArrayList<word>();
	int score = 0;
	
	Font wordFont = new Font("TimesRoman", Font.PLAIN, 20);
	JLabel scoreL;
	
	Random rand = new Random();
	
	public clientGame(JPanel panel, JLabel scoreLabel){
		canvas = new Canvas();
		canvas.setBounds(0, 0, panel.getWidth()-10, panel.getHeight()-10);
		canvas.setIgnoreRepaint(true);
		panel.add(canvas);
		panel.setBackground(Color.WHITE);
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		canvas.requestFocus();
		scoreL = scoreLabel;
	}
	
	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;
	boolean running = true;
	
	public void run(){
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;
		
		while(running){
			beginLoopTime = System.nanoTime();
			render();
			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update((int) ((currentUpdateTime - lastUpdateTime)/(1000*1000)));
			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;
			
			if(deltaLoop > desiredDeltaLoop){
				//Do nothing. We are already late.
			}else{	
				try{
					Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
				}catch(InterruptedException e){
					//Do nothing
				}
			}
		}
	}
	
	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, 1000, 700);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}
	
	protected void render(Graphics2D g){
		g.setColor(Color.BLACK);
		g.setFont(wordFont);
		for (word obj: wordList) {
			char[] ch = obj.getWord().toCharArray();
			g.drawChars(ch, 0, ch.length, (int)obj.getXpos(), (int)obj.getYpos());
		}
	}
	
	protected void update(int deltaTime){
		for (Iterator<word> iter = wordList.iterator(); iter.hasNext();) {
			word obj = iter.next();
			obj.addYpos(deltaTime *  obj.getSpeed());
			if (obj.getYpos() > canvas.getHeight()+30) {
                            obj.setYpos(0);
                            //iter.remove();
			}
		}
		wordList.addAll(wordsToAdd);
		wordsToAdd.clear();
		scoreL.setText("Score: " + score);
	}
	
	public void addWord(String word){
		wordsToAdd.add(new word(word, rand.nextInt(canvas.getWidth() - (word.length() * 16)), canvas.getY(), (rand.nextInt(100) + 50)));
	}
	
	public void addScore(){
		score++;
	}
	
	public void compareWord(String written, JTextField wordInput){
		for (Iterator<word> iter = wordList.iterator(); iter.hasNext();) {
			word obj = iter.next();
			if(obj.getWord().equals(written)){
		    	iter.remove();
		    	wordInput.setText("");
		    	addScore();
		    }
		}
	}
}