package cz.zcu.kiv.eeg.gtn.online.tcpip.objects;

/**
 * N�zev �lohy: Jednoduch� BCI T��da: RDA_MessageStart
 *
 * @author Michal Pato�ka Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 *
 * Tento objekt p�ich�z� obecn� p�i zapo�et� komunikace se serverem. Obsahuje
 * informace o po�tu kan�l�, sn�mkovac� frekvenci p��stroje EEG a seznam
 * jednotliv�ch kan�l�, spole�n� s jejich jm�ny a volt�emi.
 */
public class RDA_MessageStart extends RDA_MessageHeader {

    /**
     * Po�et kan�l� p��stroje EEG. *
     */
    private final long nChannels;
    /**
     * Sn�mkovac� frekvence. *
     */
    private final double dSamplingInterval;
    /**
     * Volt�e jednotliv�ch elektrod. *
     */
    private final double[] dResolutions;
    /**
     * N�zvy jednotliv�ch elektrod. *
     */
    private final String[] sChannelNames;

    public RDA_MessageStart(long nSize, long nType, long nChannels,
            double dSamplingInterval, double[] dResolutions, String[] sChannelNames) {
        super(nSize, nType);
        this.nChannels = nChannels;
        this.dSamplingInterval = dSamplingInterval;
        this.dResolutions = dResolutions;
        this.sChannelNames = sChannelNames;
    }

    @Override
    public String toString() {

        String navrat = "RDA_MessageStart (size = " + nSize + ") \n"
                + "Sampling interval: " + dSamplingInterval + " �S \n"
                + "Number of channels: " + nChannels + "\n";

        for (int i = 0; i < dResolutions.length; i++) {
            navrat = navrat + (i + 1) + ": " + sChannelNames[i] + ": " + dResolutions[i] + "\n";
        }

        return navrat;
    }

    public long getnChannels() {
        return nChannels;
    }

    public double getdSamplingInterval() {
        return dSamplingInterval;
    }

    public double[] getdResolutions() {
        return dResolutions;
    }

    public String[] getsChannelNames() {
        return sChannelNames;
    }
}
