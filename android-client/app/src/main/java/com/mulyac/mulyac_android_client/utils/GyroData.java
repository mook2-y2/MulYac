package com.mulyac.mulyac_android_client.utils;

public class GyroData {
    private float mX;
    private float mY;
    private float mZ;

    public GyroData(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public float getX(){
        return mX;
    }

    public float getY(){
        return mY;
    }

    public float getZ(){
        return mZ;
    }


}
