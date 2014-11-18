package cn.zju.ricky;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

public class Player {
	private String name;
	private int score;
	private static final Toolkit TLK = Toolkit.getDefaultToolkit(); // 用来将图片读入Image数组
	private final Image head;
	private int state; // 玩家当前该击什么球
	private final AffineTransform at = new AffineTransform();

	public Player(int n, String name) {
		head = TLK.getImage(Player.class.getClassLoader().getResource(
				"Image/player/player" + n + ".jpg"));
		score = 0;
		this.name = name;
		state = 0;
		at.translate(5, 5);
		at.scale(0.5, 0.5);
	}
	public String getName(){
		return name;
	}
	public int getState() {
		return state;
	}
	public int getScore() {
		return score;
	}
	public void setState(int n) {
		state = n;
	}

	public void add(int n) {
		score += n;
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 300, 200);
		g2d.drawImage(head, at, null);
		g2d.setPaint(Color.red);
		g2d.setFont(new Font("Algerian", Font.BOLD, 24));
		g2d.drawString("Score: " + score, 104, 20);
		g2d.setFont(new Font("Bradley Hand ITC", Font.BOLD, 24));
		g2d.drawString(name, 104, 50);
		// g2d.setColor(Color.cyan);
		// g2d.setFont(new Font("楷体",Font.CENTER_BASELINE,20));
		// g2d.drawString("你应当击", 104,76);
		// g2d.setFont(new Font("楷体",Font.CENTER_BASELINE,48));
		// g2d.setColor(Color.red);
		// g2d.drawString((state==1)?"彩":"红", 120, 120);
	}
}
