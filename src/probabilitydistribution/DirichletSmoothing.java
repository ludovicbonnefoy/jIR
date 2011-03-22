package probabilitydistribution;

/**
 * Classe qui renvoie la valeur lissée d'un élément.
 * Le lissage est celui de Dirichlet et le paramètre mu est fixé arbitrairement.
 * Un design pattern singleton est disponible pour cette classe.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class DirichletSmoothing 
{
	/** Propose une méthode de singleton */
	private static DirichletSmoothing instance; 
	
	/**
	 * Permet de récupérer une instance de cette classe.
	 * @return L'instance courante
	 */
	public static DirichletSmoothing getInstance() {
        if (null == instance) { // Premier appel
            instance = new DirichletSmoothing();
        }
        return instance;
	}
	
	/** Paramètre du lissage */
	private Double _mu;
	
	/** Distribution des probabilités de référence */
	private ProbabilityDistribution _world;
	
	/**
	 * Créé un lisseur de Dirichlet avec un paramètre mu par défaut et une référence vide.
	 */
	public DirichletSmoothing() 
	{
		_mu = new Double(2000);
		_world = new ProbabilityDistribution();
	}
	
	/**
	 * Créé un lisseur de Dirichlet avec un paramètre mu par défaut.
	 * @param world Référence
	 */
	public DirichletSmoothing(ProbabilityDistribution world)
	{
		_mu = new Double(2000);
		
		_world = world;
	}
	
	/**
	 * Créé un lisseur de Dirichlet.
	 * @param mu Paramètre mu
	 * @param world Référence
	 */
	public DirichletSmoothing(Double mu, ProbabilityDistribution world)
	{
		_mu = mu;
		
		_world = world;
	}
	
	/**
	 * Permet de définir le paramètre mu.
	 * @param mu
	 */
	public void setMu(Double mu)
	{
		_mu = mu;
	}
	
	/**
	 * Permet de définir un nouveau modèle de référence.
	 * @param world
	 */
	public void setReference(ProbabilityDistribution world)
	{
		_world = world;
	}
	
	/**
	 * Calcul de la valeur lissée pour la clé. 
	 * La valeur "par défaut" de mu est utilisée. 
	 * @param key La clé
	 * @param freq La fréquence de la clé dans la collection
	 * @param total La taille du vocabulaire
	 * @return Valeur lissée
	 */
	public Double smooth(String key, Long freq, Long total)
	{
		return this.smooth(key, freq, total, _mu);
	}
	
	/**
	 * Calcul de la valeur lissée pour la clé.
	 * @param key La clé
	 * @param freq La fréquence de la clé dans la collection
	 * @param total La taille du vocabulaire
	 * @param mu Valeur du paramètre mu
	 * @return Valeur lissée
	 */
	public Double smooth(String key, Long freq, Long total, Double mu)
	{
		if(freq.equals(0))
			return ( (mu / (new Double(total) + mu)) * _world.get(key));
		else
			return ( (new Double(freq) + (mu * _world.get(key))) / (new Double(total) + mu)); 
	}

}
