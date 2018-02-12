package com.app.Accelerometer;

import java.io.Serializable;

/**
 * Created by jaani on 10/30/2017.
 */

public class Record implements Serializable {
    String X,Y,Z,Time,ChangeTime;

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getZ() {
        return Z;
    }

    public void setZ(String z) {
        Z = z;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getChangeTime() {
        return ChangeTime;
    }

    public void setChangeTime(String change) {
        ChangeTime = change;
    }
}
