package cz.zcu.kiv.eeg.gtn.data.providers.bva;

import cz.zcu.kiv.eeg.gtn.data.listeners.EEGMessageListener;
import cz.zcu.kiv.eeg.gtn.data.providers.AbstractDataProvider;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGDataMessage;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGStartMessage;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGStopMessage;
import cz.zcu.kiv.eeg.gtn.data.providers.messaging.MessageType;
import cz.zcu.kiv.signal.ChannelInfo;
import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import cz.zcu.kiv.signal.EEGMarker;

import java.io.*;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides off-line data recorded with the BrainVision
 * format
 * 
 * @author Tomas Prokop
 *
 */
public class OffLineDataProvider extends AbstractDataProvider {
    private static final String VHDR_EXTENSION = ".vhdr";
	private static final String VMRK_EXTENSION = ".vmrk";
	private static final String EEG_EXTENSION  = ".eeg";
	private String vhdrFile;
    private String vmrkFile;
    private String eegFile;
    private Map<String, Integer> files;
    private boolean running;


    public OffLineDataProvider(File eegFile) {
        files = new HashMap<>();
        files.put(eegFile.getAbsolutePath(), -1);
        this.running = true;
    }

    public OffLineDataProvider(String trainDir) throws IOException {
        File dir = new File(trainDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new FileNotFoundException(dir + " is not a directory");
        }

        this.files = loadExpectedResults(trainDir);
        this.running = true;
    }

    private void setFileNames(String filename) {
        int index = filename.lastIndexOf(".");
        String baseName = filename.substring(0, index);
        this.vhdrFile = baseName + VHDR_EXTENSION;
        this.vmrkFile = baseName + VMRK_EXTENSION;
        this.eegFile = baseName +  EEG_EXTENSION;
    }

    

    @Override
    public void run() {
        try {
            int cnt = 0;
            for (Map.Entry<String, Integer> fileEntry : files.entrySet()) {
                if (!running)
                    break;

                DataTransformer dt = new EEGDataTransformer();
                setFileNames(fileEntry.getKey());
                File file = new File(fileEntry.getKey());
                if (!file.exists()) {
                    System.out.println(file.getAbsolutePath() + " does not exist!");
                    continue;
                }

                //Send start msg
                EEGStartMessage start = createStartMessage(dt, vhdrFile, cnt);
                cnt++;
                synchronized (super.listeners) {
                    for (EEGMessageListener ls : super.listeners) {
                        ls.startMessageSent(start);
                    }
                }

                ByteOrder order = ByteOrder.LITTLE_ENDIAN;
                int len = super.getAvailableChannels().length;
                double[][] data = new double[len][];
                for (int i = 0; i < len; i++) {
                    data[i] = dt.readBinaryData(vhdrFile, eegFile, i + 1, order);;
                }

                List<EEGMarker> markers = dt.readMarkerList(vmrkFile);
                cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGMarker[] eegMarkers = new cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGMarker[markers.size()];
                int i = 0;
                int target = fileEntry.getValue();
                for (EEGMarker m : markers) {
                    eegMarkers[i] = new cz.zcu.kiv.eeg.gtn.data.providers.messaging.EEGMarker(m.getStimulus(), m.getPosition());
                    eegMarkers[i].setExpectedClass(target);
                    i++;
                }

                EEGDataMessage dataMsg = new EEGDataMessage(MessageType.DATA, 1, eegMarkers, data);
                synchronized (super.listeners) {
                    for (EEGMessageListener ls : super.listeners) {
                        ls.dataMessageSent(dataMsg);
                    }
                }
                cnt++;
            }

            EEGStopMessage stop = new EEGStopMessage(MessageType.END, cnt);
            synchronized (super.listeners) {
                for (EEGMessageListener ls : super.listeners) {
                    ls.stopMessageSent(stop);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private EEGStartMessage createStartMessage(DataTransformer dt, String vhdrFile, int cnt) throws IOException {
        List<ChannelInfo> channels = dt.getChannelInfo(vhdrFile);
        String[] chNames = new String[channels.size()];
        double[] resolutions = new double[chNames.length];
        int i = 0;
        for (ChannelInfo channel : channels) {
            chNames[i] = channel.getName();
            resolutions[i] = channel.getResolution();
            i++;
        }

        int sampling;
        String val = getProperty("samplinginterval", dt);
        sampling = Integer.parseInt(val);
        val = getProperty("DataFile", dt);
        this.samplingRate = sampling;

        super.setAvailableChannels(chNames);
        EEGStartMessage msg = new EEGStartMessage(MessageType.START, cnt, chNames, resolutions, chNames.length, sampling);
        msg.setDataFileName(val);
        return msg;
    }

    private String getProperty(String propName, DataTransformer dt) {
        HashMap<String, HashMap<String, String>> props = dt.getProperties();
        for (Map.Entry<String, HashMap<String, String>> entry : props.entrySet()) {
            for (Map.Entry<String, String> prop : entry.getValue().entrySet()) {
                if (prop.getKey().equalsIgnoreCase(propName)) {
                    return prop.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    private Map<String, Integer> loadExpectedResults(String dir) throws IOException {
        Map<String, Integer> res = new HashMap<>();
        File file = new File(dir + File.separator + "infoTrain.txt");
        //File file = new File(dir + File.separator + "info.txt");
        FileInputStream fis = new FileInputStream(file);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        int num;
        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == '#') { //comment in info txt
                continue;
            }
            String[] parts = line.split(" ");
            if (parts.length > 1) {
                try {
                    num = Integer.parseInt(parts[1]);
                    res.put(dir + File.separator + parts[0], num);
                } catch (NumberFormatException ex) {
                    //NaN
                }
            }
        }

        br.close();
        return res;
    }
}
