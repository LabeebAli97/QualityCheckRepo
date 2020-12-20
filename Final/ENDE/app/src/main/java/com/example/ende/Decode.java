package com.example.ende;
import com.example.libfreq.Complex;
import com.example.libfreq.FFT;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Decode extends Fragment {



    private final int sampleFreq = 192000;
    private Button startRec;
    private Button stopRec;
    private Boolean recording;
    private Boolean decoding;
    private TextView tv1;
    private TextView tv2;
    private static final int[] frequencyArray = new int[]{18000, 18400, 18800, 19200, 19600, 20000, 20400, 20800, 21200, 22000};

    /**
     * Called when the activity is first created.
     */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.decode_layout, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startRec = (Button) view.findViewById(R.id.startbutton);
        stopRec = (Button) view.findViewById(R.id.stopbutton);

        tv1 = (TextView) view.findViewById(R.id.textView);
        tv2 = (TextView) view.findViewById(R.id.textView2);


        startRec.setEnabled(true);
        stopRec.setEnabled(false);

        decoding = false;
        tv2.setText("Code :");

        startRecButton();
        stopRecButton();
    }
    private void startRecButton() {


        startRec.setOnClickListener(v -> {

            Thread recordThread = new Thread(() -> {
                recording = true;
                startRecord();
            });
            recordThread.start();
            startRec.setEnabled(false);
            stopRec.setEnabled(true);
        });
    }

    private void stopRecButton() {
        stopRec.setOnClickListener(v -> {

            recording = false;
            startRec.setEnabled(true);
            stopRec.setEnabled(false);

        });
    }

    private void startRecord() {

        try {

            int minBufferSize = AudioRecord.getMinBufferSize(sampleFreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleFreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

            audioRecord.startRecording();

            StringBuilder decodeResult = new StringBuilder();

            while (recording) {

                audioRecord.read(audioData, 0, minBufferSize);

                double freq = findFreq(audioData);

                tv1.setText(String.valueOf(freq));

                if (freq > 11800 && freq < 12200) {
                    decoding = true;
                }

                if (decoding) {
                    if (freq > frequencyArray[1] - 100 && freq < frequencyArray[1] + 100) {
                        decodeResult.append("1");
                    } else if (freq > frequencyArray[2] - 100 && freq < frequencyArray[2] + 100) {
                        decodeResult.append("2");
                    } else if (freq > frequencyArray[3] - 100 && freq < frequencyArray[3] + 100) {
                        decodeResult.append("3");
                    } else if (freq > frequencyArray[4] - 100 && freq < frequencyArray[4] + 100) {
                        decodeResult.append("4");
                    } else if (freq > frequencyArray[5] - 100 && freq < frequencyArray[5] + 100) {
                        decodeResult.append("5");
                    } else if (freq > frequencyArray[6] - 100 && freq < frequencyArray[6] + 100) {
                        decodeResult.append("6");
                    } else if (freq > frequencyArray[7] - 100 && freq < frequencyArray[7] + 100) {
                        decodeResult.append("7");
                    } else if (freq > frequencyArray[8] - 100 && freq < frequencyArray[8] + 100) {
                        decodeResult.append("8");
                    } else if (freq > frequencyArray[9] - 100 && freq < frequencyArray[9] + 100) {
                        decodeResult.append("9");
                    } else if (freq > frequencyArray[0] - 100 && freq < frequencyArray[0] + 100) {
                        decodeResult.append("0");
                    } else if (freq > 22800 && freq < 23200) {
                        decodeResult.append("M");
                    }

                }

            }

            decodeCode(decodeResult);

            audioRecord.stop();
            decoding = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decodeCode(StringBuilder decodeResult) {
        if (decodeResult.length() != 0) {
            String a;
            String[] freqDecodeArray = new String[decodeResult.length()];
            StringBuilder decode = new StringBuilder();

            for (int i = 0; i < decodeResult.length(); i++) {
                freqDecodeArray[i] = decodeResult.substring(i, i + 1);
            }

            a = freqDecodeArray[0];
            decode.append(a);

            for (int i = 1; i < freqDecodeArray.length; i++) {

                if (!freqDecodeArray[i].equals(a)) {
                    a = freqDecodeArray[i];
                    if(!freqDecodeArray[i].equals("M")) {
                        decode.append(freqDecodeArray[i]);
                    }
                }
            }

            tv2.setText("Code : " + decode);


        } else {
            tv2.setText("Code Not Detected");
        }
    }


    private double findFreq(short[] audioData) {

        Complex[] fftTempArray = new Complex[8192];
        for (int j = 0, k = 1000; j < 8192; j++, k++) {
            fftTempArray[j] = new Complex(audioData[k], 0);
        }
        Complex[] fftArray = FFT.fft(fftTempArray);

        int bufferSizeInBytes = 8192;

        double[] magnitude = new double[bufferSizeInBytes / 2];

//      calculate power spectrum (magnitude) values from fft[]
        for (int k = 0; k < (8192 / 2) - 1; ++k) {
            double real = fftArray[k].re();
            double imaginary = fftArray[k].im();
            magnitude[k] = Math.sqrt(real * real + imaginary * imaginary);
        }

        // find largest peak in power spectrum
        double max_magnitude = magnitude[0];
        int max_index = 0;
        for (int k = 0; k < magnitude.length; ++k) {
            if (magnitude[k] > max_magnitude) {
                max_magnitude = (int) magnitude[k];
                max_index = k;
            }
        }

        //here will get frequency in hz like(17000,18000..etc)
        return (float) sampleFreq * (float) max_index / (float) 8192;
    }

}