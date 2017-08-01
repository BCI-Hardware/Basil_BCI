package cz.zcu.kiv.eeg.gtn.data.processing;

import cz.zcu.kiv.eeg.gtn.data.listeners.EEGMessageListener;
import cz.zcu.kiv.eeg.gtn.data.processing.Structures.IBuffer;
import cz.zcu.kiv.eeg.gtn.data.processing.classification.IClassifier;
import cz.zcu.kiv.eeg.gtn.data.processing.featureExtraction.IFeatureExtraction;
import cz.zcu.kiv.eeg.gtn.data.processing.preprocessing.IDataPreprocessor;
import cz.zcu.kiv.eeg.gtn.data.providers.AbstractDataProvider;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGDataMessage;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGStartMessage;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGStopMessage;

import java.util.List;

public abstract class AbstractWorkflowController implements IWorkflowController {

    private final AbstractDataProvider dataProvider;

    private final IBuffer buffer;

    private final IDataPreprocessor preprocessor;

    private final List<IFeatureExtraction> featureExtractions;

    private final IClassifier classifier;

    private int bufferMinSize;

    public AbstractWorkflowController(AbstractDataProvider dataProvider, IBuffer buffer,
                                      IDataPreprocessor preprocessor, List<IFeatureExtraction> featureExtractions, IClassifier classifier) {
        if(dataProvider == null || buffer == null || classifier == null)
            throw new IllegalArgumentException("One or more arguments are null.");

        this.preprocessor = preprocessor;
        this.dataProvider = dataProvider;
        this.buffer = buffer;
        this.featureExtractions = featureExtractions;
        this.classifier = classifier;

        dataProvider.addListener(dataListener);
    }

    public int getBufferMinSize() {
        return bufferMinSize;
    }

    public void setBufferMinSize(int bufferMinSize) {
        this.bufferMinSize = bufferMinSize;
    }

    private EEGMessageListener dataListener = new EEGMessageListener() {
        @Override
        public void startMessageSent(EEGStartMessage msg) {
            start(msg);
        }

        @Override
        public void dataMessageSent(EEGDataMessage msg) {
            storeData(msg);

            //call processData() or check any condition first or fire event via new listener?
            processData();
        }

        @Override
        public void stopMessageSent(EEGStopMessage msg) {
            stop(msg);
        }
    };
}
