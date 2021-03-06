package com.qualcomm.ftcrobotcontroller.bamboo;

import com.qualcomm.ftcrobotcontroller.bamboo.Console;
import com.qualcomm.ftcrobotcontroller.bamboo.Gyro;
import com.qualcomm.ftcrobotcontroller.bamboo.Motor;
import com.qualcomm.ftcrobotcontroller.bamboo.Root;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import java.text.DecimalFormat;

/**
 * Created by chsrobotics on 12/7/2015.
 */
public class SensorPool {

    public LightSensor lRight;
    public LightSensor lLeft;
    public Gyro gyro;
    private Console console;
    public UltrasonicSensor us;

    public SensorPool(HardwareMap hardwareMap, Console cn)
    {
        console = cn;
        lRight = hardwareMap.lightSensor.get("lightRight");
        lLeft = hardwareMap.lightSensor.get("lightLeft");
        gyro = new Gyro("gyro", hardwareMap);
        us = hardwareMap.ultrasonicSensor.get("us");

        right = new Motor("right", hardwareMap);
        left = new Motor("left", hardwareMap, true);

        rrot = new Motor("rightrot", hardwareMap);

        lRight.enableLed(true);
        lLeft.enableLed(true);

        df = new DecimalFormat("###.##");
    }

    double gypos = 0;
    double drift = 0;
    long timefirst = System.currentTimeMillis();
    long timelast = timefirst;

    public Motor right;
    public Motor left;
    public Motor rrot, lrot;

    private DecimalFormat df;

    private double r, l;

    public void update()
    {
        long timediff = timelast;
        timelast = System.currentTimeMillis();
        timediff = timelast - timediff;
            gypos += gyro.dps() * (timediff);
        double avgchange = (right.turnDiff()+left.turnDiff())/2;
        drift += gypos * avgchange;

        double dpsr = (rrot.turnDiff()*360)*(timediff);

        r = sigmoid(drift*0.05);
        l = 1 - r;

        console.log("ls", "(" + (int) (lRight.getLightDetected() * 100) + ", " + (int) (lLeft.getLightDetected() * 100) + ")");
        console.log("gy",df.format(gyro.rotation())+", "+df.format(gyro.dps()) + ", " + df.format(gypos) + ", " + df.format(drift));
        console.log("bro", df.format(right.turns()) + ", " + df.format(left.turns()));
        console.log("rl", df.format(r)+", "+df.format(l));
        console.log("ggg", df.format(dpsr)+",  "+df.format(rrot.get()));
        console.log("us", df.format(us.getUltrasonicLevel())+"");
    }

    public double sigmoid(double inp)
    {
        return 1/(1+Math.pow(Math.E, -inp));
    }

}
