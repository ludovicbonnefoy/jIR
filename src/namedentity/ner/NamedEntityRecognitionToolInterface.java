package namedentity.ner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Les classes implémentant cette inferface ont pour but de trouver les entités nommées présentent dans un texte.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public interface NamedEntityRecognitionToolInterface 
{
	/**
	 * Reconnaissance d'entité nommées sur la chaine text
	 * @param text Chaîne sur laquelle la reco va être effectuée
	 */
	public String proceed(String text);
	
	/**
	 * Reconnaissance d'entité nommées sur le fichier file
	 * @param file Fichier sur laquelle la reco va être effectuée
	 */
	public String proceed(File file) throws IOException, FileNotFoundException;
}
