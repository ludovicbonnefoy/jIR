package namedentity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
