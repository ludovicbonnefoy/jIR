package word.probabilitydistribution;

/**
 * Classe qui effectue le lissage d'une valeur associée à un ngramme.
 * Le lissage est celui de Dirichlet et le paramètre mu est fixé arbitrairement.
 * Pour la création, la classe n'admet que des distributions lissées avec Laplace pour éviter les cas nul et peu modifier la distribution de référence.
 * Un design pattern singleton est disponible pour cette classe.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NGramsDirichletSmoothing 
{
	/** Propose une méthode de singleton. */
	private static NGramsDirichletSmoothing _instance; 
	
	/** Distribution des probabilités de référence. */
	private NGramsProbabilityDistributionLaplaceSmoothed _world;
	
	/**
	 * Permet de récupérer une instance de cette classe.
	 * @return L'instance courante.
	 */
	public static NGramsDirichletSmoothing getInstance() {
        if (null == _instance) { // Premier appel
            _instance = new NGramsDirichletSmoothing(new NGramsProbabilityDistributionLaplaceSmoothed());
        }
        return _instance;
	}
	
	/**
	 * Permet de récupérer une instance de cette classe et de lui fournir un modèle de référence si l'instance n'a pas encore été initialisée.
	 * @return L'instance courante.
	 */
	public static NGramsDirichletSmoothing getInstance(NGramsProbabilityDistributionLaplaceSmoothed ngpd) {
        if (null == _instance) { // Premier appel
            _instance = new NGramsDirichletSmoothing(ngpd);
        }
        return _instance;
	}
	
	/**
	 * Créé un lisseur de Dirichlet.
	 * @param world Modèle de référence.
	 */
	private NGramsDirichletSmoothing(NGramsProbabilityDistributionLaplaceSmoothed world)
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
}
