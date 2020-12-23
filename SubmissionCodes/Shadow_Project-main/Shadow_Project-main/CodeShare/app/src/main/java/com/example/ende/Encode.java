package com.example.ende;

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

public class Encode extends Fragment {

    TextView mRangeInfoDisplay;
    private String mCode;
    private TextView mVolDisplay;
    private EditText mCodeEdit;
    private ToggleButton mShareButton;
    private SeekBar mVolumeSeekBar;
    int[] mFreqArray = new int[11];
    private AudioManager mAudioManager;
    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 10;
    public static final int MARKER_FREQ = 23000;
    private final PlaySine wave = new PlaySine();
    public static final double VOLUME_MUL_VALUE = 6.67;
    public static final int START_STOP_FREQ = 12000;
    public static final int CODE_DURATION = 120;
    public static final int START_CODE_DURATION = 300;
    public static final int TRANSFER_COMPLETED = 22400;
    public static final int START_STOP_CODE_DURATION = 300;

    private static final int[] mFrequencyArray = new int[]{18000, 18400, 18800, 19200, 19600, 20000, 20400, 20800, 21200, 22000};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.encode_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCodeEdit = (EditText) view.findViewById(R.id.editTextNumber);
        mRangeInfoDisplay = (TextView) view.findViewById(R.id.textView);
        mShareButton = (ToggleButton) view.findViewById(R.id.toggleButton);
        mVolDisplay = (TextView) view.findViewById(R.id.volume);
        mVolumeSeekBar = (SeekBar) view.findViewById(R.id.seekBar2);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mVolDisplay.setText(String.valueOf(mVolumeSeekBar.getProgress()));

        encodeInit();
        volumeSeekBarInit();
    }


    /**
     * Volume Adjustment using SeekBar
     */
    private void volumeSeekBarInit() {
        try {
            mVolumeSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mVolumeSeekBar.setProgress(mAudioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                mVolDisplay.setText(String.valueOf((int) (progress * VOLUME_MUL_VALUE)));
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
     * Validation of Code that given by user and transferring the code
     */
    private void encodeInit() {

        mShareButton.setOnClickListener(v -> {

            mCode = mCodeEdit.getText().toString();

            if (mCode.length() > MAX_LENGTH || mCode.length() == MIN_LENGTH) {
                mCodeEdit.setText("Enter a valid Code");
            }

            Thread encodeThread = new Thread(() -> {

                if (mCode.length() > MIN_LENGTH && mCode.length() <= MAX_LENGTH) {
                    boolean mPress = mShareButton.isChecked();
                    if (mPress) {

                        startStopCode();

                        for (int i = 0, j = 1; i < mCode.length(); i++, j++) {

                            mFreqArray[i] = Integer.parseInt(mCode.substring(i, j));
                            wave.setWave(mFrequencyArray[mFreqArray[i]]);
                            wave.start();

                            try {
                                Thread.sleep(CODE_DURATION);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            wave.stop();
                            markerCode();
                        }

                        stopCode();
                        mShareButton.toggle();

                    } else {
                        wave.stop();

                    }
                }
            });

            encodeThread.start();
        });
    }

    private void stopCode() {
        wave.setWave(TRANSFER_COMPLETED);
        wave.start();
        try {
            Thread.sleep(START_STOP_CODE_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }


    /**
     * 23KHz audio frequency is generated  for 120ms when markerCode() is called
     */
    private void markerCode() {
        wave.setWave(MARKER_FREQ);
        wave.start();
        try {
            Thread.sleep(CODE_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }


    /**
     * Sets the starting and ending audio frequency of code to 12KHz for 300ms
     */
    private void startStopCode() {
        wave.setWave(START_STOP_FREQ);
        wave.start();
        try {
            Thread.sleep(START_CODE_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }

}
