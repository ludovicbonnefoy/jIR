package word.probabilitydistribution.similarity;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Attention : calcul d'une divergence et non d'une similarité
 * Attention : tout les éléments de first doivent être présents dans second et inversement. Si ce n'est pas le cas, penser à utiliser des distributions lissées
 * @author ludo
 */
public class KullbackLeiblerUnion extends AbstractProbabilityDistributionSimilarity 
{
	public Double similarity() 
	{
		Double result = new Double(0); //score de similarité

		ArrayList<String> arrayVocabulary = new ArrayList<String>(_first.keySet());
		arrayVocabulary.addAll(_second.keySet());

		HashSet<String> Vocabulary = new HashSet<String>(arrayVocabulary);

		for(String key : Vocabulary) //pour chaque élément de la première distribution
			result += _first.get(key) * Math.log(_first.get(key) / _second.get(key)); //mise à jour du score

		return result;
	}

}
