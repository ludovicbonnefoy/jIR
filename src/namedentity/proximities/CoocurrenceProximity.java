package namedentity.proximities;

import token.NounTransformation;
import web.searchenginequery.AbstractWebSearchEngineQuery;
import web.searchenginequery.WebSearchEngineQueryFactory;

public class CoocurrenceProximity extends AbstractProximity 
{
	/**
	 * Initilisation de l'objet avec deux objets null.
	 */
	public CoocurrenceProximity()
	{
		super();
	}
	
	/**
	 * Initilisation de l'objet avec passage du premier objet.
	 * @param first Premier objet.
	 */
	public CoocurrenceProximity(Object first)
	{
		super(first);
	}
	
	/**
	 * Initilisation de l'objet avec passage des deux objets.
	 * @param first Premier objet.
	 * @param second Second objet.
	 */
	public CoocurrenceProximity(Object first, Object second)
	{
		super(first, second);
	}
	
	@Override
	public Double proximity()
	{
		if(_first == null)
			throw new NullPointerException("Le premier élément n'est pas défini");
		else if (_second != null)
			throw new NullPointerException("Le deuxième élément n'est pas défini");
		else
		{
			AbstractWebSearchEngineQuery qse = WebSearchEngineQueryFactory.get();
			qse.query("\""+NounTransformation.pluralForm(_second.toString())+" * "+_first.toString()+"\"", 0); //Interrogation de Boss
			
			return new Double(qse.getTotalHits());
		}
	}
}
