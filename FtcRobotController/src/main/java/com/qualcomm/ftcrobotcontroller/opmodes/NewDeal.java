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

    private final double shortdist = 11.3;
    private final double longdist = 6.4;
    private final double walldist = 21.0;
    private final double white = 50;

    private final double regspeed = 0.5;
    private final double slowspeed = 0.3;

    private final double turndeg = 30;


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
        //followWall(21, 50, 0, 0.4);
        //turnDegrees(360, 1.0);

        if(FtcRobotControllerActivity.isRed)
        {
            encoderStraight(6.6, 0.5);
            turnDegrees(37, 1.0);
            encoderStraight(0.3, 0.5);
            deployClimbers();
        }
        else
        {
            encoderStraight(-6.6, -0.5);
            turnDegrees(-37, -1.0);
            encoderStraight(-0.3, -0.5);
            deployClimbers();
        }


        //encoderStraight(15, 0.5);
        //findWall(0, 0);

        // 11.3 far, 6.4 close
        // 21 light dist, ~50 color

/*

        if(FtcRobotControllerActivity.isRed)
        {
            encoderStraight(shortdist, regspeed);
            turnDegrees(turndeg, regspeed);
            followWall(walldist, white, 6.0, slowspeed);
            adjustmentBlue();
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
        double k = 0.1;
        right.set(speed);
        left.set(speed);
        double tns = 0;
        double r=0, l=0;
        right.turnDiff();left.turnDiff();
        while((((r+l)/2 < turns && turns > 0) || ((r+l)/2 > turns && turns < 0)) && opModeIsActive()) {
            r += right.turnDiff();
            l += left.turnDiff();
            double amt = sigmoid((l - r) / k);

            right.set(amt * speed * 2);
            left.set((speed*2) - (amt * speed * 2));

            telemetry.addData("tn", ((r + l) / 2) + ", " + right.get() + " - " + left.get());
            Thread.sleep(100);
        }
        right.stop();
        left.stop();
    }

    public void findWall(double dist, double speed) throws InterruptedException
    {
        double level = (dist*2)+10;
        double lcc = 0.9;

        right.set(speed);
        left.set(speed);
        while(level > dist && opModeIsActive())
        {
            level = level*(lcc) + us.getUltrasonicLevel()*(1-lcc);
            Thread.sleep(10);
            telemetry.addData("w", us.getUltrasonicLevel() + ", " + level);
            telemetry.addData("dn", lightLeft.get());
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

        while(((gypos < degrees && degrees > 0) || (gypos > degrees && degrees < 0)) && opModeIsActive())
        {
            long timediff = timelast;
            timelast = System.currentTimeMillis();
            timediff = timelast - timediff;
            gypos += gyro.dps() * (timediff);
            telemetry.addData("turn", gypos);
        }

        right.stop();
        left.stop();
    }

    public void followWall(int dist, int stopcolor, double giveup, double speed) throws InterruptedException
    {
        double kval = 30;
        double lightc = 100;
        double lcc = 0.5;

        while(lightc > stopcolor && opModeIsActive())
        {
            lightc = lightc*(lcc)+lightLeft.get()*(1-lcc);
            double sp = sigmoid((us.getUltrasonicLevel()-dist)/kval);
            right.set(sp*(speed*2));
            left.set((speed*2)- (sp * (speed * 2)));

            telemetry.addData("us", us.getUltrasonicLevel()+", "+sp);
            telemetry.addData("ls", lightLeft.get()+", "+lightc);
            telemetry.addData("lr", left.get() + ", "+right.get());

            Thread.sleep(10);
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

    public void deployClimbers() throws InterruptedException
    {
        cds.set(1);
        Thread.sleep(1000);
        cds.set(0);
        Thread.sleep(1000);
        cds.set(1);
        Thread.sleep(1000);
    }

    public double sigmoid(double n)
    {
        return 1/(1+Math.pow(2.71828, -n));
    }
}
