package token.probabilitydistribution.similarity;

import util.GetProperties;

/**
 * Classe ayant pour but de fournir un outil de mesure de la similarité entre deux distributions. 
 * Elle permet de ne pas à avoir à modifier le code en profondeur lorsque l'on souhaite modifier la similarité utilisée.
 * La similarité a utiliser est recherchée dans un fichier properties. Dans le cas où rien n'est trouvé, une similarité par défaut est utilisée.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class FreqsProbabilityDistributionSimilarityFactory 
{
	/**
	 * Retourne un outil de mesure de la similarité entre deux distributions.
	 * @return Outil de mesure de la similarité entre deux distributions.
	 */
	public static ProbabilityDistributionSimilaritySuperInterface get()
	{
		if(GetProperties.getInstance().containsKey("probabilityDistributionSimilarity"))
		{
			String similarity = GetProperties.getInstance().getProperty("probabilityDistributionSimilarity");
			if(similarity.equals("kullbackLeibler"))
				return new KullbackLeibler();
			else if (similarity.equals("kullbackLeiblerUnion"))
				return new KullbackLeiblerUnion();
			else if (similarity.equals("jensenShannon"))
				return new JensenShannon();
			else if (similarity.equals("jensenShannonUnion"))
				return new JensenShannonUnion();
			else if (similarity.equals("cosine"))
				return new Cosine();
			else if (similarity.equals("jaccard"))
				return new Jaccard();
			else if (similarity.equals("dice"))
				return new DiceCoefficient();
		}

		return new KullbackLeibler();
	}

}
