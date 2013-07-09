package icp.algorithm.cwt.wavelets;

/**
 * T��da waveletu MexicanHat
 */
public class MexicanHat extends WaveletCWT
{
	//n�zev waveletu
	private final static String NAME = "Mexican_Hat";
	//hlavn� d�lka waveletu
	private final static int MAIN_LENGTH = 16;
	
	/**
	 * Konstruktor waveletu
	 */
	public MexicanHat()
	{
		super(NAME, MAIN_LENGTH);
	}
	
	/**
	 * Re�ln� ��st mate�sk�ho waveletu.
	 */
	@Override
	public double reCoef(double t, double a)
	{
		double tPow2 = t*t;
		
		return (1.0/Math.sqrt(a))* ((1-tPow2)*Math.exp(-tPow2/2)); 
	}
	
	/**
	 * Imagin�rn� ��st mate�sk�ho waveletu.
	 */
	@Override
	public double imCoef(double t, double a)
	{
		return 0;
	}
}
