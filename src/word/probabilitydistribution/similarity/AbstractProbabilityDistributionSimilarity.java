package word.probabilitydistribution.similarity;

import word.probabilitydistribution.NGramsProbabilityDistribution;

public abstract class AbstractProbabilityDistributionSimilarity implements ProbabilityDistributionSimilarityInterface 
{
	protected NGramsProbabilityDistribution _first, _second;
	
	/**
	 * Initialisation de l'instance.
	 */
	public AbstractProbabilityDistributionSimilarity()
	{
		_first = new NGramsProbabilityDistribution();
		_second = new NGramsProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec une première distribution.
	 * @param first Distribution
	 */
	public AbstractProbabilityDistributionSimilarity(NGramsProbabilityDistribution first)
	{
		_first = first;
		_second = new NGramsProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec deux distributions.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 */
	public AbstractProbabilityDistributionSimilarity(NGramsProbabilityDistribution first, NGramsProbabilityDistribution second)
	{
		_first = first;
		_second = second;
	}

	public void setFirst(NGramsProbabilityDistribution first)
	{
		_first = first;
	}

	public void setSecond(NGramsProbabilityDistribution second)
	{
		_second = second;
	}

	
	public Double similarity(NGramsProbabilityDistribution first, NGramsProbabilityDistribution second) 
	{
		_first = first;
		_second = second;
		
		return similarity();
	}

	public Double similarity(NGramsProbabilityDistribution second) 
	{
		_second = second;
		return similarity();
	}
}
