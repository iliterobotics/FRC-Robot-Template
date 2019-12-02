package us.ilite.robot.hardware;

import edu.wpi.first.wpilibj.AnalogInput;
import us.ilite.robot.commands.IAbsoluteDistanceProvider;

public class RockwellUltrasonic implements IAbsoluteDistanceProvider
{

    private static final double kMinMeasuringDistance = 1.75;
    private static final double kInchesPerVolt = 3.4;

    private AnalogInput mUltrasonicSensor;

    public RockwellUltrasonic()
    {
        // Change AnalogInput to Settings.ULTRASONIC_PORT
        mUltrasonicSensor = new AnalogInput(2);
    }

    /**
     * Distance is directly proportional to voltage using the equation:
     * distance = inches_per_volt * volts + min_distance
     * We obtained inches_per_volt by finding the minimum measuring distance
     * and the voltage from the sensor at a known distance, then found 
     * inches_per_volt = (known_distance - min_distance) / voltage.
     * 
     * @return The distance sensed by the ultrasonic sensor
     */
    public double getAbsoluteDistanceInInches() {
        return (getVoltage() * kInchesPerVolt) + kMinMeasuringDistance;
    }

    public double getVoltage() {
        return mUltrasonicSensor.getVoltage();
    }

}