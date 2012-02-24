package util;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import token.Token;

public class TreeTagger 
{
	private String _executablePath;
	
	public TreeTagger(String executablePath)
	{
		_executablePath = executablePath;
	}

	public ArrayList<Token> tag(String text) 
	{
		try {
			PrintWriter pwPassages;
			pwPassages = new PrintWriter(new OutputStreamWriter (new FileOutputStream("/tmp/fileToTag"),"ISO-8859-1"));
			pwPassages.print(text);
			pwPassages.flush();
			pwPassages.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return tag(new File("/tmp/fileToTag"));
	}
	
	public ArrayList<Token> tag(File file) 
	{
		ArrayList<Token> tokens = new ArrayList<Token>();
		
		try {
	        PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream ("/tmp/treetagger.sh"),"UTF-8"));
			pw.println("cat "+file.getAbsolutePath() +" | "+ _executablePath + "| iconv -f ISO-8859-1 -t UTF-8");
			pw.close();

			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(new String[]{"/bin/sh","/tmp/treetagger.sh"});

			//lecture de la sortie du TreeTagger
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
			String tokenLine = new String();
			
            Pattern p = Pattern.compile("[a-zA-Z0-9]");
			while((tokenLine = brTokens.readLine()) != null) //pour chaque token
			{
				String[] elements = tokenLine.replaceAll("\t\t+","\t").split("\t"); //on récupère les infos
				
                if(elements.length != 3) //est-elle valide?
                    continue; //si non on passe à la ligne suivante

                Matcher m = p.matcher(elements[0]); //on vérifie que il y est au moins une lettre ou chiffre dans le terme (pas juste des caractères spéciaux)
                if(!m.find()) //si on n'en trouve pas on ignore le terme en cours
                    continue;

                tokens.add(new Token(elements[0], elements[1],elements[2]));
			}
			brTokens.close();
			process.destroy();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tokens;
	}
}
