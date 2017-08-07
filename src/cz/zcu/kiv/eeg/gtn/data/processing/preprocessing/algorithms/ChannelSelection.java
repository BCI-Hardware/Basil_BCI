package cz.zcu.kiv.eeg.gtn.data.processing.preprocessing.algorithms;

import cz.zcu.kiv.eeg.gtn.data.processing.structures.EEGDataPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.zcu.kiv.eeg.gtn.data.processing.preprocessing.IPreprocessing;

/**
 * Created by Tomas Prokop on 01.08.2017.
 */
public class ChannelSelection implements IPreprocessing {
	private String[] selectedChannels;
	
		
	
    public ChannelSelection(String[] selectedChannels) {
		this.selectedChannels = selectedChannels;
	}


    // TODO: pokud nektere kanaly chybi, vratit mensi pole, nebo vyhodit vyjimku?
	@Override
    public EEGDataPackage preprocess(EEGDataPackage inputPackage) {
        List<String> currentChannelNames  = new ArrayList<String>(Arrays.asList(inputPackage.getChannelNames()));
        List<String> selectedChannelNames = new ArrayList<String>(Arrays.asList(selectedChannels));
        List<Integer> selectedPointers    = new ArrayList<Integer>();
        
        for (String selectedChannel: selectedChannelNames) {
        	int index = currentChannelNames.indexOf(selectedChannel);
        	selectedPointers.add(index);
        }
        
        // set intersection - remove all channel names not contained 
        currentChannelNames.retainAll(selectedChannelNames);
        
        double[][] originalEegData = inputPackage.getData();
        double[][] reducedData = new double[selectedPointers.size()][originalEegData[0].length];
        
        for (int i = 0; i < selectedPointers.size(); i++) {
        	System.arraycopy(originalEegData[selectedPointers.get(i)], 0, reducedData[i], 0, originalEegData[0].length);
        }
        inputPackage.setData(reducedData, this);
        
        return inputPackage;
    }
}