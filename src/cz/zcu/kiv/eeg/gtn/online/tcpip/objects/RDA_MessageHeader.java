package cz.zcu.kiv.eeg.gtn.online.tcpip.objects;

/**
 * N�zev �lohy: Jednoduch� BCI T��da: RDA_MessageHeader
 *
 * @author Michal Pato�ka Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 *
 * Tato t��da reprezentuje datov� objekt kter� p�ich�z� ze serveru. Je v�dy
 * ozna�en unik�tn� posloupnost� bajt�. Nese informace o typu a velikosti
 * n�sleduj�c�ho datov�ho bloku. Tuto hlavi�ku obsahuj� v�echny ostatn� datov�
 * objekty (s v�jimkou objektu typu RDA_Marker). D�ky t�to t��d� v�m, jak� data
 * m�m zpracov�vat.
 */
public class RDA_MessageHeader {

    /**
     * Velikost cel�ho datov�ho bloku. *
     */
    protected long nSize;
    /**
     * Typ datov�ho bloku. *
     */
    protected long nType;

    public RDA_MessageHeader(long nSize, long nType) {
        this.nSize = nSize;
        this.nType = nType;

    }

    public long getnSize() {
        return nSize;
    }

    public long getnType() {
        return nType;
    }

    @Override
    public String toString() {
        return "RDA_MessageHeader [nSize=" + nSize + ", nType=" + nType + "]";
    }
}
