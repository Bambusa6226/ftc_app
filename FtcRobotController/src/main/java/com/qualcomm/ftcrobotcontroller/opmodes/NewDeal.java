package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by chsrobotics on 2/3/2016.
 */
public class NewDeal extends LinearOpMode {

    // okay so now we have something new here
    // we go streaight until we detect the wall on our us
    // then turn to adjust sideways and track the wall keeping
    // us at n. continue going forward until we hit a line
    // with our light sensor, then adjust and drop.


    @Override
    public void runOpMode()
    {


        try {
            waitForStart();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if(FtcRobotControllerActivity.isRed)
        {
            
        }
        else
        {
            encoderStraight(6.0);
            findWall(10);
            turnDegrees(30, true);
            followWall(10, 0.9, 6.0);
            adjustmentBlue();
        }
        deployClimbers();
    }

    public void encoderStraight(double turns)
    {

    }

    public void findWall(int dist)
    {

    }

    public void turnDegrees(double degrees, boolean direction)
    {

    }

    public void followWall(int dist, double stopcolor, double giveup)
    {

    }

    public void adjustmentRed()
    {

    }

    public void adjustmentBlue()
    {

    }

    public void deployClimbers()
    {

    }
}
