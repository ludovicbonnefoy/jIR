package namedentity;

import util.GetProperties;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class StanfordNamedEntityRecognitionTool extends AbstractNamedEntityRecognitionTool 
{
	public String proceed(String text) 
	{
		//Initialisation du Stanford-NER
		String serializedClassifier = GetProperties.getInstance().getProperty("stanfordPath")+"/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz";
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

		return classifier.classifyWithInlineXML(text.replaceAll("[ ®©»«¬§¤¨¥ª´?]", " ")); //Classification du texte (privé de certains caractères).
	}
}
