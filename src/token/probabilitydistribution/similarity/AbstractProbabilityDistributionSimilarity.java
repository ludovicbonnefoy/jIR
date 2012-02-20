package token.probabilitydistribution.similarity;

import token.probabilitydistribution.NGramsProbabilityDistribution;
import token.probabilitydistribution.ProbabilityDistribution;

/**
 * Classe abstraite implémentant la quasi-totalité des méthodes de l'interface correspondante.
 * La seule méthode non-implémentée est celle du calcul proprement dit.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractProbabilityDistributionSimilarity implements ProbabilityDistributionSimilarityInterface 
{
	/** Distributions de ngrammes qui vont être comparés */
	protected ProbabilityDistribution _first, _second;
	
	/**
	 * Initialisation de l'instance avec deux distributions vides.
	 */
	public AbstractProbabilityDistributionSimilarity()
	{
		_first = new NGramsProbabilityDistribution();
		_second = new NGramsProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec une première distribution non vide.
	 * @param first Distribution non vide.
	 */
	public AbstractProbabilityDistributionSimilarity(ProbabilityDistribution first)
	{
		_first = first;
		_second = new NGramsProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec deux distributions non vides.
	 * @param first Première distribution.
	 * @param second Seconde distribution.
	 */
	public AbstractProbabilityDistributionSimilarity(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;
	}

	/**
	 * Permet de définir la première des deux distributions qui vont être comparées.
	 * @param first Distribution.
	 */
	public void setFirst(ProbabilityDistribution first)
	{
		_first = first;
	}

	/**
	 * Permet de définir la seconde des deux distributions qui vont être comparées.
	 * @param second Distribution. 
	 */
	public void setSecond(ProbabilityDistribution second)
	{
		_second = second;
	}

	public Double similarity(ProbabilityDistribution first, ProbabilityDistribution second) 
	{
		_first = first;
		_second = second;
		
		return similarity();
	}

	public Double similarity(ProbabilityDistribution second) 
	{
		_second = second;
		return similarity();
	}
}
