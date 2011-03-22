package corpus;

import java.io.File;
import java.io.FileNotFoundException;

import util.GetProperties;

public class WebPagesCorpusFactory 
{
	public static AbstractWebPagesCorpus get(File directory) throws FileNotFoundException
	{
		if(GetProperties.getInstance().containsKey("webPagesCorpusType"))
		{
			String type = GetProperties.getInstance().getProperty("webPagesCorpusType");
			
			if(type.equals("trec"))
				return new WebPagesCorpusTrecForm(directory);
			else if(type.equals("web"))
				return new WebPagesCorpus(directory);
		}
		
		return new WebPagesCorpus(directory);
	}
	
	public static AbstractWebPagesCorpus get(String corpusPath)
	{
		if(GetProperties.getInstance().containsKey("webPagesCorpusType"))
		{
			String type = GetProperties.getInstance().getProperty("webPagesCorpusType");
			
			if(type.equals("trec"))
				return new WebPagesCorpusTrecForm(corpusPath);
			else if(type.equals("web"))
				return new WebPagesCorpus(corpusPath);

		}
		
		return new WebPagesCorpus(corpusPath);
	}
}
