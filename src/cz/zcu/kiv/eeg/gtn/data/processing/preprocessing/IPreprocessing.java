package cz.zcu.kiv.eeg.gtn.data.processing.preprocessing;

import cz.zcu.kiv.eeg.gtn.data.processing.EEGDataPackage;

/**
 * Created by Tomas Prokop on 04.07.2017.
 */
public interface IPreprocessing {
    EEGDataPackage preprocess(EEGDataPackage data);
}
