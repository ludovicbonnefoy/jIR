package util.web.searchenginequery;

import util.GetProperties;

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
		}

		return new BossQuery();
	}
}
