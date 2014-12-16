package icp.online.app;

import icp.online.tcpip.DataTokenizer;
import icp.online.tcpip.TCPIPClient;
import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

public class OnLineDataProvider extends Observable {
	
	//private float[][][][] epochs;
	
	private static final int DELKABUFFERU = 10000;
	private static final int POCETHODNOTPREDEPOCHOU = 100;
	private static final int POCETHODNOTZAEPOCHOU = 512;

	/**
	 *  Po�et stimul�, po jak�m se zastav� hlavn� test.
	 */
	private static final int POCETSTIMULU = 400;

	/**
	 * Pole, ve kter�m budou ulo�eny pr�m�rn� hodnoty EEG sign�lu pro jednotliv� �islice
	 * index pole ozna�uje dan� �islo (0-9)
	 */
	private Epocha[] epochaCisla;

	/**
	 * Zapisova� pro ukl�d�n� po�tu podobnost� jednotliv�ch (zpr�m�rovan�ch) epoch s P-300 vlnou.
	 */
	//private Zapisovac zapisovac;


	/**
	 * Bufer, kter� se bude pou��vat pro ukl�d�n� hodnot z datov�ch objekt� RDA_MessageData
	 */
	private Buffer buffer;
	
	private Logger logger = Logger.getLogger(OnLineDataProvider.class);
	
	//private int[] counters;
	
	private void epochsInit() {
		// 3: FZ, CZ, PZ
		// 10: numbers from 1 to 9 . Object on zero index is not used
		// 10 epochs per number
		//epochs = new float[3][10][30][POCETHODNOTZAEPOCHOU];
		
		//counters = new int[10];
		//Arrays.fill(counters, 0);
	}
	
	/**
	 * Konstruktor, kter� vytvo�� instanci t�to ��d�c� t��dy.
	 * @param ip_adr - IP adresa serveru, na kter� se napoj� client
	 * @param port - port, kter� se pou�ije pro komunikaci se serverem
	 * @param log - loger pro logov�n� ud�lost�
	 * @param vystup - p�ijat� ��slo (ze stimulu)
	 * @param reakce reference na zapisova� reakc�
	 * @param p300 - 'idealni' P300 vlna
	 * Tato vlna se nastav� pomoc� O-Q testu, a n��sledn� se pou��v� k porovn�v�n� s
	 * pr�m�rn�mi epochami ��slic
	 */
	public OnLineDataProvider(String ip_adr, int port, Observer obs){
		super();
		addObserver(obs);
		TCPIPClient client = new TCPIPClient(ip_adr, port);
		client.start();
		DataTokenizer dtk = new DataTokenizer(client);
		dtk.start();
		
		epochsInit();
		
		this.epochaCisla = new Epocha[10];

		/* delku bufferu je nutno zvolit libovolne vhodne */
		this.buffer = new Buffer(DELKABUFFERU,POCETHODNOTPREDEPOCHOU,POCETHODNOTZAEPOCHOU);

		int cisloStimulu = 0;
		while(cisloStimulu < POCETSTIMULU + 1){
			Object o = dtk.retrieveDataBlock();
			if(o instanceof RDA_Marker){
				/* takto z�sk�m p��choz� ��slo z markeru */
				int cislo = ((Integer.parseInt(((RDA_Marker) o).getsTypeDesc().substring(11,13).trim()))-1);
				logger.debug("" + cislo);
				cisloStimulu++;
			}
			if(o instanceof RDA_MessageData){
				buffer.zapis((RDA_MessageData) o);

			}
			if(buffer.jePlny() || (cisloStimulu > POCETSTIMULU)){
				for(HodnotyVlny data = buffer.vyber(); data != null; data = buffer.vyber()){
					//Epocha epocha = new Epocha(POCETHODNOTPREDEPOCHOU,POCETHODNOTZAEPOCHOU,data.getHodnoty(),log);
					
					//epochs[0][data.getTypStimulu()][counters[data.getTypStimulu()]] = data.getHodnotyFZ();
					//epochs[1][data.getTypStimulu()][counters[data.getTypStimulu()]] = data.getHodnotyCZ();
					//epochs[2][data.getTypStimulu()][counters[data.getTypStimulu()]] = data.getHodnotyPZ();
					
					
					EpochMessenger em = new EpochMessenger();
					em.setStimulusIndex(data.getTypStimulu());
					em.setFZ(data.getHodnotyFZ());
					em.setCZ(data.getHodnotyCZ());
					em.setPZ(data.getHodnotyPZ());
					
					//counters[data.getTypStimulu()]++;
					
					this.setChanged();
					this.notifyObservers(em);
					System.out.println(em);
						/*if(this.epochaCisla[data.getTypStimulu()] != null){
							this.epochaCisla[data.getTypStimulu()].zprumeruj(epocha);
						}else{
							this.epochaCisla[data.getTypStimulu()] = epocha;
						}*/
					
				}
				buffer.vymaz();
				/*for(int i = 0; i < this.epochaCisla.length; i++){
					if(this.epochaCisla[i] != null){
						double shodujeSeNa = this.epochaCisla[i].porovnej(this.p300, 0, POCETHODNOTZAEPOCHOU);
						if(shodujeSeNa > SHODA){
							this.zapisovac.zaznamenejReakci(i);
						}
					}
				}*/
			}
		}
		//int nejvetsiReakce = this.zapisovac.getNejvetsiReakci();

		//vystup.setText("" + nejvetsiReakce);
		logger.info("EXPERIMENT SKON�IL, m��ete ukon�it m��en�");
		//log.log("Nejsiln�j�� reakce byla zaznamen�na na ��slo " + nejvetsiReakce);
	}
	
	/**
	 * @return dostupn� epochy pro v�echny dostupn� kan�ly. 
	 * [index_kan�lu (FZ == 0, CZ == 1, PZ == 2)]
	 * [index_stimulu (numbers from 1 to 9 . Object on zero index is not used)]
	 * [index_epochy (from 0 to 9)]
	 * [index_vzorku_epochy (0-511)]
	 *//*
	public synchronized float[][][][] getEpochs()
	{
		return epochs;
	}*/
}
