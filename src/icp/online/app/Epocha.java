package icp.online.app;

import org.apache.log4j.Logger;

/**
 * N�zev �lohy: Jednoduch� BCI
 * T��da: Epocha
 * @author Bohumil Podles�k
 * Prvn� verze vytvo�ena: 8.3.2010
 * @version 2.0
 * 
 * Instance t�to t��dy reprezetuj� jednotliv� epochy sign�lu.
 * Epochy je nutno upravit, proto�e sign�l m��e p�ich�zet zkreslen�, d�ky pocen� subjektu.
 * V takov�m p��pad� je nutno srovnat baseline (respektive se baseline srovn�v� v�dy).
 * Epochy se spolu dok�� pr�m�rovat a d�le zde existuje metoda na vyhodnocen� podobnosti
 * s polem float� jin�ho sign�lu.
 * Je vhodn� vyu��vat rovn� metodu, kter� upozor�uje na extr�mn� hodnoty v objektu epocha.
 * Na jej�m z�klad� se m��e vyhodnotit vznik artefaktu (mrknut�).
 *
 */
public class Epocha {
	/**
	 * Po�et polo�ek v poli hodnoty, kter� jsou p�ed markerem.
	 */
	private int predMarkerem;
	/**
	 * Po�et polo�ek v poli hodnoty, kter� jsou za markerem.
	 */
	private int zaMarkerem;
	/**
	 * Hodnoty EEG sign�lu dan� epochy.
	 */
	private float[] hodnoty;	
	/**
	 * Po�et jednotliv�ch epoch, kter� vytvo�ily zpr�m�rov�n�m instanc� t�to epochy.
	 */
	private int pocetPrumVln;
	
	private Logger logger = Logger.getLogger(Epocha.class);
	
	/**
	 * Konstruktor nov� epochy
	 * @param pocetPred - po�et polo�ek v poli hodnoty, kter� jsou p�ed markerem
	 * @param pocetZa - po�et polo�ek v poli hodnoty, kter� jsou za markerem
	 * @param hodnoty - hodnoty EEG sign�lu dane Epochy
	 */
	public Epocha(int pocetPred, int pocetZa, float[] hodnoty){
		this.predMarkerem = pocetPred;
		this.zaMarkerem = pocetZa;	
		this.hodnoty = hodnoty;
		this.pocetPrumVln = 1;
	}
	
	public int getPocetPrumVln(){
		return this.pocetPrumVln;
	}
	
	public float[] getHodnoty(int odIndexu, int doIndexu){
		if((odIndexu >= this.zaMarkerem) || (doIndexu > this.zaMarkerem)
				|| (odIndexu < 0) || (doIndexu < 0)){
			logger.error("Chyba - pokus o vybr�n� hodnot vlny na indexech mimo rozsah epochy.");
			return null;
		}else if((doIndexu - odIndexu) < 0){
			logger.error("Chyba - obr�cen� po�ad� index�.");
			return null;
		}
		float[] vals = new float[doIndexu - odIndexu];
		for(int i = 0; i < doIndexu - odIndexu; i++){
			vals[i] = this.hodnoty[odIndexu + i];
		}
		return vals;
	}
	
	public int getPocetPred(){
		return this.predMarkerem;
	}
	
	public int getPocetZa(){
		return this.zaMarkerem;
	}
}
