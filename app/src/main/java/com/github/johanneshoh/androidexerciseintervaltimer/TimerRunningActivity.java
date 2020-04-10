package com.github.johanneshoh.androidexerciseintervaltimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class TimerRunningActivity extends AppCompatActivity {

    private static int beepVolume = 100;
    static TextView currentTimeTv = null;
    static CountDownTimer timer = null;
    static boolean timerRunning = false;
    static int timerValue = 0;
    static int currentTimerIndex = 0;
    static ArrayList<Integer> timeIntervals = null;


    void startTimerOrResumeTimer(){

        findViewById(R.id.imageWorkout).setVisibility(ImageView.VISIBLE);

        TimerRunningActivity.timer = new CountDownTimer(TimerRunningActivity.timeIntervals.get(TimerRunningActivity.currentTimerIndex) * 1000, 1000) {

            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, TimerRunningActivity.beepVolume);

            public void onTick(long millisUntilFinished) {
                TimerRunningActivity.timerValue = Long.valueOf(millisUntilFinished / 1000).intValue();
                TextView ctv = ((TextView)findViewById(R.id.currentTimeTextView));
                String text = Integer.valueOf(TimerRunningActivity.timerValue).toString();
                ctv.setText(text);

                ImageView imgW = findViewById(R.id.imageWorkout);
                ImageView imgP = findViewById(R.id.imageSwitch);
                if(TimerRunningActivity.currentTimerIndex % 2 == 0){
                    imgW.setRotation(imgW.getRotation() + 12);
                    imgP.setVisibility(ImageView.INVISIBLE);
                    imgW.setVisibility(ImageView.VISIBLE);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP,150);
                } else {
                    imgW.setVisibility(ImageView.INVISIBLE);
                    imgP.setVisibility(ImageView.VISIBLE);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                }

            }

            public void onFinish() {
                TimerRunningActivity.currentTimerIndex++;
                TimerRunningActivity.timerRunning = false;
                TimerRunningActivity.timerValue = 0;
                if(TimerRunningActivity.currentTimerIndex < TimerRunningActivity.timeIntervals.size()){
                    startTimerOrResumeTimer();
                } else {
                    ((TextView)findViewById(R.id.currentTimeTextView)).setText("done!");
                    findViewById(R.id.imageWorkout).setVisibility(ImageView.INVISIBLE);
                    findViewById(R.id.imageDone).setVisibility(ImageView.VISIBLE);
                    findViewById(R.id.imageButtonPause).setVisibility(ImageView.INVISIBLE);
                }
            }

        };
        timer.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_running);

        TimerRunningActivity.timeIntervals = new ArrayList<Integer>();

        findViewById(R.id.imageWorkout).setVisibility(ImageView.VISIBLE);
        findViewById(R.id.imageSwitch).setVisibility(ImageView.INVISIBLE);
        findViewById(R.id.imageDone).setVisibility(ImageView.INVISIBLE);

        final WorkoutValues wv = WorkoutValues.loadValuesFromPreferences(TimerRunningActivity.this);

        for(int i=0; i<wv.noOfSets; i++){
            Integer w = Integer.valueOf(wv.eTimeMin * 60 + wv.eTimeSec);
            TimerRunningActivity.timeIntervals.add(w);
            // no breake in the end - just end it
            if(i < wv.noOfSets - 1){
                Integer p = Integer.valueOf(wv.pTimeMin * 60 + wv.pTimeSec);
                TimerRunningActivity.timeIntervals.add(p);
            }
        }



        TimerRunningActivity.currentTimeTv = findViewById(R.id.currentTimeTextView);
        final ImageButton imageButtonPause = findViewById(R.id.imageButtonPause);
        final ImageButton imageButtonPlay = findViewById(R.id.imageButtonPlay);

        imageButtonPause.setVisibility(ImageView.INVISIBLE);

        String tv = TimerRunningActivity.timeIntervals.get(0).toString();
        TimerRunningActivity.currentTimeTv.setText(tv);


        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setMin(0);
        seekBar.setProgress(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                TimerRunningActivity.beepVolume = this.progress;
            }
        });

        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimerRunningActivity.timerRunning = true;
                startTimerOrResumeTimer();
                imageButtonPlay.setVisibility(ImageView.INVISIBLE);
                imageButtonPause.setVisibility(ImageView.VISIBLE);
            }
        });

        imageButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerRunningActivity.timerRunning = false;
                TimerRunningActivity.timer.cancel();
                TimerRunningActivity.timeIntervals.set(TimerRunningActivity.currentTimerIndex,TimerRunningActivity.timerValue);
                imageButtonPlay.setVisibility(ImageView.VISIBLE);
                imageButtonPause.setVisibility(ImageView.INVISIBLE);
            }
        });


    }
}
