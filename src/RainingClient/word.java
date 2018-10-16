package RainingClient;

public class word {
	
	String word;
	double xpos;
	double ypos;
	double speed;
	
	public word(String word, double xpos, double ypos, double speed) {
		super();
		this.word = word;
		this.xpos = xpos;
		this.ypos = ypos;
		this.speed = speed/1000;
	}

	public double getYpos() {
		return ypos;
	}
	
	public void setYpos(double ypos) {
		this.ypos = ypos;
	}
	
	public void addYpos(double add) {
		this.ypos += add;
	}
	
	public double getXpos() {
		return xpos;
	}
	
	public String getWord() {
		return word;
	}

	public double getSpeed() {
		return speed;
	}
	
}
