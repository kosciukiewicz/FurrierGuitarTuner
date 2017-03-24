package com.example.witold.wicioguitartuner;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.witold.wicioguitartuner.AudioAnalysis.Complex;
import com.example.witold.wicioguitartuner.AudioAnalysis.FFT;
import com.example.witold.wicioguitartuner.AudioAnalysis.SoundAnalysis;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Witold on 2016-12-01.
 */
public class AudioRecorder {

    RecordAudio playTask;
    MainActivity context;
    int sampleSize;
    int bufferSize;
    int minimalLoudness;
    boolean isRecording = false;

    public AudioRecorder(MainActivity context, int sampleSize, int bufferSize, int minimalLoudness) {
        this.context = context;
        this.sampleSize = sampleSize;
        this.bufferSize = bufferSize;
        this.minimalLoudness = minimalLoudness;
    }

    public void StartRecording() {
        playTask = new RecordAudio();
        playTask.execute();
    }

    public void StopRecording() {
        isRecording = false;
    }

    private class RecordAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            isRecording = true;
            try {
                final double[] sample = new double[sampleSize];
                int sampleIndex = 0;
                boolean loudness = false;

                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, DefaultParameters.RECORDER_SAMPLERATE, DefaultParameters.RECORDER_CHANNELS, DefaultParameters.RECORDER_AUDIO_ENCODING, bufferSize);
                final short[] buffer = new short[bufferSize];
                audioRecord.startRecording();
                while (isRecording) { //record until the sample size is bug enough;

                    final int numberOfReadBytes = audioRecord.read(buffer, 0, bufferSize);
                    float totalAbsValue = 0.0f;

                    for (int i = 0; i < numberOfReadBytes; i +=2) //sprawdza głośność tylko.
                    {
                        totalAbsValue += Math.abs(buffer[i]) / (numberOfReadBytes/2);
                    }

                    if (totalAbsValue > minimalLoudness) //jeżeli głośność jest wystarczająca to dodaje nagrany buffor do próbki którą będziemy analizować.
                    {
                        if (!loudness) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    context.setRecording(true);
                                }
                            });
                            loudness = true;
                        }
                        for (int i = 0; i < bufferSize; i +=1) {
                            sample[sampleIndex] = buffer[i];
                            sampleIndex++;
                        }
                    } else {
                        if (loudness) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    context.setRecording(false);
                                }
                            });
                            loudness=false;
                        }
                    }
                    if(sampleIndex >= sampleSize)
                    {
                        sampleIndex = 0;
                        calculate(sample, sample.length);

                    }
                }
                audioRecord.stop();
                audioRecord.release();

            } catch (Throwable t) {
                t.printStackTrace();
                Log.e("AudioRecord", "Recording Failed");
            }
            return null;
        }

        protected void calculate(final double[] data, int size) //po udanym nagraniu próbki liczy FFT
        {
            final Complex[] complexResult = new Complex[size]; //najpierw zamiana na wartości zespolone
            for(int i= 0; i < size; i++)
            {
                complexResult[i] = new Complex(data[i], 0.0);
            }
            final Complex[] complexResultFromFFT = FFT.fft(SoundAnalysis.hanningWindow(complexResult,complexResult.length));

            EventBus.getDefault().post(new ChartFragment.AmplitubeDataMessage(complexResult));
            EventBus.getDefault().post(new FFTChartFragment.FFTChartDataMessage(complexResultFromFFT));
            EventBus.getDefault().post(new MainActivity.FrequencyDataMessage(getMax(complexResultFromFFT)));
        }

        protected int getMax(Complex[] data) //zwraca kubełek a nie częstotliwość
        {
            int index = 0;
            double max = Math.abs(data[0].re);
            int size = data.length;
            for(int i = 0; i < size/4; i++ )
            {
                if (max < Math.abs(data[i].re))
                {
                    max = Math.abs(data[i].re);
                    index = i;
                }
            }
            return index;
        }
        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Void result) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.setRecording(false);
                }
            });
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Koniec nagrywania", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
