package word;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implémente diverses méthodes de transformation sur des noms.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NounTransformation 
{
	/**
	 * Permet de récupérer la forme au pluriel d'un nom passé en paramètre.
	 * Fonctionne avec des règles très simples et ne permettant pas de traiter des cas particuliers.
	 * @param noun Nom à traiter.
	 * @return Forme au pluriel du nom.
	 */
	public static String pluralForm(String noun)
	{
		ArrayList<String> tokens = new ArrayList<String>();
		Collections.addAll(tokens, noun.split(" ")); //pour des noms "composés" ex : British King

		String nounCore = tokens.get(tokens.size()-1);
		
		if(nounCore.substring(nounCore.length()-1).equals("s") || nounCore.substring(nounCore.length()-1).equals("z"))
			nounCore += "es";
		else if (nounCore.substring(nounCore.length()-1).equals("y"))
			nounCore = nounCore.substring(0, nounCore.length() -1).concat("ies");
		else
			nounCore += "s";
		
		tokens.remove(tokens.size()-1);
		
		String pluralForm = new String();
		
		for(String token : tokens)
			pluralForm += token+" ";
		
		pluralForm += nounCore;

		return pluralForm; 
	}
}
