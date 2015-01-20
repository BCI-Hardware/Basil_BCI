package icp.online.tcpip;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * N�zev �lohy: Jednoduch� BCI T��da: SynchronizedLinkedListByte
 *
 * @author Michal Pato�ka Prvn� verze vytvo�ena: 3.3.2010
 * @version 2.0
 *
 * TCP/IP klient pro napojen� na RDA server. P�ipojen� je zaji�t�no pou�it�m
 * t��dy Socket a o�et�en�m pat�i�n�ch v�jimek. Data jsou zpracov�v�na po
 * jednotliv�ch bajtech, proto�e je tak server (bohu�el) zas�l�. Vyu��v�
 * vyrovn�vac� pam� (linkedlist) do kter� zapisuje z�skan� bajty ze serveru.
 * Nadstavbou t�to t��dy je b�n� t��da typu dataTokenizer, kter� z�skan� bajty
 * p�ev�d� do podoby srozumiteln�j��ch objekt�. Bajty lze z�skat pomoc� metody
 * read().
 */
public class TCPIPClient extends Thread {

    /**
     * Datov� stream p��choz�ch bajt�. *
     */
    private DataInputStream Sinput;
    /**
     * Instance t��dy socket pro nav�z�n� spojen� se serverem. *
     */
    private Socket socket;
    /**
     * Linked list jako vyrovn�vac� pam� pro z�sk�v�n� bajt�. *
     */
    private SynchronizedLinkedListByte buffer = new SynchronizedLinkedListByte();
    /**
     * Reference na logger ud�lost�. *
     */
    private static final Logger logger = Logger.getLogger(TCPIPClient.class);
    
    private boolean isRunning;

    /**
     * Konstruktor TCP/IP clienta. V parametrech m� na jakou IP a na jak� port
     * se napojuje. Je pou�it defaultn� logger.
     *
     * @param ip na jakou ip se m� p�ipojit
     * @param port na jak�m portu m� naslouchat
     * @throws java.lang.Exception
     */
    public TCPIPClient(String ip, int port) throws Exception {

        //vytvo�en� instance t��dy socket - napojen� na server
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            logger.error("Chyba p�i p�ipojov�n� na server:" + e);
            throw new Exception(e.getMessage());
        }
        logger.debug("P�ipojen� nav�z�no: "
                + socket.getInetAddress() + ":"
                + socket.getPort());

        //vytv���m datastream pro �ten� ze serveru
        try {
            Sinput = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.error("Chyba p�i vytv��en� nov�ho input streamu: " + e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Metoda pro spu�t�n� vl�kna pro �ten� bajt� ze serveru. Jeliko� tento
     * proces mus� pro�hat paraeln� s p�ev�d�n�m jednotliv�ch bajt� na datov�
     * bloky, muselo b�t pou�ito vl�knov�ho p��stupu.
     */
    @Override
    public void run() {
        // �tu data ze serveru a ukl�d�m je do bufferu
        Byte response;
        try {
            isRunning = true;
            while (isRunning) {
                try {
                    response = Sinput.readByte();
                    buffer.addLast(response);
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Probl�m p�i �ten� ze serveru: " + e);
        }

        try {
            Sinput.close();
        } catch (Exception e) {
        }
    }
    
    public void requestStop(){
        isRunning = false;
    }

    /**
     * Vr�t� pole bajt� o zadan� velikosti. Jednotliv� bajty z�sk�v� z bufferu.
     *
     * @param value pole jak� velikost pot�ebuji
     * @return pole bajt� o zadan� velikosti
     */
    public byte[] read(int value) {
        byte[] response = new byte[value];
        for (int i = 0; i < value; i++) {
            while (true) {
                if (!buffer.isEmpty()) {
                    try {
                        response[i] = buffer.removeFirst();
                        break;
                    } catch (NoSuchElementException e) {
                        logger.error("V�jimka p�i vytv��en� nov�ho input streamu: " + e.getMessage());
                    }
                }
            }
        }
        return response;
    }

    /**
     * Tato metoda zji��uje, jestli je z�sobn�k pr�zdn�.
     *
     * @return zda-li je z�sobn�k pr�zdn�
     */
    public boolean hasNext() {
        return buffer.isEmpty();
    }

}
