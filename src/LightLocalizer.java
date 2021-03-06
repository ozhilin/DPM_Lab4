import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.robotics.LightDetector;


/*
 * Lab 4 -- Localization
 * ECSE 211: Design Principles and Methods
 * 
 * Group 48
 * Michael Maatouk:	260554267
 * Oleg Zhilin		260581713
 * 
 * LightLocalizer.java:	Performs localization using the light sensor.
 */

public class LightLocalizer {
	private Odometer odo;
	private LightDetector ls;
	private Driver driver;
	private Navigator nav;
	
	private int LIGHT_THRESHOLD = 150;
	private int SENSOR_LENGTH = 12;
	
	public LightLocalizer(Odometer odo, LightDetector ls2, Driver driver, Navigator nav) {
		this.odo = odo;
		this.ls = ls2;
		this.driver = driver;
		this.nav = nav;
		// turn on the light
	}
	
	public void doLocalization() {	
		double[] lineLocations;
		
		driveToLocalizationPosition();
		
		lineLocations = recordLines();
		
		//correctTheta(lineLocations);
		correctXY(lineLocations);
		
		nav.travelTo(0, 0);
		nav.turnTo(90);
		
		Sound.twoBeeps();
	}
	
	/*
	 * 	Moves the robot to a position where it can detect all the grid lines
	 * 	in a single rotation.
	 */
	private void driveToLocalizationPosition() {
		// Adjust y coordinate
		nav.turnTo(45);
		driver.continuousMove(Driver.Direction.FORWARD);
		waitForBlackLine();
		driver.stop();
		odo.setY(SENSOR_LENGTH);
		odo.setX(SENSOR_LENGTH);
		
	}
	
	/*
	 * 	Turns in a circle until it detects four grid lines, recording the 
	 * 	angle at which they were found.
	 */
	private double[] recordLines() {
		double[] lineLocations = new double[4];
		int lineCount = 0;
		
		driver.continuousTurn(Driver.Direction.RIGHT);
		
		while (lineCount < 4) {
			LCD.drawString("" + odo.getTheta(), 0, 0);

			waitForBlackLine();
			
			// Record the angle at which the line was detected
			lineLocations[lineCount] = odo.getTheta();
			lineCount++;
			
			// Sleep to avoid catching same line twice
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// Do nothing, should not happen.
			}
		}
		driver.stop();
		
		return lineLocations;
	}
	
	/*
	 * 	Runs continuously until the sensor detects a black line.
	 */
	private void waitForBlackLine() {
		// Wait until line is detected
		while (ls.getLightValue() > LIGHT_THRESHOLD);
		Sound.beep();
	}
	
	/*
	 * 	Computes the correct coordinates of the robot.
	 */
	private void correctXY(double[] lineLocations) {
		double x = -SENSOR_LENGTH * Math.cos((lineLocations[2] - lineLocations[0]) / 2);
		double y = -SENSOR_LENGTH * Math.cos((lineLocations[3] - lineLocations[1]) / 2);
		
		odo.setX(x);
		odo.setY(y);
	}
	
	private void correctTheta(double[] lineLocations) {
		double thetaY = lineLocations[3] - lineLocations[1];
		
		thetaY = Math.toDegrees(thetaY);
		double dThetaY = 90 + (thetaY / 2) - thetaY + 180;
		odo.setTheta(odo.getTheta() + Math.toRadians(dThetaY));
	}
	
}
