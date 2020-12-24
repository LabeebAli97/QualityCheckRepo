package com.example.radioui;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayWave {

    private static final int SAMPLE_RATE = 192000;
    private static final int AMPLITUDE = 32767;
    private static final int SAMPLE_COUNT_SCALE = 100;
    private static final double TWO_PI = Math.PI * 2;
    private static final double SQUARE_LOWER_BIT = 0.0;
    private int mSampleCount;
    private final AudioTrack mAudioTrack;

    /**
     * Constructor Class
     */

    public PlayWave() {
        final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);      // Contains Estimated Minimum buffer size required for an AudioTrack
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE,
                AudioTrack.MODE_STATIC);
    }

    /**
     * Generate Sine Wave from 20Hz to 20KHz
     */

    public void setWave(int frequency) {
        mSampleCount = (int) ((float) SAMPLE_RATE / SAMPLE_COUNT_SCALE);
        short[] mSamples = new short[mSampleCount];
        double mPhase = 0;

        for (int i = 0; i < mSampleCount; i++) {
            mSamples[i] = (short) (AMPLITUDE * Math.sin(mPhase));
            mPhase += TWO_PI * frequency / SAMPLE_RATE;
        }
        mAudioTrack.write(mSamples, 0, mSampleCount);
    }

    /**
     * Reloads and Plays the Wave or Starts the Wave generation
     */

    public void start() {
        mAudioTrack.reloadStaticData();
        mAudioTrack.setLoopPoints(0, mSampleCount, -1);
        mAudioTrack.play();
    }

    /**
     * Stops the Wave Generation
     */

    public void stop() {
        mAudioTrack.stop();
    }

    /**
     * Generate Square Wave from 20Hz to 20KHz
     */

    public void setWaveSquare(int frequency) {
        mSampleCount = (int) ((float) SAMPLE_RATE / SAMPLE_COUNT_SCALE);
        short[] mSamples = new short[mSampleCount];
        double mPhase = 0;
        short mSampleCheck;

        for (int i = 0; i < mSampleCount; i++) {
            mSamples[i] = (short) (AMPLITUDE * Math.sin(mPhase));
            mSampleCheck = mSamples[i];
            if (mSampleCheck > SQUARE_LOWER_BIT) {
                mSamples[i] = AMPLITUDE;
            } else if (mSampleCheck < SQUARE_LOWER_BIT) {
                mSamples[i] = -AMPLITUDE;
            }
            mPhase += TWO_PI * frequency / SAMPLE_RATE;
        }
        mAudioTrack.write(mSamples, 0, mSampleCount);
    }

    /**
     * Generate Triangular Wave from 20Hz to 20KHz
     */

    public void setWaveTriangle(int frequency) {
        mSampleCount = (int) ((float) SAMPLE_RATE / frequency);
        short[] mSamples = new short[mSampleCount];

        for (int i = 0; i < mSampleCount; i++) {
            if (i < (mSampleCount / 4)) {
                mSamples[i] = (short) ((((float) i *
                        (4.0f * (float) AMPLITUDE)) / (float) mSampleCount));
            } else if (i > (3 * (mSampleCount / 4))) {
                mSamples[i] = (short) (((((float) i *
                        (4.0f * (float) AMPLITUDE)) / (float) mSampleCount) - 2.0f * AMPLITUDE));
            } else {
                mSamples[i] = (short) (((float) 2.0f * AMPLITUDE -
                        (((float) i * (4.0f * (float) AMPLITUDE)) / (float) mSampleCount)));
            }
        }
        mAudioTrack.write(mSamples, 0, mSampleCount);
    }

    /**
     * Generate SawTooth Wave from 20Hz to 20KHz
     */

    public void setWaveSawTooth(int frequency) {
        mSampleCount = (int) ((float) SAMPLE_RATE / frequency);
        short[] mSamples = new short[mSampleCount];

        for (int i = 0; i < mSampleCount; i++) {
            mSamples[i] = (short) (((float) (-AMPLITUDE) + (((float) i *
                    (2.0f * (float) AMPLITUDE)) / (float) mSampleCount)));
        }
        mAudioTrack.write(mSamples, 0, mSampleCount);
    }
}
