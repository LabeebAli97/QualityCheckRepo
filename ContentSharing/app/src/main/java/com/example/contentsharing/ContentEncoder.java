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

    private EditText et1, et2;
    private ToggleButton tb1;
    int[] freqArray = new int[11];
    private String code;
    private String number;
    private final PlaySine wave = new PlaySine();
    private TextView tv2;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;


    private static final int[] frequencyArray = new int[]{18000, 18100, 18200, 19000, 19100, 19200, 19300, 19400, 19500, 19600, 19700, 19800, 19900, 20000, 20200, 20400, 20600, 20800, 21000, 21100, 21200, 21300, 21400, 21500, 21600, 21900, 22000, 22100, 22200, 22300, 22400, 22500, 22600, 22700, 18400, 18500, 18600};
    //    private static final int[] frequencyArray=new int[]{10000,1000,2000,3000,4000,5000,6000,7000,8000,9000};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.content_encoder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et1 = (EditText) view.findViewById(R.id.editText);
        et2 = (EditText) view.findViewById(R.id.editText2);
        tb1 = (ToggleButton) view.findViewById(R.id.toggleButton);
        tv2 = (TextView) view.findViewById(R.id.volume);
        volumeSeekBar = (SeekBar) view.findViewById(R.id.seekBar2);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        tv2.setText(String.valueOf(volumeSeekBar.getProgress()));
        encodeInit();
        volumeSeekBarInit();
    }

    private void volumeSeekBarInit() {
        try {
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
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

    private void encodeInit() {

        tb1.setOnClickListener(v -> {

            code = et1.getText().toString().toUpperCase();
            number = et2.getText().toString();

            if (code.length() > 50 || code.length() == 0) {
                et1.setText("Unknown Name");
            }
            if (!(number.length() == 10)) {
                et2.setText("Enter a valid Phone Number");
            }

            Thread encodeThread = new Thread(() -> {


                sendName(code);

                //

                sendNumber(number);

            });

            encodeThread.start();

        });
    }

    private void sendName(String code) {

        if (number.length() == 10 && code.length() > 0 && code.length() <= 50) {
            boolean press = tb1.isChecked();
            if (press) {

                startStopCode();


                for (int i = 0; i < code.length(); i++) {
                    char aChar = code.charAt(i);
                    if (aChar != ' ') {
                        wave.setWave(frequencyArray[(int) aChar - 65]);
                    } else {
                        wave.setWave(frequencyArray[26]);
                    }
                    wave.start();

                    try {
                        Thread.sleep(150);
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

    private void sendNumber(String number) {


        if (number.length() == 10 && code.length() > 0 && code.length() <= 50) {
            boolean press = tb1.isChecked();
            if (press) {

                startStopCodeNumber();

                for (int i = 0, j = 1; i < number.length(); i++, j++) {
                    freqArray[i] = Integer.parseInt(number.substring(i, j));

                    wave.setWave(frequencyArray[freqArray[i]]);
                    wave.start();

                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    wave.stop();
                    markerCode();
                }

                startStopCode();
                tb1.toggle();

            } else {
                wave.stop();

            }
        }

    }

    private void startStopCodeNumber() {
        wave.setWave(14000);
        wave.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }

    private void markerCode() {
        wave.setWave(23000);
        wave.start();
        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }

    private void startStopCode() {
        wave.setWave(12000);
        wave.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wave.stop();
    }

}
