package token.probabilitydistribution.similarity;

import token.probabilitydistribution.AbstractFreqsProbabilityDistribution;

public interface FreqsProbabilityDistributionSimilarityInterface extends ProbabilityDistributionSimilaritySuperInterface
{
	/**
	 * Permet de changer la première distribution de probabilités.
	 * @param first
	 */
	public void setFirst(AbstractFreqsProbabilityDistribution first);

	/**
	 * Permet de changer la seconde distribution de probabilités.
	 * @param second
	 */
	public void setSecond(AbstractFreqsProbabilityDistribution second);
	
	/**
	 * Calcul du score de similarité entre deux distributions.
	 * Permet de définir les deux distributions qui vont être comparées.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double similarity(AbstractFreqsProbabilityDistribution first, AbstractFreqsProbabilityDistribution second);
	
	/**
	 * Calcul du score de similarité entre deux distributions.
	 * Permet de définir la seconde des deux distributions qui vont être comparées (en supposant que la première l'a été précédemment).
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double similarity(AbstractFreqsProbabilityDistribution second);
}
