package cn.zju.ricky;

public class Speed extends Object {
	private double vx, vy, v;

	public Speed() {
		vx = 0;
		vy = 0;
		v = 0;
	}

	public Speed(double vx, double vy) {
		set(vx, vy);
	}

	public void set(double vx, double vy) {
		this.vx = vx;
		this.vy = vy;
		this.v = Math.sqrt(vx * vx + vy * vy);
	}
	public void set(Speed s){
		this.vx = s.getX();
		this.vy = s.getY();
		this.v  = s.getV();
	}
	public double getX() {
		return vx;
	}

	public double getY() {
		return vy;
	}

	public double getV() {
		return v;
	}

	public Speed add(Speed s) {
		this.vx += s.vx;
		this.vy += s.vy;
		this.v  = Math.sqrt(this.vx*this.vx+this.vy*this.vy);
		return this;
	}

	public Speed sub(Speed s) {
		this.vx -= s.vx;
		this.vy -= s.vy;
		this.v = Math.sqrt(this.vx*this.vx+this.vy*this.vy);
		return this;
	}

	public Speed div(double d) {
		vx /= d;
		vy /= d;
		v /= d;
		return this;
	}

	public Speed mul(double d) {
		vx *= d;
		vy *= d;
		v *= d;
		return this;
	}

	public double dot(Speed s) {
		return this.vx * s.vx + this.vy * s.vy;
	}
	public double dot(double x,double y){
		return vx * x + vy * y;
	}
	public void speedUp(double d) {
		if (v == 0)
			return;
		double sin, cos;
		sin = vy / v;
		cos = vx / v;

		if ((v += d) < 0)
			v = 0;
		vx = v * cos;
		vy = v * sin;
	}

	public void speedDown(double d) {
		if (v == 0)
			return;
		double sin, cos;
		sin = vy / v;
		cos = vx / v;

		if ((v -= d) < 0)
			v = 0;
		vx = v * cos;
		vy = v * sin;
	}
	public Speed clone(){
		return new Speed(vx,vy);
	}
}
