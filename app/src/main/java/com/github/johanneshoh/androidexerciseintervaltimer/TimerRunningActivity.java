package com.github.johanneshoh.androidexerciseintervaltimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
    private static int lastBeepVolume = 100;
    private static TextView currentTimeTextView = null;
    private static CountDownTimer timer = null;
    private static int timerValue = 0;
    private static int currentTimerIndex = 0;
    private static ArrayList<Integer> timeIntervals = null;
    private static ArrayList<TimeMetaDataEnum> timeMetaData = null;

    private static ToneGenerator toneGen = null;
    
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

        Log.d("DEBUG_TRA", "startTimerOrResumeTimer ");

        Log.d("DEBUG_TRA", "timerValue: " + timerValue
                + " timer: " + timer);

        if(timer != null){
            timer.cancel();
        }

        if(timerValue == 0){
            timerValue = timeIntervals.get(currentTimerIndex);
        }

        timer = new CountDownTimer(timerValue * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("DEBUG_TRA", "OnTick called");
                try {
                    if(toneGen == null || beepVolume != lastBeepVolume)
                    toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, beepVolume);
                    lastBeepVolume = beepVolume;
                } catch (RuntimeException e){
                    // not sure why that happens on old devices with API lower than 27
                    Log.e("DEBUG_TRA", "Exception caught " + e.getStackTrace());
                }
                timerValue = Long.valueOf(Math.round(millisUntilFinished / 1000)).intValue();
                String text = Integer.valueOf(timerValue).toString();
                currentTimeTextView.setText(text);

                updateRemainingSets();
                
                int toneLength0sec = 500;
                int toneType0sec = ToneGenerator.TONE_CDMA_PIP;

                int toneLength = 150;
                int toneType = 0;

                if(timerValue == 0){
                    toneLength = toneLength0sec;
                    toneType = toneType0sec;
                }

                TimeMetaDataEnum currentMetaInfo = timeMetaData.get(currentTimerIndex);

                int timerValue100 = timeIntervals.get(currentTimerIndex);

                if(currentMetaInfo.equals(TimeMetaDataEnum.GET_READY)) {
                    imageViewGetReady.setVisibility(ImageView.VISIBLE);
                    imageViewPause.setVisibility(ImageView.INVISIBLE);
                    imageViewWorkout.setVisibility(ImageView.INVISIBLE);
                    textViewInstructions.setText(R.string.get_ready);
                    toneType = ToneGenerator.TONE_CDMA_PIP;
                } else if(currentMetaInfo.equals(TimeMetaDataEnum.WORKOUT)) {
                    imageViewWorkout.setRotation(imageViewWorkout.getRotation() + 45);
                    imageViewGetReady.setVisibility(ImageView.INVISIBLE);
                    imageViewPause.setVisibility(ImageView.INVISIBLE);
                    imageViewWorkout.setVisibility(ImageView.VISIBLE);
                    textViewInstructions.setText(R.string.do_exercise);

                    // is the timer value at least 4 seconds? (for less than this a half time beep
                    // seems to be useless...
                    if(timerValue != 0) {
                        toneType = ToneGenerator.TONE_CDMA_ONE_MIN_BEEP;
                        if (timerValue100 >= 4) {
                            // is it half time?
                            if (Math.round(timerValue100 - timerValue) == Math.round(timerValue100 / 2)) {
                                Log.d("DEBUG_TRA", "half round");
                                toneLength = toneLength * 3;
                            }
                            if (Math.round(timerValue100 - timerValue) >= Math.round(timerValue100 / 2)) {
                                textViewInstructions.setText(R.string.half_done);
                            }
                        }
                    }

                } else if(currentMetaInfo.equals(TimeMetaDataEnum.PAUSE)){
                    imageViewGetReady.setVisibility(ImageView.INVISIBLE);
                    imageViewPause.setVisibility(ImageView.VISIBLE);
                    imageViewWorkout.setVisibility(ImageView.INVISIBLE);
                    textViewInstructions.setText(R.string.pause);
                    toneType = ToneGenerator.TONE_CDMA_PIP;
                }


                if(timerValue == 0){
                    Log.d("DEBUG_TRA", "timerValue is 0");
                    // on finish is also called when timer.cancel() is called but in that case
                    // time is not really over, yet...
                    currentTimerIndex++;
                    updateRemainingSets();
                    if (currentTimerIndex < timeIntervals.size()) {
                        startTimerOrResumeTimer();
                    } else {
                        imageViewWorkout.setVisibility(ImageView.INVISIBLE);
                        imageViewDone.setVisibility(ImageView.VISIBLE);
                        imageViewPause.setVisibility(ImageView.INVISIBLE);
                        textViewInstructions.setText(R.string.done);
                    }
                }


                Log.d("DEBUG_TRA", "timerValue: " + timerValue
                        + " toneLength: " + toneLength
                        + " toneType: " + toneType);

                if(toneGen != null){
                    toneGen.startTone(toneType, toneLength);
                }

            }


            public void onFinish() {
            }

        };
        timer.start();

    }


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
