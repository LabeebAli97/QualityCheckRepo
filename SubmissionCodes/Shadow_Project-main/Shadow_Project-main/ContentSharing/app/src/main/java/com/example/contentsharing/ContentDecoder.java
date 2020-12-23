package com.example.contentsharing;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.libfreq.Complex;               // importing jar file complex class
import com.example.libfreq.FFT;                  // importing jar file FFT class


public class ContentDecoder extends Fragment {

    private TextView mFreqDisplay;
    private TextView mNameDisplay;
    private TextView mNumberDisplay;
    private ToggleButton mReceiveButton;
    private Boolean mRecording;
    private Button mSaveContact;
    private Boolean mDecodingName;
    private Boolean mDecodingNumber;
    public static final int SAMPLE_FREQUENCY = 192000;
    public static final int BUFFER_SIZE_IN_BYTES = 8192;
    public static final int MIN_START_NAM_FREQ = 18750;
    public static final int MAX_START_NAM_FREQ = 18850;
    public static final int MAX_START_NUM_FREQ = 18950;
    public static final int DECODE_RANGE = 50;
    public static final int DECODE_RANGE_1 = 100;
    public static final int MARKER_RANGE_HIGH = 23100;
    public static final int MARKER_RANGE_LOW = 22900;
    public static final int END_RANGE_LOW = 22300;
    public static final int END_RANGE_HIGH = 22500;

    private static final int[] mFrequencyArray = new int[]{18000, 18100, 18200, 19000, 19100, 19200, 19300, 19400, 19500, 19600, 19700, 19800, 19900, 20000, 20200, 20400, 20600, 20800, 21000, 21100, 21200, 21300, 21400, 21500, 21600, 21900, 22000, 22100, 22200, 22300, 22400, 22500, 22600, 22700, 18400, 18500, 18600};
                                                       //   A      B      C      D      E     F      G      H      I      J       K      L      M      N      O      P      Q       R     S      T       U      V      W     X      Y     Z      Space

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_decoder, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSaveContact = (Button) view.findViewById(R.id.button);
        mFreqDisplay = (TextView) view.findViewById(R.id.textView);
        mNameDisplay = (TextView) view.findViewById(R.id.textView2);
        mNumberDisplay = (TextView) view.findViewById(R.id.textView3);
        mReceiveButton = (ToggleButton) view.findViewById(R.id.toggleButton2);

        mDecodingName = false;
        mDecodingNumber = false;

        startButton();
        saveContactButton();
    }


    /**
     * Direct to the Contacts application in phone if Name and Number are valid when Save Button is pressed
     */

    private void saveContactButton() {
        mSaveContact.setOnClickListener(v -> {

            if (!(mNameDisplay.getText().toString().equals("Name Not Detected") || mNumberDisplay.getText().toString().equals("Number Not Detected"))) {

                Intent mIntent = new Intent(Intent.ACTION_INSERT);
                mIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                mIntent.putExtra(ContactsContract.Intents.Insert.NAME, mNameDisplay.getText().toString());
                mIntent.putExtra(ContactsContract.Intents.Insert.PHONE, mNumberDisplay.getText().toString());

                startActivity(mIntent);
            } else {
                Toast.makeText(getContext(), "Invalid Contact", Toast.LENGTH_SHORT).show();
            }

        });
    }


    /**
     * Initializes the startRecord function when startButton is pressed
     */

    private void startButton() {

        mReceiveButton.setOnClickListener(v -> {
            boolean mPress = mReceiveButton.isChecked();
            if (mPress) {

                Thread recordThread = new Thread(() -> {
                    mRecording = true;
                    startRecord();
                });
                recordThread.start();

            } else {
                mRecording = false;
            }
        });
    }


    /**
     * Takes the audio frequency as input and decodes that input
     */

    private void startRecord() {

        try {
            int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_FREQUENCY, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);  // Contains Estimated Minimum buffer size required for an AudioTrack
            AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_FREQUENCY, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, MIN_BUFFER_SIZE);
            short[] mAudioData = new short[MIN_BUFFER_SIZE];
            mAudioRecord.startRecording();
            StringBuilder mDecodeResultName = new StringBuilder();
            StringBuilder mDecodeResultNumber = new StringBuilder();

            while (mRecording) {

                mAudioRecord.read(mAudioData, 0, MIN_BUFFER_SIZE);
                double mFreq = findFreq(mAudioData);      // calls the findFreq function to get the frequency
                mFreqDisplay.setText(String.valueOf(mFreq));      // Displays the live frequency

                if (mFreq > MIN_START_NAM_FREQ && mFreq < MAX_START_NAM_FREQ) {
                    mDecodingName = true;                                // If Name start frequency is detected
                    mDecodingNumber = false;
                }

                if (mFreq > MAX_START_NAM_FREQ && mFreq < MAX_START_NUM_FREQ) {
                    mDecodingNumber = true;                              // If Number start frequency is detected
                    mDecodingName = false;
                }

                if (mDecodingName) {

                    if (mFreq > mFrequencyArray[1] - DECODE_RANGE && mFreq < mFrequencyArray[1] + DECODE_RANGE) {
                        mDecodeResultName.append("B");
                    } else if (mFreq > mFrequencyArray[2] - DECODE_RANGE && mFreq < mFrequencyArray[2] + DECODE_RANGE) {
                        mDecodeResultName.append("C");
                    } else if (mFreq > mFrequencyArray[3] - DECODE_RANGE && mFreq < mFrequencyArray[3] + DECODE_RANGE) {
                        mDecodeResultName.append("D");
                    } else if (mFreq > mFrequencyArray[4] - DECODE_RANGE && mFreq < mFrequencyArray[4] + DECODE_RANGE) {
                        mDecodeResultName.append("E");
                    } else if (mFreq > mFrequencyArray[5] - DECODE_RANGE && mFreq < mFrequencyArray[5] + DECODE_RANGE) {
                        mDecodeResultName.append("F");
                    } else if (mFreq > mFrequencyArray[6] - DECODE_RANGE && mFreq < mFrequencyArray[6] + DECODE_RANGE) {
                        mDecodeResultName.append("G");
                    } else if (mFreq > mFrequencyArray[7] - DECODE_RANGE && mFreq < mFrequencyArray[7] + DECODE_RANGE) {
                        mDecodeResultName.append("H");
                    } else if (mFreq > mFrequencyArray[8] - DECODE_RANGE && mFreq < mFrequencyArray[8] + DECODE_RANGE) {
                        mDecodeResultName.append("I");
                    } else if (mFreq > mFrequencyArray[9] - DECODE_RANGE && mFreq < mFrequencyArray[9] + DECODE_RANGE) {
                        mDecodeResultName.append("J");
                    } else if (mFreq > mFrequencyArray[0] - DECODE_RANGE && mFreq < mFrequencyArray[0] + DECODE_RANGE) {
                        mDecodeResultName.append("A");
                    } else if (mFreq > mFrequencyArray[10] - DECODE_RANGE && mFreq < mFrequencyArray[10] + DECODE_RANGE) {
                        mDecodeResultName.append("K");
                    } else if (mFreq > mFrequencyArray[11] - DECODE_RANGE && mFreq < mFrequencyArray[11] + DECODE_RANGE) {
                        mDecodeResultName.append("L");
                    } else if (mFreq > mFrequencyArray[12] - DECODE_RANGE && mFreq < mFrequencyArray[12] + DECODE_RANGE) {
                        mDecodeResultName.append("M");
                    } else if (mFreq > mFrequencyArray[13] - DECODE_RANGE && mFreq < mFrequencyArray[13] + DECODE_RANGE) {
                        mDecodeResultName.append("N");
                    } else if (mFreq > mFrequencyArray[14] - DECODE_RANGE_1 && mFreq < mFrequencyArray[14] + DECODE_RANGE_1) {
                        mDecodeResultName.append("O");
                    } else if (mFreq > mFrequencyArray[15] - DECODE_RANGE_1 && mFreq < mFrequencyArray[15] + DECODE_RANGE_1) {
                        mDecodeResultName.append("P");
                    } else if (mFreq > mFrequencyArray[16] - DECODE_RANGE_1 && mFreq < mFrequencyArray[16] + DECODE_RANGE_1) {
                        mDecodeResultName.append("Q");
                    } else if (mFreq > mFrequencyArray[17] - DECODE_RANGE_1 && mFreq < mFrequencyArray[17] + DECODE_RANGE_1) {
                        mDecodeResultName.append("R");
                    } else if (mFreq > mFrequencyArray[18] - DECODE_RANGE && mFreq < mFrequencyArray[18] + DECODE_RANGE) {
                        mDecodeResultName.append("S");
                    } else if (mFreq > mFrequencyArray[19] - DECODE_RANGE && mFreq < mFrequencyArray[19] + DECODE_RANGE) {
                        mDecodeResultName.append("T");
                    } else if (mFreq > mFrequencyArray[20] - DECODE_RANGE && mFreq < mFrequencyArray[20] + DECODE_RANGE) {
                        mDecodeResultName.append("U");
                    } else if (mFreq > mFrequencyArray[21] - DECODE_RANGE && mFreq < mFrequencyArray[21] + DECODE_RANGE) {
                        mDecodeResultName.append("V");
                    } else if (mFreq > mFrequencyArray[22] - DECODE_RANGE && mFreq < mFrequencyArray[22] + DECODE_RANGE) {
                        mDecodeResultName.append("W");
                    } else if (mFreq > mFrequencyArray[23] - DECODE_RANGE && mFreq < mFrequencyArray[23] + DECODE_RANGE) {
                        mDecodeResultName.append("X");
                    } else if (mFreq > mFrequencyArray[24] - DECODE_RANGE && mFreq < mFrequencyArray[24] + DECODE_RANGE) {
                        mDecodeResultName.append("Y");
                    } else if (mFreq > mFrequencyArray[25] - DECODE_RANGE && mFreq < mFrequencyArray[25] + DECODE_RANGE) {
                        mDecodeResultName.append("Z");
                    } else if (mFreq > mFrequencyArray[26] - DECODE_RANGE && mFreq < mFrequencyArray[26] + DECODE_RANGE) {
                        mDecodeResultName.append(" ");
                    } else if (mFreq > MARKER_RANGE_LOW && mFreq < MARKER_RANGE_HIGH) {        //Marker frequency
                        mDecodeResultName.append("*");                 // * is used to represent the marker frequency
                    }
                }


                if (mDecodingNumber) {
                    if (mFreq > mFrequencyArray[1] - DECODE_RANGE && mFreq < mFrequencyArray[1] + DECODE_RANGE) {
                        mDecodeResultNumber.append("1");
                    } else if (mFreq > mFrequencyArray[2] - DECODE_RANGE && mFreq < mFrequencyArray[2] + DECODE_RANGE) {
                        mDecodeResultNumber.append("2");
                    } else if (mFreq > mFrequencyArray[3] - DECODE_RANGE && mFreq < mFrequencyArray[3] + DECODE_RANGE) {
                        mDecodeResultNumber.append("3");
                    } else if (mFreq > mFrequencyArray[4] - DECODE_RANGE && mFreq < mFrequencyArray[4] + DECODE_RANGE) {
                        mDecodeResultNumber.append("4");
                    } else if (mFreq > mFrequencyArray[5] - DECODE_RANGE && mFreq < mFrequencyArray[5] + DECODE_RANGE) {
                        mDecodeResultNumber.append("5");
                    } else if (mFreq > mFrequencyArray[6] - DECODE_RANGE && mFreq < mFrequencyArray[6] + DECODE_RANGE) {
                        mDecodeResultNumber.append("6");
                    } else if (mFreq > mFrequencyArray[7] - DECODE_RANGE && mFreq < mFrequencyArray[7] + DECODE_RANGE) {
                        mDecodeResultNumber.append("7");
                    } else if (mFreq > mFrequencyArray[8] - DECODE_RANGE && mFreq < mFrequencyArray[8] + DECODE_RANGE) {
                        mDecodeResultNumber.append("8");
                    } else if (mFreq > mFrequencyArray[9] - DECODE_RANGE && mFreq < mFrequencyArray[9] + DECODE_RANGE) {
                        mDecodeResultNumber.append("9");
                    } else if (mFreq > mFrequencyArray[0] - DECODE_RANGE && mFreq < mFrequencyArray[0] + DECODE_RANGE) {
                        mDecodeResultNumber.append("0");
                    } else if (mFreq > MARKER_RANGE_LOW && mFreq < MARKER_RANGE_HIGH) {         // Marker frequency
                        mDecodeResultNumber.append("*");                // * is used to represent the marker frequency
                    } else if (mFreq > END_RANGE_LOW && mFreq < END_RANGE_HIGH) {
                        mRecording = false;
                        mReceiveButton.toggle();
                    }
                }
            }

            decodeCodeName(mDecodeResultName);
            decodeCodeNumber(mDecodeResultNumber);

            mAudioRecord.stop();

            mDecodingName = false;
            mDecodingNumber = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Checks Number is detected or not
     */

    private void decodeCodeNumber(StringBuilder decodeResultNumber) {
        if (decodeResultNumber.length() != 0) {
            String mNumberCode;
            String[] mFreqDecodeArray = new String[decodeResultNumber.length()];
            StringBuilder mDecode = new StringBuilder();

            for (int i = 0; i < decodeResultNumber.length(); i++) {
                mFreqDecodeArray[i] = decodeResultNumber.substring(i, i + 1);
            }

            mNumberCode = mFreqDecodeArray[0];
            mDecode.append(mNumberCode);

            for (int i = 1; i < mFreqDecodeArray.length; i++) {

                if (!mFreqDecodeArray[i].equals(mNumberCode)) {
                    mNumberCode = mFreqDecodeArray[i];
                    if (!mFreqDecodeArray[i].equals("*")) {
                        mDecode.append(mFreqDecodeArray[i]);
                    }
                }
            }

            mNumberDisplay.setText(" " + mDecode);

        } else {
            mNumberDisplay.setText("Number Not Detected");
        }
    }


    /**
     * Checks Name is detected or not
     */

    private void decodeCodeName(StringBuilder decodeResult) {
        if (decodeResult.length() != 0) {
            String mNameCode;
            String[] mFreqDecodeArray = new String[decodeResult.length()];
            StringBuilder mDecode = new StringBuilder();

            for (int i = 0; i < decodeResult.length(); i++) {
                mFreqDecodeArray[i] = decodeResult.substring(i, i + 1);
            }

            mNameCode = mFreqDecodeArray[0];
            mDecode.append(mNameCode);
            for (int i = 1; i < mFreqDecodeArray.length; i++) {

                if (!mFreqDecodeArray[i].equals(mNameCode)) {
                    mNameCode = mFreqDecodeArray[i];

                    if (!mFreqDecodeArray[i].equals("*")) {
                        if (i > 3 && (mFreqDecodeArray[i - 3].equals(" ") || mFreqDecodeArray[i - 2].equals(" "))) {     // Checking the space before the character
                            mDecode.append(mFreqDecodeArray[i].toUpperCase());         //If space is present before the character then converting that character to capital letter
                        } else {
                            mDecode.append(mFreqDecodeArray[i].toLowerCase());
                        }
                    }
                }
            }

            mNameDisplay.setText(" " + mDecode);

        } else {
            mNameDisplay.setText("Name Not Detected");
        }
    }


    /**
     * Finds the frequency using libFreq.jar file and returns the frequency
     */

    private double findFreq(short[] audioData) {

        Complex[] mFftTempArray = new Complex[BUFFER_SIZE_IN_BYTES];
        for (int j = 0, k = 1000; j < BUFFER_SIZE_IN_BYTES; j++, k++) {
            mFftTempArray[j] = new Complex(audioData[k], 0);
        }
        Complex[] mFftArray = FFT.fft(mFftTempArray);

        double[] mMagnitude = new double[BUFFER_SIZE_IN_BYTES / 2];

        //  calculate power spectrum (magnitude) values from fft[]
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

        // here will get frequency in hz like(17000,18000..etc)
        return (float) SAMPLE_FREQUENCY * (float) mMaxIndex / (float) BUFFER_SIZE_IN_BYTES;
    }
}
