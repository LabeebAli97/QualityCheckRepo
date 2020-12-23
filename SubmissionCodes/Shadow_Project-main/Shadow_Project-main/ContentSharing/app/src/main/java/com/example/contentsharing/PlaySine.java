package com.example.contentsharing;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlaySine {

    private int mSampleCount;
    private final AudioTrack mAudioTrack;
    public static final int SAMPLE_RATE = 48000;
    public static final int AMPLITUDE = 32767;
    public static final double TWO_PI = Math.PI * 2;
    public static final int SAMPLE_COUNT_SCALE = 100;
    public static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);  // Contains Estimated Minimum buffer size required for an AudioTrack


    /**
     * Constructor Class
     */

    public PlaySine() {
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE, AudioTrack.MODE_STATIC);
    }


    /**
     * Generate Sine Wave from 20Hz to 20KHz
     */

    public void setWave(int frequency) {
        mSampleCount = (int) (((float) SAMPLE_RATE / SAMPLE_COUNT_SCALE));
        short[] mSamples = new short[mSampleCount];
        double mPhase = 0;

        for (int i = 0; i < mSampleCount; i++) {
            mSamples[i] = (short) (AMPLITUDE * Math.sin(mPhase));
            mPhase += TWO_PI * frequency / SAMPLE_RATE;
        }

        mAudioTrack.write(mSamples, 0, mSampleCount);  //Writes the audio data to the audio sink for playback (streaming mode), or copies audio data for later playback (static buffer mode).
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
        mAudioTrack.flush();
        mAudioTrack.stop();
    }
}
