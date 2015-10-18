package com.mulyac.mulyac_android_client.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.mulyac.mulyac_android_client.R;
import com.mulyac.mulyac_android_client.services.BackgroundService;
import com.mulyac.mulyac_android_client.utils.GyroData;
import com.mulyac.mulyac_android_client.utils.HttpRequest;
import com.mulyac.mulyac_android_client.utils.SerialConnector;
import com.mulyac.mulyac_android_client.utils.SingletonQueue;
import com.mulyac.mulyac_android_client.utils.StorageController;
import com.mulyac.mulyac_android_client.utils.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ViewActivity extends Activity {
    private SerialConnector mSerialConnector;
    private TextView mThreadTextView;
    private StorageController mStorageController;
    private Chronometer mChronometer;
    private int mStatus;
    private int mOffCounter = 1800;


    PowerManager pm;
    PowerManager.WakeLock wl;
    KeyguardManager km;
    KeyguardManager.KeyguardLock kl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        km=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        kl=km.newKeyguardLock("INFO");
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "INFO");
        wl.acquire(); //wake up the screen
        kl.disableKeyguard();// dismiss the keyguard


        setContentView(R.layout.activity_view);
        mThreadTextView = (TextView) findViewById(R.id.threadTextView);
        mSerialConnector = new SerialConnector(getApplicationContext(), new SerialListener(), new ActivityHandler());
        mStorageController = new StorageController(getApplicationContext(), Utils.INFO_STORAGE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

    }



    @Override
    protected void onResume() {
        super.onResume();
        mSerialConnector.initialize();
    }



    @Override
    protected void onPause() {
        mSerialConnector.finalize();
        Intent serviceIntent = new Intent(getApplicationContext(), BackgroundService.class);
        startService(serviceIntent);
        finish();
        super.onPause();
    }












    public static class SerialListener{
        public void onReceive(int msgcode, int count, int arg, String message, Object object){
            switch(msgcode) {
                case Utils.MSG_DEVICD_INFO:
                    //mWaterTextView.setText(message);
                    break;
                case Utils.MSG_DEVICE_COUNT:
                    //mWaterTextView.append(Integer.toString(count) + " device(s) found \n");
                    break;
                case Utils.MSG_READ_DATA_COUNT:
                    //mWaterTextView.append(Integer.toString(count) + " buffer received \n");
                    break;
                case Utils.MSG_READ_DATA:
                    if(object != null) {
                      //  mWaterTextView.setText((String) object);
                    }
                    break;
                case Utils.MSG_SERIAL_ERROR:
                    //mWaterTextView.append(message);
                    break;
                case Utils.MSG_FATAL_ERROR_FINISH_APP:
                    break;
            }
        }
    }

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Utils.MSG_DEVICD_INFO:
                    mThreadTextView.append((String)msg.obj +"    ");
                    break;
                case Utils.MSG_DEVICE_COUNT:
                    mThreadTextView.append(Integer.toString(msg.arg1) + " device(s) found    ");
                    break;
                case Utils.MSG_READ_DATA_COUNT:
                    mThreadTextView.append(((String)msg.obj) + "     ");
                    break;
                case Utils.MSG_READ_DATA:

                    if(msg.obj != null) {
                        GyroData gyroData = (GyroData) msg.obj;
                        int status = Utils.STATUS_EXCEPTION;
                        //mThreadTextView.append(gyroData.getX()+","+gyroData.getY()+","+gyroData.getZ()+"|| " );

                        String date = new SimpleDateFormat(Utils.DATE_FORMAT, java.util.Locale.getDefault()).format(new Date());
                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        hashmap.put("id",  mStorageController.get(Utils.USERID_STRING, ""));
                        //hashmap.put("region",  mStorageController.get(Utils.REGION_STRING, ""));
                        hashmap.put("datetime", date);

                        // home : <1, 1<3, 3<
                        if(gyroData.getZ()>-3) {
                            mThreadTextView.setText("OFF");
                            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            status = Utils.STATUS_OFF;
                            mChronometer.stop();
                            mStatus = Utils.STATUS_OFF;

                            mOffCounter++;
                            if(mOffCounter>=1800){
                                hashmap.put("status", String.valueOf(status));
                                HttpRequest request = new HttpRequest(Utils.HTTP_URL, hashmap, successListener, failListener);
                                SingletonQueue.getInstance(getApplicationContext()).addToRequestQueue(request);
                                mOffCounter = 0;
                            }

                        } else if(gyroData.getZ()<-3){
                            if(mStatus == Utils.STATUS_OFF){
                                mChronometer.setBase(SystemClock.elapsedRealtime());
                                mChronometer.start();
                                mOffCounter = 1800;
                            }

                            if(gyroData.getZ()>-5){
                                mThreadTextView.setText("ON");

                                // 코딩
                                status = Utils.STATUS_HALFON;
                                mStatus = Utils.STATUS_HALFON;

                            }
                            else if(gyroData.getZ()<-5){
                                mThreadTextView.setText("ON");
                                status = Utils.STATUS_ON;
                                mStatus = Utils.STATUS_ON;

                            }

                            hashmap.put("status", String.valueOf(status));
                            HttpRequest request = new HttpRequest(Utils.HTTP_URL, hashmap, successListener, failListener);
                            SingletonQueue.getInstance(getApplicationContext()).addToRequestQueue(request);

                        }


                    }
                    break;
                case Utils.MSG_SERIAL_ERROR:
                    mThreadTextView.append((String) msg.obj + "    ");
                    break;
            }
        }
    }


    private static Response.Listener<String> successListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String reponse) {
            Log.i("onResponse", "|" + reponse.toString()	+ "|");
        }
    };

    private static Response.ErrorListener failListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("onErrorResponse", "|" + error.getLocalizedMessage() + "|" + error.getClass().toString());
            error.printStackTrace();
        }
    };
}