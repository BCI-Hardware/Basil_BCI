package cz.zcu.kiv.eeg.basil.data.providers.xdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tomas Prokop on 28.01.2019.
 */
public class StreamData<T extends Number> {
    private List<T>[] samples;
    private List<Double> timeStamps;

    public StreamData(int channels) {
        samples = new List[channels];
        for (int i = 0; i < channels; i++) {
            samples[i] = new ArrayList<>();
        }
        timeStamps = new ArrayList<>();
    }

    public List<T>[] getSamples() {
        return samples;
    }

    public List<Double> getTimeStamps() {
        return timeStamps;
    }

    public void addSample(T val, int channel) {
        samples[channel].add(val);
    }

    public void addSamples(T[] vals) {
        if (vals.length < samples.length)
            throw new ArrayIndexOutOfBoundsException("vals lenght is not equal to channel count");

        for (int i = 0; i < vals.length; i++) {
            samples[i].add(vals[i]);
        }
    }

    public void addTimeStamps(Double[] vals) {
        Collections.addAll(timeStamps, vals);
    }
}
