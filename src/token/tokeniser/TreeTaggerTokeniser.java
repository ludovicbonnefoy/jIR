package token.tokeniser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import util.Log;

/**
 * Utilisation du TreeTagger pour tokeniser des textes.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class TreeTaggerTokeniser extends AbstractExternalTokeniser 
{
	/**
	 * Création d'un tokeniseur utilisant le TreeTagger.
	 * @param executablePath Chemin où trouver le TreeTagger.
	 */
	public TreeTaggerTokeniser(String executablePath)
	{
		super(executablePath);
	}

	/**
	 * Tokenise le texte présent dans un fichier.
	 * @param file Fichier à tokeniser.
	 */
	public ArrayList<String> tokenise(File file) 
	{
		ArrayList<String> tokens = new ArrayList<String>();

		try {
	        PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream ("/tmp/treetagger.sh"),"UTF-8"));
			pw.println("cat "+file.getAbsolutePath() +" | "+ _executablePath + "| iconv -f ISO-8859-1 -t UTF-8");
			pw.close();

			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(new String[]{"/bin/sh","/tmp/treetagger.sh"});

			//lecture de la sortie du TreeTagger
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
			String tokenLine = new String();

			while((tokenLine = brTokens.readLine()) != null) //pour chaque token
			{
				String[] elements = tokenLine.replaceAll("\t\t+","\t").split("\t"); //on récupère les infos
				tokens.add(elements[0]);
			}
			brTokens.close();
			process.destroy();
		} catch (UnsupportedEncodingException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
		
		return tokens;
	}
}
