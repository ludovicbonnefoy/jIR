package probabilitydistribution;

import java.util.HashMap;

/**
 * Effectue un lissage de Laplace sur les valeurs de la distribution.
 * Le lissage de Laplace consiste à rajouter 1 à toutes les fréquences et donc de donner 1 à un élément non vu. 
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class ProbabilityDistributionLaplaceSmoothed extends ProbabilityDistribution 
{
	private static final long serialVersionUID = 429053473478553173L;

	/**
	 * Initialise l'instance.
	 */
	public ProbabilityDistributionLaplaceSmoothed()
	{
		super();
	}
	
	/**
	 * Initialise l'instance en calculant la valeur des probabilités à partir des fréquences des éléments.
	 * @param frequencies Fréquences d'apparitions
	 */
    public ProbabilityDistributionLaplaceSmoothed(HashMap<String,Long> frequencies)
    {
    	super(frequencies);
    }
    
    public ProbabilityDistributionLaplaceSmoothed(String path)
    {
    	super(path);
    }
    
	/**
	 * Renvoie la valeur lissée correspondante à la clé.
	 * @param key Clé
	 * @return Valeur lissée
	 */
	public Double get(String key)
	{
		if(_freqs.containsKey(key))
    		return new Double((_freqs.get(key) + 1) / new Double(_total));
    	else
    		return new Double( new Double(1) / new Double(_total)); 
	}
}
