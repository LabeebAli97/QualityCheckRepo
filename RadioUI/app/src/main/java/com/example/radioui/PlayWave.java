package com.example.radioui;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayWave {

    private final int sampleRate = 192000;
    private final AudioTrack audioTrack;
    private int sampleCount;
    private final int amplitude=32767;
    private final double twoPi = Math.PI * 2;

    public PlayWave() {
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
    }

    public void setWave(int frequency) {
        sampleCount = (int) ((float) sampleRate / 10);
        short[] samples = new short[sampleCount];
        double phase = 0;

        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (amplitude * Math.sin(phase));
            phase += twoPi * frequency / sampleRate;
        }
        audioTrack.write(samples, 0, sampleCount);
    }

    public void start() {
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0, sampleCount, -1);
        audioTrack.play();
    }

    public void stop() {

        audioTrack.stop();
    }

    public void setWaveSquare(int frequency) {

        sampleCount = (int) ((float) sampleRate / frequency);
        short[] samples = new short[sampleCount];
        double phase = 0;
        short sampleCheck;

        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (amplitude * Math.sin(phase));
            sampleCheck = samples[i];
            if (sampleCheck > 0.0) {
                samples[i] = 32767;
            } else if (sampleCheck < 0.0) {
                samples[i] = -32767;
            }
            phase += twoPi * frequency / sampleRate;
        }
        audioTrack.write(samples, 0, sampleCount);
    }

    public void setWaveTriangle(int frequency) {
        sampleCount = (int) ((float) sampleRate / frequency);
        short[] samples = new short[sampleCount];
        for (int i = 0; i < sampleCount; i++) {

            if (i < (sampleCount / 4)) {
                samples[i] = (short) ((((float) i * (4.0f * (float) amplitude)) / (float) sampleCount));
            } else if (i > (3 * (sampleCount / 4))) {
                samples[i] = (short) (((((float) i * (4.0f * (float) amplitude)) / (float) sampleCount) - 2.0f * amplitude));
            } else {

                samples[i] = (short) (((float) 2.0f * amplitude - (((float) i * (4.0f * (float) amplitude)) / (float) sampleCount)));
            }
        }
        audioTrack.write(samples, 0, sampleCount);
    }

    public void setWaveSawTooth(int frequency) {
        sampleCount = (int) ((float) sampleRate / frequency);
        short[] samples = new short[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (((float) (-amplitude) + (((float) i * (2.0f * (float) amplitude)) / (float) sampleCount)));
        }
        audioTrack.write(samples, 0, sampleCount);
    }
}
