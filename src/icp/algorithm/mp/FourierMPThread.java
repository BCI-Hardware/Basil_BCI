package icp.algorithm.mp;

import icp.aplication.SessionManager;

import java.util.Arrays;

public class FourierMPThread extends Thread
{
	private SessionManager sm;
	
	private Base[] bases;
	
	private double[] signal;
	
	private int numberOfIterations;
	
	private DecompositionCollection dc;
	
	private double unit;
	
	public FourierMPThread(SessionManager sm, Base ... bases)
	{
		this.sm = sm;
		dc = null;
		this.bases = bases;
	}
	
	public void init(double[] signal, int numberOfIterations)
	{
		this.signal = signal;
		this.numberOfIterations = numberOfIterations;
		//BEGIN - kontrola vlastnost� parametr� metody
		/*
		 * Kontroluje se, �e d�lka vstupn�ho sign�lu (tj. pole "signal") je rovna n�kter� 
		 * mocnin� dvou. D�le se kontroluje, jestli je po�et iterac� kladn� ��slo.
		 * 
		 * Do prom�nn� "nDouble" se ulo�� v�sledek logaritmu z ��sla "signal.length" o z�kladu dva jako
		 * desetinn� ��slo.
		 */
		double nDouble = (Math.log(signal.length)) / Math.log(2);
		/*
		 * Do prom�nn� "nLong" se ulo�� v�sledek logaritmu z ��sla "signal.length" o z�kladu dva jako
		 * cel� ��slo.
		 */
		long nLong = Math.round((Math.log(signal.length)) / Math.log(2));
		/*
		 * D�lka vstupn�ho sign�lu je mocninou dvou pr�v� tehdy, kdy� "nDoulbe" je rovno nule. 
		 * Aby se p�ede�lo porovn�n� doubleov�ho ��sla s nulou, definuje se tolerance "ALLOWANCE",
		 * kter� je jist� dost mal�.
		 */
		final double ALLOWANCE = 1e-32;
		
		if (numberOfIterations <= 0 || (Math.abs(nDouble - nLong)) > ALLOWANCE)
			throw new IllegalArgumentException();
		//END- kontrola vlastnost� parametr� metody
		
		dc = new DecompositionCollection(Arrays.copyOf(signal, signal.length));
		unit = 100D / ((double) (numberOfIterations * bases.length));
	}
	
	@Override
	public void run()
	{
		Atom currentAtom = null;
		Atom bestAtom;
		
		for (int i = 0; i < numberOfIterations; i++)
		{
			bestAtom = null;
			
			for (Base b: bases)
			{
				currentAtom = b.getOptimalAtom(signal);
				sm.sendProgressUnits(unit);
				if (bestAtom == null || bestAtom.getDifference() > currentAtom.getDifference())
				{
					bestAtom = currentAtom;
				}
			}
			
			dc.addAtom(bestAtom);
			bestAtom.subtrackFrom(signal);
		}
	}
	
	public DecompositionCollection getDC()
	{
		return dc;
	}
}
