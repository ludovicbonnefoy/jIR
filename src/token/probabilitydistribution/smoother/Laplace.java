package token.probabilitydistribution.smoother;

public class Laplace 
{
	/**
	 * Calcul et retourne la valeur lissée d'un ngramme.
	 * @param ngram Le ngramme dont on veut lisser la valeur.
	 * @param occ La fréquence du ngramme dans la collection.
	 * @param total La taille du vocabulaire.
	 * @return Valeur lissée.
	 */
	public static Double smooth(Long occ, Long total)
	{
		return new Double(occ+1.)/total;
	}
}
