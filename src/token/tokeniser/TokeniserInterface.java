package token.tokeniser;

import java.io.File;
import java.util.ArrayList;

/**
 * Les classes implémentant cette inferface ont pour but de découper un texte en tokens.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
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
