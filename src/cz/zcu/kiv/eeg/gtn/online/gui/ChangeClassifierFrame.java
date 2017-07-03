package cz.zcu.kiv.eeg.gtn.online.gui;

//SAE must be imported
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cz.zcu.kiv.eeg.gtn.application.classification.*;
import cz.zcu.kiv.eeg.gtn.application.classification.DBNDeepLearning4j;
import cz.zcu.kiv.eeg.gtn.application.classification.test.TrainUsingOfflineProvider;
import cz.zcu.kiv.eeg.gtn.application.featureextraction.IFeatureExtraction;

/**
 * Window for changing classifier and its parameters
 * 
 * @author Jaroslav Klaus
 *
 */
@SuppressWarnings("serial")
public class ChangeClassifierFrame extends JFrame {

	/**
	 * Reference to mainFrame
	 */
	private MainFrame mainFrame;

	/**
	 * Feature Extraction method
	 */
	private IFeatureExtraction fe;

	/**
	 * Parameters for Feature Extraction method
	 */
	private List<String> feParams;

	/**
	 * Spinner for MLP's middle neurons
	 */
	private JSpinner middleNeuronsSpinner;

	/**
	 * Spinner for KNN's number of neighbors
	 */
	private JSpinner neighborsNumberSpinner;

	/**
	 * Spinner for SVM's cost
	 */
	private JSpinner svmCost;
	
	/**
	 * Spinner for DBN's iterations
	 */
	private JSpinner dbnNeuron;
	/**
	 * Spinner for SDA's iterations
	 */
	private JSpinner sdaNeuron;

	/**
	 * Radio button for selecting MLP classifier
	 */
	private JRadioButton mlpBttn;

	/**
	 * Radio button for selecting KNN classifier
	 */
	private JRadioButton knnBttn;

	/**
	 * Radio button for selecting LDA classifier
	 */
	private JRadioButton ldaBttn;

	/**
	 * Radio button for selecting SVM classifier
	 */
	private JRadioButton svmBttn;

	/**
	 * Radio button for selecting Correlation classifier
	 */
	private JRadioButton correlationBttn;
	
	/**
	 * Radio button for selecting DBN classifier
	 */
	private JRadioButton dbnBttn;
	
	/**
	 * Radio button for selecting SDA classifier
	 */
	private JRadioButton saeBttn;

	/**
	 * Constructor for creating this window and creating its variables
	 * 
	 * @param mainFrame
	 *            - reference to mainFrame
	 * @param fe
	 *            - Feature Extraction method
	 * @param feParams
	 *            - parameters for Feature Extraction method
	 */
	public ChangeClassifierFrame(MainFrame mainFrame, IFeatureExtraction fe,
			List<String> feParams) {
		super("Choose Classifier and its Parameters");
		this.mainFrame = mainFrame;
		this.fe = fe;
		this.feParams = feParams;
		this.getContentPane().add(createClassifierFrame());
		this.setVisible(false);
		this.pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(900, 600);
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setEscListener(this);
	}

	/**
	 * Sets listener to Esc key. After pressing Esc, this window closes and
	 * nothing is changed
	 * 
	 * @param frame
	 *            - this frame
	 */
	private void setEscListener(final ChangeClassifierFrame frame) {
		ActionListener escListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};

		this.getRootPane().registerKeyboardAction(escListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Creates the main layout and panel
	 * 
	 * @return main panel
	 */
	private JPanel createClassifierFrame() {
		GridLayout mainLayout = new GridLayout(1, 2);
		JPanel contentJP = new JPanel(mainLayout);
		contentJP.add(createRadioBttns());
		contentJP.add(createParameters());

		return contentJP;
	}

	/**
	 * Creates panel with radio buttons for selecting classifier and its actions
	 * when selected
	 * 
	 * @return panel with radio buttons or selecting classifier
	 */
	private JPanel createRadioBttns() {
			
		mlpBttn = new JRadioButton("MLP");
		mlpBttn.setSelected(false);
		mlpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(true);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
				dbnNeuron.setEnabled(false);
				sdaNeuron.setEnabled(false);
			}
		});

		knnBttn = new JRadioButton("K Nearest Neighbors");
		knnBttn.setSelected(false);
		knnBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(true);
				svmCost.setEnabled(false);
				dbnNeuron.setEnabled(false);
				sdaNeuron.setEnabled(false);
			}
		});

		ldaBttn = new JRadioButton("Linear Discriminant Analysis");
		ldaBttn.setSelected(false);
		ldaBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
				dbnNeuron.setEnabled(false);
				sdaNeuron.setEnabled(false);
			}
		});

		svmBttn = new JRadioButton("Support Vector Machines");
		svmBttn.setSelected(false);
		svmBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(true);
				dbnNeuron.setEnabled(false);
				sdaNeuron.setEnabled(false);
			}
		});

		correlationBttn = new JRadioButton("Correlation");
		correlationBttn.setSelected(false);
		correlationBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
				dbnNeuron.setEnabled(false);
				sdaNeuron.setEnabled(false);
			}
		});
		dbnBttn = new JRadioButton("Deep Belief Network");
		dbnBttn.setSelected(false);
		dbnBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
				dbnNeuron.setEnabled(true);
				sdaNeuron.setEnabled(false);
			}
		});
		
		
		saeBttn = new JRadioButton("Stacked Auto Encoder");
		saeBttn.setSelected(false);
		saeBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
				dbnNeuron.setEnabled(false);
				sdaNeuron.setEnabled(true);
			}
		});		

		ButtonGroup group = new ButtonGroup();
		group.add(mlpBttn);
		group.add(knnBttn);
		group.add(ldaBttn);
		group.add(svmBttn);
		group.add(correlationBttn);
		group.add(dbnBttn);
		group.add(saeBttn);
		

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Classifier"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(mlpBttn);
		pane.add(knnBttn);
		pane.add(ldaBttn);
		pane.add(svmBttn);
		pane.add(correlationBttn);
		pane.add(dbnBttn);
		pane.add(saeBttn);
		return pane;
	}

	/**
	 * Create panel with parameters for classifiers
	 * 
	 * @return panel with parameters for classifiers
	 */
	private JPanel createParameters() {
		// MLP
		JPanel mlpPane = createMlpPane();

		// KNN
		JPanel knnPane = createKnnPane();

		// LDA
		JPanel ldaPane = createLdaPane();

		// SVM
		JPanel svmPane = createSvmPane();

		// Correlation
		JPanel correlationPane = createCorrelationPane();
		
		//DBN 
		JPanel dbnPane = createDBNPane();
		
		//SAE
		JPanel saePane = createSAEPane();
		
		
		// Buttons
		JPanel bttnPane = createBttnPane();

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Parameters"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(mlpPane);
		pane.add(knnPane);
		pane.add(ldaPane);
		pane.add(svmPane);
		pane.add(correlationPane);	
		pane.add(dbnPane);
		pane.add(saePane);		
		pane.add(bttnPane);

		return pane;
	}

	/**
	 * Creates panel with parameters for MLP
	 * 
	 * @return panel with parameters for MLP
	 */
	private JPanel createMlpPane() {
		JPanel mlpPane = new JPanel();
		mlpPane.setBorder(BorderFactory.createTitledBorder("MLP"));
		mlpPane.setLayout(new GridLayout(0, 2));

		JLabel middleNeuronsLabel = new JLabel("Number of Middle Neurons");
		mlpPane.add(middleNeuronsLabel);

		SpinnerNumberModel middleNeuronsSnm = new SpinnerNumberModel(8, 1, 750,
				1);
		middleNeuronsSpinner = new JSpinner(middleNeuronsSnm);
		middleNeuronsSpinner.setEnabled(false);
		middleNeuronsSpinner.setToolTipText("Select number of neurons for middle layer of neural network.");
		mlpPane.add(middleNeuronsSpinner);

		return mlpPane;
	}

	/**
	 * Creates panel with parameters for KNN
	 * 
	 * @return panel with parameters for KNN
	 */
	private JPanel createKnnPane() {
		JPanel knnPane = new JPanel();
		knnPane.setBorder(BorderFactory
				.createTitledBorder("K Nearest Neighbors"));
		knnPane.setLayout(new GridLayout(0, 2));

		JLabel neighborsNumberLabel = new JLabel("Number of Neighbors");
		knnPane.add(neighborsNumberLabel);

		SpinnerNumberModel neighborsNumberSnm = new SpinnerNumberModel(1, 1,
				750, 1);
		neighborsNumberSpinner = new JSpinner(neighborsNumberSnm);
		neighborsNumberSpinner.setEnabled(false);
		neighborsNumberSpinner.setToolTipText("Select number of nearest neighbors that will be used for classification.");
		knnPane.add(neighborsNumberSpinner);

		return knnPane;
	}

	/**
	 * Creates panel with parameters for LDA
	 * 
	 * @return panel with parameters for LDA
	 */
	private JPanel createLdaPane() {
		JPanel ldaPane = new JPanel();
		ldaPane.setBorder(BorderFactory
				.createTitledBorder("Linear Discriminant Analysis"));

		ldaPane.add(new JLabel("No parameters for this classifier"));

		return ldaPane;
	}

	/**
	 * Creates panel with parameters for SVM
	 * 
	 * @return panel with parameters for SVM
	 */
	private JPanel createSvmPane() {
		JPanel svmPane = new JPanel();
		svmPane.setBorder(BorderFactory
				.createTitledBorder("Support Vector Machines"));
		svmPane.setLayout(new GridLayout(0, 2));

		// Gamma
		JLabel svmCostLabel = new JLabel("Cost");
		svmPane.add(svmCostLabel);

		SpinnerNumberModel svmCostSnm = new SpinnerNumberModel(0, 0, 15000,
				0.001);
		svmCost = new JSpinner(svmCostSnm);
		svmCost.setEnabled(false);
		svmCost.setToolTipText("Select cost for SVM classification.");
		svmPane.add(svmCost);

		return svmPane;
	}

	/**
	 * Creates panel with parameters for Correlation
	 * 
	 * @return panel with parameters for Correlation
	 */
	private JPanel createCorrelationPane() {
		JPanel correlationPane = new JPanel();
		correlationPane.setBorder(BorderFactory
				.createTitledBorder("Correlation"));

		return correlationPane;
	}
	/**
	 * Creates panel with parameters for DBN
	 * 
	 * @return panel with parameters for DBN
	 */
	private JPanel createDBNPane() {
		JPanel DBNPane = new JPanel();
		DBNPane.setBorder(BorderFactory
				.createTitledBorder("Deep Belief Network"));
		DBNPane.setLayout(new GridLayout(0, 2));
		JLabel iterLabel = new JLabel("Number of neurons");
		DBNPane.add(iterLabel);

		SpinnerNumberModel iterSnm = new SpinnerNumberModel(24, 1,
				48, 1);
		dbnNeuron = new JSpinner(iterSnm);
		dbnNeuron.setEnabled(false);
		dbnNeuron.setToolTipText("Select number of neurons that will be used for classification.");
		DBNPane.add(dbnNeuron);
		return DBNPane;
	}
	/**
	 * Creates panel with parameters for SAE
	 * 
	 * @return panel with parameters for SAE
	 */
	private JPanel createSAEPane() {
		JPanel saePane = new JPanel();
		saePane.setBorder(BorderFactory
				.createTitledBorder("Stacked Auto Encoder"));
		saePane.setLayout(new GridLayout(0, 2));
		JLabel iterLabel = new JLabel("Number of neurons");
		saePane.add(iterLabel);

		SpinnerNumberModel iterSnm = new SpinnerNumberModel(24, 1,
				48, 1);
		sdaNeuron = new JSpinner(iterSnm);
		sdaNeuron.setEnabled(false);
		sdaNeuron.setToolTipText("Select number of neurons that will be used for classification.");
		saePane.add(sdaNeuron);
		return saePane;
	}

	/**
	 * Creates panel with OK button
	 * 
	 * @return panel with OK button
	 */
	private JPanel createBttnPane() {
		JPanel bttnPane = new JPanel();

		JButton okBttn = new JButton("OK");
		final ChangeClassifierFrame c = this;
		okBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mlpBttn.isSelected()) {
					int input = fe.getFeatureDimension();
					int output = 1;
					int middle = (Integer) middleNeuronsSpinner.getValue();
					ArrayList<Integer> nnStructure = new ArrayList<Integer>();
					nnStructure.add(input);
					nnStructure.add(middle);
					nnStructure.add(output);

					IERPClassifier classifier = new MLPClassifier(nnStructure);
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();
					for (int p : nnStructure) {
						classifierParams.add(p + "");
					}

					trainingDialog(c, mainFrame, classifier, classifierParams);
				} else if (knnBttn.isSelected()) {
					int neighborsNumber = (Integer) neighborsNumberSpinner
							.getValue();

					IERPClassifier classifier = new KNNClassifier(
							neighborsNumber);
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();
					classifierParams.add(neighborsNumber + "");

					trainingDialog(c, mainFrame, classifier, classifierParams);
				} else if (ldaBttn.isSelected()) {
					IERPClassifier classifier = new LinearDiscriminantAnalysisClassifier();
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();

					trainingDialog(c, mainFrame, classifier, classifierParams);
				} else if (svmBttn.isSelected()) {
					IERPClassifier classifier;
					try {
						classifier = new SVMClassifier((Double) svmCost
								.getValue());
						classifier.setFeatureExtraction(fe);

						List<String> classifierParams = new ArrayList<String>();
						classifierParams.add(svmCost.getValue() + "");

						trainingDialog(c, mainFrame, classifier,
								classifierParams);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (correlationBttn.isSelected()) {
					IERPClassifier classifier = new CorrelationClassifier();
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();

					trainingDialog(c, mainFrame, classifier, classifierParams);
				}
				else if (dbnBttn.isSelected()) {
					int neurons = (Integer) dbnNeuron.getValue();
					IERPClassifier classifier = new DBNDeepLearning4j(neurons);
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();
					classifierParams.add(dbnNeuron.getValue() + "");
					
					trainingDialog(c, mainFrame, classifier, classifierParams);
				}
				else if (saeBttn.isSelected()) {
					int neurons = (Integer) sdaNeuron.getValue();
					IERPClassifier classifier = new SDADeepLearning4jEarlyStop(neurons);
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();
					classifierParams.add(sdaNeuron.getValue() + "");
					
					trainingDialog(c, mainFrame, classifier, classifierParams);
				}
				else {
					JOptionPane
							.showMessageDialog(null,
									"Choose one Feature Extraction method and fill in its parameters");
				}
			}
		});

		bttnPane.setLayout(new BoxLayout(bttnPane, BoxLayout.LINE_AXIS));
		bttnPane.add(Box.createHorizontalGlue());
		bttnPane.add(okBttn);

		return bttnPane;

	}

	/**
	 * Creates training dialog that appears after choosing Classifier and
	 * clicking OK button
	 * 
	 * @param c
	 *            - this frame
	 * @param mainFrame
	 *            - reference to mainFrame
	 * @param classifier
	 *            - created Classifier
	 * @param classifierParams
	 *            - parameters for classifier
	 */
	private void trainingDialog(ChangeClassifierFrame c, MainFrame mainFrame,
			IERPClassifier classifier, List<String> classifierParams) {
		if (mainFrame.isTrained() == false) {
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"You have to train the classifier in order to use it",
					"Classifier is not trained", JOptionPane.OK_CANCEL_OPTION);
			if (dialogResult == JOptionPane.OK_OPTION) {
				JFileChooser save = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"CLASSIFIER files .classifier", "CLASSIFIER", "classifier");
				save.setDialogTitle("Save file with trained classifier and file with configuration");
				save.addChoosableFileFilter(filter);
				save.setFileFilter(filter);
				save.setCurrentDirectory(new File(System
						.getProperty("user.dir")));
				int saveResult = save.showSaveDialog(c);
				String file = "";
				if (saveResult == JFileChooser.APPROVE_OPTION) {
					file = save.getSelectedFile().getPath();
					String configurationFile = file + ".txt";
					String classifierFile = file + ".classifier";

					c.dispose();

					new TrainUsingOfflineProvider(c.fe, classifier, classifierFile, MainFrame.dataFilter);
					mainFrame.setFe(fe);
					mainFrame.setClassifier(classifier);
					mainFrame.setFeStatus("Feature Extraction: "
							+ fe.getClass().getSimpleName());
					mainFrame.setClassifierStatus("Classifier: "
							+ classifier.getClass().getSimpleName());

					writeLastTrainedClassifier(fe.getClass().getSimpleName(),
							feParams, classifier.getClass().getSimpleName(),
							classifierParams, configurationFile);
					mainFrame.setTrained(true);
				}
			}
		} else {
			c.dispose();
		}
	}

	/**
	 * Writes last trained Feature Extraction method and Classifier to a file
	 * 
	 * @param feName
	 *            - simple class name of Feature Extraction method
	 * @param feParams
	 *            - parameters for Feature Extraction method
	 * @param classifierName
	 *            - simple class name of Classifier
	 * @param classifierParams
	 *            - parameters for Classifier
	 * @param file
	 *            - name of the file to write into
	 */
	private void writeLastTrainedClassifier(String feName,
			List<String> feParams, String classifierName,
			List<String> classifierParams, String file) {
		try {
			File f = new File(file);
			FileWriter fw = new FileWriter(f);
			fw.write(feName + "\n");
			for (String param : feParams) {
				fw.write(param + "\n");
			}
			fw.write(classifierName + "\n");
			for (String param : classifierParams) {
				fw.write(param + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
