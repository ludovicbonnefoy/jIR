package namedentity.ner;

import util.GetProperties;
import edu.stanford.nlp.ie.crf.CRFClassifier;

/**
 * Utilisation de l'outil du Stanford pour effectuer la reconnaissance d'entités nommées dans du texte.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class StanfordNamedEntityRecognitionTool extends AbstractNamedEntityRecognitionTool 
{
	public StanfordNamedEntityRecognitionTool() 
	{
	}
	
	public String proceed(String text) 
	{
		String serializedClassifier = GetProperties.getInstance().getProperty("stanfordClassifierPath");
		@SuppressWarnings("rawtypes")
		CRFClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

		return classifier.classifyWithInlineXML(text.replaceAll("[ ®©»«¬§¤¨¥ª´]", " ").replaceAll("?", ".")); //Classification du texte (privé de certains caractères).
	}
}
