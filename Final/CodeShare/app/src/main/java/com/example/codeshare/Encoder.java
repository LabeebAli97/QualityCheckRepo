package com.example.codeshare;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Encoder extends Fragment {

    private EditText et1;
    private ToggleButton tb1;
    int[] freqArray = new int[11];
    private String code;
    private final PlaySine wave = new PlaySine();
    TextView tv1;
    private TextView tv2;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;
    private static final int[] frequencyArray=new int[]{18000,18400,18800,19200,19600,20000,20400,20800,21200,22000};
    //    private static final int[] frequencyArray=new int[]{10000,1000,2000,3000,4000,5000,6000,7000,8000,9000};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        et1 = (EditText) findViewById(R.id.editTextNumber);
        tv1 = (TextView) findViewById(R.id.textView);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton);
        tv2 = (TextView) findViewById(R.id.volume);
        volumeSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        tv2.setText(String.valueOf(volumeSeekBar.getProgress()));

        encodeInit();
        volumeSeekBarInit();

        return inflater.inflate(R.layout.encoder_layout,container,false);
    }


}
