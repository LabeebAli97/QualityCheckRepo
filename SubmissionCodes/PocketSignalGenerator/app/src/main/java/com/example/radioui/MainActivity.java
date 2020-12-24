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

    private static final int MIN_FREQUENCY = 20;
    private static final int MAX_FREQUENCY = 20000;
    private static final double VOLUME_MUL_VALUE = 6.67;
    private static final int SQUARE_CODE = 2131362101;
    private static final int TRIANGLE_CODE = 2131361806;
    private static final int SAWTOOTH_CODE = 2131362060;
    private RadioGroup mRadioGroup;
    private ToggleButton mOnOff;
    private TextView mFreqRangeText, mVolDisplayText, mFreqDisplayText;
    private EditText mFreqEnter;
    private AudioManager mAudioManager;
    private SeekBar mVolumeSeekBar;
    private SeekBar mFreqSeekBar;
    private double mFrequency = 20;
    private final PlayWave wave = new PlayWave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mOnOff = (ToggleButton) findViewById(R.id.toggleButton);
        mFreqRangeText = (TextView) findViewById(R.id.tv1);
        mVolDisplayText = (TextView) findViewById(R.id.volume);
        mFreqDisplayText = (TextView) findViewById(R.id.textView3);
        mFreqEnter = (EditText) findViewById(R.id.ed);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mVolumeSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        mFreqSeekBar = (SeekBar) findViewById(R.id.seekBar);

        mFreqRangeText.setText("Enter a frequency in range of 20Hz-20Khz");
        mVolDisplayText.setText(String.valueOf(mVolumeSeekBar.getProgress()));
        mFreqDisplayText.setText(String.valueOf(mFreqSeekBar.getProgress()));

        volumeSeekBarInit();
        freqSeekBarInit();

        /** When Toggle Button is pressed */

        mOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mId = mRadioGroup.getCheckedRadioButtonId();         // Gives Radio Button id in the Radio Group

            if (isChecked) {
                switch (mId) {
                    case SQUARE_CODE:           // Square Wave Radio button id in Radio Group
                        onOffSquare();
                        break;
                    case TRIANGLE_CODE:           // Triangular Wave Radio button id in Radio Group
                        onOffTriangle();
                        break;
                    case SAWTOOTH_CODE:           // SawTooth Wave Radio button id in Radio Group
                        onOffSawTooth();
                        break;
                    default:                  // Default Sine Wave Radio button id is considered
                        onOffSin();
                        break;
                }
            } else {
                wave.stop();
            }
        });
    }

    /**
     * Calls the SetWave function in the PlayWave class if start button is checked
     */

    private void onOffSin() {
        checkFrequencyRange();

        if (mFrequency >= MIN_FREQUENCY && mFrequency <= MAX_FREQUENCY) {
            wave.setWave((int) mFrequency);                           // Calls the setWave function in the PlayWave class
            boolean on = mOnOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    /**
     * Calls the setWaveSquare function in the PlayWave class if start button is checked
     */

    private void onOffSquare() {
        checkFrequencyRange();

        if (mFrequency >= MIN_FREQUENCY && mFrequency <= MAX_FREQUENCY) {
            wave.setWaveSquare((int) mFrequency);                 // Calls the setWaveSquare function in the PlayWave class
            boolean on = mOnOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    /**
     * Calls the setWaveTriangle function in the PlayWave class if start button is checked
     */

    private void onOffTriangle() {
        checkFrequencyRange();

        if (mFrequency >= MIN_FREQUENCY && mFrequency <= MAX_FREQUENCY) {
            wave.setWaveTriangle((int) mFrequency);            // Calls the setWaveTriangle function in the PlayWave class
            boolean on = mOnOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    /**
     * Calls the setWaveSawTooth function in the PlayWave class if start button is checked
     */

    private void onOffSawTooth() {
        checkFrequencyRange();

        if (mFrequency >= MIN_FREQUENCY && mFrequency <= MAX_FREQUENCY) {
            wave.setWaveSawTooth((int) mFrequency);            // Calls the setWaveSawTooth function in the PlayWave class
            boolean on = mOnOff.isChecked();
            if (on) {
                wave.start();
            } else {
                wave.stop();
            }
        }
    }

    /**
     * Volume Adjustment through Volume Seek Bar
     */

    private void volumeSeekBarInit() {
        try {
            mVolumeSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mVolumeSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                mVolDisplayText.setText(String.valueOf((int) (progress * VOLUME_MUL_VALUE)));             // Updating the Volume text box with progressed value
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Frequency Adjustment through Frequency Seek Bar
     */

    private void freqSeekBarInit() {
        mFreqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                wave.stop();
                mFreqDisplayText.setText(String.valueOf(progress));                        // Updating the progressed frequency value to the text view Box
                mFreqEnter.setText(String.valueOf(progress));                        // Updating the progressed frequency value to the edit text Box
                mFrequency = Double.parseDouble(mFreqDisplayText.getText().toString());
                wave.setWave((int) mFrequency);
                wave.start();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int mFreq = mFreqSeekBar.getProgress();
                wave.setWave((int) mFreq);
                wave.start();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.stop();
            }
        });
    }

    /**
     * Checking the given frequency Range
     */

    private void checkFrequencyRange() {
        try {
            mFrequency = Double.parseDouble(mFreqEnter.getText().toString());

            if (mFrequency >= MIN_FREQUENCY && mFrequency <= MAX_FREQUENCY) {
                mFreqDisplayText.setText(String.valueOf(mFrequency));
            } else {
                mFreqRangeText.setText("Enter a frequency in range of 20Hz-20Khz");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

