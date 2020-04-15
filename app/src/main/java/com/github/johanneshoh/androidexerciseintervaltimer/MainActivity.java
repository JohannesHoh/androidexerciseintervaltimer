package com.github.johanneshoh.androidexerciseintervaltimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {


    EditText numberOfSetsText;
    NumberPicker exerciseTimeMinNumberPickerView;
    NumberPicker exerciseTimeSecNumberPickerView;
    NumberPicker pauseTimeMinNumberPickerView;
    NumberPicker pauseTimeSecNumberPickerView;


    private void setIntervalTimerValues(int noOfSets, int eTimeMin, int eTimeSec, int pTimeMin, int pTimeSec){
        numberOfSetsText.setText(Integer.valueOf(noOfSets).toString());
        exerciseTimeMinNumberPickerView.setValue(eTimeMin);
        exerciseTimeSecNumberPickerView.setValue(eTimeSec);
        pauseTimeMinNumberPickerView.setValue(pTimeMin);
        pauseTimeSecNumberPickerView.setValue(pTimeSec);
    }

    private void setDefaultValues(){
        setIntervalTimerValues(5, 0, 30, 0, 5);
    }

    private void setValuesFromPreferences(){
        WorkoutValues wv = WorkoutValues.loadValuesFromPreferences(MainActivity.this);
        if(wv != null){
            setIntervalTimerValues(wv.noOfSets, wv.eTimeMin, wv.eTimeSec, wv.pTimeMin, wv.pTimeSec);
        } else {
            setDefaultValues();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberOfSetsText = findViewById(R.id.numberOfSetsText);
        exerciseTimeMinNumberPickerView = findViewById(R.id.exerciseTimeMinNumberPicker);
        exerciseTimeSecNumberPickerView = findViewById(R.id.exerciseTimeSecNumberPicker);
        pauseTimeMinNumberPickerView = findViewById(R.id.pauseTimeMinNumberPicker);
        pauseTimeSecNumberPickerView = findViewById(R.id.pauseTimeSecNumberPicker);


        // set min and max values for time pickers

        exerciseTimeMinNumberPickerView.setMinValue(0);
        exerciseTimeMinNumberPickerView.setMaxValue(99);

        exerciseTimeSecNumberPickerView.setMinValue(1);
        exerciseTimeSecNumberPickerView.setMaxValue(59);


        pauseTimeMinNumberPickerView.setMinValue(0);
        pauseTimeMinNumberPickerView.setMaxValue(99);

        pauseTimeSecNumberPickerView.setMinValue(0);
        pauseTimeSecNumberPickerView.setMaxValue(59);


        // initialize all
        setValuesFromPreferences();


        // set button listeners

        Button buttonMinus = findViewById(R.id.buttonMinus);
        Button buttonPlus = findViewById(R.id.buttonPlus);

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = getNoOfSetsValue();
                if(currentValue > 1) {
                    numberOfSetsText.setText(Integer.valueOf(currentValue - 1).toString());
                }
            }
        });

        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = getNoOfSetsValue();
                numberOfSetsText.setText(Integer.valueOf(currentValue + 1).toString());
            }
        });


        // set activity

        final Intent intent = new Intent(this, TimerRunningActivity.class);
        Button buttonStartView = findViewById(R.id.buttonStart);
        buttonStartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int noOfSets = getNoOfSetsValue();
                int eTimeMin = exerciseTimeMinNumberPickerView.getValue();
                int eTimeSec = exerciseTimeSecNumberPickerView.getValue();
                int pTimeMin = pauseTimeMinNumberPickerView.getValue();
                int pTimeSec = pauseTimeSecNumberPickerView.getValue();
                WorkoutValues wv = new WorkoutValues("", noOfSets, eTimeMin, eTimeSec, pTimeMin, pTimeSec);
                WorkoutValues.saveValuesToPreferences(MainActivity.this, wv);
                startActivity(intent);
            }
        });


    }

    private int getNoOfSetsValue() {
        int noOfSets = 1;
        String setsText = numberOfSetsText.getText().toString();
        if(!"".equals(setsText)){
            noOfSets = Integer.valueOf(setsText);
        }
        return noOfSets;
    }
}

