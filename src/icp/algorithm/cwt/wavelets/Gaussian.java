package icp.algorithm.cwt.wavelets;

/**
 * T��da waveletu Gaussian
 */
public class Gaussian extends WaveletCWT
{
	//n�zev waveletu
	private final static String NAME = "Gaussian";
	//hlavn� d�lka waveletu
	private final static int MAIN_LENGTH = 10;
	
	/**
	 * Konstruktor waveletu
	 */
	public Gaussian()
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
		
		return (1.0/Math.sqrt(a))*(-t)*Math.exp(-tPow2/2);
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
