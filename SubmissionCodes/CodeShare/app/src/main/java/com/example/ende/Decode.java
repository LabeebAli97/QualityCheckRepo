package com.example.ende;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.libfreq.Complex;        //import the libfreq.jar file to access the complex java class
import com.example.libfreq.FFT;            //import the libfreq.jar file to access the FFT java class

public class Decode extends Fragment {

    private static final int MIN_FREQ = 11800;
    private static final int MAX_FREQ = 12200;
    private static final int SAMPLE_FREQUENCY = 192000;
    private static final int BUFFER_SIZE_IN_BYTES = 8192;
    private static final int END_RANGE_LOW = 22300;
    private static final int END_RANGE_HIGH = 22500;
    private static final int DECODE_RANGE = 100;
    private static final int MARKER_RANGE_HIGH = 23200;
    private static final int MARKER_RANGE_LOW = 22800;
    private static final int[] FREQUENCY_ARRAY = new int[]{18000, 18400, 18800, 19200, 19600, 20000,
            20400, 20800, 21200, 22000};
    private TextView mFreqDisplay;
    private TextView mCodeDisplay;
    private ToggleButton mReceiveButton;
    private Boolean mRecording;
    private Boolean mDecoding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.decode_layout, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFreqDisplay = (TextView) view.findViewById(R.id.textView);
        mCodeDisplay = (TextView) view.findViewById(R.id.textView2);
        mReceiveButton = (ToggleButton) view.findViewById(R.id.toggleButton2);
        mDecoding = false;
        mCodeDisplay.setText("Code :");

        startRecButton();
    }

    /**
     * Starts the code detection when start button is pressed
     */
    private void startRecButton() {
        mReceiveButton.setOnClickListener(v -> {
            boolean mPress = mReceiveButton.isChecked();
            if (mPress) {
                Thread recordThread = new Thread(() -> {
                    mRecording = true;
                    startRecord();
                });
                recordThread.start();
            }
            else{
                mRecording =false;
            }
        });
    }

    /**
     * Takes the audio frequency code as input and decodes that input
     */
    private void startRecord() {
        try {
            int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_FREQUENCY,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);  // Contains Estimated Minimum buffer size required for an AudioTrack
            AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_FREQUENCY,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    MIN_BUFFER_SIZE);
            short[] mAudioData = new short[MIN_BUFFER_SIZE];
            mAudioRecord.startRecording();
            StringBuilder mDecodeResult = new StringBuilder();

            while (mRecording) {
                mAudioRecord.read(mAudioData, 0, MIN_BUFFER_SIZE);
                double mFreq = findFreq(mAudioData);      // calls the findFreq function to get the frequency
                mFreqDisplay.setText(String.valueOf(mFreq));       // Displays the live frequency

                if (mFreq > MIN_FREQ && mFreq < MAX_FREQ) {
                    mDecoding = true;
                }

                if (mDecoding) {
                    if (mFreq > FREQUENCY_ARRAY[1] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[1] + DECODE_RANGE) {
                        mDecodeResult.append("1");
                    } else if (mFreq > FREQUENCY_ARRAY[2] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[2] + DECODE_RANGE) {
                        mDecodeResult.append("2");
                    } else if (mFreq > FREQUENCY_ARRAY[3] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[3] + DECODE_RANGE) {
                        mDecodeResult.append("3");
                    } else if (mFreq > FREQUENCY_ARRAY[4] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[4] + DECODE_RANGE) {
                        mDecodeResult.append("4");
                    } else if (mFreq > FREQUENCY_ARRAY[5] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[5] + DECODE_RANGE) {
                        mDecodeResult.append("5");
                    } else if (mFreq > FREQUENCY_ARRAY[6] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[6] + DECODE_RANGE) {
                        mDecodeResult.append("6");
                    } else if (mFreq > FREQUENCY_ARRAY[7] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[7] + DECODE_RANGE) {
                        mDecodeResult.append("7");
                    } else if (mFreq > FREQUENCY_ARRAY[8] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[8] + DECODE_RANGE) {
                        mDecodeResult.append("8");
                    } else if (mFreq > FREQUENCY_ARRAY[9] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[9] + DECODE_RANGE) {
                        mDecodeResult.append("9");
                    } else if (mFreq > FREQUENCY_ARRAY[0] - DECODE_RANGE &&
                            mFreq < FREQUENCY_ARRAY[0] + DECODE_RANGE) {
                        mDecodeResult.append("0");
                    } else if (mFreq > MARKER_RANGE_LOW &&
                            mFreq < MARKER_RANGE_HIGH) {     //Marker frequency
                        mDecodeResult.append("*");                  // M is used to represent the marker frequency
                    } else if (mFreq > END_RANGE_LOW &&
                            mFreq < END_RANGE_HIGH) {
                        mRecording = false;
                        mReceiveButton.toggle();
                    }
                }
            }
            decodeCode(mDecodeResult);
            mAudioRecord.stop();
            mDecoding = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the detected code and display the code
     */
    private void decodeCode(StringBuilder decodeResult) {

        if (decodeResult.length() != 0) {
            String mCode;
            String[] mFreqDecodeArray = new String[decodeResult.length()];
            StringBuilder mDecode = new StringBuilder();

            for (int i = 0; i < decodeResult.length(); i++) {
                mFreqDecodeArray[i] = decodeResult.substring(i, i + 1);
            }
            mCode = mFreqDecodeArray[0];
            mDecode.append(mCode);

            for (int i = 1; i < mFreqDecodeArray.length; i++) {
                if (!mFreqDecodeArray[i].equals(mCode)) {
                    mCode = mFreqDecodeArray[i];
                    if (!mFreqDecodeArray[i].equals("*")) {
                        mDecode.append(mFreqDecodeArray[i]);
                    }
                }
            }
            mCodeDisplay.setText("Code : " + mDecode);
        } else {
            mCodeDisplay.setText("Code Not Detected");
        }
    }

    /**
     * Finds the frequency using FFT and Complex java class files in libFreq.jar file and returns the frequency
     */
    private double findFreq(short[] audioData) {
        Complex[] mFftTempArray = new Complex[BUFFER_SIZE_IN_BYTES];

        for (int j = 0, k = 1000; j < BUFFER_SIZE_IN_BYTES; j++, k++) {
            mFftTempArray[j] = new Complex(audioData[k], 0);
        }
        Complex[] mFftArray = FFT.fft(mFftTempArray);
        double[] mMagnitude = new double[BUFFER_SIZE_IN_BYTES / 2];

        // calculate power spectrum (magnitude) values from fft[]
        for (int k = 0; k < (BUFFER_SIZE_IN_BYTES / 2) - 1; ++k) {
            double mReal = mFftArray[k].re();
            double mImaginary = mFftArray[k].im();
            mMagnitude[k] = Math.sqrt(mReal * mReal + mImaginary * mImaginary);
        }

        // find largest peak in power spectrum
        double mMaxMagnitude = mMagnitude[0];
        int mMaxIndex = 0;
        for (int k = 0; k < mMagnitude.length; ++k) {
            if (mMagnitude[k] > mMaxMagnitude) {
                mMaxMagnitude = (int) mMagnitude[k];
                mMaxIndex = k;
            }
        }
        //here will get frequency in hz like(17000,18000..etc)
        return (float) SAMPLE_FREQUENCY * (float) mMaxIndex / (float) BUFFER_SIZE_IN_BYTES;
    }
}