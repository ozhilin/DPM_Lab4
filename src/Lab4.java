import lejos.nxt.*;
import lejos.robotics.LightDetector;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		final Odometer odo = new Odometer();
		final Navigator nav = new Navigator(odo);
		final Driver driver = new Driver();
		
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		final LightDetector ls = new ColorSensor(SensorPort.S1);	
		
		
		final USLocalizer usl = new USLocalizer(odo, us, driver, USLocalizer.LocalizationType.RISING_EDGE);
		final LightLocalizer lsl = new LightLocalizer(odo, ls, driver, nav);
		
		Button.waitForAnyPress();
		
		(new Thread() {
			public void run() {
				odo.start();
				// perform the ultrasonic localization
				usl.doLocalization();
				// perform the light sensor localization
				lsl.doLocalization();
			}
		}).start();
		
		Button.waitForAnyPress();
		System.exit(0);
	}
}
