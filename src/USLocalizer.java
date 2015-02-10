
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private UltrasonicSensor us;
	private Driver driver; 
	private LocalizationType locType;
	private int noWall= 36; 
	private int distance; 
	public enum Direction {
		LEFT, RIGHT
	}
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, Driver driver, LocalizationType locType) {
		this.odo = odo;
		this.us = us;
		this.locType = locType;
		this.driver = driver;
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double angleA, angleB, dChange;  
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			//Start with a clockwise rotation
			//if we start facing a wall then
			// rotate the robot until it doesn't see a wall
		   
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			while(distance < noWall){
				distance = getFilteredData();
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
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			
			while(distance >= noWall){
				distance = getFilteredData();
			}
			angleA = Math.toDegrees( odo.getTheta());
			
			//CounterClockwise rotation 
			// Turn until it doesn't see a wall
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(distance < noWall){
				distance = getFilteredData();
			}
	 
			// keep rotating until the robot sees a wall, then latch the angle
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(distance >= noWall){
				distance = getFilteredData();
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
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			while(distance >= noWall){
				distance = getFilteredData();
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
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.RIGHT);
			
			while(distance < noWall){
				distance = getFilteredData();		
			}
			
			angleA = Math.toDegrees(odo.getTheta());
			
			//CounterClockwise rotation
			// turn until it sees a wall again 
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(distance >= noWall){
				distance = getFilteredData();
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
			distance = getFilteredData();
			driver.continuousTurn(Driver.Direction.LEFT);
			while(distance < noWall){
				distance = getFilteredData();	
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
