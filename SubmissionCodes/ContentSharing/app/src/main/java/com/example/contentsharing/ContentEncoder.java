package com.example.contentsharing;

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

public class ContentEncoder extends Fragment {

    private static final int NUMBER_LENGTH = 10;
    private static final int MIN_NAME_LENGTH = 0;
    private static final int MARKER_FREQ = 23000;
    private static final int MAX_NAME_LENGTH = 50;
    private static final double VOLUME_MUL_VALUE = 6.67;
    private static final int TRANSFER_COMPLETED = 22400;
    private static final int START_NUM_TRANSFER = 18900;
    private static final int START_STOP_NAM_TRANSFER = 18800;
    private static final int CODE_DURATION = 120;
    private static final int START_STOP_CODE_DURATION = 300;
    private static final int[] FREQUENCY_ARRAY = new int[]{18000, 18100, 18200, 19000, 19100, 19200,
                                                        //   A      B      C      D      E     F
            19300, 19400, 19500, 19600, 19700, 19800, 19900, 20000, 20200, 20400, 20600, 20800,
            // G      H      I      J       K     L     M      N      O      P      Q       R
            21000, 21100, 21200, 21300, 21400, 21500, 21600, 21900, 22000, 22100, 22200, 22300,
            // S      T       U      V      W     X      Y     Z    Space
            22400, 22500, 22600, 22700, 18400, 18500, 18600};
    private String mNameCode;
    private EditText mName;
    private EditText mNumber;
    private TextView mVolDisplay;
    private String mNumberCode;
    private ToggleButton mShareButton;
    private SeekBar mVolumeSeekBar;
    private AudioManager mAudioManager;
    private int[] mFreqArray = new int[11];
    private final PlaySine wave = new PlaySine();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_encoder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mName = (EditText) view.findViewById(R.id.editText);
        mNumber = (EditText) view.findViewById(R.id.editText2);
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
     * Validation of Name and Phone Number given by user
     */

    private void encodeInit() {
        mShareButton.setOnClickListener(v -> {
            mNameCode = mName.getText().toString().toUpperCase();
            mNumberCode = mNumber.getText().toString();

            if (mNameCode.length() > MAX_NAME_LENGTH || mNameCode.length() == MIN_NAME_LENGTH) {
                mName.setText("Unknown Name");
            }
            if (mNumberCode.length() != NUMBER_LENGTH) {
                mNumber.setText("Enter a valid Phone Number");
            }

            Thread encodeThread = new Thread(() -> {
                sendName(mNameCode);
                sendNumber(mNumberCode);
            });

            encodeThread.start();
        });
    }

    /**
     * Sending the Name through audio frequencies using marker digits
     */

    private void sendName(String code) {

        if (mNumberCode.length() == NUMBER_LENGTH && code.length() >
                MIN_NAME_LENGTH && code.length() <= MAX_NAME_LENGTH) {
            boolean mPress = mShareButton.isChecked();
            if (mPress) {
                startStopCode();

                for (int i = 0; i < code.length(); i++) {
                    char aChar = code.charAt(i);
                    if (aChar != ' ') {
                        wave.setWave(FREQUENCY_ARRAY[(int) aChar - 65]);
                    } else {
                        wave.setWave(FREQUENCY_ARRAY[26]);
                    }
                    wave.start();
                    try {
                        Thread.sleep(CODE_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wave.stop();
                    markerCode();
                }
                startStopCode();
            } else {
                wave.stop();
            }
        }
    }

    /**
     * Sending the Phone Number through audio frequencies using marker digits
     */

    private void sendNumber(String number) {

        if (number.length() == NUMBER_LENGTH && mNameCode.length() > MIN_NAME_LENGTH &&
                mNameCode.length() <= MAX_NAME_LENGTH) {
            boolean mPress = mShareButton.isChecked();
            if (mPress) {
                startStopCodeNumber();

                for (int i = 0, j = 1; i < number.length(); i++, j++) {
                    mFreqArray[i] = Integer.parseInt(number.substring(i, j));
                    wave.setWave(FREQUENCY_ARRAY[mFreqArray[i]]);
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
    }

    /**
     * Sets the stopping audio frequency of 23200Hz to indicate that Phone Number transfer is completed
     */

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
     * Sets the starting audio frequency of 18900Hz to transfer the Phone number
     */

    private void startStopCodeNumber() {
        wave.setWave(START_NUM_TRANSFER);
        wave.start();
        try {
            Thread.sleep(START_STOP_CODE_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }

    /**
     * 23KHz audio frequency is generated when markerCode() is called
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
     * Sets the starting and ending audio frequency of 18800Hz for Name transfer
     */

    private void startStopCode() {
        wave.setWave(START_STOP_NAM_TRANSFER);
        wave.start();
        try {
            Thread.sleep(START_STOP_CODE_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }
}
