import lejos.nxt.*;

/*
 * Lab 3 -- Navigation
 * ECSE 211: Design Principles and Methods
 * 
 * Group 48
 * Michael Maatouk:	260554267
 * Oleg Zhilin		260581713
 * 
 * Odometer.java:	Keeps track of position and orientation of robot.
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// Robot wheels
	private NXTRegulatedMotor leftMotor = Motor.A;
	private NXTRegulatedMotor rightMotor = Motor.B;
	private enum Side {
		LEFT, RIGHT
	}

	// Calculation data
	private double radius = 2.1;
	private double width = 16.1;	// 16.3
	
	private double rightTacho = 0;
	private double leftTacho = 0;
	
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 15;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = Math.toRadians(90.0);
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here

			double deltaLeftTacho = getDeltaTacho(Side.LEFT);
			double deltaRightTacho = getDeltaTacho(Side.RIGHT);
			
			// Called delta C in tutorial slides
			double arcDisplacement = (deltaRightTacho + deltaLeftTacho)*radius/2;
			double angularDisplacement = (deltaRightTacho - deltaLeftTacho)*radius/width;
			
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				x += arcDisplacement*Math.cos(theta + angularDisplacement/2);
				y += arcDisplacement*Math.sin(theta + angularDisplacement/2);
				theta += angularDisplacement;
				theta %= Math.PI*2;
				// Keep the angle between [0,360]
				if (theta < 0) {
					theta += Math.PI*2;
				}
				displayCoordinates(x, y, theta);
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// Compute the difference in the tachometer value since the last reading
	private double getDeltaTacho(Side side) {
		double tachoCurrent; 
		double delta = 0;
		
		switch (side) {
			case LEFT:
				// Get the current tacho value
				tachoCurrent = leftMotor.getTachoCount() * Math.PI/180;
			    delta = tachoCurrent - leftTacho;
				leftTacho = tachoCurrent;
				break;
			case RIGHT:
				 tachoCurrent = rightMotor.getTachoCount() * Math.PI/180;
				 delta = tachoCurrent - rightTacho;
				rightTacho = tachoCurrent;
				break;
		}
		return delta; 
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
	
	private void displayCoordinates(double x, double y, double theta) {
		LCD.drawString("X: " + x, 0, 1);
		LCD.drawString("Y: " + y, 0, 2);
		LCD.drawString("T: " + Math.toDegrees(theta), 0, 3);
	}
}