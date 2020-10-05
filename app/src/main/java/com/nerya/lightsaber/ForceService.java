package com.nerya.lightsaber;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.nerya.lightsaber.App.CHANNEL_ID;

public class ForceService extends Service implements SensorEventListener{
    @Override
    public void onCreate() {
        super.onCreate();
    }
    private SensorManager senSensorManager;
    private Sensor proximity;
    private boolean changed;
    private float val;
    private boolean isRunning;
    private static final int SENSOR_SENSITIVITY = 4;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Force Service")
                .setContentText("The Force is Listening")
                .setSmallIcon(R.drawable.saber_on)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        isRunning = false;
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity = senSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        senSensorManager.registerListener((SensorEventListener) this, proximity , SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_PROXIMITY) {
            Log.i("hey","hey");
            val = event.values[0];
            //changed = true;
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                //near
//                if(!isRunning){
//                    changed = false;
//                    isRunning = true;
//                }else{
//                    changed = true;
//                }
                changed = false;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(!changed){
                            Intent intent = new Intent(ForceService.this,MainActivity.class);
                            startActivity(intent);
                            isRunning = true;
                        }
                    }
                }, 3000);
            } else {
                changed = true;
            }
        }
//        if(mySensor.getType() == Sensor.TYPE_PROXIMITY)
//        {
//
//
//            if(event.values[0] < 1){
//
//            }
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}