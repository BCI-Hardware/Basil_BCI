package icp.algorithm.cwt.wavelets;

/**
 * Abstraktn� t��da waveletu pro spojitou waveletovou transformaci.
 */
public abstract class WaveletCWT
{
	//n�zev waveletu
	private String name;
	//hlavn� d�lka waveletu
	private int mainLength;

    /**
     *  Konstruktor WaveletCWT
     *
     *  @param name - n�zev waveletu.
    */
    public WaveletCWT(String name, int mainLength)
    {
    	this.name = name;
    	this.mainLength = mainLength;
    }

    /**
	 * Re�ln� ��st mate�sk�ho waveletu.
	 */
    public abstract double reCoef(double t, double a);
    
    /**
	 * Imagin�rn� ��st mate�sk�ho waveletu.
	 */
    public abstract double imCoef(double t, double a);
    
    /**
     * @return n�zev waveletu.
    */
    public String getName()
    {
        return name;
    }
    
    /**
     * @return hlavn� d�lka waveletu.
    */
    public int getMainLength()
    {
        return mainLength;
    }
}
