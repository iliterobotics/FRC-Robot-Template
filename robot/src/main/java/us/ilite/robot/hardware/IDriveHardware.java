package us.ilite.robot.hardware;

import com.team254.lib.geometry.Rotation2d;

import us.ilite.robot.modules.DriveMessage;

public interface IDriveHardware extends IHardware {

    void set(DriveMessage pDriveMessage);
    void configureMode(ECommonControlMode pControlMode);

    void setImu(IMU pImu);
    IMU getImu();
    Rotation2d getHeading();

    double getLeftInches();
    double getRightInches();

    double getLeftVelInches();
    double getRightVelInches();

    double getLeftVelTicks();
    double getRightVelTicks();

    double getLeftTarget();
    double getRightTarget();

    double getLeftCurrent();
    double getRightCurrent();

    double getLeftVoltage();
    double getRightVoltage();

}
