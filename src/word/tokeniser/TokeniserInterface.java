package word.tokeniser;

import java.io.File;
import java.util.ArrayList;

public interface TokeniserInterface 
{
	/**
	 * Tokenise le texte passé en paramètre
	 * @param text Texte à tokeniser
	 * @return Le texte tokenisé, un token par ligne
	 */
	public ArrayList<String> tokenise(String text);
	
	/**
	 * Tokenise le texte dans le fichier passé en paramètre
	 * @param file Fichier dans lequel est le texte à tokeniser
	 * @return Le texte tokenisé, un token par ligne
	 */
	public ArrayList<String> tokenise(File file);

}
