package cn.zju.ricky;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Ball {
	private Speed speed; // 球的速度
	private Point2D position; // 球中心当前位置
	private final Point2D originPos;
	private Point2D lastPos; // 球上一次的位置
	private final int value; // 球的分值
	private final int type;
	private boolean ball_in; // 球是否进网
	private final Image ballImg; // 球的图片
	private static final double E = 0.88; // 碰撞系数
	private static final double force = 0.02; // 球桌的阻力
	public static final double radius = 10.0; // 球的半径
	public static final AudioClip collisound = Applet.newAudioClip(Ball.class
			.getClassLoader().getResource("Sound/collib.au"));
	private final double imgWidth = 54.0;
	private static final Toolkit TLK = Toolkit.getDefaultToolkit(); // 用来将图片读入Image数组
	private static final HashMap<String, Integer> bvalue = new HashMap<String, Integer>();
	static {
		bvalue.put("white", 0);
		bvalue.put("red", 1);
		bvalue.put("yellow", 2);
		bvalue.put("green", 3);
		bvalue.put("brown", 4);
		bvalue.put("blue", 5);
		bvalue.put("pink", 6);
		bvalue.put("black", 7);
	}
	private AffineTransform at;

	public Ball(double pos_x, double pos_y, String color) {
		position = new Point2D.Double(pos_x, pos_y);
		lastPos = new Point2D.Double(pos_x, pos_y);
		originPos = new Point2D.Double(pos_x, pos_y);
		ballImg = TLK.getImage(Ball.class.getClassLoader().getResource(
				"Image/ball/" + color + ".png"));
		if (color != "red")
			type = 1;
		else
			type = 0;
		speed = new Speed();
		ball_in = false;
		value = bvalue.get(color);
		at = new AffineTransform();
		// collisound.loop();
	}

	public void setPos(double x, double y) {
		position.setLocation(x, y);
	}

	public void setPos(Point2D p) {
		position.setLocation(p.getX(), p.getY());
	}

	public Point2D getPos() {
		return position;
	}
	public Point2D getLastPos(){
		return lastPos;
	}
	public void setToLastPos() {
		position.setLocation(lastPos);
	}

	public void setSpeed(double vx, double vy) {
		lastPos.setLocation(position);
		speed.set(vx, vy);
	}

	public Speed getSpeed() {
		return speed;
	}

	public boolean isCollision(Ball ball) {
		boolean retn = false;
		if (position.distance(ball.position) < 2 * radius + 2) {
			Speed t = new Speed(ball.position.getX() - this.position.getX(),
					ball.position.getY() - this.position.getY());
			double t1 = this.speed.dot(t);
			double t2 = ball.speed.dot(t);
			if (t1 - t2 > 0)
				retn = true;
		}
		return retn;
	}

	public void collision(Ball ball) {
		double sin, cos, ball_avx, ball_avy, ball_bvx, ball_bvy;
		double xx, yy, dd;
		int flag;
		ball_avx = speed.getX();
		ball_avy = speed.getY();
		ball_bvx = ball.speed.getX();
		ball_bvy = ball.speed.getY();

		yy = position.getY() - ball.position.getY();
		xx = position.getX() - ball.position.getX();
		dd = Math.sqrt(xx * xx + yy * yy);

		sin = Math.abs(yy / dd);
		cos = Math.abs(xx / dd);

		if ((yy) * (xx) >= 0) {
			flag = 1;
			ball_avx = cos * speed.getX() + sin * speed.getY();
			ball_avy = cos * speed.getY() - sin * speed.getX();
			ball_bvx = cos * ball.speed.getX() + sin * ball.speed.getY();
			ball_bvy = cos * ball.speed.getY() - sin * ball.speed.getX();
		} else {
			flag = 0;
			ball_avx = cos * speed.getX() - sin * speed.getY();
			ball_avy = cos * speed.getY() + sin * speed.getX();
			ball_bvx = cos * ball.speed.getX() - sin * ball.speed.getY();
			ball_bvy = cos * ball.speed.getY() + sin * ball.speed.getX();
		}

		this.speed.set(((1 - E) * ball_avx + (1 + E) * ball_bvx) / 2, ball_avy);

		ball.speed.set(((1 + E) * ball_avx + (1 - E) * ball_bvx) / 2, ball_bvy);

		if (flag == 1) {
			ball_avx = cos * speed.getX() - sin * speed.getY();
			ball_avy = cos * speed.getY() + sin * speed.getX();
			ball_bvx = cos * ball.speed.getX() - sin * ball.speed.getY();
			ball_bvy = cos * ball.speed.getY() + sin * ball.speed.getX();
		} else {
			ball_avx = cos * speed.getX() + sin * speed.getY();
			ball_avy = cos * speed.getY() - sin * speed.getX();
			ball_bvx = cos * ball.speed.getX() + sin * ball.speed.getY();
			ball_bvy = cos * ball.speed.getY() - sin * ball.speed.getX();
		}

		this.speed.set(ball_avx, ball_avy);
		ball.speed.set(ball_bvx, ball_bvy);
		collisound.play();
	}

	public void collision2(Ball ball) {
		Speed e12, e21;
		e12 = new Speed(ball.position.getX() - this.position.getX(),
				ball.position.getY() - this.position.getY());
		e12.div(e12.getV());
		e21 = new Speed(this.position.getX() - ball.position.getX(),
				this.position.getY() - ball.position.getY());
		e21.div(e21.getV());

		Speed s1 = e12.mul(this.speed.dot(e12));
		Speed s2 = e21.mul(ball.speed.dot(e21));

		this.speed.sub(s1).add(
				s2.clone().mul(1 + E).add(s1.clone().mul(1 - E)).div(2));
		ball.speed.sub(s2).add(
				s1.clone().mul(1 + E).add(s2.clone().mul(1 - E)).div(2));
	}

	public double distance(Ball ball) {
		return getPos().distance(ball.getPos());
	}

	public void move() {
		position.setLocation(position.getX() + speed.getX(), position.getY()
				+ speed.getY());
		speed.speedDown(force);
	}

	public boolean isIn() {
		return ball_in;
	}

	public void setIn(boolean r) {
		ball_in = r;
		//reset();
	}

	public void reset() {
		position.setLocation(originPos);
		speed.set(0, 0);
		ball_in = false;
	}

	public int score() {
		return value;
	}

	public int getType() {
		return type;
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		at.setToIdentity();
		at.translate(this.position.getX() - radius, this.position.getY()
				- radius);
		at.scale(2 * radius / imgWidth, 2 * radius / imgWidth);

		// g2d.drawImage(ballImg, (int) (position.getX() - radius),(int)
		// (position.getY() - radius), null);
		if (!ball_in)
			g2d.drawImage(ballImg, at, null);
		move();
	}

}
