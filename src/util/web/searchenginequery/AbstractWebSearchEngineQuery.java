package util.web.searchenginequery;

import java.util.ArrayList;

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
    
	public ArrayList<String> getURLs() 
	{
		return _urls;
	}

	public ArrayList<String> getSnippets() 
	{
		return _snippets;
	}

	public Long getTotalHits() 
	{
		return _totalHits;
	}

}
