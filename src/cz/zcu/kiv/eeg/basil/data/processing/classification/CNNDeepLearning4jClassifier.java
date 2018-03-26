package cz.zcu.kiv.eeg.basil.data.processing.classification;

import cz.zcu.kiv.eeg.basil.data.processing.featureExtraction.FeatureVector;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomas Prokop on 12.03.2018.
 */
public class CNNDeepLearning4jClassifier extends DeepLearning4jClassifier{

    @Override
    public void train(List<FeatureVector> featureVectors, List<Double> targets, int numberOfiter) {

        // Customizing params of classifier
        final int numRows = featureVectors.get(0).getFeatureVector().length;   // number of targets on a line
        final int numColumns = 2;   // number of labels needed for classifying
        //this.iterations = numberOfiter; // number of iteration in the learning phase
        int listenerFreq = numberOfiter / 500; // frequency of output strings
        int seed = 123; //  seed - one of parameters. For more info check http://deeplearning4j.org/iris-flower-dataset-tutorial

        //Load Data - when target is 0, label[0] is 0 and label[1] is 1.
        double[][] labels = new double[featureVectors.size()][numColumns]; // Matrix of labels for classifier
        double[][] features_matrix = new double[featureVectors.size()][numRows]; // Matrix of features
        for (int i = 0; i < featureVectors.size(); i++) { // Iterating through epochs
            double[] features = featureVectors.get(i).getFeatureVector(); // Feature of each epoch
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
        SplitTestAndTrain tat = dataSet.splitTestAndTrain(0.8);
        Nd4j.ENFORCE_NUMERICAL_STABILITY = true; // Setting to enforce numerical stability

        // Building a neural net
        build(numRows, numColumns, seed, listenerFreq);

        System.out.println("Train model....");
        model.fit(tat.getTrain()); // Learning of neural net with training data
        model.finetune();

        Evaluation eval = new Evaluation(numColumns);
        eval.eval(dataSet.getLabels(), model.output(dataSet.getFeatureMatrix(), Layer.TrainingMode.TEST));
        System.out.println(eval.stats());
    }

    private void build(int numRows, int outputNum, int seed, int listenerFreq) {
        System.out.print("Build model....SDA");
        //MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
/*                //.seed(seed)
                .iterations(1500)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.005)
                .updater(Updater.NESTEROVS).momentum(0.9)
                //.regularization(true).dropOut(0.99)
                // .regularization(true).l2(1e-4)
                .list()
                .layer(1, new ConvolutionLayer.Builder(new int []{4, 4}, new int[]{1,1})
                        .nIn(numRows).nOut(20)

                .pretrain(false).backprop(true).build();*/
        //model = new MultiLayerNetwork(conf); // Passing built configuration to instance of multilayer network
        //model.init(); // Initialize mode


        //UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        ArrayList listenery = new ArrayList();
        listenery.add(new ScoreIterationListener(500));
        listenery.add(new StatsListener(statsStorage));
        model.setListeners(listenery);
    }

    @Override
    public double classify(FeatureVector fv) {
        double[] featureVector = fv.getFeatureVector(); // Extracting features to vector
        INDArray features = Nd4j.create(featureVector); // Creating INDArray with extracted features
        return model.output(features, Layer.TrainingMode.TEST).getDouble(0); // Result of classifying
    }
}
