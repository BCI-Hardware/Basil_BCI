package cz.zcu.kiv.rondik.mp.algorithm;

import java.util.Arrays;

/**
 * 
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
public class FourierMP 
{	
	private Base[] bases;
	
	public FourierMP(Base ... bases)
	{
		this.bases = bases;
	}

	/**
	 * Slou�� k proveden� algoritmu Matching Pursuit. Algoritmus pou��v� k aproximaci 
	 * vstupn�ho sign�lu slovn�k Gaborov�ch funkc�. K urychlen� v�po�tu byla pou�ita 
	 * rychl� Fourierova transformace, jej� d�sledkem je po�adavek na d�lku vstupn�ho 
	 * sign�lu (viz <b>Parameters</b>). 
	 * @param signal Vstupn� sign�l d�lky <code>2^n</code>, kde <code>n</code> je 
	 * p�irozen� ��slo. N�rok na d�lku vstupn�ho sign�lu je d�n pou�it�m rychl� Fourierovy 
	 * transformace v algoritmu Matching Pursuit. 
	 * @param iterations Po�et iterac� algoritmu Matching Pursuit.
	 * @return Atomy, kter� jsou v�sledkem algoritmu Matching Pursuit.
	 * @throws IllegalArgumentException Pokud je zad�n sign�l d�lky, kter� nen� mocninou ��sla dv�
	 * nebo pokud je po�et iterac� men�� nebo roven nule.
	 */
	public DecompositionCollection doMP(double[] signal, int iterations) throws IllegalArgumentException
	{
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
		
		if (iterations <= 0 || (Math.abs(nDouble - nLong)) > ALLOWANCE)
			throw new IllegalArgumentException();
		//END- kontrola vlastnost� parametr� metody
		
		DecompositionCollection collection = new DecompositionCollection(Arrays.copyOf(signal, signal.length));
		//double energy1 = 0, energy2 = 0;
		
		
//		// V�po�et energie vstupn�ho sign�lu.
//		for (int i = 0; i < signal.length; i++)
//		{
//			energy1 += Math.pow(signal[i], 2);
//		}
		
		Atom currentAtom = null;
		Atom bestAtom;
		
		for (int i = 0; i < iterations; i++)
		{
			bestAtom = null;
			System.out.println(i + "^th iteration");
			
			for (Base b: bases)
			{
				currentAtom = b.getOptimalAtom(signal);
				
				if (bestAtom == null || bestAtom.getDifference() > currentAtom.getDifference())
				{
					bestAtom = currentAtom;
				}
			}
			
			collection.addAtom(bestAtom);
			bestAtom.subtrackFrom(signal);
			
//			energy2 = 0;
//			for (int j = 0; j < signal.length; j++)
//			{
//				energy2 += Math.pow(signal[j], 2);
//			}
			/*if (energy2 > energy1)
				return collection;*/
		}
		return collection;
	}
}
