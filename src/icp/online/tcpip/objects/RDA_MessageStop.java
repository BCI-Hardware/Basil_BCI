package icp.online.tcpip.objects;

/**
 * N�zev �lohy: Jednoduch� BCI T��da: RDA_MessageStop
 *
 * @author Michal Pato�ka Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 *
 * Tento pr�zdn� objekt p�ich�z� p�i ukon�en� komunikace se serverem.
 * @author Michal Pato�ka.
 */
public class RDA_MessageStop extends RDA_MessageHeader {

    public RDA_MessageStop(long nSize, long nType) {
        super(nSize, nType);
    }

    @Override
    public String toString() {
        return "RDA_MessageStop []";
    }

}
