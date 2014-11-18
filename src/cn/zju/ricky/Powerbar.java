package cn.zju.ricky;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Powerbar {
	private int force; // 击球力度
	private int flag;
	private boolean start;
	private static final Shape reg = new Rectangle(254, 50, 230, 20);
	private static final Rectangle2D rec = new Rectangle2D.Double();
	private static final Point2D p1 = new Point2D.Float(254.f, 50.f);
	private static final Point2D p2 = new Point2D.Float(480.f, 50.f);
	private final GradientPaint g1 = new GradientPaint(p1, Color.yellow, p2,
			Color.red, true);

	// private static final String p = "力量条";
	public Powerbar() {
		start = false;
		flag = 1;
		force = 0;
	}

	public double getPower() {
		start = false;
		return force * 20.0 / reg.getBounds().width;
	}

	public void start() {
		start = true;
	}

	public void stop() {
		start = false;
	}

	public boolean getStatus() {
		return start;
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// Rectangle2D rec = new Rectangle2D.Double(p1.getX() + 2, p1.getY() +
		// 2,
		// force, reg.getBounds().height - 3);
		rec.setRect(p1.getX() + 2, p1.getY() + 2, force,
				reg.getBounds().height - 3);
		g2d.setPaint(g1);
		g2d.draw(reg);
		g2d.fill(rec);
		if (start) {
			int t = force + 3 * flag;
			if (0 >= t || t > reg.getBounds().width - 2)
				flag = -flag;
			force += 3 * flag;
		}
	}
}