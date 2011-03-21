package util;

import java.util.ArrayList;
import java.util.Collections;

public class NounTransformation 
{
	public static void main(String[] args) 
	{
		System.out.println(pluralForm("black sport shoe"));
	}
	
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
