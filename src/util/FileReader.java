package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Cette classe propose différentes fonctions permettant de récupérer du contenu dans des documents.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class FileReader 
{
	/** 
	 * Récupère l'intégralité du texte présent dans le fichier passé en paramètre.
	 * @param file Fichier dans lequel récupéré le texte.
	 * @return Texte présent dans le fichier.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String fileToString(File file) throws IOException, FileNotFoundException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		StringBuffer result = new StringBuffer();
		
		while ((line = br.readLine()) != null)
			result.append(line);

		br.close();
		
		return result.toString();
	}
}
