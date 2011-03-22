package probabilitydistribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import util.GetProperties;
import util.TreeTagger;
import web.util.html.HTML2Text;

/**
 * Propose des calculs de ngrammes sur divers type de corpus.
 * Sur le ClueWeb, des pages webs, un fichier texte, ou d'une chaîne,  
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NGramsProbabilityDistribution extends ProbabilityDistribution
{
	private static final long serialVersionUID = -4539352062797597509L;

	/**
	 * Calcul la proba d'apparition des n-grammes sur un fichier.
	 * @param filePath Fichier où compter les n-grams
	 * @param n La taille des n-grammes
	 */
	public void fromFile(File file, Integer n)
	{
		_freqs = new HashMap<String, Long>();        
		
		TreeTagger treeTagger = new TreeTagger(GetProperties.getInstance().getProperty("treeTaggerPath"));
		ArrayList<ArrayList<String>> tokensTag = treeTagger.tag(file);

		ArrayList<String> ngramList = new ArrayList<String>();
		
		for(ArrayList<String> tokenTag : tokensTag)
		{
			String word = tokenTag.get(0).toLowerCase(); //passage en minuscule

			if(tokenTag.get(1).equals("CD")) //si on a un chiffre  
				word = "@card@"; //on remplace la valeur par @card@, on ne prend pas en compte la valeur

			if(!tokenTag.get(2).equals("<unknown>")) //si on a le lemme
				word = tokenTag.get(2).toLowerCase(); //on prend le lemme

			//on met à jour le n-grammes
			ngramList.remove(0); //pour cela on supprime le premier élément
			ngramList.add(word); //et on rajoute ce terme à la fin

			if(!ngramList.contains("")) //permet de vérifier que l'on est bien un n-gramme complet (pour les premiers cas)
			{
				String ngram = new String(); //on va mettre "à plat" le n-gramme

				for(String ngramWord : ngramList) //pour chaque élément du n-gramme
					ngram += ngramWord+" "; //on concatène le terme

				ngram = ngram.trim(); //on supprime l'espace superflu

				Long count = _freqs.containsKey(ngram) ? _freqs.get(ngram) : 0; //on récupère le compte précédent de ce n-gramme
				
				_freqs.put(ngram, count + 1);  //on lui ajoute 1
				_total++;
				
			}
		}
	}


	/** 
	 * Calcul la proba des n-grammes sur une chaîne.
	 * @param text La chaîne à analyser
	 * @param n La taille des n-grammes
	 */
	public void fromString(String text, Integer n)
	{
		try
		{
			//Ecrit le texte dans un fichier
			PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream ("/tmp/NgramsFromString"),"ISO-8859-1"));
			pw.print(text);
			pw.flush();
			pw.close();

		}catch(Exception e){
			System.err.println(e);
		}

		fromFile(new File("/tmp/NgramsFromString"), n);
	}

	/**
	 * Calcul des probas des n-grammes dans un corpus composé de pages web.
	 * @param corpusDirectory Le corpus
	 * @param n La taille des n-grammes
	 */
	public void fromWebCorpus(File corpusDirectory, Integer n)
	{
		try
		{
			if(corpusDirectory.exists())  //vérifie que le dossier du corpus existe bien
			{ 
				File[] files = corpusDirectory.listFiles(); //chargement de la liste des fichiers composant le corpus

				PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream ("/tmp/NgramsFromCorpus"),"ISO-8859-1")); //fichier qui va contenir l'ensemble du texte "propre" du corpus

				for(int i=0; i<files.length; ++i) //pour chaque fichier 
				{
					String text = util.FileReader.fileToString(files[i]);
					pw.print(text); //écriture dans le fichier
					pw.flush();
				}

				pw.close(); //fermeture du flux vers le fichier
			}
		}catch(Exception e){
			System.err.println(e);
		}

		fromFile(new File("/tmp/NgramsFromCorpus"), n);
	}	
	
	/**
	 * Calcul des probas des n-grammes dans un corpus de documents.
	 * @param corpusDirectory Le corpus
	 * @param n La taille des n-grammes
	 */
	public void fromCorpus(File corpusDirectory, Integer n)
	{
		try
		{
			if(corpusDirectory.exists())  //vérifie que le dossier du corpus existe bien
			{ 
				File[] files = corpusDirectory.listFiles(); //chargement de la liste des fichiers composant le corpus

				PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream ("/tmp/NgramsFromWebCorpus"),"ISO-8859-1")); //fichier qui va contenir l'ensemble du texte "propre" du corpus

				for(int i=0; i<files.length; ++i) //pour chaque fichier 
				{
					HTML2Text parser = new HTML2Text(); //va permettre de nettoyer le texte des balises html
					try{
						parser.parse(new FileReader(files[i])); //suppression des balises
						String text = parser.getCleanText(); //on a ici le texte du document web, sans les balises

						pw.print(text); //écriture dans le fichier
					}catch (Exception e) {}
					catch (StackOverflowError sofe) {}

					pw.flush();
				}

				pw.close(); //fermeture du flux vers le fichier
			}
		}catch(Exception e){
			System.err.println(e);
		}

		fromFile(new File("/tmp/NgramsFromWebCorpus"), n);
	}	
	
	/**
	 * Calcul des probas des n-grammes présents dans un fichier warc.
	 * Ce genre de fichier est composé de pages web mais contient aussi des informations inutiles ici.
	 * Cette fonction gère des fichiers warc gzippé uniquement.
	 * @param n La taille des n-grammes.
	 * @param warcFile Le fichier à traiter.
	 * @return Les n-grammes: map[n-gramme] = frequence
	 */
	public void fromWarc(File warcFile, Integer n)
	{
		try{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream ("/tmp/NgramsFromWarc"),"ISO-8859-1")); //écriture du contenu des documents dans un fichier

			BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(warcFile)),"UTF-8")); //lecture du fichier warc
			String line;

			String webPage = new String(); //le contenu d'une page web du fichier warc
			Integer compt = -3; //permet de déterminer quand le contenu d'une page commence (pour passe les entêtes du fichier warc)

			while(( line= br.readLine()) != null)
			{
				if(line.equals("WARC/0.18")) //nouvelle page web
				{
					compt = -3; //réinitialisation du compteur

					if(webPage.equals("")) //si on est dans le premier cas
						continue; //il n'y a encore rien à faire

					//Sinon il faut nettoyer le fichier et ensuite le rajouter dans un fichier texte
					HTML2Text parser = new HTML2Text();
					try{
						parser.parse(new StringReader(webPage)); //suppression des balises html
						pw.println(parser.getCleanText()); //écriture du texte dans un fichier
					}catch (Exception e) {}
					catch (StackOverflowError sofe) {}

					webPage = new String();
				}

				else if(compt >= 0) //on est dans la partie texte
					webPage += line+"\n";

				else if(compt >= -2) //attends une ligne en particulier
				{
					if(line.contains("Content-Length"))
						++compt;
				}

				else if(line.contains("WARC-TREC-ID")) //attends cette ligne
					++compt;

			}

			pw.close();
			br.close();

		}catch (Exception e) {
			System.err.println(e.getMessage());
		}

		fromFile(new File("/tmp/NgramsFromWarc"), n);  //retourne le résultat de la fonction qui gère les fichier
	}
}
