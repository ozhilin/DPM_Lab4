import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		final Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		LightSensor ls = new LightSensor(SensorPort.S1);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
		//usl.doLocalization();
		// perform the light sensor localization
		//LightLocalizer lsl = new LightLocalizer(odo, ls);
		//lsl.doLocalization();			
		
		(new Thread() {
			public void run() {
				odo.getNavigation().travelTo(30,30);
			}
		}).start();
		
		Button.waitForAnyPress();
		System.exit(0);
	}
}
