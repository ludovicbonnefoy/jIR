package token.probabilitydistribution.similarity;

public interface ProbabilityDistributionSimilaritySuperInterface 
{
	/**
	 * Calcul du score de similarité entre deux distributions.
	 * On suppose que les deux distributions ont été définie précédemment.
	 * @return Score de similarité
	 */
	public Double similarity();
}
