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

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    private static final int PRIORITY_HIGH = 2;
    private static final int PRIORITY_LOW = 1;
    private static final int PLAY_ONCE = 0;
    private static final int LOOP = -1;
    private static final float RATE_NORMAL = 1f;

    private SensorManager mSensorManager;
    private Sensor mGyroSensor;
    private Sensor mProximitySensor;

    private SoundPool mSoundPool;
    private int mSoundOn;
    private boolean isOn;
    private int mSoundHit;
    private int mSoundHum = 0;
    private int mDarkHumId = 0;
    private int mLightHumId = 0;
    private int clash = 0;
    private int offSound = 0;
    private ImageView saber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(this);
        mSoundHum = mSoundPool.load(this, R.raw.hum, 1);
        mSoundHit = mSoundPool.load(this, R.raw.lswall01, 1);
        mSoundOn = mSoundPool.load(this, R.raw.lightsaber_ignites, 1);
        clash = mSoundPool.load(this, R.raw.clash,1);
        offSound = mSoundPool.load(this, R.raw.saber_off,1);

        saber =findViewById(R.id.saber);
        isOn = false;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private SensorEventListener mGyroListener = new SensorEventListener() {

        private boolean mHitStarted = false;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            // Rotational speed
            float[] values = event.values;
            float x = values[0];
            float z = values[2];

            float zStrength = z * z;
//            if (zStrength > 150f && !mHitStarted) {
//                mSoundPool.play(mSoundHit, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
//                mHitStarted = true;
//            } else {
//                mHitStarted = false;
//            }

            float strength = (zStrength + x * x) / 145f;
            float lightVolume = Math.min(strength + 0.1f, 1f); // 0.1 to 1.0
            float darkVolume = Math.max(0.4f - strength, 0.1f); // 0.4 to 0.1
            float rate = Math.min(strength / 2f + 1f, 1.2f); // 1.0 to 1.2

            if (mLightHumId != 0) {
                mSoundPool.setVolume(mLightHumId, lightVolume, lightVolume);
                mSoundPool.setRate(mDarkHumId, rate);
                mSoundPool.setVolume(mDarkHumId, darkVolume, darkVolume);
            }
        }
    };

    private SensorEventListener mProximityListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float distance = event.values[0];

            if (mLightHumId != 0 && distance < 1f) {
                //mSoundPool.play(mSoundHit, 0.5f, 0.5f, PRIORITY_LOW, PLAY_ONCE, RATE_NORMAL);
            }
        }
    };
    private void openSaber(){
        if (!isOn){
            saber.setImageResource(R.drawable.saber_on);
            isOn = true;
            startSound();
        }else{
            mSoundPool.play(offSound, 0.2f, 0.2f, 1, 0, 1.0f);
            saber.setImageResource(R.drawable.saber_off);
            isOn = false;
            stopSound();
        }
        Log.i("hell","started");
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mGyroListener, mGyroSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mProximityListener, mProximitySensor,
                SensorManager.SENSOR_DELAY_UI);
        if (mDarkHumId == 0) {
            //startSound();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mGyroListener, mGyroSensor);
        mSensorManager.unregisterListener(mProximityListener, mProximitySensor);
        stopSound();
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (mDarkHumId == 0 && sampleId == mSoundOn) {

        }
    }
    private void clash(){
        if(isOn){
            mSoundPool.play(clash, 1f, 1f, 1, 0, 1.0f);
        }
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
    private void startSound() {
        mSoundPool.play(mSoundOn, 0.5f, 0.5f, PRIORITY_HIGH, PLAY_ONCE, RATE_NORMAL);
        mDarkHumId = mSoundPool.play(mSoundHum, 0.4f, 0.4f, PRIORITY_HIGH, LOOP, RATE_NORMAL);
        mLightHumId = mSoundPool.play(mSoundHum, 0.1f, 0.1f, PRIORITY_HIGH, LOOP, 1.2f);
    }

    private void stopSound() {
        mSoundPool.stop(mDarkHumId);
        mSoundPool.stop(mLightHumId);
        mDarkHumId = 0;
        mLightHumId = 0;
    }
}