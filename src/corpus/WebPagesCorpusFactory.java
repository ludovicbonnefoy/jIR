package corpus;

import java.io.File;
import java.io.FileNotFoundException;

import util.GetProperties;

/**
 * Classe ayant pour but de fournir un corpus. 
 * Elle permet de ne pas à avoir à modifier le code en profondeur lorsque l'on souhaite modifier le type de corpus.
 * Le type corpus a utiliser est recherché dans un fichier properties. Dans le cas où rien n'est trouvé, un corpus d'un type par défaut est créé.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
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
