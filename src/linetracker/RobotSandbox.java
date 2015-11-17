package linetracker;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;

/**
 * The playground for the robot. Contains the robot and a line. Runs its own
 * thread that does discrete (step-by-step) simulation of the robot movement.
 * Animation framerate is set to 50Hz.
 * 
 * @author Ondrej Stanek
 */
public class RobotSandbox extends Canvas implements Runnable {

	// graphic buffer
	private Image dbImage;
	private Graphics dbg;

	public Robot robot;
	private final GeneralPath guideline;

	Thread animationThread;
	private final long DELAY = 1000 / 50; // 50 FPS animation

	/**
	 * Initializes the robot and a sample guideline.
	 */
	public RobotSandbox() {

		this.guideline = new GeneralPath();

		this.setSize(600, 300);

		this.guideline.append(new CubicCurve2D.Float(200, 100, 200, 200, 100, 100, 100, 200), false);
		this.guideline.append(new CubicCurve2D.Float(100, 200, 100, 300, 300, 200, 400, 200), false);
		this.guideline.append(new CubicCurve2D.Float(400, 200, 600, 200, 200, 0, 200, 100), false);

		this.robot = new Robot(200, 100, Math.PI / 2, this.guideline);

		this.animationThread = new Thread(this);
	}

	/**
	 * Starts the animation thread of this object.
	 */
	public void StartAnimation() {
		this.animationThread.start();
		// robot.regulator.StartPIDregulation();
		this.robot.regulator.StartRegulation();
	}

	@Override
	public void update(Graphics g) {
		if (this.dbImage == null) {
			this.dbImage = this.createImage(this.getSize().width, this.getSize().height);
			this.dbg = this.dbImage.getGraphics();
		}
		// clear screen in background
		this.dbg.setColor(this.getBackground());
		this.dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

		// draw elements in background
		this.dbg.setColor(this.getForeground());
		this.paint(this.dbg);

		// draw image on the screen
		g.drawImage(this.dbImage, 0, 0, this);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw guideline
		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(1));
		g2.draw(this.guideline);

		// draw the robot
		// g2.setStroke(new BasicStroke(1));
		g2.setPaint(Color.blue);
		g2.draw(this.robot);

	}

	/**
	 * Periodically updates robots position and redraw the canvas
	 */
	public void run() {

		this.robot.regulator.StartRegulation(); // nao tem na outra
		// Remember the starting time
		long tm = System.currentTimeMillis();
		while (Thread.currentThread() == this.animationThread) {

			if (this.robot.regulator.hasClient()) {

				this.robot.Move();
				this.repaint();

				// Delay depending on how far we are behind.
				try {
					tm += this.DELAY;
					Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
				} catch (InterruptedException e) {
					break;
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}
