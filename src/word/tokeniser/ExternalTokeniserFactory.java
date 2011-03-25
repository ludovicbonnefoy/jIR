package word.tokeniser;

import util.GetProperties;

/**
 * Classe ayant pour but de fournir un tokeniseur de texte. 
 * Elle permet de ne pas à avoir à modifier le code en profondeur lorsque l'on souhaite modifier le tokeniseur utilisé.
 * L'outil à utiliser est recherché dans un fichier properties. Dans le cas où rien n'est trouvé, un outil par défaut est créé.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
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
