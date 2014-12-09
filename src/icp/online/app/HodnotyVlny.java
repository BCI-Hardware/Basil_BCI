package icp.online.app;

/**
 * N�zev �lohy: Jednoduch� BCI
 * T��da: HodnotyVlny
 * @author Bohumil Podles�k
 * Prvn� verze vytvo�ena: 8.4.2010
 * @version 1.2
 * 
 * T��da nahrazuj�c� v jav� neexistuj�c� datov� typ z�znam.
 * Je nutn� kv�li metod� vracej�c� pole hodnot a typ stimulu.
 * @author Bohumil Podles�k
 */
public class HodnotyVlny {
	private float[] hodnotyFZ;
	private float[] hodnotyCZ;
	private float[] hodnotyPZ;
	private int typStimulu;
	
	public HodnotyVlny(float[] hodnotyFZ, float[] hodnotyCZ, float[] hodnotyPZ, int typStimulu){
		this.hodnotyFZ = hodnotyFZ;
		this.hodnotyCZ = hodnotyCZ;
		this.hodnotyPZ = hodnotyPZ;
		this.typStimulu = typStimulu;
	}
	
	public int getTypStimulu(){
		return this.typStimulu;
	}

	public float[] getHodnotyFZ() {
		return hodnotyFZ;
	}

	public float[] getHodnotyCZ() {
		return hodnotyCZ;
	}

	public float[] getHodnotyPZ() {
		return hodnotyPZ;
	}
}
