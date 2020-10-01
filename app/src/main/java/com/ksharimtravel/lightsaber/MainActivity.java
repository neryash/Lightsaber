package com.ksharimtravel.lightsaber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static androidx.constraintlayout.solver.SolverVariable.Type.CONSTANT;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor senAccelerometer,sigSense;
    private MediaPlayer player;
    private SoundPool mSoundPool;
    private int sound1,sound2,onSound,offSound,clash;
    AudioManager mAudioManager;
    private long lastTime;
    private int id,humId;
    private boolean isOn = false;
    private float lastSense;
    private ImageView saber;
    private TriggerEventListener triggerEventListener;
    //private Sensor proximity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startService();
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        lastTime = System.currentTimeMillis();
        saber = findViewById(R.id.saber);
        lastSense = 0;
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //proximity = senSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener((SensorEventListener) this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        //senSensorManager.registerListener((SensorEventListener) this, proximity , SensorManager.SENSOR_DELAY_NORMAL);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        sigSense = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        triggerEventListener = new TriggerEventListener() {
//            @Override
//            public void onTrigger(TriggerEvent event) {
//                Log.i("a","sig");
//            }
//        };
        //senSensorManager.requestTriggerSensor(triggerEventListener, sigSense);
        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, // Stream type
                0, // Index 15
                AudioManager.FLAG_PLAY_SOUND // Flags
        );
        saber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaber();
            }
        });
        int media_max_volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i("vol",media_max_volume + "");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(20)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this,ForceService.class));
                startService();
            }
        });
        sound1 = mSoundPool.load(this, R.raw.hum,1);
        sound2 = mSoundPool.load(this, R.raw.hum_high,1);
        onSound = mSoundPool.load(this, R.raw.saber_on,1);
        offSound = mSoundPool.load(this, R.raw.saber_off,1);
        clash = mSoundPool.load(this, R.raw.clash,1);
    }
    private void openSaber(){
        if (!isOn){
            mSoundPool.play(onSound, 0.5f, 0.5f, 1, 0, 1.0f);
            id = mSoundPool.play(sound2, 1.0f, 1.0f, 1, -1, 1.0f);
            humId = mSoundPool.play(sound1, 0.5f, 0.5f, 1, -1, 1.0f);
            saber.setImageResource(R.drawable.saber_on);
            isOn = true;
        }else{
            mSoundPool.pause(id);
            mSoundPool.pause(humId);
            mSoundPool.play(offSound, 0.2f, 0.2f, 1, 0, 1.0f);
            saber.setImageResource(R.drawable.saber_off);
            isOn = false;
        }
        Log.i("hell","started");
    }
    private void clash(){
        if(isOn){
            mSoundPool.play(clash, 1f, 1f, 1, 0, 1.0f);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if(mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            Log.i("hey",sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2]);
            //Toast.makeText(MainActivity.this, sensorEvent.values[0] + "", Toast.LENGTH_SHORT).show();
        }
        if(mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            long curTime = System.currentTimeMillis();
            int xa = (int) Math.floor(x);
            int fVol = (Math.abs(xa * 4));
            float volume;
            if(fVol > 0.3){
                volume = fVol/100f;
            }else{
                volume = 0f;
            }
            if(curTime -lastTime > 200){
//                mAudioManager.setStreamVolume(
//                        AudioManager.STREAM_MUSIC, // Stream type
//                        Math.abs((int) (Math.floor(x)+1)), // Index
//                        AudioManager.FLAG_SHOW_UI // Flags
//                );\
                mSoundPool.setVolume(id,volume,volume);
                if(Math.abs(xa-lastSense) > 15f){

                }
                lastTime = curTime;
                lastSense = xa;
            }
        }
        //play();
    }
    public void startService() {
        Log.i("start","start");
        Intent serviceIntent = new Intent(this, ForceService.class);
        MainActivity.this.startService(serviceIntent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            clash();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            openSaber();
            return true;
        }else{
            return super.onKeyDown(keyCode,event);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        mSoundPool.release();
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener((SensorEventListener) this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener((SensorEventListener) this, sigSense, SensorManager.SENSOR_DELAY_NORMAL);
        //senSensorManager.registerListener((SensorEventListener) this, proximity , SensorManager.SENSOR_DELAY_NORMAL);
    }
}