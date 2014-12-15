package icp.online.tcpip;

import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;
import icp.online.tcpip.objects.RDA_MessageHeader;
import icp.online.tcpip.objects.RDA_MessageStart;
import icp.online.tcpip.objects.RDA_MessageStop;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI Třída: DataTokenizer
 *
 * @author Michal Patočka První verze vytvořena: 3.3.2010
 * @version 2.0
 *
 * Tato třída formuje z toku bajtů získané od klienta TCPIP datové
 * objekty. Celý proces detekce začíná hledáním unikátní posloupnosti 12
 * bajtů, které označují hlavičku datového objektu. Toto je
 * naimplementováno tak, že v nekonečném cyklu přidávám poslední a
 * ubírám první z pole 12 bajtů a hledám shodu mezi tímto polem a polem
 * označujicím hlavičku. Je - li tato posloupnost nalezena, znamená to, že
 * přišel jeden z pěti typů datových objektů. Jaký typ přišel a jaká
 * je jeho délka zjistím přečtením následujících 8 bajtů, které
 * následují po hlavičce. Obecně platí, že na začátku každého přenosu
 * přijde objekt typu RDA_MessageStart, ve kterém jsou deklarovány použité
 * parametry pro následující datový přenos. Poté chodí značné
 * množství objektů typu RDA_MessageData, přičemž každý z nich může
 * obsahovat několik objektů typu RDA_Marker (nejčastěji však pouze jeden).
 * Když zjistím typ objektu, jaký přichází, není problém do něj
 * načíst data konverzí pole určitého množství bajtů, do požadovaného
 * datového typu. Pokud je objekt neznámého typu, tak ho nezpracovávám
 * (při testech chodily objekty typu nType = 10000 jako výplň mezi
 * jednotlivými objekty). Všechny objekty načítám do bufferu, kde jsou
 * připraveny k vyzvednutí pomocí metody retriveDataBlock().
 */
public class DataTokenizer extends Thread {

    /**
     * Počet kanálů EEG *
     */
    private int noOfChannels;
    /**
     * Buffer jako vyrovnávací paměť pro dočasné uložení objektů *
     */
    private SynchronizedLinkedListObject buffer = new SynchronizedLinkedListObject();
    /**
     * Unikátní posloupnost 12 bajtů, která označuje hlavičku datového
     * objektu. *
     */
    private static final byte[] UID = {-114, 69, 88, 67, -106, -55, -122, 76, -81, 74, -104, -69, -10, -55, 20, 80};
    /**
     * Reference na TCP/IP klienta, ze kterého získávám bajty ke
     * zpracování. *
     */
    private TCPIPClient client;

    private RDA_MessageStart start;

    /**
     * Reference na logger událostí. *
     */
    private static Logger logger = Logger.getLogger(DataTokenizer.class);

    /**
     * Zjišťuje jestli jsou dvě pole bajtů shodná.
     *
     * @param one první pole bajtů
     * @param two drué pole bajtů
     * @return shoda/neshoda
     */
    private boolean comparator(byte[] one, byte[] two) {
        for (int i = 0; i < one.length; i++) {
            if (one[i] != two[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Poli bajtů přidá na konec nový bajt a posune index celého pole o 1,
     * čímž vymaže odkaz na první bajt.
     *
     * @param field pole bajtů
     * @param ap přidávaný bajt
     * @return posunuté pole s bajtem navíc
     */
    private byte[] appendByte(byte[] field, byte ap) {
        for (int i = 0; i < (field.length - 1); i++) {
            field[i] = field[i + 1];
        }
        field[field.length - 1] = ap;
        return field;
    }

    /**
     * Tato metoda zapisuje objekty typu RDA_Marker a předává na ně
     * reference příslušnému datovému objektu.
     *
     * @param markerCount počet markerů, které se zpracovávají.
     * @return pole markerů
     */
    private RDA_Marker[] writeMarkers(int markerCount) {
        RDA_Marker[] nMarkers = new RDA_Marker[markerCount];
        for (int i = 0; i < markerCount; i++) {
            byte[] nSize = client.read(4);
            long size = getInt(nSize);

            byte[] nPosition = client.read(4);
            long position = getInt(nPosition);

            byte[] nPoints = client.read(4);
            long points = getInt(nPoints);

            client.read(4);
            /*
             * Tuto funkci doposud servr nemá implementovanou.
             * Proto vrací blbost. V návodu je že se defaultně jedná
             * o všechny kanály, proto hodnota -1.
             * long channel = arr2long(nChannel);*/
            long channel = -1;

            byte[] sTypeDesc = client.read((int) size - 16);
            String typeDesc = "";
            for (int j = 0; j < sTypeDesc.length; j++) {
                char znak = (char) sTypeDesc[j];
                typeDesc = typeDesc + znak;
            }
            nMarkers[i] = new RDA_Marker(size, position, points, channel, typeDesc);
        }
        return nMarkers;
    }

    /**
     * Konstruktor, kterému je předávám odkaz na TCP/IP clienta. Je použit
     * defaultní logger.
     *
     * @param client TCP/IP client pro získávání dat
     */
    public DataTokenizer(TCPIPClient client) {
        this.client = client;
    }

    private int getInt(byte[] buff) {
        ByteBuffer bf = ByteBuffer.wrap(buff);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getInt();
    }

    private double getDouble(byte[] buff) {
        ByteBuffer bf = ByteBuffer.wrap(buff);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getDouble();
    }

    private float getFloat(byte[] buff) {
        ByteBuffer bf = ByteBuffer.wrap(buff);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getFloat();
    }

    /**
     * Metoda pro spuštění vlákna DataTokenizeru. Jelikož proces
     * získávání dat a jejich převádění na datové objekty musí být
     * paraelizován, musí být použito vláknového zpracování.
     */
    @Override
    public void run() {

        byte[] value = client.read(16);
        while (true) {

            if (comparator(value, UID)) {

                byte[] nSize = client.read(4);
                byte[] nType = client.read(4);
                int size = getInt(nSize);
                int type = getInt(nType);
                RDA_MessageHeader pHeader = new RDA_MessageHeader(size, type);

                //RDA_MessageStart
                if (pHeader.getnType() == 1) {

                    byte[] nChannels = client.read(4);
                    long channels = getInt(nChannels);
                    noOfChannels = (int) channels;

                    byte[] dSamplingInterval = client.read(8);
                    double samplingInterval = getDouble(dSamplingInterval);

                    String[] typeDesc = new String[(int) channels];
                    double[] resolutions = new double[(int) channels];

                    for (int j = 0; j < channels; j++) {
                        byte[] dResolutions = client.read(8);
                        resolutions[j] = getDouble(dResolutions);
                    }

                    //jména kanálů mohou mít proměnnou délku, jsou oddělená znakem \0
                    for (int j = 0; j < channels; j++) {
                        byte[] b = client.read(1);
                        char rd = (char) b[0];
                        String channelName = "";
                        while (rd != '\0') {
                            channelName = channelName + rd;
                            b = client.read(1);
                            rd = (char) b[0];
                        }
                        typeDesc[j] = channelName;
                    }

                    RDA_MessageStart pMsgStart = new RDA_MessageStart(pHeader.getnSize(), pHeader.getnType(),
                            channels, samplingInterval, resolutions, typeDesc);
                    start = pMsgStart;
                    buffer.addLast(pMsgStart);
                    logger.debug("Zahájena komunikace se serverem.");

                    //RDA_MessageStop	
                } else if (pHeader.getnType() == 3) {
                    RDA_MessageStop pMsgStop = new RDA_MessageStop(pHeader.getnSize(), pHeader.getnType());
                    buffer.addLast(pMsgStop);
                    logger.debug("Ukončena komunikace se serverem.");

                    break;

                    //RDA_MessageData	
                } else if (pHeader.getnType() == 4) {

                    byte[] nBlock = client.read(4);
                    int block = getInt(nBlock);

                    byte[] nPoints = client.read(4);
                    int points = getInt(nPoints);

                    byte[] nMarkers = client.read(4);
                    int markers = getInt(nMarkers);

                    float[] data = new float[noOfChannels * (int) points];

                    int ch = 0;
                    for (int j = 0; j < data.length; j++) {
                        byte[] fData = client.read(4);
                        data[j] = getFloat(fData) * (float)start.getdResolutions()[0];
                    }

                    //RDA_Marker
                    RDA_Marker[] markerField = null;
                    if (markers > 0) {
                        markerField = writeMarkers((int) markers);
                    }

                    RDA_MessageData pMsgData = new RDA_MessageData(pHeader.getnSize(), pHeader.getnType(),
                            block, points, markers, data, markerField);

                    buffer.addLast(pMsgData);

                    for (int j = 0; j < markers; j++) {
                        buffer.addLast(markerField[j]);
                        logger.debug("Příchozí marker: " + markerField[j].getsTypeDesc());
                    }

                } else {
                    //všechny neznámé typy objektů se ignorují
                }

            }
            byte[] ap = client.read(1);
            value = appendByte(value, ap[0]);
        }

    }

    /**
     * Tato metoda vrací první objekt na vrcholu bufferu, do kterého jsou
     * načítány datové bloky.
     *
     * @return datový objekt
     */
    public synchronized Object retrieveDataBlock() {

        Object o = null;
        while (true) {
            if (!buffer.isEmpty()) {
                try {
                    o = buffer.removeFirst();
                    break;
                } catch (NoSuchElementException e) {
                    //OVERFLOW
                    e.printStackTrace();
                }
            }
        }
        return o;
    }

    /**
     * Tato metoda zjišťuje, jetli je prázdný buffer.
     *
     * @return zda - li je prázdný buffer.
     */
    public boolean hasNext() {
        return buffer.isEmpty();
    }

}