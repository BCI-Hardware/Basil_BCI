package icp.data.formats;

import icp.data.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Rozhran�, kter� by melo b�t implementov�no ka�d�m programem
 * podporovan�m form�tem. Vynucuje implementaci metody load, se kterou
 * pracuje rozhran� mezi datovou a aplika�n� vrstvou.
 * @author Ji�� Ku�era
 * @version 14. 11. 2007
 */
public interface DataFormatLoader {
    
    /**
     * Na�te data z datov�ho souboru do <code>BufferCreator</code>u a vr�t� hlavi�ku <code>Header</code>.
     * Cestu k datov�mu souboru poskytne <code>BufferCreator</code>.
     * @param loader
     * @return Hlavi�ka typu <code>Header</code> s informacemi o na�ten�m souboru.
     * @throws java.io.IOException
     * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
     */
    public Header load(BufferCreator loader) throws IOException, CorruptedFileException;

    /**
     * Vrac� ArrayList obsahuj�c� markery.
     * @return ArrayList obsahuj�c� markery.
     */
    public ArrayList<Epoch> getEpochs();
}
