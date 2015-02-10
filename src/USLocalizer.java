
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
			
			//Start with a clockwise rotation
			//if we start facing a wall then
			// rotate the robot until it doesn't see a wall
		   
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			while(Distance < noWall && rotationStep == 1){
				Distance = getFilteredData();
			}
		
			driver.stop();
			
			//to not have the previous wall detection interfere with the next one 
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
			
			
			rotationStep =2 ;// second stage of the rotation
			
			
			//CounterClockwise rotation 
			// Turn until it doesn't see a wall
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
			
			//calculations to adjust the odometer  
			   if(angleA < angleB){
				   dChange = (45 -((angleA+angleB)/2));
		
			   }
			   else {
				   dChange = (225 -((angleA+angleB)/2));
	
			   } 
			   
		} else {
			
			// Start with a clockwise rotation
			// If we started by not facing a wall
			//turn till it sees a wall 
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			while(Distance >= noWall && rotationStep == 1){
				Distance = getFilteredData();
			}
			
			
			//to not have the previous wall detection again 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			 // keep rotating until the robot till it doesn't see a wall
			//then latch the angle
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			
			while(Distance < noWall && rotationStep == 1){
				Distance = getFilteredData();
				
			}
			
			angleA = Math.toDegrees(odo.getTheta());
			
			//CounterClockwise rotation
			// turn until it sees a wall again 
			rotationStep =2 ;
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(Distance >= noWall && rotationStep == 2){
				Distance = getFilteredData();
			}
			
		    //to not have the previous wall detection interfere with
			//the next one 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// keep rotating until it doesn't see a wall
			//then latch the angle
			Distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(Distance < noWall && rotationStep == 2){
				Distance = getFilteredData();
				
			}
			driver.stop();
			angleB = Math.toDegrees(odo.getTheta());

			//calculations to adjust the odometer
			   if(angleA < angleB){
				   dChange = (225 -((angleA+angleB)/2));
	
			   }
			   else {
				   dChange = (45 -((angleA+angleB)/2));

			   } 
			   

		}
		
	
		odo.setTheta((odo.getTheta() + Math.toRadians(dChange))%360 );
		
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
