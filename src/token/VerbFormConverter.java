package token;

/**
 * Converts English verbs between infinitive, 3rd person singular, simple past,
 * and past participle.
 * 
 * @author Nico Schlaefer
 * @version 2007-05-17
 */
public class VerbFormConverter {
	/**
	 * Converts the infinitive of a verb to 3rd person singular.
	 * 
	 * @param verb verb in infinitive
	 * @return 3rd person singular
	 */
	public static String infinitiveToThirdPersonS(String verb) {
		verb = verb.toLowerCase();
		
		if (verb.equals("have"))
			// "have" is irregular.
			return "has";
		else if (verb.equals("be"))
			// "have" is irregular.
			return "is";
		else if (verb.matches(".*(ch|sh|s|x|z|o)"))
			// If the verb ends in "ch", "sh", "s", "x", "z" or "o",
			// append "es".
			return verb + "es";
		else if (verb.matches(".*[bcdfghjklmnpqrstvwxyz]y"))
			// If the verb ends in "y" immediately preceded by a consonant, drop
			// the "y" and append "ies".
			return verb.substring(0, verb.length() - 1) + "ies";
		else
			// If none of the above cases applies, just append "s".
			return verb + "s";
	}
}
