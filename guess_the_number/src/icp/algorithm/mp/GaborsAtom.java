package icp.algorithm.mp;

/**
 * T��da reprezentuj�c� jeden Gabor�v atom.
 * 
 * T��da byla p�evzata z diplomov� pr�ce Ing. Jaroslava Svobody 
 * (<cite>Svoboda Jaroslav.  Metody zpracov�n� evokovan�ch potenci�l�,  Plze�,  2008.  
 * Diplomov� pr�ce  pr�ce  na Kated�e   informatiky a v�po�etn�  
 * techniky Z�pado�esk� univerzity v Plzni. Vedouc� diplomov� pr�ce Ing. Pavel 
 * Mautner, Ph.D.</cite>) a n�sledn� upravena.
 *
 * @author Tom� �ond�k
 * @version 19. 11. 2008
 */
public class GaborsAtom extends Atom
{
	/**
	 * Frekvence
	 */
	private double frequency;
	/**
	 * F�zov� posun
	 */
	private double phase;
	/**
	 * Modulus
	 */
	private double modulus;

	/**
	 * Vytv��� instanci Gaborova atomu.
	 * 
	 * @param scale m���tko Gaborova atomu
	 * @param position posunut� Gaborova atomu
	 * @param frequency frekvence Gaborova atomu
	 * @param phase f�zov� posun Gaborova atomu
	 */
	public GaborsAtom(double scale, double position, double frequency, double phase)
	{
		this.scale = scale;
		this.position = position;
		this.frequency = frequency;
		this.phase = phase;
	}
	
	public double[] getValues(int length)
	{
		double sum = 0;
		
		// V�po�et energie Gaborova atomu v bodech, ve kter�ch je ur�en� vstupn� sign�l.
		for (int j = 0; j < length; j++)
		{
			sum += Math.pow(this.evaluate(j), 2);
		}
		
		sum = this.getModulus() / Math.sqrt(sum);
		
		double dec;
		double[] decArray = new double[length]; 
		// Ode�ten� Gaborova atomu od sign�lu a v�po�et energie takto vznikl�ho sign�lu.
		for (int j = 0; j < length; j++)
		{
			dec = this.evaluate(j) * sum;
			decArray[j] = dec;
		}
		return decArray;
	}
	
	@Override
	public void subtrackFrom(double[] signal)
	{
		double sum = 0;
		
		// V�po�et energie Gaborova atomu v bodech, ve kter�ch je ur�en� vstupn� sign�l.
		for (int j = 0; j < signal.length; j++)
		{
			sum += Math.pow(this.evaluate(j), 2);
		}
		
		sum = this.getModulus() / Math.sqrt(sum);
		
		double dec;
		
		// Ode�ten� Gaborova atomu od sign�lu a v�po�et energie takto vznikl�ho sign�lu.
		for (int j = 0; j < signal.length; j++)
		{
			dec = this.evaluate(j) * sum;
			signal[j] -= dec;
		}
	}
	
	public GaborsAtom(){};

	/**
	 * Metoda vy��sluje funk�n� hodnotu Gaussova ok�nka v �ase v z�vislosti na hodnot�ch atribut� <code>scale</code>, 
	 * <code>position</code>, <code>frequency</code> a <code>phase</code>.
	 * 
	 * @param numberOfSample �as ve kter�m bude hodnota vy��slena
	 * @return hodnota Gaborova atomu v po�adovan�m bod�
	 */
	@Override
	public double evaluate(double numberOfSample)
	{
		return Utils.gaussianWindow((numberOfSample - position) / scale)
				* Math.cos(numberOfSample * frequency + phase);
	}
	/**
	 * Vrac� hodnotu frekvence Gaborova atomu (atributu <code>frequency</code>).
	 * @return frekvence Gaborova atomu
	 */
	public double getFrequency()
	{
		return frequency;
	}
	/**
	 * Nastavuje hodnotu frekvence Gaborova atomu (atributu <code>frequency</code>).
	 * @param frequency frekvence Gaborova atomu
	 */
	public void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}
	/**
	 * Vrac� hodnotu f�zov�ho posunu Gaborova atomu (atributu <code>phase</code>).
	 * @return f�zov� posun Gaborova atomu
	 */
	public double getPhase()
	{
		return phase;
	}
	/**
	 * Nastavuje hodnotu f�zov�ho posunu Gaborova atomu (atributu <code>phase</code>).
	 * @param phase f�zov� posun Gaborova atomu
	 */
	public void setPhase(double phase)
	{
		this.phase = phase;
	}
	/**
	 * @return the modulus
	 */
	public double getModulus()
	{
		return modulus;
	}

	/**
	 * @param modulus the modulus to set
	 */
	public void setModulus(double modulus)
	{
		this.modulus = modulus;
	}
	/**
	 * Vrac� textovou reprezentaci Gaborova atomu. P�ekr�v� metodu ze t��dy <code>Object</code>.
	 * @return textov� reprezentace Gaborova atomu
	 */
	@Override
	public String toString()
	{
		return "Gabors atom - position: " + position 
		+ ", scale: " + scale 
		+ ",  frequency: "  + frequency
		+ ", modulus: " + modulus
		+ ", phase: "
		+ phase;
	}
}
