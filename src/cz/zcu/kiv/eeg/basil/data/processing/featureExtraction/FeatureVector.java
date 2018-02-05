package cz.zcu.kiv.eeg.basil.data.processing.featureExtraction;

import java.util.ArrayList;
import java.util.Arrays;

import cz.zcu.kiv.eeg.basil.data.processing.math.SignalProcessing;

/**
 * Represents a feature vector as 
 * an input for classification
 * 
 * Created by Tomas Prokop on 07.08.2017.
 */
public class FeatureVector {
    private double[] featureVector;
    private double expectedOutput;
    
    public FeatureVector() {
    	this.featureVector = null;
    	this.expectedOutput = 0;
    }
    
    public FeatureVector(double[] featureVector) {
    	this();
		this.featureVector = featureVector;
	}
    
	public FeatureVector(double[] featureVector, double expectedOutput) {
		this.featureVector = featureVector;
		this.expectedOutput = expectedOutput;
	}

	/**
     * Join two feature vectors
     * 
     * @param features feature vector
     */
    public void addFeatures(double[] features) {
        if (featureVector == null)
            featureVector = features;
        else {
            double[] copy = new double[featureVector.length + features.length];
            System.arraycopy(featureVector, 0, copy, 0, featureVector.length);
            System.arraycopy(features, 0, copy, featureVector.length, features.length);
            featureVector = copy;
        }
    }

    public double[] getFeatureVector() {
        return featureVector;
    }

    public void normalize() {
        this.featureVector = SignalProcessing.normalize(featureVector);
    }

    public int size(){
        return featureVector == null ? 0 : featureVector.length;
    }
    
    public double getExpectedOutput() {
    	return expectedOutput;
    }
    
    public void setExpectedOutput(double expectedOutput) {
    	this.expectedOutput = expectedOutput;
    }
    
    

}