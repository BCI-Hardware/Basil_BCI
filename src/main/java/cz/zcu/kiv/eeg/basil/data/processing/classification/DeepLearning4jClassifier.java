package cz.zcu.kiv.eeg.basil.data.processing.classification;

import org.datavec.api.util.ndarray.RecordConverter;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.DataSets;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import cz.zcu.kiv.eeg.basil.data.processing.featureExtraction.FeatureVector;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.DataSetUtils;
import org.neuroph.core.data.DataSetRow;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tomas Prokop on 15.01.2018.
 */
public abstract class DeepLearning4jClassifier implements IClassifier {

    protected MultiLayerNetwork model;            //multi layer neural network with a logistic output layer and multiple hidden neuralNets
    protected int iterations;                    //Iterations used to classify

    /*Parametric constructor */
    public DeepLearning4jClassifier() {
    }

    // method for testing the classifier.
    @Override
    public ClassificationStatistics test(List<FeatureVector> featureVectors, List<Double> targets) {
        ClassificationStatistics resultsStats = new ClassificationStatistics(); // initialization of classifier statistics
        for (int i = 0; i < featureVectors.size(); i++) {   //iterating epochs
            double output = this.classify(featureVectors.get(i));   //   output means score of a classifier from method classify
            resultsStats.add(output, targets.get(i));   // calculating statistics
        }
        return resultsStats;    //  returns classifier statistics
    }

    protected DataSet createDataSet(List<FeatureVector> featureVectors) {

        // Customizing params of classifier
        final int numRows = featureVectors.get(0).size();   // number of targets on a line
        final int numColumns = 2;   // number of labels needed for classifying

        double[][] labels = new double[featureVectors.size()][numColumns]; // Matrix of labels for classifier
        double[][] features_matrix = new double[featureVectors.size()][numRows]; // Matrix of features
        for (int i = 0; i < featureVectors.size(); i++) { // Iterating through epochs
            double[] features = featureVectors.get(i).getFeatureArray(); // Feature of each epoch
            for (int j = 0; j < numColumns; j++) {   //setting labels for each column
                labels[i][0] = featureVectors.get(i).getExpectedOutput(); // Setting label on position 0 as target
                labels[i][1] = Math.abs(1 - labels[i][0]);  // Setting label on position 1 to be different from label[0]
            }
            features_matrix[i] = features; // Saving features to features matrix
        }

        // Creating INDArrays and DataSet
        INDArray output_data = Nd4j.create(labels); // Create INDArray with labels(targets)
        INDArray input_data = Nd4j.create(features_matrix); // Create INDArray with features(data)
        DataSet dataSet = new DataSet(input_data, output_data); // Create dataSet with features and labels

        return dataSet;
    }

    protected List<DataSet> createDataSet2(List<FeatureVector> featureVectors) {

        try {
            double[][] m = featureVectors.get(0).getFeatureMatrix();
            int[] shape = {m[0].length, 1, m.length };
            //DataSet 3d = Nd4j.create(shape);
            DataSet ds = new DataSet();

            List<DataSet> lst = new ArrayList<>(featureVectors.size());
            int i = 1;
            for (FeatureVector fv : featureVectors) {
                DataSet d;
                INDArray sh = Nd4j.create(shape);

                INDArray matrix = Nd4j.create(fv.getFeatureMatrix());
                matrix = matrix.reshape(shape);
                int[] ss = matrix.shape();
                double[] l = {fv.getExpectedOutput(),Math.abs(1 - fv.getExpectedOutput())};
                INDArray label = Nd4j.create(l);
                d = new DataSet(matrix, label);
                //int dimen = d.getFeatures().size(2);
                lst.add(d);
                //ds.addFeatureVector(matrix, (int) fv.getExpectedOutput());
/*                if(ds.getFeatures() != null) {
                    //ds.addRow(d, i);

                    ds.addFeatureVector(matrix, (int) fv.getExpectedOutput());
                }
                else {
                 ds.setFeatures(matrix);
                 ds.setLabels(label);
                }*/
            }

            ds = DataSet.merge(lst);
            //lst = ds.asList();

            DataSet first = lst.get(0);
            int[] shape1 = first.getFeatureMatrix().shape();

            return lst;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected DataSet createDataSet3(List<FeatureVector> featureVectors) {
        try {
            double[][] m = featureVectors.get(0).getFeatureMatrix();
            int[] shape = {1, 1, m.length, m[0].length };

            List<DataSet> lst = new ArrayList<>(featureVectors.size());
            DataSet d;
            for (FeatureVector fv : featureVectors) {
                double[] l = {fv.getExpectedOutput(),Math.abs(1 - fv.getExpectedOutput())};
                INDArray label = Nd4j.create(l);
                d = new DataSet(fv.getShapedFeatureVector(shape), label);
                lst.add(d);
            }

            return DataSet.merge(lst);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected DataSet createDataSet4(List<FeatureVector> featureVectors) {

        try {
            FeatureVector first = featureVectors.get(0);
            int[] orig = first.shape();
            int[] shape = new int[4];

            Arrays.fill(shape,1);
            int offset = shape.length - orig.length;
            for (int i = orig.length -1; i >= 0; i--)
                shape[offset + i] = orig[i];

            int[] t = {1,1,3,1536};

            List<DataSet> lst = new ArrayList<>(featureVectors.size());
            DataSet d;
            for (FeatureVector fv : featureVectors) {
                double[] l = {fv.getExpectedOutput(),Math.abs(1 - fv.getExpectedOutput())};
                INDArray label = Nd4j.create(l);
                d = new DataSet(fv.getShapedFeatureVector(t), label);
                lst.add(d);
            }

            return DataSet.merge(lst);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // method not implemented. For loading use load(String file)
    @Override
    public void load(InputStream is) {
        throw new NotImplementedException();
    }

    // method not implemented. For saving use method save(String file)
    @Override
    public void save(OutputStream dest) {
        throw new NotImplementedException();
    }

    /**
     * Save Model to zip file
     * using save methods from library deeplearning4j
     *
     * @param pathname path name and file name. File name should end with .zip extension.
     */
    public void save(String pathname) {
        File locationToSave = new File(pathname);      //Where to save the network. Note: the file is in .zip format - can be opened externally
        boolean saveUpdater = true;   //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        try {
            ModelSerializer.writeModel(model, locationToSave, saveUpdater);
            System.out.println("Saved network params " + model.params());
            System.out.println("Saved");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads Model from file.
     * It uses load methods from library deepalerning4j
     *
     * @param pathname pathname and file name of loaded Model
     */
    public void load(String pathname) {
        File locationToLoad = new File(pathname);
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(locationToLoad);
            System.out.println("Loaded");
            System.out.println("Loaded network params " + model.params());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
