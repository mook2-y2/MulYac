package com.mulyac.mulyac_android_client.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.mulyac.mulyac_android_client.activities.ViewActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SerialConnector {
    private Context mContext;
    private ViewActivity.SerialListener mListener;
    private Handler mHandler;

    private UsbManager mUsbManager;
    private UsbSerialDriver mUsbSerialDriver;


    private SerialMonitorThread mSerialThread;

    private UsbSerialPort mSerialPort;
    private PendingIntent mPermissionIntent;
    private static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbEndpoint mEndpointOut;
    private UsbEndpoint mEndpointIn;


    public SerialConnector(Context context, ViewActivity.SerialListener listener, Handler handler) {
        mContext = context;
        mListener = listener;
        mHandler = handler;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            Log.i("usb", "permission granted for device " + device);
                        }
                    }
                    else {
                        Log.i("usb", "permission denied for device " + device);
                    }
                }
            }
        }
    };


    public void initialize(){
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        if (availableDrivers.isEmpty()) {
            mListener.onReceive(Utils.MSG_SERIAL_ERROR, 0, 0, "Error : No Available device.", null);
            return;
        }

        mUsbSerialDriver = availableDrivers.get(0);
        if(mUsbSerialDriver == null){
            mListener.onReceive(Utils.MSG_SERIAL_ERROR, 0, 0, "Error : Driver is Null", null);
            return;
        }

        UsbInterface dataInterface = mUsbSerialDriver.getDevice().getInterface(1);

        for (int j=0; j<dataInterface.getEndpointCount(); j++) {
            UsbEndpoint ep = dataInterface.getEndpoint(j);
            if (ep.getDirection() == UsbConstants.USB_DIR_OUT &&
                    ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                mEndpointOut = dataInterface.getEndpoint(j);
            }
            if (ep.getDirection() == UsbConstants.USB_DIR_IN &&
                    ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                mEndpointIn = dataInterface.getEndpoint(j);
            }
        }






        mUsbManager.requestPermission(mUsbSerialDriver.getDevice(), mPermissionIntent);
        UsbDeviceConnection connection = mUsbManager.openDevice(mUsbSerialDriver.getDevice());
        connection.claimInterface(dataInterface, true);



        if (connection == null) {
            mListener.onReceive(Utils.MSG_SERIAL_ERROR, 0, 0, "Error : Cannot connect to device", null);
            return;
        }

        mSerialPort = mUsbSerialDriver.getPorts().get(0);
        try {
            mSerialPort.open(connection);
            mSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            e.printStackTrace();
            mListener.onReceive(Utils.MSG_SERIAL_ERROR, 0, 0, "Error : Cannot open port", null);
            try {
                mSerialPort.close();
            } catch (IOException e2) {
            }
            mSerialPort = null;
            return;
        } finally{

        }
        startThread(mUsbSerialDriver.getDevice(), connection);
    }




    // start thread
    private void startThread(UsbDevice device, UsbDeviceConnection connection) {
        if(mSerialThread == null) {
            mSerialThread = new SerialMonitorThread(device, connection);
            mSerialThread.start();
            mListener.onReceive(Utils.MSG_DEVICD_INFO, 0, 0, "Start Thread", null);
        }
    }



    // stop thread
    private void stopThread() {
        if(mSerialThread != null && mSerialThread.isAlive())
            mSerialThread.interrupt();
        if(mSerialThread != null) {
            mSerialThread.setKillSign(true);
            mSerialThread = null;
        }
    }





    public class SerialMonitorThread extends Thread {
        // Thread status
        private boolean mKillSign = false;
        private SerialCommand mCmd = new SerialCommand();
        private UsbDevice mUsbDevice;
        private UsbDeviceConnection mUsbDeviceConnection;

        public SerialMonitorThread(UsbDevice device, UsbDeviceConnection connection) {
            mUsbDevice = device;
            mUsbDeviceConnection = connection;
        }



        private void initializeThread() {
            // This code will be executed only once.
        }

        private void finalizeThread() {
        }

        // stop this thread
        public void setKillSign(boolean isTrue) {
            mKillSign = isTrue;
        }

        /**
         *	Main loop
         **/
        @Override
        public void run()
        {
            //byte buffer[] = new byte[128];
            // buffer에 값이 안들어가고 있음. 역시 c에도 전혀 값이 안써짐.


            while(!Thread.interrupted())
            {
                if(mSerialPort != null) {
                    //Arrays.fill(buffer, (byte) 0x00);



                        // Read received buffer
                        //int numBytesRead = mSerialPort.read(buffer, 1000);
                        byte buffer[] = new byte[128];
                        int numBytesRead = mUsbDeviceConnection.bulkTransfer(mEndpointIn,buffer, buffer.length, 300);
                        String str = "";

                        if(numBytesRead>0) {

                            // Extract data from buffer
                            for(int i=0; i<numBytesRead; i++) {
                                //char c = (char)buffer[i];
                                char c = (char) (buffer[i] & 0xff);

                                if(c == 'e') {
                                    // This is end signal. Send collected result to UI
                                    if(true) {
                                        float x = Float.valueOf(mCmd.toStringX());
                                        float y = Float.valueOf(mCmd.toStringY());
                                        float z = Float.valueOf(mCmd.toStringZ());
                                        GyroData gyroData = new GyroData(x, y, z);
                                        Message message = mHandler.obtainMessage(Utils.MSG_READ_DATA, gyroData);
                                        mHandler.sendMessage(message);
                                    }
                                } else {
                                    mCmd.addChar(c);
                                }

                            }
                        } // End of if(numBytesRead > 0)

                }

                try {
                    Thread.sleep(10);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                if(mKillSign)
                    break;

            }	// End of while() loop

            // Finalize
            finalizeThread();

        }	// End of run()


    }	// End of SerialMonitorThread


    public void finalize() {
        try {
            mUsbSerialDriver = null;
            stopThread();

            mSerialPort.close();
            mSerialPort = null;
        } catch(Exception ex) {
            mListener.onReceive(Utils.MSG_SERIAL_ERROR, 0, 0, "Error: Cannot finalize serial connector \n" + ex.toString() + "\n", null);
        }
    }

}
