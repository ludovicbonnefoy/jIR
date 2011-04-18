package token.probabilitydistribution.similarity;

import token.probabilitydistribution.AbstractFreqsProbabilityDistribution;
import token.probabilitydistribution.NGramsProbabilityDistribution;

public abstract class AbstractFreqsProbabilityDistributionSimilarity implements FreqsProbabilityDistributionSimilarityInterface
{
	/** Distributions de ngrammes qui vont être comparés */
	protected  AbstractFreqsProbabilityDistribution _first, _second;
	
	/**
	 * Initialisation de l'instance avec deux distributions vides.
	 */
	public AbstractFreqsProbabilityDistributionSimilarity()
	{
		_first = new NGramsProbabilityDistribution();
		_second = new NGramsProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec une première distribution non vide.
	 * @param first Distribution non vide.
	 */
	public AbstractFreqsProbabilityDistributionSimilarity(AbstractFreqsProbabilityDistribution first)
	{
		_first = first;
		_second = new NGramsProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec deux distributions non vides.
	 * @param first Première distribution.
	 * @param second Seconde distribution.
	 */
	public AbstractFreqsProbabilityDistributionSimilarity(AbstractFreqsProbabilityDistribution first, AbstractFreqsProbabilityDistribution second)
	{
		_first = first;
		_second = second;
	}

	/**
	 * Permet de définir la première des deux distributions qui vont être comparées.
	 * @param first Distribution.
	 */
	public void setFirst(AbstractFreqsProbabilityDistribution first)
	{
		_first = first;
	}

	/**
	 * Permet de définir la seconde des deux distributions qui vont être comparées.
	 * @param second Distribution. 
	 */
	public void setSecond(AbstractFreqsProbabilityDistribution second)
	{
		_second = second;
	}

	public Double similarity(AbstractFreqsProbabilityDistribution first, AbstractFreqsProbabilityDistribution second) 
	{
		_first = first;
		_second = second;
		
		return similarity();
	}

	public Double similarity(AbstractFreqsProbabilityDistribution second) 
	{
		_second = second;
		return similarity();
	}
}
