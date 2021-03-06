package token.tokeniser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import util.Log;

/**
 * Classe abstraite implémentant la création d'un tokeniseur et une ou deux méthodes communes.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractExternalTokeniser implements TokeniserInterface 
{
	/** Chemin complet où l'on va trouver l'éxécutable du tokeniser. */
	protected String _executablePath;
	
	/**
	 * Construit un objet et enregistre le chemin pour trouver l'éxécutable.
	 * @param executablePath Chemin de l'éxécutable.
	 */
	public AbstractExternalTokeniser(String executablePath)
	{
		_executablePath = executablePath;
	}
	
	/**
	 * Tokenise le texte présent dans une chaîne.
	 * @param text Chaîne à tokeniser.
	 */
	public ArrayList<String> tokenise(String text) 
	{
		try {
			PrintWriter pwPassages;
			pwPassages = new PrintWriter(new OutputStreamWriter (new FileOutputStream("/tmp/fileToTokenise"),"ISO-8859-1"));
			pwPassages.print(text);
			pwPassages.flush();
			pwPassages.close();
		} catch (UnsupportedEncodingException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
		
		return tokenise(new File("/tmp/fileToTokenise"));
	}


}
