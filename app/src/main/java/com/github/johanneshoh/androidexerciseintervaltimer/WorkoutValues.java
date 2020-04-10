package com.github.johanneshoh.androidexerciseintervaltimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

class WorkoutValues {

        private static final String PREF_KEY_FRAGMENT = "INTERVAL_TIMER_WORKOUT_VALUES_";

        String workoutName = "";
        int noOfSets = 0;
        int eTimeMin = 0;
        int eTimeSec = 0;
        int pTimeMin = 0;
        int pTimeSec = 0;

        WorkoutValues(){

        }

        WorkoutValues(String workoutName, int noOfSets, int eTimeMin, int eTimeSec, int pTimeMin, int pTimeSec){
            this.workoutName = workoutName;
            this.noOfSets = noOfSets;
            this.eTimeMin = eTimeMin;
            this.eTimeSec = eTimeSec;
            this.pTimeMin = pTimeMin;
            this.pTimeSec = pTimeSec;
        }

        public  String toString(){
            return this.toCsvString();
        }

        public String toCsvString() {
            String str = this.workoutName + ";"
                    + Integer.valueOf((this.noOfSets)) + ";"
                    + Integer.valueOf(this.eTimeMin) + ";"
                    + Integer.valueOf(this.eTimeSec) + ";"
                    + Integer.valueOf(this.pTimeMin) + ";"
                    + Integer.valueOf(this.pTimeSec) + ";";
            return str;
        }

        public static WorkoutValues fromCsvString(String str){

            String[] split = str.split(";");

            String workoutName = split[0];

            int noOfSets = Integer.valueOf(split[1]);
            int eTimeMin = Integer.valueOf(split[2]);
            int eTimeSec = Integer.valueOf(split[3]);
            int pTimeMin = Integer.valueOf(split[4]);
            int pTimeSec = Integer.valueOf(split[5]);

            return new WorkoutValues(workoutName, noOfSets, eTimeMin, eTimeSec, pTimeMin, pTimeSec);
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        public static void saveValuesToPreferences(Context context, WorkoutValues wv){

            SharedPreferences sharedPref = context.getSharedPreferences("interval_timer_settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String intervalTimerValues = wv.toString();

            // currently the suffix key is hardcoded to 1 but intended to be used for
            // multiple different training settings
            editor.putString(PREF_KEY_FRAGMENT + "1", intervalTimerValues);

            editor.commit();
        }

        public static WorkoutValues loadValuesFromPreferences(Context context){

            SharedPreferences sharedPref = context.getSharedPreferences("interval_timer_settings", Context.MODE_PRIVATE);
            // currently the key suffix is hardcoded to 1 but intended to be used for
            // multiple different training settings
            String workoutValues = sharedPref.getString(PREF_KEY_FRAGMENT + "1", null);
            if(workoutValues == null){
                return null;
            } else {
                try {
                    WorkoutValues wv = WorkoutValues.fromCsvString(workoutValues);
                    return wv;
                } catch (Exception e){
                    Log.e("", e.toString());
                    return null;
                }
            }

        }

    }
