package cz.zcu.kiv.eeg.basil.data.providers.xdf;

import java.util.HashMap;

/**
 * Created by Tomas Prokop on 28.01.2019.
 */
public class XdfFileData {
    private FileHeader header;
    private HashMap<Integer, StreamHeader> streamHeaders;
    private HashMap<Integer, StreamData> data;

    public XdfFileData() {
        streamHeaders = new HashMap<>();
        data = new HashMap<>();
    }

    public FileHeader getHeader() {
        return header;
    }

    public void setHeader(FileHeader header) {
        this.header = header;
    }


}
