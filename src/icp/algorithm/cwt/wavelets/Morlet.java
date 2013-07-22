package icp.algorithm.cwt.wavelets;

/**
 * T��da waveletu Morlet
 */
public class Morlet extends WaveletCWT
{
	//n�zev waveletu
	private final static String NAME = "Morlet";
	//hlavn� d�lka waveletu
	private final static int MAIN_LENGTH = 16;
	
	/**
	 * Konstruktor waveletu
	 */
	public Morlet()
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
		
		return (1.0/Math.sqrt(a))*Math.cos(5*t)*Math.exp(-tPow2/2); 
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
