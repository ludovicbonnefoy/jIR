package web.searchenginequery;

import util.GetProperties;

/**
 * Classe ayant pour but de fournir un outil pour interroger le web. 
 * Elle permet de ne pas à avoir à modifier le code en profondeur lorsque l'on souhaite modifier l'outil utilisé.
 * L'outil a utiliser est recherché dans un fichier properties. Dans le cas où rien n'est trouvé, un outil par défaut est initialisé.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class WebSearchEngineQueryFactory 
{
	public static AbstractWebSearchEngineQuery get()
	{
		if(GetProperties.getInstance().containsKey("webSearchEngine"))
		{
			String engine = GetProperties.getInstance().getProperty("webSearchEngine");
			if(engine.equals("boss"))
				return new BossQuery();
			else if (engine.equals("google"))
				return new GoogleQuery();
			else if(engine.equals("bing"))
				return new BingQuery();
		}

		return new BingQuery();
	}
	
	public static AbstractWebSearchEngineQuery get(String market)
	{
		return new BingQuery(market);
	}
}
