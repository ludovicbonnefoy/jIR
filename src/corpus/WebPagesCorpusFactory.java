package corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import util.GetProperties;

public class WebPagesCorpusFactory 
{
	public static AbstractWebPagesCorpus get()
	{
		if(GetProperties.getInstance().containsKey("webPagesCorpusType"))
		{
			String type = GetProperties.getInstance().getProperty("webPagesCorpusType");
			
			if(type.equals("trec"))
			{
				return new WebPagesCorpusTrecForm();
			}
		}
		
		return new WebPagesCorpus();
	}
	
	public static AbstractWebPagesCorpus get(File directory, Set<String> urls) throws FileNotFoundException, IOException, InterruptedException
	{
		if(GetProperties.getInstance().containsKey("webPagesCorpusType"))
		{
			String type = GetProperties.getInstance().getProperty("webPagesCorpusType");
			
			if(type.equals("trec"))
			{
				return new WebPagesCorpusTrecForm(directory, urls);
			}
		}
		
		return new WebPagesCorpus(directory, urls);
	}
}
