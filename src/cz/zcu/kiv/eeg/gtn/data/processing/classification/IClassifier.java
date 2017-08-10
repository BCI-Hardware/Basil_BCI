package cz.zcu.kiv.eeg.gtn.data.processing.classification;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cz.zcu.kiv.eeg.gtn.data.processing.featureExtraction.FeatureVector;


/**
 * 
 * Interface for supervised classifiers
 * for EEG-based feature vectors
 * 
 * @author Lukas Vareka
 *
 */
public interface IClassifier {

	/**
	 * Train the classifier using information from the supervizor
	 * @param featureVectors list of feature vectors
	 * @param targets target classes - list of expected classes (0 or 1)
	 * @param numberOfiter number of training iterations
	 */
    void train(List<FeatureVector> featureVectors, List<Double> targets, int numberOfiter);
	
	/**
	 * Test the classifier using the data with known resulting classes
	 * @param featureVectors list of feature vectors
	 * @param targets target classes - list of expected classes (0 or 1)
	 * @return
	 */
    ClassificationStatistics test(List<FeatureVector> featureVectors, List<Double> targets);
	
	/**
	 *
	 * Calculate the output of the classifier for the selected epoch
	 * 
	 * @param fv - feature vector
	 * @return  - probability of the epoch to be target; e.g. nontarget - 0, target - 1
	 */
    double classify(FeatureVector fv);
	
	/**
	 * 
	 * Load the classifier from configuration
	 * @param is configuration of the classifier
	 */
    void load(InputStream is);
	
	/**
	 * Save the classifier
	 * @param dest destination stream
	 */
    void save(OutputStream dest);
	
	void save(String file);
	
	void load(String file);
}
