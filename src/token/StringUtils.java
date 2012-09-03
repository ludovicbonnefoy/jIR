package token;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Propose des méthodes pour tester des chaînes de caractères ou les modifier.
 * @author "Ludovic Bonnefoy (ludovic.bonnefoy@etd.univ-avignon.fr)"
 */
public class StringUtils 
{
	/**
	 * La chaîne str est-elle un acronyme ?
	 * True si toutes les lettres sont en capitale.
	 * @param str Chaîne à tester.
	 * @return true si la chaîne est un acronyme.
	 */
	public static boolean isAcronym(String str)
	{
		for (int i=0;i<str.length();i++){
			char ch= str.charAt(i);
			if (Character.isLowerCase(ch))
				return false;
		}
		return true;
	}
	
	/**
	 * Candidate est elle une version étendue probable d'acronym.
	 * True si chaque lettre du début d'un mot (BW = begin word) est en capitale et correspond à une lettre d'acronym (dans le bon ordre).
	 * @param acronym
	 * @param candidate
	 * @return True si candidate correspond à acronym.
	 */
	public static boolean isAcronymExpansionBW(String acronym, String candidate)
	{
		String candidateParts[] = candidate.split(" ");
		if(candidateParts.length != acronym.length())
			return false;
			
		for(int i = 0; i < candidateParts.length; i++)
			if(!candidateParts[i].startsWith(Character.toString(acronym.toUpperCase().charAt(i))))
					return false;
		
		return true;
	}
	
	/**
	 * Candidate est elle une version étendue probable d'acronym.
	 * True si chaque lettre en capitale (C) dans candidate correspond à une lettre d'acronym (dans le bon ordre).
	 * @param acronym
	 * @param candidate
	 * @return True si candidate correspond à acronym.
	 */
	public static boolean isAcronymExpansionC(String acronym, String candidate)
	{
		if(numberOfCapitalizedChar(candidate) != acronym.length())
			return false;
		
		Matcher m = Pattern.compile("[A-Z]").matcher(candidate);
		int i = 0;
		while(m.find())
		{
			System.out.println(candidate.substring(m.start(), m.start()+1));
			if(! Character.toString(acronym.toUpperCase().charAt(i)).equals(candidate.substring(m.start(), m.start()+1)))
				return false;
			i++;
		}

		return true;
	}
	
	/**
	 * Compte le nombre de lettres en majuscules dans str.
	 * @param str
	 * @return Nombre de lettres en majuscules.
	 */
	public static Integer numberOfCapitalizedChar(String str)
	{
		Matcher mat = Pattern.compile("[A-Z]").matcher(str);
		int i = 0;
		while(mat.find())
			i++;

		return i;
	}
		
	/**
	 * Passe en minuscule tout les éléments d'une liste.
	 * @param list Liste à passer en minuscule.
	 * @return Liste dont tous les éléments sont en minuscule.
	 */
	public static ArrayList<String> lowerCasedList(ArrayList<String> list)
	{
		ArrayList<String> lowerCased = new ArrayList<String>();  //mise en minuscule pour que la casse ne rentre pas en compte la casse
		for(String element : list)
			lowerCased.add(element.toLowerCase().trim());
		
		return lowerCased;
	}
	
	/**
	 * Passe en majuscule tout les éléments d'une liste.
	 * @param list Liste à passer en majuscule.
	 * @return Liste dont tous les éléments sont en majuscule.
	 */
	public static ArrayList<String> upperCasedList(ArrayList<String> list)
	{
		ArrayList<String> upperCased = new ArrayList<String>();  //mise en majuscule pour que la casse ne rentre pas en compte la casse
		for(String element : list)
			upperCased.add(element.toUpperCase().trim());
		
		return upperCased;
	}
}
