package web.searchenginequery;

import java.util.ArrayList;

/**
 * Classe abstraite implémentant la quasi-totalité des méthodes de l'interface correspondante.
 * La seule méthode non implémentée est celle interrogeant le moteur de recherche.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractWebSearchEngineQuery implements WebSearchEngineQueryInterface 
{
	/** Liste des urls des résultats dans l'ordre décroissants de pertinence */
    protected ArrayList<String> _urls;
    
    /** Liste des snippets correspondants aux urls */
    protected ArrayList<String> _snippets;
    
    /** Nombre de résultats existants à cette requête */
    protected Long _totalHits;

    public AbstractWebSearchEngineQuery()
    {
    	_urls = new ArrayList<String>();
    	_snippets = new ArrayList<String>();
    	
    	_totalHits = new Long(0);
    }
    
	@SuppressWarnings("unchecked")
	public ArrayList<String> getURLs() 
	{
		return (ArrayList<String>) _urls.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getSnippets() 
	{
		return (ArrayList<String>)_snippets.clone();
	}

	public Long getTotalHits() 
	{
		return _totalHits;
	}

}
