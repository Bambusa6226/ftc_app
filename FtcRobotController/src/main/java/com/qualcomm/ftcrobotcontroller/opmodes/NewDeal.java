package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.bamboo.BServo;
import com.qualcomm.ftcrobotcontroller.bamboo.Gyro;
import com.qualcomm.ftcrobotcontroller.bamboo.Light;
import com.qualcomm.ftcrobotcontroller.bamboo.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Created by chsrobotics on 2/3/2016.
 */
public class NewDeal extends LinearOpMode {

    // okay so now we have something new here
    // we go streaight until we detect the wall on our us
    // then turn to adjust sideways and track the wall keeping
    // us at n. continue going forward until we hit a line
    // with our light sensor, then adjust and drop.



    Light lightRight;
    Light lightLeft;
    Gyro gyro;
    UltrasonicSensor us;

    Motor right, left, rotLeft, rotRight, extLeft, extRight;
    BServo cds;




    @Override
    public void runOpMode() throws InterruptedException
    {

        lightRight = new Light("lightRight", hardwareMap);
        lightLeft = new Light("lightLeft", hardwareMap);
        gyro = new Gyro("gyro", hardwareMap);
        us = hardwareMap.ultrasonicSensor.get("us");

        right = new Motor("right", hardwareMap);
        left = new Motor("left", hardwareMap, true);

        rotRight = new Motor("rightrot", hardwareMap, true);
        rotLeft = new Motor("leftrot", hardwareMap);
        extRight = new Motor("rightext", hardwareMap);
        extLeft = new Motor("leftext", hardwareMap, true);
        cds = new BServo("cds", hardwareMap);

        lightRight.enable();
        lightLeft.enable();

        telemetry.addData("wait", FtcRobotControllerActivity.waittime);

        waitForStart();

        // wait for the time which is set in the robot controller application.

        if (FtcRobotControllerActivity.waittime != 0) {
            long st = System.currentTimeMillis();
            while ((System.currentTimeMillis() - st) / 1000 < FtcRobotControllerActivity.waittime && opModeIsActive()) {
                    Thread.sleep(1000);
            }
        }

        // and we start....
        //encoderStraight(10, 0.5);
        findWall(10, 0.2);
/*

        if(FtcRobotControllerActivity.isRed)
        {
            
        }
        else
        {
            encoderStraight(6.0, 0.5);
            findWall(10, 0.2);
            turnDegrees(30, 0.5);
            followWall(10, 90, 6.0, 0.5);
            adjustmentBlue();
        }
        deployClimbers();*/
    }

    public void encoderStraight(double turns, double speed) throws InterruptedException
    {
        double k = 10;
        right.set(speed);
        left.set(speed);
        double tns = 0;
        double r=0, l=0;
        right.turnDiff();left.turnDiff();
        while((r+l)/2 < turns && opModeIsActive())
        {
             r += right.turnDiff();
             l += left.turnDiff();
            double amt = sigmoid((l-r)*k);
            right.set(amt*speed*2);
            left.set(1-(amt*speed*2));
            telemetry.addData("tn", tns+", "+right.get()+" - "+left.get());
            Thread.sleep(100);
        }
        right.stop();
        left.stop();
    }

    public void findWall(double dist, double speed) throws InterruptedException
    {
        double level = dist*2;
        right.set(speed);
        left.set(speed);
        while(level > dist && opModeIsActive())
        {
            level = level*(0.8) + us.getUltrasonicLevel()*0.2;
            Thread.sleep(10);
            telemetry.addData("w",us.getUltrasonicLevel()+", "+ level);
        }
        right.stop();
        left.stop();
    }

    public void turnDegrees(double degrees, double speed)
    {
        double gypos = 0;
        long timefirst = System.currentTimeMillis();
        long timelast = timefirst;

        right.set(speed);
        left.set(-speed);

        while(gypos < degrees && opModeIsActive())
        {
            long timediff = timelast;
            timelast = System.currentTimeMillis();
            timediff = timelast - timediff;
            gypos += gyro.dps() * (timediff);
        }

        right.stop();
        left.stop();
    }

    public void followWall(int dist, int stopcolor, double giveup, double speed)
    {
        double kval = 1;
        double lightc = 0;
        double lcc = 0.5;

        while(lightc < stopcolor && opModeIsActive())
        {
            lightc = lightc*(lcc)+lightLeft.get()*(1-lcc);
            double sp = sigmoid((us.getUltrasonicLevel()-dist)*kval);
            right.set(sp*(speed*2));
            left.set(1-(sp*(speed*2)));
        }
        right.stop();
        left.stop();
    }

    public void adjustmentRed()
    {

    }

    public void adjustmentBlue()
    {

    }

    public void deployClimbers()
    {
        cds.set(1);
    }

    public double sigmoid(double n)
    {
        return 1/(1+Math.pow(2.71828, -n));
    }
}
