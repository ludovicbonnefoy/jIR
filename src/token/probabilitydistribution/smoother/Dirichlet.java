package token.probabilitydistribution.smoother;

import java.util.HashMap;

import token.probabilitydistribution.NGramsProbabilityDistributionLaplaceSmoothed;

/**
 * Classe qui effectue le lissage d'une valeur associée à un ngramme.
 * Le lissage est celui de Dirichlet et le paramètre mu est fixé arbitrairement.
 * Pour la création, la classe n'admet que des distributions lissées avec Laplace pour éviter les cas nul et peu modifier la distribution de référence.
 * Un design pattern singleton est disponible pour cette classe.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class Dirichlet
{
	/** Propose une méthode de singleton. */
	private static Dirichlet _instance; 
	
	/** Distribution des probabilités de référence. */
	private NGramsProbabilityDistributionLaplaceSmoothed _world;
	
	/**
	 * Permet de récupérer une instance de cette classe.
	 * @return L'instance courante.
	 */
	public static Dirichlet getInstance() 
	{
        if (null == _instance) 
            _instance = new Dirichlet(new NGramsProbabilityDistributionLaplaceSmoothed());
        return _instance;
	}
	
	/**
	 * Permet de récupérer une instance de cette classe et de lui fournir un modèle de référence si l'instance n'a pas encore été initialisée.
	 * @return L'instance courante.
	 */
	public static Dirichlet getInstance(NGramsProbabilityDistributionLaplaceSmoothed ngpd) 
	{
        if (null == _instance) 
            _instance = new Dirichlet(ngpd);
        return _instance;
	}
	
	/**
	 * Créé un lisseur de Dirichlet.
	 * @param world Modèle de référence.
	 */
	private Dirichlet(NGramsProbabilityDistributionLaplaceSmoothed world)
	{
		_world = world;
	}
	
	/**
	 * Permet de définir un nouveau modèle de référence.
	 * @param world Modèle de référence.
	 */
	public void setReference(NGramsProbabilityDistributionLaplaceSmoothed world)
	{
		_world = world;
	}
	
	/**
	 * Calcul et retourne la valeur lissée d'un ngramme.
	 * @param ngram Le ngramme dont on veut lisser la valeur.
	 * @param occ La fréquence du ngramme dans la collection.
	 * @param total La taille du vocabulaire.
	 * @return Valeur lissée.
	 */
	public Double smooth(String ngram, Long occ, Long total)
	{
		return smooth(ngram, occ, total, 2000.);
	}
	
	/**
	 * Calcul et retourne la valeur lissée d'un ngramme.
	 * @param ngram Le ngramme dont on veut lisser la valeur.
	 * @param occ La fréquence du ngramme dans la collection.
	 * @param total La taille du vocabulaire.
	 * @param mu Valeur du paramètre mu de lissage.
	 * @return Valeur lissée.
	 */
	public Double smooth(String ngram, Long occ, Long total, Double mu)
	{
		if(occ.equals(0))
			return ( (mu / (new Double(total) + mu)) * _world.get(ngram));
		else
			return ( (new Double(occ) + (mu * _world.get(ngram))) / (new Double(total) + mu)); 
	}

	/**
	 * Calcul et retourne la valeur lissée de ngrammes.
	 * @param occs La fréquence des ngramme dans la collection.
	 * @param total La taille du vocabulaire.
	 * @return Valeur lissée.
	 */
	public HashMap<String, Double> smooth(HashMap<String, Long> occs, Long total)
	{
		return smooth(occs, total, 2000.);
	}
	
	/**
	 * Calcul et retourne la valeur lissée de ngrammes.
	 * @param occs La fréquence des ngramme dans la collection.
	 * @param total La taille du vocabulaire.
	 * @param mu Valeur du paramètre mu de lissage.
	 * @return Valeur lissée.
	 */
	public HashMap<String, Double> smooth(HashMap<String, Long> occs, Long total, Double mu)
	{
		HashMap<String, Double> probas = new HashMap<String, Double>();
		
		for(String key : occs.keySet())
			probas.put(key, smooth(key, occs.get(key), total, mu));
		
		return probas;
	}
}
