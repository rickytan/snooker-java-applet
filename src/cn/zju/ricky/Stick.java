package cn.zju.ricky;

import java.awt.*;
import java.awt.geom.*;

public class Stick {
	private double angle, old_angle; // ��˽Ƕ�
	private double stick_x, stick_y;
	private final Image stickImg; // ���ͼƬ
	private final int imgWidth = 13;
	private boolean hitting; // ���򶯻��߳�
	private Ball centerBall; // ����������
	private Point mouse;
	private double ti;
	private double dis, old_dis; // �˵���ľ���
	private double force; // ��������
	private static final Toolkit TLK = Toolkit.getDefaultToolkit(); // ������ͼƬ����Image����
	private AffineTransform at = null; // ���ڸ˵���ת
	private boolean hidden = true;

	public Stick(Ball ball) {
		stickImg = TLK.getImage(Stick.class.getClassLoader().getResource(
				"Image/stick/stick.png"));
		centerBall = ball;
		old_angle = -Math.PI / 2;
		angle = 0.0;
		old_dis = dis = 20.0;
		stick_x = imgWidth;
		stick_y = dis;
		at = new AffineTransform();
		mouse = new Point(300, 300);
		ti = 5 * Math.PI;
	}

	public void setCenterBall(Ball ball) {
		centerBall = ball;
	}

	public void setMouse(Point p) {
		mouse = p;
	}

	public void hide(boolean h) {
		hidden = h;
	}

	public void hitBall(double force) {
		hitting = true;
		this.force = force;
	}

	public void putMainBall(Ball ball) {
		if (Table.inMainBallArea(mouse)) {
			ball.setPos(mouse);
		}
	}

	public void setDirection() {
		angle = Math.atan2(mouse.y - stick_y, mouse.x - stick_x);
	}

	public boolean getHideStatus() {
		return hidden;
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		double x = centerBall.getPos().getX();
		double y = centerBall.getPos().getY();

		if (hitting) {
			dis = 10 * Math.cos(ti) + 30;
			ti -= 0.22;
			if (ti < 0) {
				hitting = false;
				ti = 5 * Math.PI;
				centerBall.setSpeed(force * Math.cos(angle), force
						* Math.sin(angle));
				hide(true);
				dis = 20;
			}
		} else
			setDirection();

		if (x != stick_x || y != stick_y || old_angle != angle
				|| old_dis != dis) {
			at.setToIdentity();
			at.translate(x - imgWidth / 2, y + dis);
			at.rotate(angle + Math.PI / 2, imgWidth / 2, -dis);
			stick_x = x;
			stick_y = y;
			old_angle = angle;
			at.scale(0.7, 0.7);
		}
		if (!hidden) {
			g2d.drawImage(stickImg, at, null);
		}
	}
}
