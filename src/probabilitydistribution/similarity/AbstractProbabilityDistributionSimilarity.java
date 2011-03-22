package probabilitydistribution.similarity;

import probabilitydistribution.ProbabilityDistribution;

public abstract class AbstractProbabilityDistributionSimilarity implements ProbabilityDistributionSimilarityInterface 
{
	protected ProbabilityDistribution _first, _second;
	
	/**
	 * Initialisation de l'instance.
	 */
	public AbstractProbabilityDistributionSimilarity()
	{
		_first = new ProbabilityDistribution();
		_second = new ProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec une première distribution.
	 * @param first Distribution
	 */
	public AbstractProbabilityDistributionSimilarity(ProbabilityDistribution first)
	{
		_first = first;
		_second = new ProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec deux distributions.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 */
	public AbstractProbabilityDistributionSimilarity(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;
	}

	public void setFirst(ProbabilityDistribution first)
	{
		_first = first;
	}

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
