package icp.algorithm.math;

/**
 * T��da pro matematick� operace.
 * 
 * @author Petr Soukal
 */
public class Mathematic
{
	//konstanty
	public final static int CONST_2 = 2;
	public final static int ZERO = 0;
	
	/**
	 * Metoda vypo��t�v� logaritmus o z�kladu 2 z vlo�en�ho ��sla.
	 * 
	 * @param x - ��slo ze kter�ho se po��t� logaritmus o z�kladu 2.
	 * @return log2 z x.
	 */
	public static double log2(int x){
		double log2 = Math.log(x)/Math.log(CONST_2);
		
		return log2;
	}
	
	/**
	 * Pokud neni vlo�en� ��slo mocninou z�kladu 2, tak vr�t� prvn� v�t�� ��slo,
	 * kter� je mocninou z�kladu 2.
	 * 
	 * @param x - ��slo, u kter�ho se zji��uje zda je z�kladu 2.
	 * @return ��slo x nebo prvn� v�t�� ��slo, kter� je mocninou z�kladu 2.
	 */
	public static int newMajorNumberOfPowerBase2(int x){
		double number = log2(x);
		int temp = (int)number;
		
		if(number%temp == 0)		
			return x;
		else
		{
			temp += 1;
			int newNumber = (int) Math.pow(CONST_2, temp);
			return newNumber;
		}
	}
	
	/**
	 * Pokud neni vlo�en� ��slo mocninou z�kladu 2, tak vr�t� prvn� men�� ��slo,
	 * kter� je mocninou z�kladu 2.
	 * 
	 * @param x - ��slo, u kter�ho se zji��uje zda je z�kladu 2.
	 * @return ��slo x nebo prvn� men�� ��slo, kter� je mocninou z�kladu 2.
	 */
	public static int newMinorNumberOfPowerBase2(int x){
		double number = (int)log2(x);
		
		return (int) Math.pow(CONST_2, number);
	}
}
