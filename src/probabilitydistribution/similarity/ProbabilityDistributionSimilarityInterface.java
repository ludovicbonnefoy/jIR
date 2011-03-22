package probabilitydistribution.similarity;

import probabilitydistribution.ProbabilityDistribution;

public interface ProbabilityDistributionSimilarityInterface 
{
	/**
	 * Permet de changer la première distribution de probabilités.
	 * @param first
	 */
	public void setFirst(ProbabilityDistribution first);

	/**
	 * Permet de changer la seconde distribution de probabilités.
	 * @param second
	 */
	public void setSecond(ProbabilityDistribution second);
	
	/**
	 * Calcul du score de similarité entre deux distributions.
	 * Permet de définir les deux distributions qui vont être comparées.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double similarity(ProbabilityDistribution first, ProbabilityDistribution second);
	
	/**
	 * Calcul du score de similarité entre deux distributions.
	 * Permet de définir la seconde des deux distributions qui vont être comparées (en supposant que la première l'a été précédemment).
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double similarity(ProbabilityDistribution second);
	
	/**
	 * Calcul du score de similarité entre deux distributions.
	 * On suppose que les deux distributions ont été définie précédemment.
	 * @return Score de similarité
	 */
	public Double similarity();

}
