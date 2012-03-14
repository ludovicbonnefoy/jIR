package namedentity.ner;

import util.GetProperties;

/**
 * Classe ayant pour but de fournir un outil de reconnaissance d'entités nommées. 
 * Elle permet de ne pas à avoir à modifier le code en profondeur lorsque l'on souhaite modifier l'outil utilisé.
 * L'outil a utiliser est recherché dans un fichier properties. Dans le cas où rien n'est trouvé, un outil par défaut est initialisé.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NamedEntityRecognitionToolFactory 
{
	public static AbstractNamedEntityRecognitionTool get()
	{
		if(GetProperties.getInstance().containsKey("namedEntityRecognitionTool"))
		{
			String type = GetProperties.getInstance().getProperty("namedEntityRecognitionTool");
			
			if(type.equals("stanfordNER"))
			{
				return new StanfordNamedEntityRecognitionTool();
			}
		}
		
		return new StanfordNamedEntityRecognitionTool();
	}
}
