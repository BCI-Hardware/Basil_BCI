package icp.online.tcpip.objects;

/**
 * N�zev �lohy: Jednoduch� BCI T��da: RDA_Marker
 *
 * @author Michal Pato�ka Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 *
 * Tento objekt reprezentuje p��choz� markery. Obsahuje informace o jeho
 * velikosti, relativn�m odsazen�m v datov�m bloku (m��e b�t od 0 a� po velikost
 * tohoto bloku) a po�tu obsa�en�ch datov�ch blok� (toto ��slo je standartn� 1).
 * D�le obsahuje inforamci o tom ke kter� elektrod� p��slu��. Jeliko� v�ak
 * server doposud nem� implemetovanou funkci pro zas�l�n� marker� pouze z
 * ur�it�ho po�tu elektrod, je tato hodnota standartn� nastavena na -1, co�
 * znamen�, �e plat� pro v�echny elektrody. Nejd�le�it�j�� ��st� tohoto objektu
 * je v�ak informace o n�zvu tohoto impulzu, kter� je odd�len� nulov�mi znaky
 * (/0).
 */

public class RDA_Marker {

    /**
     * Velikost tohoto bloku v bajtech. *
     */
    private final long nSize;
    /**
     * Relativn� odsazen� v datov�m bloku. *
     */
    private final long nPosition;
    /**
     * Po�et obsa�en�ch blok� (standartn� 1). *
     */
    private final long nPoints;
    /**
     * Zasa�en� kan�l (standartn� -1 - v�echny kan�ly). *
     */
    private final long nChannel;
    /**
     * N�zev p��choz�ho markeru. *
     */
    private final String sTypeDesc;

    public RDA_Marker(long nSize, long nPosition, long nPoints, long nChannel,
            String sTypeDesc) {
        super();
        this.nSize = nSize;
        this.nPosition = nPosition;
        this.nPoints = nPoints;
        this.nChannel = nChannel;
        this.sTypeDesc = sTypeDesc;
    }

    @Override
    public String toString() {
        return "RDA_Marker (size = " + nSize + ")\n"
                + "Channel= " + nChannel + "\n"
                + "Points= " + nPoints + "\n"
                + "Position= " + nPosition + "\n"
                + "TypeDesc=" + sTypeDesc + "\n";
    }

    public long getnSize() {
        return nSize;
    }

    public long getnPosition() {
        return nPosition;
    }

    public long getnPoints() {
        return nPoints;
    }

    public long getnChannel() {
        return nChannel;
    }

    public String getsTypeDesc() {
        return sTypeDesc;
    }

}
