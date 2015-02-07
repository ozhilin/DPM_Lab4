/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;

public class Navigation {
	private Odometer odo;
	private TwoWheeledRobot robot;
	
	private double POS_ERR = 1;
	private double DEG_ERR = 20;
	
	private double FWD_SPEED = 300;
	private double TURN_SPEED = 30;
	
	public Navigation(Odometer odo) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		double[] position = new double[3];
		odo.getPosition(position);
		
		double odoX = position[0];
		double odoY = position[1];
		
		while (Math.abs(x - odoX) > POS_ERR || Math.abs(y - odoY) > POS_ERR) {
			
			odo.getPosition(position);
			odoX = position[0];
			odoY = position[1];
			
			minAng = (Math.atan2(y - odoY, x - odoX) * (180.0 / Math.PI));
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			robot.setForwardSpeed(FWD_SPEED);
		}
		robot.setForwardSpeed(FWD_SPEED);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {
		double[] position = new double[3];
		odo.getPosition(position);
		double currentAngle = position[2];
		double error = angle - currentAngle;

		while (Math.abs(error) > DEG_ERR) {
			odo.getPosition(position);
			currentAngle = position[2];
			
			error = angle - currentAngle;

			if (error < -180.0) {
				robot.setRotationSpeed(-TURN_SPEED);
			} else if (error < 0.0) {
				robot.setRotationSpeed(TURN_SPEED);
			} else if (error > 180.0) {
				robot.setRotationSpeed(TURN_SPEED);
			} else {
				robot.setRotationSpeed(-TURN_SPEED);
			}
		}

		if (stop) {
			robot.setForwardSpeed(0);
		}
	}
}
