import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/*
 * Lab 3 -- Navigation
 * ECSE 211: Design Principles and Methods
 * 
 * Group 48
 * Michael Maatouk:	260554267
 * Oleg Zhilin		260581713
 * 
 * Driver.java:	Provides utility methods for moving the robot
 */

public class Driver {
	private NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	
	private double radius = 2.1, width = 16.1;
	
	private int TURN_SPEED = 70;
	private int FWD_SPEED = 200;
	private int FWD_ACCEL = 200;
	
	private double AVOID_DISTANCE = 30;
	private double AVOID_RTURN = 90;
	private double AVOID_LTURN = 85;
	
	public enum Direction {
		LEFT, RIGHT
	}
	
	public Driver() {
		
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		
		leftMotor.setAcceleration(FWD_ACCEL);
		rightMotor.setAcceleration(FWD_ACCEL);
	}
	
	/*
	 * 	Move the robot forward a specified distance (in cm)
	 */
	public void move(double distance) {
		leftMotor.setSpeed(FWD_SPEED);
		rightMotor.setSpeed(FWD_SPEED);
		
		leftMotor.rotate(convertDistance(radius, distance), true);
		rightMotor.rotate(convertDistance(radius, distance), true);
	}
	
	/*
	 * 	Turns the robot in the specified direction at a desired angle (in degrees)
	 * 	Raw turning function, does not read any value from the odometer.
	 */
	public void turn(Direction direction, double angle) {
		stop();
		leftMotor.setSpeed(TURN_SPEED);
		rightMotor.setSpeed(TURN_SPEED);
		switch (direction) {
			case LEFT:
				leftMotor.rotate(-convertAngle(radius, width, angle), true);
				rightMotor.rotate(convertAngle(radius, width, angle), false);
				break;
			case RIGHT:
				leftMotor.rotate(convertAngle(radius, width, angle), true);
				rightMotor.rotate(-convertAngle(radius, width, angle), false);
				break;
		}
			
	}
	
	/*
	 * 	Stops the robot and floats the motors 
	 */
	public void stop() {
		leftMotor.flt(true);
		rightMotor.flt(false);
		
		leftMotor.stop(true);
		rightMotor.stop(false);
	}
	
	/*
	 * 	Dodges an obstacle by following a predetermined path.
	 */
	private void avoid() {
		turn(Direction.RIGHT, AVOID_RTURN);
		move(AVOID_DISTANCE);
		turn(Direction.LEFT, AVOID_LTURN);
		move(AVOID_DISTANCE);
	}
	
	// The following two utility methods come from the provided SquareDriver class in lab 2.
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
