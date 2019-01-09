package WorldBuilder;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Timer;
import java.util.TimerTask;



public class Rast_Portal extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	
	
	
	private static final long serialVersionUID = 1L;					//Because why not
    
    
	//Various functionalities require different ArrayLists
	private ArrayList<PointEntity> entities
	  = new ArrayList<PointEntity>();  								    //Main Point array, containing all 3-space Point
	private Texture text = new Texture();								//Object initializes all textures, and prepares them to be used
	
	//Screen Properties (init in constructor)
	private final Dimension SCREENDIM;									//Screen height, width
	private final int SCREENX;											//Number of pixels from center to right of screen for conversion
	private final int SCREENY;											//Number of pixels from center to  top  of screen for conversion
	private final double SCREENRATIO;
	private final int PERSP = 1;										//Distance between user and screen, i.e. perspective shift constant
	
	//These make up the user perspective plane
	private Point u = new Point(0,0,0);									//Point of convergence, where all vectors from Point are mapped to (u for user)
	private Point c = new Point(0,PERSP,0);								//Center of perspective plane, the plane representing the screen (c for center)
	private Point ref1 = new Point(1,0,0);								//Perspective plane ref point, making vector c->ref1 a horizontal basis vector of the screen
	private Point ref2 = new Point(0,0,1);								//Perspective plane ref point, making vector c->ref2 a vertical basis vector of the screen
	
	//Spherical Coordinates used to keep user at constant radius from entity
	private double theta;												//XY plane positive rotation   (Radians, like a civilized person)
	private double phi;													//Z-Vector angle (a.k.a pitch) (Radians, like a civilized person)
	
	//Booleans holding directional data to prevent issues with the keyPressed method
	private boolean left = false;										//Left
	private boolean right = false;										//Up
	private boolean forward = false;									//Up x2
	private boolean backward = false;									//Through the 4th dimension
	
	private final double SPEED = .1;									//Speed in units/sec (one unit is 1/2 height of screen)
	
	Comparator<PointEntity> comparator = new Comparator<PointEntity>(){ //Comparator that orders the pointentities in order to be painted
        @Override														//Still need to come up with a better method
        public int compare(PointEntity o1, PointEntity o2) {
            return o1.avgpt.dot(c) > o2.avgpt.dot(c) ? -1 : 1;
        }
    };
	
	
	//CONSTRUCTION METHODS//////////////////////////////////////////////////////////////////////////////
 	public Rast_Portal() {
		
		//Initialize screen values, including ref1
		SCREENDIM   = Toolkit.getDefaultToolkit().getScreenSize();
		SCREENX     = SCREENDIM.width /2;
		SCREENY     = SCREENDIM.height/2;
		SCREENRATIO = SCREENX/(1.0*SCREENY);
		
		//Create Panel
		setSize(SCREENDIM.width, SCREENDIM.height);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		//Initialize all triangles in space
		createEntities();
		
		//Start framerate timer. The final number controls miliseconds per frame
		Timer t = new Timer();
		t.schedule(new TimerTask() {public void run() {repaint();}}, 0, 40);
	}
	//
	//
	//
 	public void createEntities() {
		
		/* Template

		 entities.add(new PointEntity(
				 new Point( x1, y1, z1, xd1, yd1), 
 				 new Point( x2, y2, z2, xd2, yd2), 
 				 new Point( x3, y3, z3, xd3, yd3), 
 				 o));
 		
		*/
		
 		
 		/* Singular White Square in space
		entities.add(new PointEntity(
				 new Point( 5, 1, 1,   0,   0), 
				 new Point( 5, 1,-1,   0,1024), 
				 new Point( 5,-1, 1,1024,   0), 
				 0xFFFFFF));

		entities.add(new PointEntity(
				 new Point( 5, 1,-1,   0,1024),
				 new Point( 5,-1,-1,1024,1024),
				 new Point( 5,-1, 1,1024,   0), 
				 0xFFFFFF));
		*/
		/* Charizard in Space
		entities.add(new PointEntity(
				 new Point( 5, 2, 2,   0,   0), 
				 new Point( 5, 2,-2,   0,1024), 
				 new Point( 5,-2, 2,1024,   0), 
				 text.charz));
		
		entities.add(new PointEntity(
				 new Point( 5, 2,-2,   0,1024),
				 new Point( 5,-2,-2,1024,1024),
				 new Point( 5,-2, 2,1024,   0), 
				 text.charz));
		*/
		///* Success at a HIGH QUALITY picture (this case the moon)
		entities.add(new PointEntity(
				 new Point( 5, 2, 2,   0,   0),
				 new Point( 5, 2,-2,   0,6000),
				 new Point( 5,-2, 2,6000,   0),
				 text.moon));
				
		entities.add(new PointEntity(
				 new Point( 5, 2,-2,   0,6000),
				 new Point( 5,-2,-2,6000,6000),
				 new Point( 5,-2, 2,6000,   0), 
				 text.moon));
				//*/
		/* Colored Cube in Space
		//Top
		entities.add(new PointEntity(
				new Point( 6, 1, 1, 0, 0),
				new Point( 4, 1, 1, 0, 0),
				new Point( 6,-1, 1, 0, 0),
				0xFF0000));
						
		entities.add(new PointEntity(
				new Point( 4,-1, 1, 0, 0),
				new Point( 4, 1, 1, 0, 0),
				new Point( 6,-1, 1, 0, 0),
				0xFF0000));
		//Bottom
		entities.add(new PointEntity(
				new Point( 6, 1,-1, 0, 0),
				new Point( 4, 1,-1, 0, 0),
				new Point( 6,-1,-1, 0, 0),
				0xFF0000));
						
		entities.add(new PointEntity(
				new Point( 4,-1,-1, 0, 0),
				new Point( 4, 1,-1, 0, 0),
				new Point( 6,-1,-1, 0, 0),
				0xFF0000));
		//Right
		entities.add(new PointEntity(
				new Point( 6, 1, 1, 0, 0),
				new Point( 4, 1, 1, 0, 0),
				new Point( 6, 1,-1, 0, 0),
				0x00FF00));
						
		entities.add(new PointEntity(
				new Point( 4, 1,-1, 0, 0),
				new Point( 4, 1, 1, 0, 0),
				new Point( 6, 1,-1, 0, 0),
				0x00FF00));
		//Left
		entities.add(new PointEntity(
				new Point( 6,-1, 1, 0, 0),
				new Point( 4,-1, 1, 0, 0),
				new Point( 6,-1,-1, 0, 0),
				0x00FF00));
						
		entities.add(new PointEntity(
				new Point( 4,-1,-1, 0, 0),
				new Point( 4,-1, 1, 0, 0),
				new Point( 6,-1,-1, 0, 0),
				0x00FF00));
		//Front
		entities.add(new PointEntity(
				new Point( 6, 1, 1, 0, 0),
				new Point( 6,-1, 1, 0, 0),
				new Point( 6, 1,-1, 0, 0),
				0x0000FF));
						
		entities.add(new PointEntity(
				new Point( 6,-1,-1, 0, 0),
				new Point( 6,-1, 1, 0, 0),
				new Point( 6, 1,-1, 0, 0),
				0x0000FF));
		//Back
		entities.add(new PointEntity(
				new Point( 4, 1, 1, 0, 0),
				new Point( 4,-1, 1, 0, 0),
				new Point( 4, 1,-1, 0, 0),
				0x0000FF));
						
		entities.add(new PointEntity(
				new Point( 4,-1,-1, 0, 0),
				new Point( 4,-1, 1, 0, 0),
				new Point( 4, 1,-1, 0, 0),
				0x0000FF));
		*/
		
		
	}
	//
	//
	//
	public void request() {requestFocusInWindow();}
	//END CONSTRUCTORS//////////////////////////////////////////////////////////////////////////////////
	
	
	
	//LISTENERS/////////////////////////////////////////////////////////////////////////////////////////
	public void mouseMoved   (MouseEvent e) {
		
		//Dictates the direction the user is looking based on mouse
		
		//Ang =  offset - position * Codomain / Domain
		phi   =          e.getY()  *   Math.PI/(SCREENDIM.getHeight()); 
		theta = (SCREENX-e.getX()) * 6*Math.PI/(SCREENDIM.getWidth ());
	}
	//
	//
	//
	public void keyPressed   (KeyEvent e)   {
		
		//Dictates direction the user is moving. Best to have variables turned on and off, because it's jumpy otherwise
		switch(e.getKeyCode()) {
			
			case KeyEvent.VK_W:
				forward  = true;
				break;
			case KeyEvent.VK_A:
				left     = true;
				break;
			case KeyEvent.VK_S:
				backward = true;
				break;
			case KeyEvent.VK_D:
				right    = true;
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			default:
				break;
		}
	}
	//
	//
	//
	public void keyReleased  (KeyEvent e)   {
		
		//Same as last, only turning variables off
		switch(e.getKeyCode()) {
		
			case KeyEvent.VK_W:
				forward  = false;
				break;
			case KeyEvent.VK_A:
				left     = false;
				break;
			case KeyEvent.VK_S:
				backward = false;
				break;
			case KeyEvent.VK_D:
				right    = false;
				break;
			default:
				break;
		}
	}
	//
	//
	//
	public void mouseDragged (MouseEvent e) {}
	public void mouseClicked (MouseEvent e) {}
	public void mousePressed (MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited  (MouseEvent e) {}
	public void keyTyped     (KeyEvent   e) {}
	//END LISTENERS/////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//PAINT METHODS/////////////////////////////////////////////////////////////////////////////////////
	public void updateFrame() {
		
		//Change direction and position of the camera
		
		c.sphereCoords(PERSP, phi, theta);
		ref1.pUpdate(Math.sin(theta), -Math.cos(theta), 0);
		ref2.pUpdate(Point.cross(ref1, c)).scale(1/PERSP);
		
		//Unit vect. forward is (0,0,1)xref1 = (-y, -x, 0) => u' = (ux-speed*refy, uy-speed*refx, uz)
		if (forward ) {
			u.x += SPEED*c.x;
			u.y += SPEED*c.y;
		}
		if (backward) {
			u.x -= SPEED*c.x;
			u.y -= SPEED*c.y;
		}
		if (right   ) {
			u.x += SPEED*ref1.x;
			u.y += SPEED*ref1.y;
		}
		if (left    ) {
			u.x -= SPEED*ref1.x;
			u.y -= SPEED*ref1.y;
		}
	}
	//
	//
	//
	public BufferedImage paintFrame() {
		
		//Paint the actual buffered image before putting it up on the jframe
		
		BufferedImage b = new BufferedImage(2*SCREENX, 2*SCREENY, BufferedImage.TYPE_INT_RGB);
		
		int sx, ex, sy, ey;					//Minimum and maximum pixels to be checked for painting a triangle
		Point i, j, k;						//The three points that make up a triangle
		double[][] m;						//The matrix to be solved to find a pixel's color
		double denom;						//Variable to help find whether a point is in a triangle
		double rx, ry;						//Pixel location in space storage
		double m0, m1, m2;					//Stuff for solving the matrix
		double[] s = new double[6];			//More stuff for solving matrix
		double[] t = new double[6];			//Even more
		double s_ans, t_ans;				//Final solutions to matrix
		int dx, dy, color;					//Location of pixel on texture, and its color
		
		
		//Sort in order of distance from user
		entities.sort(comparator);
		
		for (PointEntity e : entities) {
			
			updateRFromP(e.pts);
			//Store the pixels making up a triangle
			i = e.pts.get(0);
			j = e.pts.get(1);
			k = e.pts.get(2);
			
			//Check if the triangle is on screen, and paint if so. If not, skip. Will find a better method in the future
			if( Math.min(Math.min(i.dot(c), j.dot(c)), k.dot(c)) > u.dot(c) ) {
				
				//Box of pixels to be checked and possibly colored			
				sx = SCREENX + Math.max(-SCREENX, (int)( SCREENY*Math.min(Math.min(i.rx, j.rx), k.rx)));
				ex = SCREENX + Math.min( SCREENX, (int)( SCREENY*Math.max(Math.max(i.rx, j.rx), k.rx)));
				sy = SCREENY + Math.max(-SCREENY, (int)(-SCREENY*Math.max(Math.max(i.ry, j.ry), k.ry)));
				ey = SCREENY + Math.min( SCREENY, (int)(-SCREENY*Math.min(Math.min(i.ry, j.ry), k.ry)));
				
				//No need to do awkward matrix stuff if we can avoid it. Otherwise, this solves the color matrix
				if (e.img != null) {
					
					//Matrix [i s f : a] (initial, secondary, final, answer)
					m = new double[][] {{j.x-i.x, k.x-i.x, u.x-i.x},
					 				{j.y-i.y, k.y-i.y, u.y-i.y},
					 				{j.z-i.z, k.z-i.z, u.z-i.z}};
					
					//Figure out matrix, send it to s and t. Exported to a different method because it's better
					matrix(s, m, 0);
					matrix(t, m, 1);
					
					//Get rid of that matrix for memory sake
					m = null;
				}
				
				//Scan over pixels, and get the color at each location
				//u is p1p2, v is p1p3
				for (int x = sx; x < ex; x++) {
					for (int y = sy; y < ey; y++) {
						
						//Pixel space location
						rx = -SCREENRATIO + x/(1.0*SCREENY);
						ry =            1 - y/(1.0*SCREENY);
						
						//If the image is null, no need to do awkward matrix stuff. Otherwise, fire away
						//s and t decide if a point is in the triangle. If it is, we change color from -1 to it's value.
						color = -1;
						
						if (e.img == null) {
							
							denom = ((j.rx-i.rx)*(k.ry-i.ry)-(k.rx-i.rx)*(j.ry-i.ry));
							
							s_ans =-((k.rx-i.rx)*(ry-i.ry)-(rx-i.rx)*(k.ry-i.ry))/denom;
							t_ans = ((j.rx-i.rx)*(ry-i.ry)-(rx-i.rx)*(j.ry-i.ry))/denom;
							
							if(Math.min(s_ans, t_ans) >= 0 && s_ans + t_ans <= 1)
								color = e.rgbValue;
							
						} else {
							
							m0 = - rx*ref1.x - ry*ref2.x - c.x;
							m1 = - rx*ref1.y - ry*ref2.y - c.y;
							m2 = 	 		 - ry*ref2.z - c.z;
							
							s_ans =(m0*s[0] - m1*s[1] + m2*s[2])
								  /(m0*s[3] - m1*s[4] + m2*s[5]);
						
							t_ans =(m0*t[0] - m1*t[1] + m2*t[2])
								  /(m0*t[3] - m1*t[4] + m2*t[5]);
							
							if (Math.min(s_ans, t_ans) >= 0 && s_ans + t_ans <= 1) {
								
								dx = i.dx + (int)(s_ans*(j.dx - i.dx) + t_ans*(k.dx - i.dx));
								dy = i.dy + (int)(s_ans*(j.dy - i.dy) + t_ans*(k.dy - i.dy));
							
								color = Texture.color(e.img, dx, dy);
							}
						}
						
						//If the point is decidedly in the triangle, color it.
						if (color != -1)
							b.setRGB( x, y, color);
					}
				}
			}
		}
		
		return b;
	}
	//
	//
	//
	public void updateRFromP(ArrayList<Point> pts) {
		
		//Update each point's position on screen
		Point up = new Point();
		
		//p(|c|^2/p*c) - c
		for (Point p : pts) {
			up.pUpdate(Point.vectSubt(u, p));
			p.rUpdate(up.dot(ref1), up.dot(ref2));
			p.rScale(PERSP*PERSP/up.dot(c));
		}
	}
	//
	//
	//
	public void matrix (double[] var, double[][] m, int x) {
		var[0] = m[1][1-x]*m[2][2] - m[1][2]*m[2][1-x];
		var[1] = m[0][1-x]*m[2][2] - m[0][2]*m[2][1-x];
		var[2] = m[0][1-x]*m[1][2] - m[0][2]*m[1][1-x];
		var[3] = m[1][1-x]*m[2][x] - m[1][x]*m[2][1-x];
		var[4] = m[0][1-x]*m[2][x] - m[0][x]*m[2][1-x];
		var[5] = m[0][1-x]*m[1][x] - m[0][x]*m[1][1-x];
	}
	//
	//
	//
	@Override
	protected void paintComponent(Graphics g) {
		
		updateFrame();
		BufferedImage b = paintFrame();
		g.drawImage(b, 0, 0, null);
	}
	//END PAINT METHODS/////////////////////////////////////////////////////////////////////////////////
	
	
	
	//MAIN METHOD///////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		JFrame f = new JFrame();
		Rast_Portal panel = new Rast_Portal();
		
		f.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.setUndecorated(true);
		f.add(panel);
		
		f.setVisible(true);
		panel.request();
		panel.repaint();
	}
}



class PointEntity {
	
	ArrayList<Point> pts;
	BufferedImage img;
	int rgbValue;
	Point avgpt = new Point();
	
	public PointEntity(Point a, Point b, Point c, BufferedImage img) {
		
		this.pts = new ArrayList<Point>();
		pts.add(a);
		pts.add(b);
		pts.add(c);
		
		this.img = img;
		
		avgpt.pUpdate(a.x+b.x+c.x, a.y+b.y+c.y, a.z+b.z+c.z);
		avgpt.scale(1/3.0);
	} 
	
	public PointEntity(Point a, Point b, Point c, int rgbValue) {
		
		this.pts = new ArrayList<Point>();
		pts.add(a);
		pts.add(b);
		pts.add(c);
		
		this.rgbValue = rgbValue;
		
		avgpt.pUpdate(a.x+b.x+c.x, a.y+b.y+c.y, a.z+b.z+c.z);
		avgpt.scale(1/3.0);
	} 
}



class Texture {
	
	private static String userdir = System.getProperty("user.dir");
	BufferedImage charz;
	BufferedImage moon;
	
	public Texture() {
		//Load all textures
		try {
			charz = ImageIO.read(new File(userdir + "\\tmp\\charizard.png"));
			moon  = ImageIO.read(new File(userdir + "\\tmp\\moonHQ.jpg"));
			//Enter more textures here if nescissary.
			
		} catch (IOException e) {}
	}
	
	public static int color(BufferedImage i, int x, int y) {
		
		//Returns the image color. If it's not in the picture, returns -1 (a preset trigger number)
		if (x < i.getWidth() && y < i.getHeight() && Math.min(x, y) >= 0)
			return i.getRGB(x, y);
			
		return -1;
	}
}