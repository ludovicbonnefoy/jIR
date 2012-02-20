package token.probabilitydistribution;

import java.util.HashMap;
import java.util.Set;

public interface ProbabilityDistribution 
{
	/**
	 * Permet de récupérer la probabilité d'apparition du terme.
	 * @param term Terme dont on veux la probabilité d'apparition.
	 * @return Probabilité d'apparition du terme.
	 */
	public Double get(String term);

	/**
	 * Retourne true si le terme est présent.
	 * @param term Terme recherché.
	 * @return true si le terme est présent.
	 */
	public boolean containsKey(String term);

	/**
	 * Permet de récupérer la liste des termes présents.
	 * @return Liste des termes présents.
	 */
	public Set<String> keySet();
	
	/**
	 * Sérialise l'objet au chemin indiqué (chemin complet = contenant le nom).
	 * @param path Chemin complet où doit être stocké l'objet sérialisé.
	 */
	public void serialize(String path);
	
	/**
	 * Renvoie l'ensemble des termes présents avec leur probabilité.
	 * @return Ensemble des couples termes/proba.
	 */
	public HashMap<String, Double> getProbabilityMap();
}
