package com.example.radioui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {


    private RadioGroup radioGroup;
    private ToggleButton onOff;
    private double frequency = 20;
    private TextView tv1, tv2, tv3;
    private EditText et1;
    private final PlayWave wave = new PlayWave();
    private AudioManager audioManager;
    private SeekBar volumeSeekBar;
    private SeekBar freqSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        onOff = (ToggleButton) findViewById(R.id.toggleButton);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.volume);
        tv3 = (TextView) findViewById(R.id.textView3);
        et1 = (EditText) findViewById(R.id.ed);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        freqSeekBar = (SeekBar) findViewById(R.id.seekBar);

        tv1.setText("Enter a frequency in range of 20Hz-20Khz");
        tv2.setText(String.valueOf(volumeSeekBar.getProgress()));
        tv3.setText(String.valueOf(freqSeekBar.getProgress()));

        volumeSeekBarInit();
        freqSeekBarInit();


        onOff.setOnCheckedChangeListener((buttonView, isChecked) -> {

            int id = radioGroup.getCheckedRadioButtonId();
//              tv1.setText(String.valueOf(id));

            if (isChecked) {
                switch (id) {
                    case 2131362101:
                        onOffSquare();
                        break;
                    case 2131361806:
                        onOffTriangle();
                        break;
                    case 2131362060:
                        onOffSawTooth();
                        break;
                    default:
                        onOffSin();
                        break;
                }
            } else {
                wave.stop();
            }
        });
    }


    private void onOffSin() {

        checkFrequencyRange();


        if (frequency >= 20 && frequency <= 20000) {
            wave.setWave((int) frequency);
            boolean on = onOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }


    private void onOffSquare() {

        checkFrequencyRange();

        if (frequency >= 20 && frequency <= 20000) {
            wave.setWaveSquare((int) frequency);
            boolean on = onOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    private void onOffTriangle() {

        checkFrequencyRange();

        if (frequency >= 20 && frequency <= 20000) {
            wave.setWaveTriangle((int) frequency);
            boolean on = onOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    private void onOffSawTooth() {

        checkFrequencyRange();

        if (frequency >= 20 && frequency <= 20000) {
            wave.setWaveSawTooth((int) frequency);
            boolean on = onOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    private void volumeSeekBarInit() {
        try {
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        } catch (Exception e) {
            e.printStackTrace();
        }
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                tv2.setText(String.valueOf((int) (progress * 6.67)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void freqSeekBarInit() {
        // When seekbar is changed

        freqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                wave.stop();
                tv3.setText(String.valueOf(progress));
                et1.setText(String.valueOf(progress));
                frequency = Double.parseDouble(tv3.getText().toString());
                wave.setWave((int) frequency);
                wave.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int freq = freqSeekBar.getProgress();
                wave.setWave((int) freq);
                wave.start();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.stop();
            }
        });
    }

    private void checkFrequencyRange() {
        try {
            frequency = Double.parseDouble(et1.getText().toString());
            if (frequency >= 20 && frequency <= 20000) {

                tv3.setText(String.valueOf(frequency));
            } else {
                tv1.setText("Enter a frequency in range of 20Hz-20Khz");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

