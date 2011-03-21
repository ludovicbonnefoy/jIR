package util.tokeniser;

import util.GetProperties;

public class ExternalTokeniserFactory 
{
	public static AbstractExternalTokeniser get(String executablePath)
	{
		if(GetProperties.getInstance().containsKey("externalTokeniser"))
		{
			String external = GetProperties.getInstance().getProperty("externalTokeniser");
			if(external.equals("treeTagger"))
				return new TreeTaggerTokeniser(executablePath);
		}

		return new TreeTaggerTokeniser(executablePath);
	}
}
