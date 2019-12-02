package us.ilite.robot.commands;

import java.util.List;

import us.ilite.common.lib.physics.DriveCharacterization;
import com.team254.lib.util.ReflectingCSVWriter;

import us.ilite.common.lib.util.Conversions;
import us.ilite.robot.hardware.ECommonControlMode;
import us.ilite.robot.hardware.ECommonNeutralMode;
import us.ilite.robot.modules.Drive;
import us.ilite.robot.modules.DriveMessage;

/**
 * Straight port from Team 254's 2018 robot code: https://github.com/Team254/FRC-2018-Public
 */
public class CollectVelocityData implements ICommand {
    private static final double kMaxPower = 0.50;
    private static final double kRampRate = 0.01;
    private final Drive mDriveTrain;

    private final ReflectingCSVWriter<DriveCharacterization.VelocityDataPoint> mLeftCSVWriter, mRightCSVWriter;
    private final List<DriveCharacterization.VelocityDataPoint> mLeftVelocityData, mRightVelocityData;
    private final boolean mTurn;
    private final boolean mReverse;

    private boolean isFinished = false;
    private double mStartTime = 0.0;

    /**
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */

    public CollectVelocityData(Drive pDriveTrain, List<DriveCharacterization.VelocityDataPoint> leftData, List<DriveCharacterization.VelocityDataPoint> rightData, boolean reverse, boolean turn) {
        mDriveTrain = pDriveTrain;
        mLeftVelocityData = leftData;
        mRightVelocityData = rightData;
        mReverse = reverse;
        mTurn = turn;
        mLeftCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/LEFT_VELOCITY_DATA.csv", DriveCharacterization.VelocityDataPoint.class);
        mRightCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/RIGHT_VELOCITY_DATA.csv", DriveCharacterization.VelocityDataPoint.class);
    }

    @Override
    public void init(double pNow) {
        mStartTime = pNow;
    }

    @Override
    public boolean update(double pNow) {
        double percentPower = kRampRate * (pNow - mStartTime);
        if (percentPower > kMaxPower) {
            isFinished = true;
            return true;
        }
        DriveMessage driveMessage = new DriveMessage(
                (mReverse ? -1.0 : 1.0) * percentPower,
                (mReverse ? -1.0 : 1.0) * (mTurn ? -1.0 : 1.0) * percentPower,
                ECommonControlMode.PERCENT_OUTPUT,
                ECommonControlMode.PERCENT_OUTPUT);
        driveMessage.setNeutralMode(ECommonNeutralMode.COAST);
        mDriveTrain.setDriveMessage(driveMessage);

        updateData(mLeftVelocityData, mLeftCSVWriter, percentPower, mDriveTrain.getDriveHardware().getLeftVelTicks());
        updateData(mRightVelocityData, mRightCSVWriter, percentPower, mDriveTrain.getDriveHardware().getRightVelTicks());

        return isFinished;
    }

    public void updateData(List<DriveCharacterization.VelocityDataPoint> pVelocityDataPoints, ReflectingCSVWriter<DriveCharacterization.VelocityDataPoint> pCSVWriter, double pCurrentPercentPower, double pVelocityTicks) {
        pVelocityDataPoints.add(new DriveCharacterization.VelocityDataPoint(
                Conversions.ticksPer100msToRadiansPerSecond((int)pVelocityTicks), //convert velocity to radians per second
                pCurrentPercentPower * 12.0 //convert to volts
        ));
        pCSVWriter.add(pVelocityDataPoints.get(pVelocityDataPoints.size() - 1));
    }

    @Override
    public void shutdown(double pNow) {
        mDriveTrain.setDriveMessage(new DriveMessage(0.0, 0.0, ECommonControlMode.PERCENT_OUTPUT).setNeutralMode(ECommonNeutralMode.COAST));
        mLeftCSVWriter.flush();
        mRightCSVWriter.flush();
    }
}
