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



    private static enum TimeMetaDataEnum {
        GET_READY, WORKOUT, PAUSE;
    }

    private static boolean workoutValuesChanged = true;

    private static ImageView imageViewWorkout;
    private static ImageView imageViewPause;
    private static ImageView imageViewDone;
    private static ImageView imageViewGetReady;
    private static ImageButton imageButtonPause;
    private static ImageButton imageButtonPlay;

    private static TextView textViewInstructions;
    private static TextView textViewRemainingSets;
    private static WorkoutValues workoutValues = null;
    private static int beepVolume = 100;
    private static TextView currentTimeTextView = null;
    private static CountDownTimer timer = null;
    private static int timerValue = 0;
    private static int currentTimerIndex = 0;
    private static ArrayList<Integer> timeIntervals = null;
    private static ArrayList<TimeMetaDataEnum> timeMetaData = null;

    public static void reset(){
        workoutValuesChanged = true;
        imageViewWorkout = null;
        imageViewPause = null;
        imageViewDone = null;
        imageViewGetReady = null;
        imageButtonPause = null;
        imageButtonPlay = null;
        textViewInstructions = null;
        textViewRemainingSets = null;
        workoutValues = null;
        beepVolume = 100;
        currentTimerIndex = 0;
        currentTimeTextView = null;
        timerValue = 0;
        currentTimerIndex = 0;
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        timeIntervals = new ArrayList<Integer>();
        timeMetaData = new ArrayList<TimeMetaDataEnum>();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {

        workoutValues = WorkoutValues.loadValuesFromPreferences(this);

        imageViewWorkout = findViewById(R.id.imageWorkout);
        imageViewPause = findViewById(R.id.imageSwitch);
        imageViewDone = findViewById(R.id.imageDone);
        imageViewGetReady = findViewById(R.id.imageGetReady);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        textViewInstructions = findViewById(R.id.instructionsText);
        textViewRemainingSets = findViewById(R.id.setsRemainaingTextView);
        imageButtonPause = findViewById(R.id.imageButtonPause);
        imageButtonPlay = findViewById(R.id.imageButtonPlay);

        imageViewWorkout.setVisibility(ImageView.INVISIBLE);
        imageViewPause.setVisibility(ImageView.INVISIBLE);
        imageViewDone.setVisibility(ImageView.INVISIBLE);
        imageViewGetReady.setVisibility(ImageView.VISIBLE);

        imageButtonPause.setVisibility(ImageView.INVISIBLE);

        // set timers
        if(timeMetaData == null || timeMetaData.size() == 0){
            Integer getReady = Integer.valueOf(5);
            timeIntervals.add(getReady);
            timeMetaData.add(TimeMetaDataEnum.GET_READY);
            for(int i=0; i<workoutValues.noOfSets; i++){
                Integer w = Integer.valueOf(workoutValues.eTimeMin * 60 + workoutValues.eTimeSec);
                timeIntervals.add(w);
                timeMetaData.add(TimeMetaDataEnum.WORKOUT);
                // no brake in the end - just end it
                if(i < workoutValues.noOfSets - 1){
                    Integer p = Integer.valueOf(workoutValues.pTimeMin * 60 + workoutValues.pTimeSec);
                    timeIntervals.add(p);
                    timeMetaData.add(TimeMetaDataEnum.PAUSE);
                }
            }
        }

        if(timer == null){
            imageButtonPlay.setVisibility(ImageView.VISIBLE);
            imageButtonPause.setVisibility(ImageView.INVISIBLE);
        } else {
            imageButtonPlay.setVisibility(ImageView.INVISIBLE);
            imageButtonPause.setVisibility(ImageView.VISIBLE);
        }


        String tv = timeIntervals.get(currentTimerIndex).toString();
        currentTimeTextView.setText(tv);

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
                beepVolume = this.progress;
            }
        });

        if(!workoutValuesChanged){
            seekBar.setProgress(beepVolume);
        }

        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimerOrResumeTimer();
                imageButtonPlay.setVisibility(ImageView.INVISIBLE);
                imageButtonPause.setVisibility(ImageView.VISIBLE);
            }
        });

        imageButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                timer = null;
                timeIntervals.set(currentTimerIndex,timerValue);
                imageButtonPlay.setVisibility(ImageView.VISIBLE);
                imageButtonPause.setVisibility(ImageView.INVISIBLE);
            }
        });
    }


    private void updateRemainingSets() {
        int workoutTimersRemaining = 0;
        for(int i=currentTimerIndex; i<timeIntervals.size(); i++){
            if(timeMetaData.get(i) == TimeMetaDataEnum.WORKOUT){
                workoutTimersRemaining++;
            }
        }
        textViewRemainingSets.setText(Integer.valueOf(workoutTimersRemaining).toString());
    }

    void startTimerOrResumeTimer(){

        if(timer != null){
            timer.cancel();
        }

        if(timerValue == 0){
            timerValue = timeIntervals.get(currentTimerIndex) * 1000;
        }

        timer = new CountDownTimer(timerValue, 1000) {

            public void onTick(long millisUntilFinished) {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, beepVolume);
                timerValue = Long.valueOf(Math.round(millisUntilFinished / 1000)).intValue();
                String text = Integer.valueOf(timerValue).toString();
                currentTimeTextView.setText(text);

                updateRemainingSets();

                int toneLength = 150;
                if(timerValue <= 3){
                    toneLength = 150;
                }

                TimeMetaDataEnum currentMetaInfo = timeMetaData.get(currentTimerIndex);
                if(currentMetaInfo.equals(TimeMetaDataEnum.GET_READY)) {
                    imageViewGetReady.setVisibility(ImageView.VISIBLE);
                    imageViewPause.setVisibility(ImageView.INVISIBLE);
                    imageViewWorkout.setVisibility(ImageView.INVISIBLE);
                    textViewInstructions.setText(R.string.get_ready);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP,toneLength);
                } else if(currentMetaInfo.equals(TimeMetaDataEnum.WORKOUT)) {
                    imageViewWorkout.setRotation(imageViewWorkout.getRotation() + 45);
                    imageViewGetReady.setVisibility(ImageView.INVISIBLE);
                    imageViewPause.setVisibility(ImageView.INVISIBLE);
                    imageViewWorkout.setVisibility(ImageView.VISIBLE);
                    textViewInstructions.setText(R.string.do_exercise);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, toneLength);
                } else if(currentMetaInfo.equals(TimeMetaDataEnum.PAUSE)){
                    imageViewGetReady.setVisibility(ImageView.INVISIBLE);
                    imageViewPause.setVisibility(ImageView.VISIBLE);
                    imageViewWorkout.setVisibility(ImageView.INVISIBLE);
                    textViewInstructions.setText(R.string.pause);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP,toneLength);
                }

            }


            public void onFinish() {
                currentTimerIndex++;
                timerValue = 0;
                updateRemainingSets();
                if(currentTimerIndex < timeIntervals.size()){
                    startTimerOrResumeTimer();
                } else {
                    imageViewWorkout.setVisibility(ImageView.INVISIBLE);
                    imageViewDone.setVisibility(ImageView.VISIBLE);
                    imageViewPause.setVisibility(ImageView.INVISIBLE);
                    textViewInstructions.setText(R.string.done);
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

        init();
        if(workoutValuesChanged == false && timer != null){
            startTimerOrResumeTimer();
        }
        workoutValuesChanged = false;

        updateRemainingSets();
    }

}
