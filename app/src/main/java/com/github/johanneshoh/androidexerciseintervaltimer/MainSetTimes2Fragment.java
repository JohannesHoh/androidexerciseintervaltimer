package com.github.johanneshoh.androidexerciseintervaltimer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainSetTimes2Fragment extends Fragment {
    public MainSetTimes2Fragment() {
        // Required empty public constructor
    }

    public static MainSetTimes2Fragment newInstance(String param1, String param2) {
        MainSetTimes2Fragment fragment = new MainSetTimes2Fragment();
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
        return inflater.inflate(R.layout.fragment_main_set_times2, container, false);
    }
}
