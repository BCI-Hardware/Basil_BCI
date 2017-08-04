package cz.zcu.kiv.eeg.gtn.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;


import cz.zcu.kiv.eeg.gtn.data.processing.featureExtraction.HHTFeatureExtraction;
import cz.zcu.kiv.eeg.gtn.data.processing.featureExtraction.IFeatureExtraction;
import cz.zcu.kiv.eeg.gtn.data.processing.featureExtraction.MatchingPursuitFeatureExtraction;
import cz.zcu.kiv.eeg.gtn.data.processing.featureExtraction.WaveletTransformFeatureExtraction;
import cz.zcu.kiv.eeg.gtn.utils.Const;

/**
 * 
 * @author Jaroslav Klaus
 *
 */
@SuppressWarnings("serial")
public class ChangeFeatureExtractionFrame extends JFrame {

	/**
	 * Reference to mainFrame
	 */
	private MainFrame mainFrame;

	/**
	 * Spinner for epoch size
	 */
	private JSpinner epochSpinner;

	/**
	 * Spinner model for epoch size that sets the bounds of input
	 */
	private final SpinnerNumberModel epochSnm = new SpinnerNumberModel(512, 1,
			Const.POSTSTIMULUS_VALUES, 1);

	/**
	 * Spinner for subsampling number
	 */
	private JSpinner subsampleSpinner;

	/**
	 * Spinner model for subsampling number that sets the bounds of input
	 */
	private final SpinnerNumberModel subsampleSnm = new SpinnerNumberModel(1,
			1, Const.POSTSTIMULUS_VALUES, 1);

	/**
	 * Spinner for number of skipped samples
	 */
	private JSpinner skipSpinner;

	/**
	 * Spinner model for nubmer of skipped samples that sets the bounds of input
	 */
	private final SpinnerNumberModel skipSnm = new SpinnerNumberModel(200, 1,
			Const.POSTSTIMULUS_VALUES, 1);

	/**
	 * Combo box for wavelet names
	 */
	private JComboBox waveletNameComboBox;

	/**
	 * Names of wavelets that can be used
	 */
	private String[] waveletNames = { "Coiflet 6", "Coiflet 12", "Coiflet 18",
			"Coiflet 24", "Coiflet 30", "Daubechies 4", "Daubechies 6",
			"Daubechies 8", "Daubechies 10", "Daubechies 12", "Daubechies 14",
			"Daubechies 16", "Daubechies 18", "Daubechies 20", "Haar",
			"Symmlet 4", "Symmlet 6", "Symmlet 8" };

	/**
	 * Spinner for size of feature vector in WT
	 */
	private JSpinner wtFeatureSize;

	/**
	 * Spinner for size of sample window in HHT
	 */
	private JSpinner hhtSampleWindowSize;

	/**
	 * Spinner for shift of sample window in HHT
	 */
	private JSpinner hhtSampleWindowShift;

	/**
	 * Spinner for amplitude threshold in HHT
	 */
	private JSpinner hhtAmplitudeThreshold;

	/**
	 * Spinner for minimal frequency in HHT
	 */
	private JSpinner hhtMinFreq;

	/**
	 * Spinner for maximal frequency in HHT
	 */
	private JSpinner hhtMaxFreq;

	/**
	 * Combo box for type of features in HHT
	 */
	private JComboBox hhtTypeOfFeatures;

	/**
	 * Types of features in HHT
	 */
	private String[] hhtFeatureTypes = { "Amplitudes", "Frequencies" };

	/**
	 * Radio button for selecting Filter And Subsampling Feature Extraction
	 */
	private JRadioButton fasBttn;

	/**
	 * Radio button for selecting Wavelet Transform Feature Extraction
	 */
	private JRadioButton wtBttn;

	/**
	 * Radio button for selecting Matching Pursuit Feature Extraction
	 */
	private JRadioButton mpBttn;

	/**
	 * Radio button for selecting Hilbert-Huang Transform Feature Extraction
	 */
	private JRadioButton hhtBttn;

	/**
	 * Parameters for Feature Extraction
	 */
	private List<String> feParams;

	/**
	 * Constructor for creating this window and initializing its variables
	 * 
	 * @param mainFrame
	 *            - reference to mainFrame
	 */
	public ChangeFeatureExtractionFrame(MainFrame mainFrame) {
		super("Choose Feature Extractor and Its Parameters");
		this.mainFrame = mainFrame;
		this.getContentPane().add(createFeFrame());
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
	private void setEscListener(final ChangeFeatureExtractionFrame frame) {
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
	private JPanel createFeFrame() {
		GridLayout mainLayout = new GridLayout(1, 2);
		JPanel contentJP = new JPanel(mainLayout);
		contentJP.add(createRadioBttns());
		contentJP.add(createParameters());

		return contentJP;
	}

	/**
	 * Creates panel with radio buttons for selecting Feature Extraction method
	 * and its actions
	 * 
	 * @return panel with radio buttons or selecting Feature Extraction method
	 */
	private JPanel createRadioBttns() {
		fasBttn = new JRadioButton("Filter and Subsample");
		fasBttn.setSelected(false);
		fasBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				subsampleSpinner.setEnabled(true);
				waveletNameComboBox.setEnabled(false);
				wtFeatureSize.setEnabled(false);
				hhtSampleWindowSize.setEnabled(false);
				hhtSampleWindowShift.setEnabled(false);
				hhtAmplitudeThreshold.setEnabled(false);
				hhtMinFreq.setEnabled(false);
				hhtMaxFreq.setEnabled(false);
				hhtTypeOfFeatures.setEnabled(false);
			}
		});

		wtBttn = new JRadioButton("Wavelet Transform");
		wtBttn.setSelected(false);
		wtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				subsampleSpinner.setEnabled(false);
				waveletNameComboBox.setEnabled(true);
				wtFeatureSize.setEnabled(true);
				hhtSampleWindowSize.setEnabled(false);
				hhtSampleWindowShift.setEnabled(false);
				hhtAmplitudeThreshold.setEnabled(false);
				hhtMinFreq.setEnabled(false);
				hhtMaxFreq.setEnabled(false);
				hhtTypeOfFeatures.setEnabled(false);
			}
		});

		mpBttn = new JRadioButton("Matching Pursuit");
		mpBttn.setSelected(false);
		mpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				subsampleSpinner.setEnabled(true);
				waveletNameComboBox.setEnabled(false);
				wtFeatureSize.setEnabled(false);
				hhtSampleWindowSize.setEnabled(false);
				hhtSampleWindowShift.setEnabled(false);
				hhtAmplitudeThreshold.setEnabled(false);
				hhtMinFreq.setEnabled(false);
				hhtMaxFreq.setEnabled(false);
				hhtTypeOfFeatures.setEnabled(false);
			}
		});

		hhtBttn = new JRadioButton("Hilbert-Huang Transform");
		hhtBttn.setSelected(false);
		hhtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				subsampleSpinner.setEnabled(true);
				waveletNameComboBox.setEnabled(false);
				wtFeatureSize.setEnabled(false);
				hhtSampleWindowSize.setEnabled(true);
				hhtSampleWindowShift.setEnabled(true);
				hhtAmplitudeThreshold.setEnabled(true);
				hhtMinFreq.setEnabled(true);
				hhtMaxFreq.setEnabled(true);
				hhtTypeOfFeatures.setEnabled(true);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(fasBttn);
		group.add(wtBttn);
		group.add(mpBttn);
		group.add(hhtBttn);

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Feature Extraction"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(fasBttn);
		pane.add(wtBttn);
		pane.add(mpBttn);
		pane.add(hhtBttn);

		return pane;
	}

	/**
	 * Create panel with parameters for Feature Extraction methods
	 * 
	 * @return panel with parameters for Feature Extraction methods
	 */
	private JPanel createParameters() {
		JPanel allPane = createAllPane();

		// Filter and Subsample
		JPanel fasPane = createFasPane();

		// Wavelet Transform
		JPanel wtPane = createWtPane();

		// Matching Pursuit
		JPanel mpPane = createMpPane();

		// Hilbert-Huang Transform
		JPanel hhtPane = createHhtPane();

		// Buttons
		JPanel bttnPane = createBttnPane();

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Parameters"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(allPane);
		pane.add(fasPane);
		pane.add(wtPane);
		pane.add(mpPane);
		pane.add(hhtPane);
		pane.add(bttnPane);

		return pane;
	}

	/**
	 * Creates panel with parameters for all extraction methods
	 * 
	 * @return panel with parameters for all extraction methods
	 */
	private JPanel createAllPane() {
		JPanel allPane = new JPanel();
		allPane.setBorder(BorderFactory
				.createTitledBorder("Parameters applicable to all Feature Extraction methods"));
		allPane.setLayout(new GridLayout(0, 2));

		JLabel allEpochLabel = new JLabel("Epoch Size");
		allPane.add(allEpochLabel);

		epochSpinner = new JSpinner(epochSnm);
		epochSpinner.setEnabled(true);
		epochSpinner.setToolTipText("Select the number of samples that will be used from input epochs to be processed.");
		allPane.add(epochSpinner);

		JLabel allSubsampleLable = new JLabel("Subsampling Factor");
		allPane.add(allSubsampleLable);

		subsampleSpinner = new JSpinner(subsampleSnm);
		subsampleSpinner.setEnabled(true);
		subsampleSpinner.setToolTipText("Select the factor of subsampling that will be applied to processed signal.");
		allPane.add(subsampleSpinner);

		JLabel allSkipLabel = new JLabel("Skip Samples");
		allPane.add(allSkipLabel);

		skipSpinner = new JSpinner(skipSnm);
		skipSpinner.setEnabled(true);
		skipSpinner.setToolTipText("Select the number of samples from unput epochs that will be skiped.");
		allPane.add(skipSpinner);

		return allPane;
	}

	/**
	 * Creates panel with parameters for Filter And Subsampling Feature
	 * Extraction
	 * 
	 * @return panel with parameters for Filter And Subsampling Feature
	 *         Extraction
	 */
	private JPanel createFasPane() {
		JPanel fasPane = new JPanel();
		fasPane.setBorder(BorderFactory
				.createTitledBorder("Filter and Subsample"));
		fasPane.setLayout(new GridLayout(0, 2));

		JLabel fasLabel = new JLabel("No more parameters to set");
		fasPane.add(fasLabel);

		return fasPane;
	}

	/**
	 * Creates panel with parameters for Wavelet Transform Feature Extraction
	 * 
	 * @return panel with parameters for Wavelet Transform Feature Extraction
	 */
	private JPanel createWtPane() {
		JPanel wtPane = new JPanel();
		wtPane.setBorder(BorderFactory.createTitledBorder("Wavelet Transform"));
		wtPane.setLayout(new GridLayout(0, 2));

		// Wavelet Name
		JLabel waveletNameLabel = new JLabel("Wavelet Name");
		wtPane.add(waveletNameLabel);

		waveletNameComboBox = new JComboBox(waveletNames);
		waveletNameComboBox.setSelectedIndex(8);
		waveletNameComboBox.setEnabled(false);
		waveletNameComboBox.setToolTipText("Select the wavelet that will be used for signal processing.");
		wtPane.add(waveletNameComboBox);

		// Wavelet Feature Size
		JLabel wtFeatureSizeLabel = new JLabel("Feature Size");
		wtPane.add(wtFeatureSizeLabel);

		SpinnerNumberModel wtFeatureSizeSnm = new SpinnerNumberModel(32, 1,
				1024, 1);
		wtFeatureSize = new JSpinner(wtFeatureSizeSnm);
		wtFeatureSize.setEnabled(false);
		wtFeatureSize.setToolTipText("Select the number of features that will be used from processed signal starting at first sample. Value shouldn't be greater than Epoch size parameter.");
		wtPane.add(wtFeatureSize);

		return wtPane;
	}

	/**
	 * Creates panel with parameters for Matching Pursuit Feature Extraction
	 * 
	 * @return panel with parameters for Matching Pursuit Feature Extraction
	 */
	private JPanel createMpPane() {

		JPanel mpPane = new JPanel();
		mpPane.setBorder(BorderFactory.createTitledBorder("Matching Pursuit"));
		mpPane.setLayout(new GridLayout(0, 2));

		return mpPane;
	}

	/**
	 * Creates panel with parameters for Hilbert-Huang Transform Feature
	 * Extraction
	 * 
	 * @return panel with parameters for Hilbert-Huang Transform Feature
	 *         Extraction
	 */
	private JPanel createHhtPane() {
		JPanel hhtPane = new JPanel();
		hhtPane.setBorder(BorderFactory
				.createTitledBorder("Hilbert-Huang Transform"));
		hhtPane.setLayout(new GridLayout(0, 2));

		// Sample Window Size
		JLabel sampleWindowSizeLabel = new JLabel("Sample Window Size");
		hhtPane.add(sampleWindowSizeLabel);

		SpinnerNumberModel sampleWindowSizeSnn = new SpinnerNumberModel(256, 1,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtSampleWindowSize = new JSpinner(sampleWindowSizeSnn);
		hhtSampleWindowSize.setEnabled(false);
		hhtSampleWindowSize.setToolTipText("Select the size of the window, in which will be properties of processed signal evaluated. Value shouldn't be greater than Epoch size parameter.");
		hhtPane.add(hhtSampleWindowSize);

		// Sample Window Shift
		JLabel sampleWindowsShiftLabel = new JLabel("Sample Window Shift");
		hhtPane.add(sampleWindowsShiftLabel);

		SpinnerNumberModel sampleWindowShiftSnn = new SpinnerNumberModel(8, 1,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtSampleWindowShift = new JSpinner(sampleWindowShiftSnn);
		hhtSampleWindowShift.setEnabled(false);
		hhtSampleWindowShift.setToolTipText("Select the number of samples, for which will be window shifted after each evaluating iteration. Value shouldn't be greater than Sample Window Size parameter.");
		hhtPane.add(hhtSampleWindowShift);

		// Amplitude Threshold
		JLabel amplitudeThresholdLabel = new JLabel("Amplitude Threshold");
		hhtPane.add(amplitudeThresholdLabel);

		SpinnerNumberModel amplitudeThresholdSnn = new SpinnerNumberModel(3.0, 0,
				Double.MAX_VALUE, 0.001);
		hhtAmplitudeThreshold = new JSpinner(amplitudeThresholdSnn);
		hhtAmplitudeThreshold.setEnabled(false);
		hhtAmplitudeThreshold.setToolTipText("Select the expected bottom amplitude threshold of P3 wave.");
		hhtPane.add(hhtAmplitudeThreshold);

		// Min Frequency
		JLabel minFreqLabel = new JLabel("Min Frequency");
		hhtPane.add(minFreqLabel);

		SpinnerNumberModel minFreqSnn = new SpinnerNumberModel(0.2, 0,
				Double.MAX_VALUE, 0.001);
		hhtMinFreq = new JSpinner(minFreqSnn);
		hhtMinFreq.setEnabled(false);
		hhtMinFreq.setToolTipText("Select the expected minimal frequency of P3 wave. Value shouldn't be greater than Max Frequency parameter.");
		hhtPane.add(hhtMinFreq);

		// Max Frequency
		JLabel maxFreqLabel = new JLabel("Max Frequency");
		hhtPane.add(maxFreqLabel);

		SpinnerNumberModel maxFreqSnn = new SpinnerNumberModel(3.0, 0,
				Double.MAX_VALUE, 0.001);
		hhtMaxFreq = new JSpinner(maxFreqSnn);
		hhtMaxFreq.setEnabled(false);
		hhtMaxFreq.setToolTipText("Select the expected maximal frequency of P3 wave. Value should be equal or greater than Min Frequency parameter.");
		hhtPane.add(hhtMaxFreq);

		// Type of Features
		JLabel typeOfFeatures = new JLabel("Type of Features");
		hhtPane.add(typeOfFeatures);

		hhtTypeOfFeatures = new JComboBox(hhtFeatureTypes);
		hhtTypeOfFeatures.setSelectedIndex(0);
		hhtTypeOfFeatures.setEnabled(false);
		hhtTypeOfFeatures.setToolTipText("Select the type of features, which will be used from HHT processing of the signal.");
		hhtPane.add(hhtTypeOfFeatures);

		return hhtPane;
	}

	/**
	 * Creates panel with Next button
	 * 
	 * @return panel with Next button
	 */
	private JPanel createBttnPane() {
		JPanel bttnPane = new JPanel();
		JButton okBttn = new JButton("Next");
		final ChangeFeatureExtractionFrame c = this;
		okBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fasBttn.isSelected()) {

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new WaveletTransformFeatureExtraction();
					
					c.dispose();

					feParams = new ArrayList<String>();
					feParams.add(epochSpinner.getValue() + "");
					feParams.add(subsampleSpinner.getValue() + "");
					feParams.add(skipSpinner.getValue() + "");

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe, feParams);
					cc.setVisible(true);

				} else if (wtBttn.isSelected()) {

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new WaveletTransformFeatureExtraction();
					/*((WaveletTransformFeatureExtraction) fe)
							.setEpochSize((Integer) epochSpinner.getValue());
					((WaveletTransformFeatureExtraction) fe)
							.setSkipSamples((Integer) skipSpinner.getValue());
					((WaveletTransformFeatureExtraction) fe)
							.setWaveletName(waveletNameComboBox
									.getSelectedIndex());
					((WaveletTransformFeatureExtraction) fe)
							.setFeatureSize((Integer) wtFeatureSize.getValue());*/

					c.dispose();

					feParams = new ArrayList<String>();
					feParams.add(epochSpinner.getValue() + "");
					feParams.add(skipSpinner.getValue() + "");
					feParams.add(waveletNameComboBox.getSelectedIndex() + "");
					feParams.add(wtFeatureSize.getValue() + "");

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe, feParams);
					cc.setVisible(true);

				} else if (mpBttn.isSelected()) {

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new MatchingPursuitFeatureExtraction();
					// TODO Set params

					c.dispose();

					feParams = new ArrayList<String>();
					feParams.add(epochSpinner.getValue() + "");
					feParams.add(subsampleSpinner.getValue() + "");
					feParams.add(skipSpinner.getValue() + "");

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe, feParams);
					cc.setVisible(true);

				} else if (hhtBttn.isSelected()) {
					if (hhtConditions() == true) {
						mainFrame.setTrained(false);

						IFeatureExtraction fe = new HHTFeatureExtraction();
						((HHTFeatureExtraction) fe)
								.setSampleWindowSize((Integer) hhtSampleWindowSize
										.getValue());
						((HHTFeatureExtraction) fe)
								.setSampleWindowShift((Integer) hhtSampleWindowShift
										.getValue());
						((HHTFeatureExtraction) fe)
								.setAmplitudeThreshold((Double) hhtAmplitudeThreshold
										.getValue());
						((HHTFeatureExtraction) fe)
								.setMinFreq((Double) hhtMinFreq.getValue());
						((HHTFeatureExtraction) fe)
								.setMaxFreq((Double) hhtMaxFreq.getValue());
						((HHTFeatureExtraction) fe)
								.setTypeOfFeatures(hhtTypeOfFeatures
										.getSelectedIndex() + 1);

						c.dispose();

						feParams = new ArrayList<String>();
						feParams.add(epochSpinner.getValue() + "");
						feParams.add(subsampleSpinner.getValue() + "");
						feParams.add(skipSpinner.getValue() + "");
						feParams.add(hhtSampleWindowSize.getValue() + "");
						feParams.add(hhtSampleWindowShift.getValue() + "");
						feParams.add(hhtAmplitudeThreshold.getValue()
								+ "");
						feParams.add(hhtMinFreq.getValue() + "");
						feParams.add(hhtMaxFreq.getValue() + "");
						feParams.add((hhtTypeOfFeatures.getSelectedIndex() + 1)
								+ "");

						ChangeClassifierFrame cc = new ChangeClassifierFrame(
								mainFrame, fe, feParams);

						cc.setVisible(true);
					}
				} else {
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
	 * Checks parameters for HHT
	 * 
	 * @return <code>true</code> if all conditions are met;<code>false</code> if
	 *         at least one condition is not met
	 */
	private boolean hhtConditions() {
		if (((Integer) hhtSampleWindowShift.getValue()) > ((Integer) hhtSampleWindowSize
				.getValue())) {
			JOptionPane.showMessageDialog(null,
					"Sample Window Shift must be <= Sample Window Size");
			return false;
		}
		if (((Double) hhtMinFreq.getValue()) > ((Double) hhtMaxFreq
				.getValue())) {
			JOptionPane.showMessageDialog(null,
					"Min Frequency must be <= Max Frequency");
			return false;
		}

		return true;
	}
}
