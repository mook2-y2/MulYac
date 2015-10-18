package com.mulyac.mulyac_android_client.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.mulyac.mulyac_android_client.activities.ViewActivity;
import com.mulyac.mulyac_android_client.utils.GyroData;
import com.mulyac.mulyac_android_client.utils.HttpRequest;
import com.mulyac.mulyac_android_client.utils.SerialConnector;
import com.mulyac.mulyac_android_client.utils.SingletonQueue;
import com.mulyac.mulyac_android_client.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BackgroundService extends Service {
    private SerialConnector mSerialConnector;

    @Override
    public void onCreate() {
        super.onCreate();
        mSerialConnector = new SerialConnector(getApplicationContext(), new ViewActivity.SerialListener(), new ActivityHandler());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSerialConnector.initialize();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Utils.MSG_READ_DATA:

                    if(msg.obj != null) {
                        GyroData gyroData = (GyroData) msg.obj;

                        if(gyroData.getZ()>1){
                            Intent viewIntent = new Intent(BackgroundService.this, ViewActivity.class);
                            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(viewIntent);
                            mSerialConnector.finalize();
                            stopSelf();
                        }
                    }
                    break;

            }
        }
    }

}
