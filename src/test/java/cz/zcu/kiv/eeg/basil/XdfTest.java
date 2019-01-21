package test.java.cz.zcu.kiv.eeg.basil;

import cz.zcu.kiv.eeg.basil.data.providers.xdf.XdfReader;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Tomas Prokop on 21.01.2019.
 */
public class XdfTest {

    @Test
    public void textXdfRead() {
        XdfReader xRead = new XdfReader();
        boolean res = xRead.read("E:\\xdf_sample.xdf");
        assertTrue(res);
    }
}
