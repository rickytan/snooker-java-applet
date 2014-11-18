package cn.zju.ricky;

import java.applet.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;

public class Table {
	private static final int basex, basey; // 桌子左上角的坐标
	private static final int width, height; // 桌子宽高
	private static final int off_x, off_y; // 桌子内边界宽度
	private static AudioClip ac;
	private static final int holeRadius = 14;
	private static final Point2D[] c_point;
	private static final Point2D[] holes;
	private static final Rectangle2D boundry;
	private static final Area MainBallArea;
	static final Toolkit TLK = Toolkit.getDefaultToolkit(); // 用来将图片读入Image数组
	private static final Image tableImg = TLK.getImage(Table.class
			.getClassLoader().getResource("Image/table/table.png"));
	static {
		width = 742;
		height = 408;
		basex = 0;
		basey = 150;
		off_x = 40;
		off_y = 38;
		c_point = new Point2D[12];
		c_point[0] = new Point2D.Double(basex + off_x - Ball.radius, basey
				+ off_y + 7 + Ball.radius);
		c_point[1] = new Point2D.Double(basex + off_x - Ball.radius, basey
				+ height - off_y - Ball.radius - 9);
		c_point[2] = new Point2D.Double(basex + off_x + 10 + Ball.radius, basey
				+ height - off_y + Ball.radius);
		c_point[3] = new Point2D.Double(width / 2 - Ball.radius - holeRadius,
				basey + height - off_y + Ball.radius);
		c_point[4] = new Point2D.Double(width / 2 + holeRadius + Ball.radius,
				basey + height - off_y + Ball.radius);
		c_point[5] = new Point2D.Double(basex + width - off_x - Ball.radius
				- 10, basey + height - off_y + Ball.radius);
		c_point[6] = new Point2D.Double(basex + width - off_x + Ball.radius,
				basey + height - off_y - Ball.radius - 9);
		c_point[7] = new Point2D.Double(basex + width - off_x + Ball.radius,
				basey + off_y + 7 + Ball.radius);
		c_point[8] = new Point2D.Double(basex + width - off_x - Ball.radius
				- 10, basey + off_y - Ball.radius);
		c_point[9] = new Point2D.Double(width / 2 + holeRadius + Ball.radius,
				basey + off_y - Ball.radius);
		c_point[10] = new Point2D.Double(width / 2 - Ball.radius - holeRadius,
				basey + off_y - Ball.radius);
		c_point[11] = new Point2D.Double(basex + off_x + 10 + Ball.radius,
				basey + off_y - Ball.radius);

		boundry = new Rectangle2D.Double(40.0 + Ball.radius + 2,
				188.0 + Ball.radius + 2, 650.0 - Ball.radius - 4,
				320.0 - Ball.radius - 4);

		holes = new Point2D[6];
		holes[0] = new Point2D.Double(30, 178);
		holes[1] = new Point2D.Double(371, 172);
		holes[2] = new Point2D.Double(712, 178);
		holes[3] = new Point2D.Double(30, 528);
		holes[4] = new Point2D.Double(371, 534);
		holes[5] = new Point2D.Double(712, 528);
		MainBallArea = new Area(new Ellipse2D.Double(115, 296, 114, 114));
		Area s = new Area(new Rectangle2D.Double(172, 296, 60, 114));
		MainBallArea.subtract(s);
	}

	public Table() {

		URL url = null;
		try {
			url = new File("Sound/sample.mid").toURI().toURL();
		} catch (Exception e) {
			// nothing
		}
		try{
		ac = Applet.newAudioClip(url);
		ac.loop();
		}catch (NullPointerException e){
			// nothing
		}
	}

	public boolean isCollision(Ball ball) {
		return !boundry.contains(ball.getPos());
	}

	public void collision(Ball ball) {
		double t_x = ball.getPos().getX();
		double t_y = ball.getPos().getY();

		// 将球桌边界设置成单向通过

		// 如果直接碰到水平方向的桌边，且速度方向与所处的边同向
		if ((basex + off_x + 10 + Ball.radius <= t_x
				&& t_x <= width / 2 - Ball.radius - holeRadius || width / 2
				+ holeRadius + Ball.radius <= t_x
				&& t_x <= basex + width - off_x - Ball.radius - 10)
				&& ball.getSpeed().dot(0, 1)
						* (t_y - (basey + off_y + height / 2)) > 0) {
			ball.setSpeed(ball.getSpeed().getX(), -ball.getSpeed().getY());
			// 如果直接碰到竖直方向的桌边，且速度方向与所处的边同向
		} else if ((basey + off_y + 7 + Ball.radius <= t_y && t_y <= basey
				+ height - off_y - Ball.radius - 9)
				&& ball.getSpeed().dot(1, 0)
						* (t_x - (basex + off_x + width / 2)) > 0) {
			ball.setSpeed(-ball.getSpeed().getX(), ball.getSpeed().getY());
			// 如果碰到桌角，且速度方向正在向桌边靠近
		} else {
			Point2D p = ball.getPos();
			for (int i = 0; i < c_point.length; i++) {
				if (c_point[i].distance(p) < 2 * Ball.radius
						&& ball.getSpeed().dot(c_point[i].getX() - p.getX(),
								c_point[i].getY() - p.getY()) > 0) {
					Speed s = new Speed(p.getX() - c_point[i].getX(), p.getY()
							- c_point[i].getY());
					s.div(s.getV());
					ball.getSpeed().sub(s.mul(2 * ball.getSpeed().dot(s)));
					break; // 每次只可能碰到一个角
				}
			}
		}
	}

	public static boolean inMainBallArea(int x, int y) {
		return MainBallArea.contains(x, y);
	}

	public static boolean inMainBallArea(Point2D p) {
		return MainBallArea.contains(p);
	}

	public boolean ballIn(Ball ball) {
		boolean retn = false;
		Point2D p = ball.getPos();
		for (int i = 0; i < holes.length; i++) {
			if (holes[i].distance(p) < Table.holeRadius) {
				retn = true;
				break;
			}
		}
		return retn;
	}

	public void draw(Graphics g) {
		// Graphics2D g2d = (Graphics2D) g;

		g.drawImage(tableImg, basex, basey, null);
		// g2d.setColor(Color.yellow);

		// g2d.drawLine(basex + off_x, basey + off_y, basex + off_x, basey
		// + height - off_y);
		// g2d.drawRect(off_x + basex, off_y + basey, width - 2 * off_x, height
		// - 2 * off_y);
		// g2d.draw(boundry);
		// Ellipse2D e;
		// 左上
		// g2d.draw(e = new Ellipse2D.Double(basex + off_x - 2 * Ball.radius,
		// basey + off_y + 7, 2 * Ball.radius, 2 * Ball.radius));
		// 左下
		// g2d.draw(e = new Ellipse2D.Double(basex + off_x - 2 * Ball.radius,
		// basey + height - off_y - 2 * Ball.radius - 9, 2 * Ball.radius,
		// 2 * Ball.radius));
		// 上面中间两个
		// g2d.draw(e = new Ellipse2D.Double(width / 2 - 2 * Ball.radius
		// - holeRadius, basey + off_y - 2 * Ball.radius, 2 * Ball.radius,
		// 2 * Ball.radius));
		// g2d.draw(e = new Ellipse2D.Double(width / 2 + holeRadius, basey +
		// off_y
		// - 2 * Ball.radius, 2 * Ball.radius, 2 * Ball.radius));
		// 下面中间两个
		// g2d.draw(e = new Ellipse2D.Double(width / 2 - 2 * Ball.radius
		// - holeRadius, basey + height - off_y, 2 * Ball.radius,
		// 2 * Ball.radius));
		// g2d.draw(e = new Ellipse2D.Double(width / 2 + holeRadius, basey
		// + height - off_y, 2 * Ball.radius, 2 * Ball.radius));
		// 右上
		// g2d.draw(e = new Ellipse2D.Double(basex + width - off_x, basey +
		// off_y
		// + 7, 2 * Ball.radius, 2 * Ball.radius));
		// 右下
		// g2d
		// .draw(e = new Ellipse2D.Double(basex + width - off_x, basey
		// + height - off_y - 2 * Ball.radius - 9,
		// 2 * Ball.radius, 2 * Ball.radius));
		// 上左
		// g2d.draw(e = new Ellipse2D.Double(basex + off_x + 10, basey + off_y -
		// 2
		// * Ball.radius, 2 * Ball.radius, 2 * Ball.radius));
		// 上右
		// g2d.draw(e = new Ellipse2D.Double(basex + width - off_x - 2
		// * Ball.radius - 10, basey + off_y - 2 * Ball.radius,
		// 2 * Ball.radius, 2 * Ball.radius));
		// 下左
		// g2d.draw(e = new Ellipse2D.Double(basex + off_x + 10, basey + height
		// - off_y, 2 * Ball.radius, 2 * Ball.radius));
		// 下右
		// g2d.draw(e = new Ellipse2D.Double(basex + width - off_x - 2
		// * Ball.radius - 10, basey + height - off_y, 2 * Ball.radius,
		// 2 * Ball.radius));
		// for (int i = 0; i < holes.length; i++)
		// g2d.drawRect((int) holes[i].getX(), (int) holes[i].getY(), 1, 1);
		// for (int i = 0; i < c_point.length; i++)
		// g2d
		// .drawRect((int) c_point[i].getX(), (int) c_point[i].getY(),
		// 1, 1);

	}
}