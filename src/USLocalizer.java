
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private UltrasonicSensor us;
	private Navigator navigator;
	private Driver driver; 
	private LocalizationType locType;
	private NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private int noWall= 36; 
	private int Distance; 
	public enum Direction {
		LEFT, RIGHT
	}
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, Driver driver, Navigator navigator, LocalizationType locType) {
		this.odo = odo;
		this.us = us;
		this.locType = locType;
		this.driver = driver;
		this.navigator= navigator;
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB, dChange;
		int rotationStep =1; // defining the different stages of the process  
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
		   
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			while(Distance < noWall && rotationStep == 1){
				Distance = getFilteredData();
			}
		
			driver.stop();
			
			//to not have the previous wall detection again 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
			 // keep rotating until the robot sees a wall, then latch the angle
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			
			while(Distance >= noWall && rotationStep == 1){
				Distance = getFilteredData();
				
			}
			
			angleA = Math.toDegrees( odo.getTheta());
			
			// switch direction and wait until it sees no wall
			rotationStep =2 ;
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(Distance < noWall && rotationStep == 2){
				Distance = getFilteredData();
			}
	 
			
			
			
			// keep rotating until the robot sees a wall, then latch the angle
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(Distance >= noWall && rotationStep == 2){
				Distance = getFilteredData();
				
			}
			driver.stop();
			angleB = Math.toDegrees(odo.getTheta());
			
			 
			   if(angleA < angleB){
				   dChange = (45 -((angleA+angleB)/2));
				   
			   Sound.beep();
			   }
			   else {
				   dChange = (225 -((angleA+angleB)/2));
				  
			   Sound.twoBeeps();
			   } 
			   
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			//turn till it sees a wall 
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			while(Distance >= noWall && rotationStep == 1){
				Distance = getFilteredData();
			}
			Sound.beep(); 
			
			//to not have the previous wall detection again 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			
		
			 // keep rotating until the robot doesn't see a wall, then latch the angle
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			
			while(Distance < noWall && rotationStep == 1){
				Distance = getFilteredData();
				
			}
			
			angleA = Math.toDegrees(odo.getTheta());
			LCD.drawString(angleA+"", 0, 4);
			
			// switch direction and wait until it sees no wall
			rotationStep =2 ;
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(Distance >= noWall && rotationStep == 2){
				Distance = getFilteredData();
			}
		  Sound.beep(); 
			
		//to not have the previous wall detection again 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(Distance < noWall && rotationStep == 2){
				Distance = getFilteredData();
				
			}
			driver.stop();
			angleB = Math.toDegrees(odo.getTheta());
			LCD.drawString(angleB + "", 0, 5);
			
			 
			   if(angleA < angleB){
				   dChange = (225 -((angleA+angleB)/2));
				   
			   Sound.beep();
			   }
			   else {
				   dChange = (45 -((angleA+angleB)/2));
				  
			   Sound.twoBeeps();
			   } 
			   

		}
		
		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
	
	   	//double turn = 360-dChange
		LCD.drawString( "angleA: " +  angleA + "", 0, 4);
		LCD.drawString("angnleB: " + angleB + "", 0, 5);
		LCD.drawString("dChange: "+dChange, 0, 7);
		
		odo.setTheta((odo.getTheta() + Math.toRadians(dChange))%360 );
		
		return;
	}
	
	
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		
		// filter out large values
		if (distance > noWall)
		distance = noWall;
				
		return distance;
	}

}
