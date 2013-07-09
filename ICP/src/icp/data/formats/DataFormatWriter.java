package icp.data.formats;


import icp.data.*;

import java.io.File;
import java.io.IOException;

/**
 * Rozhran� modul� pro ukl�d�n� datov�ch soubor�.
 * @author Ji�� Ku�era
 */
public interface DataFormatWriter {
    /**
     * Zap�e data do souboru.
     * @param header Hlavi�ka s informacemi o souboru.
     * @param buffer T��da s daty.
     * @param outputFile V�stupn� soubor.
     * @throws java.io.IOException
     */
    public void write(Header header, Buffer buffer, File outputFile)
            throws IOException;

}
