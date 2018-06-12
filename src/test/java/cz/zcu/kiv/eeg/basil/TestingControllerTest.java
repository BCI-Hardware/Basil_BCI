package cz.zcu.kiv.eeg.basil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cz.zcu.kiv.eeg.basil.data.processing.IWorkflowController;
import cz.zcu.kiv.eeg.basil.data.processing.TestingWorkflowController;
import cz.zcu.kiv.eeg.basil.data.processing.classification.IClassifier;
import cz.zcu.kiv.eeg.basil.data.processing.classification.MLPClassifier;
import cz.zcu.kiv.eeg.basil.data.processing.featureExtraction.IFeatureExtraction;
import cz.zcu.kiv.eeg.basil.data.processing.preprocessing.*;
import cz.zcu.kiv.eeg.basil.data.processing.preprocessing.algorithms.BandpassFilter;
import cz.zcu.kiv.eeg.basil.data.processing.preprocessing.algorithms.BaselineCorrection;
import cz.zcu.kiv.eeg.basil.data.processing.preprocessing.algorithms.ChannelSelection;
import cz.zcu.kiv.eeg.basil.data.processing.preprocessing.algorithms.IntervalSelection;
import cz.zcu.kiv.eeg.basil.data.processing.structures.Buffer;
import cz.zcu.kiv.eeg.basil.data.processing.structures.EEGDataPackage;
import cz.zcu.kiv.eeg.basil.data.processing.structures.IBuffer;
import cz.zcu.kiv.eeg.basil.data.providers.bva.OffLineDataProvider;
import cz.zcu.kiv.eeg.basil.data.providers.messaging.EEGMarker;


/**
 * This class can be used to test data providers,
 * processing, feature extraction and classification
 * (if the classifier is already trained).
 * 
 * 
 * @author lvareka
 *
 */
public class TestingControllerTest {
	
	@Test
	public void testTestingController() {
		// data provider
		File f = new File("src/main/resources/data/P300/LED_28_06_2012_104.vhdr");
	    OffLineDataProvider provider = new OffLineDataProvider(f);

	    // buffer
	    IBuffer buffer = new Buffer();
	    
	    // preprocessings
	    ISegmentation epochExtraction = new EpochExtraction(100, 1000);
	    List<IPreprocessing> preprocessing = new ArrayList<IPreprocessing>();
		List<IPreprocessing> prepreprocessing = new ArrayList<IPreprocessing>();
	    preprocessing.add(new BaselineCorrection(0, 100));
	    prepreprocessing.add(new ChannelSelection(new String[]{"Cz","Pz", "Fz"} ));
		prepreprocessing.add(new BandpassFilter(0.1, 8));
	    Averaging averaging = new Averaging(Arrays.asList(new EEGMarker("S  2", -1)));
	    AbstractDataPreprocessor dataPreprocessor = new EpochDataPreprocessor(preprocessing, prepreprocessing, averaging, epochExtraction);
	    
	    // feature extraction
	    List<IFeatureExtraction> featureExtraction = new ArrayList<IFeatureExtraction>();
	    IClassifier classification       		   = new MLPClassifier();
	    
	    // controller
	    IWorkflowController workFlowController = new TestingWorkflowController(provider, buffer, dataPreprocessor, featureExtraction, classification);
	   
	    // run data provider thread
	    Thread t = new Thread(provider);
	    t.setName("DataProviderThread");
	    t.start();
	   
	    try {
			t.join();
			System.out.println("Remaining buffer size: "       + buffer.size());
			System.out.println("Remaining number of markers: " + buffer.getMarkersSize());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
}