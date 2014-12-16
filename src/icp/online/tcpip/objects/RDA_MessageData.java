package icp.online.tcpip.objects;

/**
 * N�zev �lohy: Jednoduch� BCI
 * T��da: RDA_MessageData
 * @author Michal Pato�ka
 * Prvn� verze vytvo�ena: 3.3.2010
 * @version 1.0
 * 
 * Tato t��da je obecnou datovou t��dou zas�lanou serverem. Obsahuje informace o po�tu obsa�en�ch
 * datov�ch blok�, po�tu obsa�en�ch marker� a relativn� po�ad� tohoto bloku od za��tku komunikace.
 * D�le samoz�ejm� obsahuje samotn� data, kter� jsou ulo�ena v poli o d�lce (po�et kan�l� * po�et 
 * datov�ch blok�). Rovn� obsahuje informace o p��tomn�ch markerech.
 */
public class RDA_MessageData extends RDA_MessageHeader {
	/** Po�ad� tohoto bloku od po��tku komunikace. **/
	private long nBlock;
	/** Po�et obsa�en�ch datov�ch blok�. **/
	private long nPoints;
	/** Po�et obsa�en�ch marker�. **/
	private long nMarkers;
	/** Pole s ulo�en�mi hodnotami (samotn� data). **/
	private float[] fData;
	/** Pole s referencemi na obsa�en� markery. **/
	private RDA_Marker[] markers;
	
	public RDA_MessageData(long nSize, long nType, long nBlock,
			long nPoints, long nMarkers, float[] fData, RDA_Marker[] markers) {
		super(nSize, nType);
		this.nBlock = nBlock;
		this.nPoints = nPoints;
		this.nMarkers = nMarkers;
		this.fData = fData;
		this.markers = markers;
	}

	public String toString() {
		
		String navrat = "RDA_MessageData (size = "+ super.getnSize() +  ") \n" +
		"block NO.: " + nBlock + " \n" +
		"points: " + nPoints + "\n" +
		"NO of markers: " + nMarkers + "\n";
		int nChannels = fData.length/(int)nPoints;
		
		for(int i = 0; i < nChannels; i++){
			navrat = navrat + (i+1) + ": ";
			for(int j = i; j < fData.length; j += nPoints){
				navrat = navrat + fData[j] + ", ";
			}
			navrat = navrat + "\n";
		}
		navrat = navrat + "\n";
		
		for(int i = 0; i < nMarkers; i++){
			navrat = navrat + markers[i].toString();
		}
		
		return navrat;
	}

	public long getnBlock() {
		return nBlock;
	}

	public long getnPoints() {
		return nPoints;
	}

	public long getnMarkers() {
		return nMarkers;
	}

	public float[] getfData() {
		return fData;
	}

	public RDA_Marker[] getMarkers() {
		return markers;
	}

	
	
	
}
