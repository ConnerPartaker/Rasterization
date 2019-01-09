package WorldBuilder;

//Point class - A record of the 3D location of any point, and it's position on the screen
public class Point {
	
	//Real Coords
	public double x;
	public double y;
	public double z;
	
	//Graphics Coords in form pr = rx(ref1) + ry(ref2)
	public double rx;
	public double ry;
	
	//Coordinates of associated point on texture
	public int dx;
	public int dy;
	
	
	//Constructors
	public Point(double x, double y, double z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	//
	//
	//
	public Point(double x, double y, double z, int dx, int dy) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.dx = dx;
		this.dy = dy;
	}
	//
	//
	//
	public Point(Point p) {
		
		pUpdate(p);
		rUpdate(p);
		this.dx = p.dx;
		this.dy = p.dy;
	}
	//
	//
	//
	public Point() {}
	
	
	
	public Point clone(Point p) {
		
		pUpdate(p);
		rUpdate(p);
		this.dx = p.dx;
		this.dy = p.dy;
		return this;
	}
	//
	//
	//
	public static void swap(Point i, Point j) {
		Point x = new Point(i);
		
		i.pUpdate(j);
		i.rUpdate(j);
		i.dx = j.dx;
		i.dy = j.dy;
		
		j.pUpdate(x);
		j.rUpdate(x);
		j.dx = x.dx;
		j.dy = x.dy;
	}
	
	
	
	//Updaters
  	public Point pUpdate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	//
	//
	//
	public Point pUpdate(Point p) {
		x = p.x;
		y = p.y;
		z = p.z;
		return this;
	}
	//
	//
	//
	public void rUpdate(double rx, double ry) {
		this.rx = rx;
		this.ry = ry;
	}
	//
	//
	//
	public void rUpdate(Point p) {
		rx = p.rx;
		ry = p.ry;
	}
	
	
	
	//Set the point with spherical coordinates
  	public Point sphereCoords(double r, double phi, double theta) {
		
		x = r*Math.sin(phi)*Math.cos(theta);
		y = r*Math.sin(phi)*Math.sin(theta);
		z = r*Math.cos(phi);
		return this;
	}
	
	
	
	//Vector scaling
	public Point scale(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	//
	//
	//
	public void rScale(double scalar) {
		rx *= scalar;
		ry *= scalar;
	}
	//
	//
	//
	public static Point scale(Point p, double scalar) {
		Point  x = new Point(p);
		return x.scale(scalar);
	}
	//
	//
	//
	public Point uvect() {
		return this.scale(1/mag(this));
	}
	//
	//
	//
	public static Point uvect(Point p) {
		return scale(p, 1/mag(p));
	}
	
	
	
	//Vector Addition and Subtraction
	public Point vectAdd(Point i) {
		x+=i.x;
		y+=i.y;
		z+=i.z;
		return this;
	}
	//
	//
	//
	public static Point vectAdd(Point i, Point j) {
		return new Point(i.x + j.x, i.y + j.y, i.z + j.z);
	}
	//
	//
	//
	public Point vectSubt(Point i) {
		x-=i.x;
		y-=i.y;
		z-=i.z;
		return this;
	}
	//
	//
	//
	public static Point vectSubt(Point j, Point i) {
		return new Point(i.x - j.x, i.y - j.y, i.z - j.z);
	}
	
	
	
	//Magnitude Finder
	public static double mag(Point p) {
		return Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
	}
	
	
	
	//Dot Products
	public double dot(Point i) {
		return x*(i.x) + y*(i.y) + z*(i.z);
	}

	
	
	//Cross product
	public static Point cross(Point i, Point j) {
		return new Point(i.y*j.z - i.z*j.y, -(i.x*j.z - i.z*j.x), i.x*j.y - i.y*j.x);
	}
}
