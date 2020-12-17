package com.example.encoding10bit;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlaySine {
    private final int sampleRate = 48000;
    private final AudioTrack audioTrack;
    private int sampleCount;

    public PlaySine(){
        int bufferSize=AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRate , AudioFormat.CHANNEL_OUT_MONO , AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
    }

    public void setWave(int frequency) {

        sampleCount = (int) (((float) sampleRate / 100));
        short[] samples = new short[sampleCount];
        double phase = 0;
        int amplitude=32767;
        double twoPi = Math.PI * 2;

        for (int i = 0; i < sampleCount; i++) {
            samples[i] = (short) (amplitude * Math.sin(phase));
            phase += twoPi * frequency / sampleRate;
        }

        audioTrack.write(samples, 0, sampleCount);
    }

    public void start(){
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0, sampleCount, -1);
        audioTrack.play();
    }

    public void stop(){
        audioTrack.flush();
        audioTrack.stop();
    }
}