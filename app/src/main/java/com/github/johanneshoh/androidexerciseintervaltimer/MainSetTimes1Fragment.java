package com.github.johanneshoh.androidexerciseintervaltimer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainSetTimes1Fragment extends Fragment {
    public MainSetTimes1Fragment() {
        // Required empty public constructor
    }

    public static MainSetTimes1Fragment newInstance(String param1, String param2) {
        MainSetTimes1Fragment fragment = new MainSetTimes1Fragment();
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
        return inflater.inflate(R.layout.fragment_main_set_times1, container, false);
    }
}
