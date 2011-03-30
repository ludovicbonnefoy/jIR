package namedentity;

import util.GetProperties;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

/**
 * Utilisation de l'outil du Stanford pour effectuer la reconnaissance d'entités nommées dans du texte.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class StanfordNamedEntityRecognitionTool extends AbstractNamedEntityRecognitionTool 
{
	public String proceed(String text) 
	{
		String serializedClassifier = GetProperties.getInstance().getProperty("stanfordClassifierPath");
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

		return classifier.classifyWithInlineXML(text.replaceAll("[ ®©»«¬§¤¨¥ª´?]", " ")); //Classification du texte (privé de certains caractères).
	}
}
