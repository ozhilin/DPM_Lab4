import lejos.nxt.LCD;
import lejos.nxt.Sound;

/*
 * Lab 3 -- Navigation
 * ECSE 211: Design Principles and Methods
 * 
 * Group 48
 * Michael Maatouk:	260554267
 * Oleg Zhilin		260581713
 * 
 * Navigator.java:	Controls the movement of the robot to get it to travel
 * 					to the designated position.
 */

public class Navigator {
	private Odometer odometer;
	private Driver driver;
	
	private double POS_ERROR = 0.5;		// in cm		
	private double ANGLE_ERROR = 6; 	// in degrees
	
	public Navigator(Odometer odometer) {
		this.odometer = odometer;
		this.driver = new Driver();
	}
	
	/*
	 * 	Move the robot to the provided coordinates
	 * 	Corrects its movement by comparing with the values measured by odometer
	 */
	public void travelTo(double x, double y) {
		double xPos, yPos, toDestination, xDistance, yDistance;
		
		do {
			// Get the current coordinates
			xPos = odometer.getX();
			yPos = odometer.getY();
			
			// Set distance from current position to goal
			xDistance = x - xPos;
			yDistance = y - yPos;
			
			/*
			 * 	Adjust the orientation of the robot
			 */
			
			// Compute the necessary turns if necessary
			double minAngle = Math.toDegrees(Math.atan2(yDistance, xDistance));
			
			// Set the angle to the interval [0, 360]
			if (minAngle < 0)
				minAngle += 360;
			
			if (Math.abs(xDistance) > POS_ERROR || Math.abs(yDistance) > POS_ERROR)
				turnTo(minAngle);
			/*
			 * 	Move the robot to its destination
			 */
			toDestination = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
			driver.move(toDestination);
		} while (Math.abs(xDistance) > POS_ERROR || Math.abs(yDistance) > POS_ERROR);
		Sound.twoBeeps();
	}
	
	/*
	 * 	Turn the robot to a specified orientation. 
	 * 	Corrects its movement so it agrees with the measurements of the odometer 
	 */
	public void turnTo(double theta) {
		double currentAngle, angleError;
		
		currentAngle = Math.toDegrees(odometer.getTheta());
		
		// Set the current angle to the range [0,360]
		if (currentAngle < 0) 
			currentAngle += 360;
		
		angleError = theta - currentAngle;
		// Turn until odometer reports orientation is correct
		while (Math.abs(angleError) > ANGLE_ERROR) {
			currentAngle = Math.toDegrees(odometer.getTheta());
			angleError = theta - currentAngle;
			
			/*	
			 * 	During testing, it was noticed that the robot always leans left 
			 * 	when going straight.
			 *	This compensation factor corrects this deviation.
			 */
			LCD.drawString("AngleError " + angleError, 0, 5);
			int leftCompensationFactor = -5;
			if (angleError < -180.0) {
				driver.turn(Driver.Direction.LEFT, 360 - Math.abs(angleError)/* + leftCompensationFactor*/);
			} else if (angleError < 0.0) {
				driver.turn(Driver.Direction.RIGHT, Math.abs(angleError));
			} else if (angleError > 180.0) {
				driver.turn(Driver.Direction.RIGHT, 360 - angleError);
			} else {
				driver.turn(Driver.Direction.LEFT, angleError/* + leftCompensationFactor*/);
			}
		} 
	}
	
}
