import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/*
 * Lab 4 -- Localization
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
	
	public enum Direction {
		LEFT, RIGHT, FORWARD, BACKWARD
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
		setSpeed(FWD_SPEED);
		
		leftMotor.rotate(convertDistance(radius, distance), true);
		rightMotor.rotate(convertDistance(radius, distance), true);
	}
	
	/*
	 * 	Turns the robot in the specified direction by a desired angle (in degrees)
	 * 	Raw turning function, does not read any value from the odometer.
	 */
	public void turn(Direction direction, double angle) {
		stop();
		setSpeed(TURN_SPEED);
		switch (direction) {
			case LEFT:
				leftMotor.rotate(-convertAngle(radius, width, angle), true);
				rightMotor.rotate(convertAngle(radius, width, angle), false);
				break;
			case RIGHT:
				leftMotor.rotate(convertAngle(radius, width, angle), true);
				rightMotor.rotate(-convertAngle(radius, width, angle), false);
				break;
			default:	// When direction provided is FORWARD/BACKWARD
				break;
		}
			
	}
	
	/*
	 * 	Moves forward continuously until driver.stop() is called.
	 */
	public void continuousMove(Direction direction) {
		// Go slowly enough to detect line
		final int SLOW_COEFFICIENT = 20;
		leftMotor.setSpeed(FWD_SPEED/SLOW_COEFFICIENT);
		rightMotor.setSpeed(FWD_SPEED/SLOW_COEFFICIENT);
		switch (direction) {
		case FORWARD:
			leftMotor.forward();
			rightMotor.forward();
			break;
		case BACKWARD:
			leftMotor.backward();
			rightMotor.backward();
			break;
		default:		// Called if LEFT or RIGHT are provided as arguments
			break;
		}
		leftMotor.setSpeed(FWD_SPEED);
		rightMotor.setSpeed(FWD_SPEED);
	}
	
	/*
	 * Tells the robot to start turning in a direction, stop will have to be 
	 * called to stop this turn
	 */
	public void continuousTurn(Direction direction) {
		stop();
		setSpeed(TURN_SPEED);
		switch (direction) {
		case LEFT:
			leftMotor.backward();
			rightMotor.forward();
			break;
		case RIGHT:
			rightMotor.backward();
			leftMotor.forward();
			break;
		default:	// When direction provided is FORWARD/BACKWARD
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
	
	private void setSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}
	
	// The following two utility methods come from the provided SquareDriver class in lab 2.
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
