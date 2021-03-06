package com.example.witold.wicioguitartuner.AudioUtils;

import com.example.witold.wicioguitartuner.AudioUtils.AudioRecorder.DefaultParameters;

/**
 * Created by Witold on 2016-12-04.
 */
public class SingleFrequency {
    double freqValue;
    int bucket;
    String note;

    public SingleFrequency (double freqValue, String note)
    {
        this.freqValue = freqValue;
        this.note = note;
        this.bucket = countBucket();
    }

    public int countBucket()
    {
        return (int)(DefaultParameters.SAMPLE_SIZE*freqValue/DefaultParameters.RECORDER_SAMPLERATE);
    }

    public int getBucket(){
        return bucket;
    }

    public double getFreqValue()
    {
        return freqValue;
    }

    public String getNote()
    {
        return note;
    }

    public String toString()
    {
        return note + ": " + freqValue + ": " + bucket;
    }
}
