package namedentity.proximities;

import token.probabilitydistribution.NGramsProbabilityDistributionDirichletSmoothed;
import token.probabilitydistribution.similarity.ProbabilityDistributionSimilarityFactory;
import token.probabilitydistribution.similarity.ProbabilityDistributionSimilarityInterface;
import web.searchenginequery.AbstractWebSearchEngineQuery;
import web.searchenginequery.WebSearchEngineQueryFactory;

public class SnippetProximity extends AbstractProximity 
{
	/** Nombre de snippets utilisés pour faire construire les modèles */
	private Integer _firstNbrSnippets, _secondNbrSnippets; 
	
	/**
	 * Initilisation de l'objet avec deux objets null.
	 */
	public SnippetProximity()
	{
		super();
		_firstNbrSnippets = 100;
		_secondNbrSnippets = 100;
	}
	
	/**
	 * Initilisation de l'objet avec passage du premier objet.
	 * @param first Premier objet.
	 */
	public SnippetProximity(Object first)
	{
		super(first);
		_firstNbrSnippets = 100;
		_secondNbrSnippets = 100;
	}
	
	/**
	 * Initilisation de l'objet avec passage des deux objets.
	 * @param first Premier objet.
	 * @param second Second objet.
	 */
	public SnippetProximity(Object first, Object second)
	{
		super(first, second);
		_firstNbrSnippets = 100;
		_secondNbrSnippets = 100;
	}
	
	/**
	 * Initilisation de l'objet avec passage du premier objet et du nombre de snippest à utiliser pour construire le modèle correspondant.
	 * @param first Premier objet.
	 * @param firstNbrSnippets Nombre de snippest à utiliser pour construire le modèle correspondant.
	 */
	public SnippetProximity(Object first, Integer firstNbrSnippets)
	{
		super(first);
		_firstNbrSnippets = firstNbrSnippets;
		_secondNbrSnippets = 100;
	}
	
	/**
	 * Initilisation de l'objet avec passage des deux objets et du nombre de snippest à utiliser pour construire les modèle correspondants.
	 * @param first Premier objet.
	 * @param firstNbrSnippets Nombre de snippest à utiliser pour construire le modèle correspondant du premier objet.
	 * @param second Second objet.
	 * @param secondNbrSnippets Nombre de snippest à utiliser pour construire le modèle correspondant du second objet.
	 */
	public SnippetProximity(Object first, Integer firstNbrSnippets, Object second, Integer secondNbrSnippets)
	{
		super(first);
		_firstNbrSnippets = firstNbrSnippets;
		_secondNbrSnippets = 100;
	}
	
	/**
	 * Permet de définir le nombre de snippest à utiliser pour construire le modèle correspondant du premier objet.
	 * @param firstNbrSnippets Nombre de snippest à utiliser pour construire le modèle correspondant du premier objet.
	 */
	public void setFirstNbrSnippets(Integer firstNbrSnippets)
	{
		_firstNbrSnippets = firstNbrSnippets;
	}
	
	/**
	 * Permet de définir le nombre de snippest à utiliser pour construire le modèle correspondant du second objet.
	 * @param secondNbrSnippets Nombre de snippest à utiliser pour construire le modèle correspondant du second objet.
	 */
	public void setSecondNbrSnippets(Integer secondNbrSnippets)
	{
		_secondNbrSnippets = secondNbrSnippets;
	}
	
	@Override
	public Double proximity() 
	{
		AbstractWebSearchEngineQuery qse = WebSearchEngineQueryFactory.get();
		qse.query(_first.toString(), _firstNbrSnippets); 
	
		String snippets = new String();
		for(String snippet : qse.getSnippets())
			snippets += snippet;
		
		NGramsProbabilityDistributionDirichletSmoothed pddsFirst = new NGramsProbabilityDistributionDirichletSmoothed();
		pddsFirst.fromString(snippets, 1);
		
		qse.query(_second.toString(), _secondNbrSnippets); 
		
		snippets = new String();
		for(String snippet : qse.getSnippets())
			snippets += snippet;
		
		NGramsProbabilityDistributionDirichletSmoothed pddsSecond = new NGramsProbabilityDistributionDirichletSmoothed();
		pddsSecond.fromString(snippets, 1);
		
		ProbabilityDistributionSimilarityInterface pds = ProbabilityDistributionSimilarityFactory.get();
		Double similarity = Math.abs(pds.similarity(pddsFirst,pddsSecond));
		
		return similarity;
	}
}
