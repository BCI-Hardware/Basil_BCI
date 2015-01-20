package icp.online.app;

import icp.algorithm.math.Baseline;
import java.util.Arrays;
import java.util.LinkedList;

import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;

/**
 * N�zev �lohy: Jednoduch� BCI
 * T��da: Buffer
 * @author Bohumil Podles�k
 * Prvn� verze vytvo�ena: 28.3.2010
 * @version 2.0
 * 
 * T��da, do kter� se mohou pr�b�n� ukl�dat datov� objekty RDA MessageData.
 * Tyto objekty jsou n�sledn� zpracov�ny a mohou b�t vr�ceny jako pole typu float.
 */
public class Buffer {
	/**
	 * Rezerva pro zapln�nost pole bufferu.
	 * Ur�uje, kolik polo�ek z�stane maxim�ln� voln�ch, aby metoda jePlny() vratila true.
	 */
	private static final int REZERVA = 20;
	/**
	 * Ur�uje po�et hodnot p��slu�ej�c� jedn� elektrod� v jednom datov�m bloku.
	 */
	private static final int POCETHODNOTELEKTRODY = 20;	
	/**
	 * Index elektrody FZ. Tato elektroda je v poli zaps�na jako �estn�ct� v po�ad�.
	 */
	public static int indexFz = 16;
	/**
	 * Index elektrody PZ. Tato elektroda je v poli zaps�na jako sedmn�ct� v po�ad�.
	 */
	public static int indexPz = 17;
	/**
	 * Index elektrody FZ. Tato elektroda je v poli zaps�na jako osmn�ct� v po�ad�.
	 */
	public static int indexCz = 18;
        
        public static int numChannels = 19;
		
	private float[] dataFZ;
	private float[] dataCZ;
	private float[] dataPZ;
	
	
	private int konec;
	private int delka;
	private int predMarkerem;
	private int zaMarkerem;	
	private LinkedList<Integer> frontaIndexu;
	private LinkedList<Integer> frontaTypuStimulu;

	
	/**
	 * Konstruktor
	 * Z prvu je nutn� za��nat plnit buffer a� od indexu predMarkerem, proto�e jinak by
	 * v p��pad� brzk�ho p��chodu markeru bylo nutno vyb�rat hodnoty mimo rozsah pole.
	 * D�lka pole hodnot v Bufferu by m�la b�t mnohem v�t�� ne� predMarkerem + zaMarkerem
	 * @param delka - po��te�n� d�lka pole, do kter�ho Buffer ukl�d� hodnoty
	 * @param predMarkerem - po�et polo�ek pole, kter� se budou vyb�rat p�ed markerem
	 * @param zaMarkerem - po�et polo�ek pole, kter� se budou vyb�rat za markerem
	 */
	public Buffer(int delka, int predMarkerem, int zaMarkerem){
		this.delka = delka;
		
		this.dataFZ = new float[delka];
		this.dataCZ = new float[delka];
		this.dataPZ = new float[delka];
		
		for(int i = 0; i < this.delka; i++){
			this.dataFZ[i] = Float.MAX_VALUE;
			this.dataCZ[i] = Float.MAX_VALUE;
			this.dataPZ[i] = Float.MAX_VALUE;
		}
		this.konec = predMarkerem;
		this.predMarkerem = predMarkerem;
		this.zaMarkerem = zaMarkerem;
		this.frontaIndexu = new LinkedList<>();
		this.frontaTypuStimulu = new LinkedList<>();
	}
	
	/**
	 * V�b�r hodnot pouze ze t�� elektrod (FZ, PZ, CZ).
	 * Data ze t�hto t�� elektrod se zpr�m�ruj� v�en�m aritmetick�m pr�m�rem.
	 * @param pole - pole hodnot, ve kter�m jsou z�znamy v�ech elektrod
	 * @param pomerFZ - pom�r pro v�en� pr�m�r z elektrody FZ
	 * @param pomerPZ - pom�r pro v�en� pr�m�r z elektrody PZ
	 * @param pomerCZ - pom�r pro v�en� pr�m�r z elektrody CZ
	 * @return zpr�m�rovan� pole vypo�ten� z elektrod FZ, PZ a CZ
	 */	
	private float[] vyberFZ(float[] pole){
		float[] vybraneHodnoty = new float[POCETHODNOTELEKTRODY];
		for(int i = 0; i < POCETHODNOTELEKTRODY; i++){
			vybraneHodnoty[i] = pole[indexFz + numChannels*i];
		}
		return vybraneHodnoty;
	}
	
	private float[] vyberPZ(float[] pole){
		float[] vybraneHodnoty = new float[POCETHODNOTELEKTRODY];
		for(int i = 0; i < POCETHODNOTELEKTRODY; i++){
			vybraneHodnoty[i] = pole[indexPz + numChannels*i];
		}
		return vybraneHodnoty;
	}
	
	private float[] vyberCZ(float[] pole){
		float[] vybraneHodnoty = new float[POCETHODNOTELEKTRODY];
		for(int i = 0; i < POCETHODNOTELEKTRODY; i++){
			vybraneHodnoty[i] = pole[indexCz + numChannels*i];
		}
		return vybraneHodnoty;
	}
	
	/**
	 * Z�pis dat do bufferu (do pole data) a p�id�n� markeru do fronty.
	 * Pokud by byl buffer p��li� zapln�n a neve�ly by se do n�j informace o dal��m prvku,
	 * pak se jeho velikost zdvojn�sob�. V ide�ln�m p��pad� se ale bude pou��vat tak,
	 * aby jeho zv�t�ov�n� nemuselo nast�vat.
	 * @param datObjekt - objekt, obsahuj�c� pole dat pro z�pis do bufferu
	 */
	public void zapis(RDA_MessageData datObjekt){
		float[] hodnotyFZ = vyberFZ(datObjekt.getfData());
		float[] hodnotyCZ = vyberCZ(datObjekt.getfData());
		float[] hodnotyPZ = vyberPZ(datObjekt.getfData());
		
		/* pokud zbyva malo mista v bufferu */
		if(this.delka - this.konec <= hodnotyFZ.length){
			this.dataFZ = zvetsi(this.dataFZ);//pak zvetsi pole hodnot
			this.dataCZ = zvetsi(this.dataCZ);
			this.dataPZ = zvetsi(this.dataPZ);
		}
		/* zapis hodnot do pole bufferu */
                System.arraycopy(hodnotyFZ, 0, this.dataFZ, konec, hodnotyFZ.length);
                System.arraycopy(hodnotyCZ, 0, this.dataCZ, konec, hodnotyCZ.length);
                System.arraycopy(hodnotyPZ, 0, this.dataPZ, konec, hodnotyPZ.length);
		
		RDA_Marker[] markery = datObjekt.getMarkers();
		if(markery != null){
                    for (RDA_Marker marker : markery) {
                        /* promenna index znaci index prave vybraneho markeru;
                        je to aktualni pozice v poli (neaktualizovany this.konec po nahrani novych dat)
                        + relativni pozice markeru uvnitr datoveho objektu */
                        int index = this.konec + (int) marker.getnPosition();
                        this.frontaIndexu.addLast(index);
                        /* nutno ulozit zaroven index stimulu do fronty */
                        this.frontaTypuStimulu.addLast(Integer.parseInt(marker.getsTypeDesc().substring(11, 13).trim()) - 1);
                    }
		}
		
		this.konec += hodnotyFZ.length;
	}
	
	/**
	 * Dvojn�sobn� zv�t�en� st�vaj�c�ho pole dat.
	 * Z�rove� dojde k nastaven� v�ech pot�ebn�ch atribut� t��dy
	 * (d�lka, index za��tku a konce)
	 * @return - nov� dvojn�sobn� zv�t�en� pole
	 */
	private float[] zvetsi(float[] pole){
		System.out.println("*** VOLANA METODA ZVETSI Z INTANCE BUFFERU ***");
		int novaDelka = 2 * this.delka;
		float[] novaData = new float[novaDelka];
                System.arraycopy(pole, 0, novaData, 0, this.konec);
		for(int i = this.konec; i < novaDelka; i++){
			novaData[i] = Float.MAX_VALUE;
		}
		this.delka = novaDelka;
		return novaData;
	}
	
	/**
	 * V�b�r z Bufferu.
	 * Pokud nen� v bufferu ��dn� marker, pak v n�m jist� nejsou u� ��dn� data, kter� by
	 * m�la n�jakou hodnotu (ve smyslu vyb�r�n� pole dat pro Epochu) a metoda vrati null.
	 * Pokud neni v bufferu zapsanych dost hodnot za poslednim markerem, metoda take vrati null.
	 * Tato metoda bude vyb�rat hodnoty z bufferu podle marker� postupn� (FIFO).
	 * @return - pole float� o d�lce (this.pred + this.po), obsahuj�c� hodnoty z bufferu
	 * kolem posledn�ho markeru plus ��slo Stimulu (0 - 9)
	 */
	public EpochDataCarrier vyber(){
		if(this.frontaIndexu.isEmpty()){
			return null;
		}
		/* pokud neni k vybrani epochy kolem markeru jeste nacteno dostatek hodnot
		   (moznost navraceni Float.MAX_VALUE nebo prekroceni delky pole) */
		if(this.frontaIndexu.peek() + this.zaMarkerem > this.konec){
			/* pak se take vrati null */
			return null;
		}
		
		float[] vybraneHodnotyFZ = new float[this.predMarkerem + this.zaMarkerem];
		float[] vybraneHodnotyCZ = new float[vybraneHodnotyFZ.length];
		float[] vybraneHodnotyPZ = new float[vybraneHodnotyFZ.length];
		
		/* index markeru minus pocet hodnot pred markerem je indexem prvni polozky, kterou vybereme */
		/* nutno kvuli zaznamenani, na ktery stimul byla tato reakce zaznamenana */
		int typVlny = this.frontaTypuStimulu.removeFirst();
		int index = this.frontaIndexu.removeFirst() - this.predMarkerem;		
		for(int i = 0; i < (this.predMarkerem + this.zaMarkerem); i++){
			vybraneHodnotyFZ[i] = this.dataFZ[index + i];
			vybraneHodnotyCZ[i] = this.dataCZ[index + i];
			vybraneHodnotyPZ[i] = this.dataPZ[index + i];
		}
		
		Baseline.correct(vybraneHodnotyFZ, this.predMarkerem);
		Baseline.correct(vybraneHodnotyCZ, this.predMarkerem);
		Baseline.correct(vybraneHodnotyPZ, this.predMarkerem);
		
		float[]	baselineFZ = Arrays.copyOfRange(vybraneHodnotyFZ, this.predMarkerem, vybraneHodnotyFZ.length);
		float[]	baselineCZ = Arrays.copyOfRange(vybraneHodnotyCZ, this.predMarkerem, vybraneHodnotyCZ.length);
		float[]	baselinePZ = Arrays.copyOfRange(vybraneHodnotyPZ, this.predMarkerem, vybraneHodnotyPZ.length);
		
		return new EpochDataCarrier(baselineFZ, baselineCZ, baselinePZ, typVlny);
	}
	
	/**
	 * Metoda pro kontrolu zapln�nosti bufferu.
	 * Slou�i k tomu, aby t��da, kter� s n�m bude pracovat v�dela, kdy m� za��t vyb�rat prvky
	 * @return - true, kdy� je buffer pln� (zb�v� v n�m m�n� m�sta ne� REZERVA)
	 */
	public boolean jePlny(){
		return (this.delka - this.konec <= REZERVA);
	}
	
	/**
	 * Uvoln� buffer. Doporu�eno prov�d�t poka�d�, kdy� se vyberou v�echny hodnoty, ktere
	 * jdou pomoci metody vyber().
	 * Tato metoda nastav� polo�ky pole float[] data na Float.MAX_VALUE.
	 * Nebudou v�ak vymaz�ny v�echy polo�ky, ponech� se n�kolik polo�ek p�ed koncem (this.konec)
	 * toto je z d�vodu, aby v p��pad� brzk�ho p��chodu markeru mohl tento odkazovat na
	 * star� polo�ky. Marker ozna�uje pozici, p�ed kterou se berou data v metod� vyber(). 
	 * Je nutno pro nejhor�� p��pad (marker p�ijde hned v prvn�m objektu) ponechat nejm�n�
	 * this.predMarkerem star�ch hodnot.
	 * Pokud v bufferu zustal nejaky marker, tak se musi ponechat vice polozek.
	 * V takovem pripade se v promazanem bufferu musi na pocatku objevit this.predMarkerem
	 * hodnot pred indexem markeru ze stareho bufferu a zaroven i vsechna data po nem.
	 * Toto se provede z duvodu, aby dalsi prichozi data mohla navazat na minula data a
	 * neztratil se tak zbytecne jeden blok.
	 * Z tohoto duvodu vyplyva, ze tato metoda vymaze pouze minimum polozek, pokud se vola
	 * velmi casto, respektive kdyz je fronta markeru skoro plna (probihalo malo vyberu).
	 */
	public void vymaz(){
		/* indexPredMarkerem znaci pozici v poli, kde byl nalezen marker, ktery je na rade ve fronte,
		   minus pocet hodnot pred timto markerem, ktere se musi zachovat;
		   jinymi slovy, je to index prvniho nevraceneho datoveho bloku (vetsinou
		   nekompletniho), ktery se pouzije jako pole hodnot pro konstrukci epochy */		
		int indexPredMarkerem;
		if(this.frontaIndexu.peekFirst() == null){
			/* pokud zadny marker ve fronte neni, bude se brat nejhorsi pripad a to ten, ze
			   marker prijde prave nasledujici polozku po posledni hodnote (this.data[this.konec])*/
			indexPredMarkerem = this.konec - this.predMarkerem;
		}else{
			/* jinak standardne this.predMarkerem hodnot pred markerem */
			indexPredMarkerem = this.frontaIndexu.peek() - this.predMarkerem;
		}
		
		/* this.konec minus index je delka bloku (nebo vice bloku dohromady) */
		for(int i = 0; i < this.konec - indexPredMarkerem; i++){
			/* tento se prekopiruje na pocatek pole, pouzitim in-place algoritmu */
			this.dataFZ[i] = this.dataFZ[indexPredMarkerem + i];
			this.dataCZ[i] = this.dataCZ[indexPredMarkerem + i];
			this.dataPZ[i] = this.dataPZ[indexPredMarkerem + i];
		}
		/* zbytek pole se vymaze - nastavi se hodnota Float.MAX_VALUE */
		for(int i = this.konec - indexPredMarkerem; i < this.delka; i++){
			this.dataFZ[i] = Float.MAX_VALUE;
			this.dataCZ[i] = Float.MAX_VALUE;
			this.dataPZ[i] = Float.MAX_VALUE;
		}
		
		/* pokud zustaly indexy markeru ve fronte indexu, musi se prepsat na nove hodnoty */
		LinkedList<Integer> novaFrontaIndexu = new LinkedList<>();
		while(!this.frontaIndexu.isEmpty()){
			/* v�echny indexy marker� z fronty se mus� p�epsat -> ode��st od nich
			   indexPredMarkerem; t�m se v�echny posunou na za��tek  */
			int indexMarkeru = this.frontaIndexu.removeFirst() - indexPredMarkerem;			
			novaFrontaIndexu.add(indexMarkeru);
		}
		this.frontaIndexu = novaFrontaIndexu;
		
		/* novy konec bude nyni o index prvniho nezpracovaneho datoveho bloku mensi
		   lze si to predstavit, jako ze zadne polozky pole pred timto blokem jiz
		   neexistuji, a tak se tento blok zacne cislovat od nuly */
		this.konec = this.konec - indexPredMarkerem;
	}
	
	public int kolikIndexu(){
		return this.frontaIndexu.size();
	}
	
	public int kolikStimulu(){
		return this.frontaTypuStimulu.size();
	}
}
