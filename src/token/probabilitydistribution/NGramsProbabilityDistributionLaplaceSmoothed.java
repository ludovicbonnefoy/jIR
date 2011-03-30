package token.probabilitydistribution;

import java.io.File;

/**
 * Effectue un lissage de Laplace sur les valeurs de la distribution.
 * Le lissage de Laplace consiste à rajouter 1 au nombre d'occurences et donc de donner 1 à tout élément non vu. 
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NGramsProbabilityDistributionLaplaceSmoothed extends NGramsProbabilityDistribution 
{
	private static final long serialVersionUID = 429053473478553173L;
	
	/**
	 * Initialisation d'une nouvelle distribution de termes.
	 */
	public NGramsProbabilityDistributionLaplaceSmoothed()
	{
		super();
	}
	
	/**
	 * Copie d'une distribution de termes.
	 * @param ngpd Distribution à copier.
	 */
    public NGramsProbabilityDistributionLaplaceSmoothed(NGramsProbabilityDistribution ngpd)
    {
    	super(ngpd);
    }
    
	/**
	 * Récupération d'une distribution sérialisée.
	 * @param serializedNGramsProbabilityDistribution Fichier contenant la distribution sérialisée.
	 */
    public NGramsProbabilityDistributionLaplaceSmoothed(File serializedNGramsProbabilityDistribution)
    {
    	super(serializedNGramsProbabilityDistribution);
    }
    
	/**
	 * Permet de récupérer la probabilité lissée d'apparition du ngramme.
	 * @param ngram Ngramme dont on veux la probabilité d'apparition.
	 * @return Probabilité lissée d'apparition du ngramme.
	 */
	public Double get(String ngram)
	{
		if(_freqs.containsKey(ngram))
    		return new Double((_freqs.get(ngram) + 1) / new Double(_total));
    	else
    		return new Double( new Double(1) / new Double(_total)); 
	}
}
