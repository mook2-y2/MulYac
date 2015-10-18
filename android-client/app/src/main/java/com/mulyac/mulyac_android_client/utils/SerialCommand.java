package com.mulyac.mulyac_android_client.utils;

public class SerialCommand {
    /**
     //---------- buffer structure when received
     mDataArray[0] : a
     mDataArray[1] : 1
     mDataArray[2] : 2
     mDataArray[3] : .
     mDataArray[4] : 3
     mDataArray[5] : 4
     ...
     mDataArray[n] : z

     ==> converts into float number : 12.34xxxxxx
     */


    public static final int SIZE_IN_BYTE = 20;
    private StringBuilder mStringX;
    private StringBuilder mStringY;
    private StringBuilder mStringZ;
    private int whichAxis = -1;
    private int X_AXIS = 0;
    private int Y_AXIS = 1;
    private int Z_AXIS = 2;


    public SerialCommand() {
        mStringX = new StringBuilder();
        mStringY = new StringBuilder();
        mStringZ = new StringBuilder();
    }

    public void initialize() {
        whichAxis = -1;
        mStringX = new StringBuilder();
        mStringY = new StringBuilder();
        mStringZ = new StringBuilder();
    }

    public void addChar(char c) {
        if(c < 0x00)
            return;
        if(c == 's') {
            initialize();
        }
        else if(c=='x') {
            whichAxis = X_AXIS;
        }
        else if(c=='y'){
            whichAxis = Y_AXIS;
        }
        else if(c=='z'){
            whichAxis = Z_AXIS;
        }
        else {
            if(whichAxis == X_AXIS) {
                mStringX.append(c);
            } else if(whichAxis == Y_AXIS){
                mStringY.append(c);
            } else if(whichAxis == Z_AXIS){
                mStringZ.append(c);
            }
        }
    }

    public String toStringX() {
        return mStringX.length()>0 ? mStringX.toString() : "No data";
    }
    public String toStringY() {
        return mStringY.length()>0 ? mStringY.toString() : "No data";
    }
    public String toStringZ() {
        return mStringZ.length()>0 ? mStringZ.toString() : "No data";
    }
}
