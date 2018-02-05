package cz.zcu.kiv.eeg.basil.data.processing.math;

/**
 * Class for removal of artifacts in EEG signal using the amplitude method. This
 * method searches the EEG signal for value greater than set threshold. If
 * signal exceeds set threshold, it will be removed from the signal.
 *
 * @author Jan Vampol
 * @version 1.01
 */
public class AmplitudeArtifactDet implements IArtifactDetection {

    public static final double DEFAULT_THRESHOLD = 75;

    /**
     * Maximum value allowed in the signal
     */
    public double threshold;

    /**
     * Parameterized constructor of this class. Sets a threshold to value of
     * given parameter.
     *
     * @param maxDiff Given value of threshold
     */
    public AmplitudeArtifactDet(double maxDiff) {
        threshold = maxDiff;
    }

    /**
     * Creates an instance of this class with threshold set to
     */
    public AmplitudeArtifactDet() {
        this(DEFAULT_THRESHOLD);
    }

    /**
     * Checks all the values of given instance of EpochMessenger. Epoch data is
     * in 2D array of doubles. If any of values exceeds threshold, whole epoch
     * is removed from the signal.
     *
     * @param epoch Epoch tested for artifacts
     * @return null if given epocj contains artefact, epoch otherwise.
     */
/*    @Override
    public EpochMessenger detectArtifact(EpochMessenger epoch) {
        double[][] values = epoch.getEpoch();

        for (double[] value : values) {
            for (int j = 0; j < value.length; j++) {
                if (Math.abs(value[j]) > threshold) {
                    return null;
                }
            }
        }
        return epoch;
    }*/

    /**
     * Returns the threshold.
     *
     * @return threshold The threshold (maximal amplitude).
     */
    public double getThreshold() {
        return this.threshold;
    }

    /**
     * Sets the threshold (maximal amplitude). If the given parameter is lower
     * than 0, the threshold is set to default value of
     * {@link #DEFAULT_THRESHOLD}.
     *
     * @param threshold Maximal amplitude.
     */
    public void setThreshold(double threshold) {
        if (threshold < 0) {
            this.threshold = DEFAULT_THRESHOLD;
            return;
        }
        this.threshold = threshold;
    }
}
