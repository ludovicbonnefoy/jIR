package word.probabilitydistribution.similarity;

/**
 * Calcul de la divergence classique de Kullback-Leibler entre deux distributions.
 * Attention : calcul d'une divergence et non d'une similarité.
 * Attention : tout les éléments de first doivent être présents dans second. Si ce n'est pas le cas, penser à utiliser des distributions lissées.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class KullbackLeibler extends AbstractProbabilityDistributionSimilarity 
{
	public Double similarity() 
	{
		Double result = new Double(0); //score de divergence

		for(String key : _first.keySet()) //pour chaque élément de la première distribution
			result += _first.get(key) * Math.log(_first.get(key) / _second.get(key)); //mise à jour du score

		return result;
	}
}
