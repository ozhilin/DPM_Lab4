import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;
import lejos.robotics.LightDetector;

public class LightLocalizer {
	private Odometer odo;
	private LightDetector ls;
	private Driver driver;
	private Navigator nav;
	
	private int LIGHT_THRESHOLD = 140;
	private int SENSOR_LENGTH = -12;
	
	public LightLocalizer(Odometer odo, LightDetector ls2, Driver driver, Navigator nav) {
		this.odo = odo;
		this.ls = ls2;
		this.driver = driver;
		this.nav = nav;
		// turn on the light
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		driver.continuousTurn(Driver.Direction.RIGHT);
		
		int lineCount = 0;
		double[] lineLocations = new double[4];
		while (lineCount < 4) {
			LCD.drawString("" + odo.getTheta(), 0, 0);
			if (ls.getLightValue() < LIGHT_THRESHOLD) {
				lineLocations[lineCount] = odo.getTheta();
				Sound.beep();

				lineCount++;
				// Sleep to avoid catching same line twice
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		driver.stop();
		
		correctXY(lineLocations);
		
		nav.travelTo(0, 0);
		nav.turnTo(90);
		
		Sound.beep();
	}
	
	private void correctXY(double[] lineLocations) {
		double x = SENSOR_LENGTH * Math.cos((lineLocations[2] - lineLocations[0]) / 2);
		double y = SENSOR_LENGTH * Math.cos((lineLocations[3] - lineLocations[1]) / 2);
		
		odo.setX(x);
		odo.setY(y);
	}
	
	private void correctTheta(double[] lineLocations) {
		double thetaY = Math.toDegrees(lineLocations[3] - lineLocations[1]);
		double thetaX = Math.toDegrees(lineLocations[2] - lineLocations[0]);
		
		double dThetaY = 90 + thetaY/2 - (thetaY - 180);
		double dThetaX = 90 + thetaX/2 - (thetaX - 180);
		
		double dTheta = (dThetaY + dThetaX)/2;
		dTheta = Math.toRadians(dTheta);
		odo.setTheta(odo.getTheta() + dTheta);
	}

}
