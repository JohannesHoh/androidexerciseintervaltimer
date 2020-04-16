package com.github.johanneshoh.androidexerciseintervaltimer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TimerRunning2Fragment extends Fragment {
    public TimerRunning2Fragment() {
        // Required empty public constructor
    }

    public static TimerRunning2Fragment newInstance(String param1, String param2) {
        TimerRunning2Fragment fragment = new TimerRunning2Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer_running2, container, false);
    }
}
