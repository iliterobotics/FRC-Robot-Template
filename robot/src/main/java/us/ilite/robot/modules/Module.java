package us.ilite.modules;

/**
 * The Module class defines how code written to control a specific subsystem (shooter, elevator, arm, etc.).
 * It also contains optional design patterns to adhere to.
 * All methods are passed a time, which is expected to be consistent between all modules updated in the same [mode]Periodic() call.
 */
public abstract class Module {

    public abstract void init(double pNow) {

    }

    public abstract void periodicInput(double pNow) {

    }

    public abstract void update(double pNow) {

    }


    public void periodicOutput(double pNow) {

    }

    /**
     * Runs a self-test routine on this module's hardware.
     */
    public boolean checkModule(double pNow) {
        return true;
    }

    /**
     * Zeroes sensors.
     */
    public void zeroSensors(double pNow) {
    }

}