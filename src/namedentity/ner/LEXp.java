package namedentity.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import token.Token;
import util.GetProperties;
import util.Log;
import util.Maps;
import util.TreeTagger;

/**
 * Adaptation de l'algoritme LEX pour localiser les entités nommées complexes dans du texte.
 * Deux versions sont proposées par les auteurs : LEX and LEX++.
 * Ici la version implémentée est constituée de LEX et une partie des éléments d'ajouts proposés dans LEX++ (d'où le nom LEX+ (LEXp))
 * L'élément non implémenté de LEX++ est l'attache des suffixes et préfixes tels que décrits dans l'article.
 * @author Ludovic Bonnefoy
 */
public class LEXp extends AbstractNamedEntityRecognitionTool
{
	static private HashMap<String, Double> _ratioBeginOther;

	@SuppressWarnings("unchecked")
	public LEXp() 
	{
		super();

		if(_ratioBeginOther == null)
		{
			File f = new File(GetProperties.getInstance().getProperty("ratioBeginOther"));
			if(!f.exists())
			{
				_ratioBeginOther = new HashMap<String, Double>();
				initRatioBeginOther();

				try 
				{
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GetProperties.getInstance().getProperty("ratioBeginOther"))); //Emplacement de l'objet sérialisé
					oos.writeObject(_ratioBeginOther); 
					oos.flush();
					oos.close();
				}
				catch (java.io.IOException e) {
					Log.getInstance().add(e);
					e.printStackTrace();
				}
			}
			else
			{
				try 
				{
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GetProperties.getInstance().getProperty("ratioBeginOther")));

					_ratioBeginOther = (HashMap<String, Double>)(ois.readObject());
					ois.close();
				}catch(Exception e){
					Log.getInstance().add(e);
					e.printStackTrace();
				}
			}
		}
	}

	private void initRatioBeginOther()
	{
		File bigramDirectory = new File(GetProperties.getInstance().getProperty("Web1TPath")+"/2gms");

		File[] files = bigramDirectory.listFiles(); //chargement de la liste des fichiers composant le corpus
		HashMap<String, Long> occurrenceTot = new HashMap<String, Long>();
		HashMap<String, Long> occurrenceBeginningSentence = new HashMap<String, Long>();

		for(int i=0; i<files.length; ++i) //pour chaque fichier 
		{	
			if(files[i].getName().contains("gz") || !files[i].getName().contains("2gm") || files[i].getName().contains("idx"))
				continue;

			try 
			{
				BufferedReader twoGmsFile = new BufferedReader(new InputStreamReader(new FileInputStream(files[i].getAbsolutePath()),"UTF-8"));
				String line;
				while((line = twoGmsFile.readLine()) != null) //pour chaque token
				{
					String[] tokens = line.substring(0, line.lastIndexOf("\t")).split(" ");
					Long occ = new Long(line.substring(line.lastIndexOf("\t")+1));

					if(tokens[0].equals("<S>"))
						occurrenceBeginningSentence.put(tokens[1], Maps.getLong(occurrenceBeginningSentence, tokens[1]) + occ);

					//compte du nombre d'occurrence total
					occurrenceTot.put(tokens[1], Maps.getLong(occurrenceBeginningSentence, tokens[1]) + occ);
				}

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
		}

		for(String token : occurrenceTot.keySet())
			_ratioBeginOther.put(token, Math.max(new Double(Maps.getLong(occurrenceBeginningSentence, token)), 1.) / occurrenceTot.get(token)); //le 1 = lissage de Laplace
	}

	//problème avec l'espace devant inter mis lorsqu'il est vide
	@Override
	public String proceed(String text) 
	{
		String annotatedString = "";
		String entity="", inter="";//entity est ce que l'on cherche à étendre et inter sont les mots en minuscule que l'on trouve après entity mais avant un autre mot en majuscule et intersize le nombre de mots dans inter
		String numberBefore ="", numberAfter = "";
		Integer entitySize = 0, interSize = 0;
		Double delta = 0.5; //nom des variables dans l'article
		Double theta = 0.000002, gamma = 0.000002 /*, phi =0.5*/; //à régler via un corpus
		Boolean sentenceBeginning = true; //est-on en train d'étudier le premier mot d'une phrase

		TreeTagger treeTagger = new TreeTagger(GetProperties.getInstance().getProperty("treeTaggerPath"));
		ArrayList<Token> tokens = treeTagger.tag(text);

		for(Token token : tokens)
		{
			if(Character.isUpperCase(token.getToken().charAt(0)))// si le mot commence par une majuscule
			{
				if(entity.equals(""))
				{
					if(sentenceBeginning == true)
					{
						Double ratio = (_ratioBeginOther.containsKey(token.getToken()) ? _ratioBeginOther.get(token.getToken()) : 1.); //si le mot n'a jamais été vu alors c'est surement une entité donc score max
						if(ratio.compareTo(delta) > 0)
						{
							annotatedString += token.getToken()+" ";
							sentenceBeginning = false;
							continue;
						}
					}

					entity = token.getToken();
					entitySize = 1;
				}
				else
				{
					Double score = computeSCP(entity, entitySize, inter, interSize, token.getToken(), 1); 

					if(score.compareTo(theta) >= 0)
					{
						if(interSize == 0)
						{
							entity += " "+token.getToken();
							entitySize ++;
						}
						else
						{
							entity += inter + " "+token.getToken();
							entitySize += interSize +1;
						}

						numberAfter = "";
						inter = "";
						interSize = 0;
					}
					else  //on démarre une nouvelle entité et donc il faut enregister la précédente
					{
						if(!numberBefore.equals(""))
						{
							if(computeSCP(numberBefore,1, "", 0, entity, entitySize).compareTo(gamma) >= 0)
								entity = numberBefore +" "+ entity;
							else
								annotatedString += numberBefore +" ";
						}
						
						if(!numberAfter.equals(""))
						{
							if(computeSCP(entity, entitySize, "", 0, numberAfter, 1).compareTo(gamma) >= 0)
							{
								entity = entity + " " + numberAfter;
								if(interSize > 1)
									inter = inter.substring(inter.indexOf(numberAfter)+numberAfter.length()+1);
									
								interSize --;
							}
						}
						
						numberBefore = "";
						numberAfter = "";
						
						annotatedString += "<entity> " + entity + " </entity> ";
						if(interSize > 0)
							annotatedString += inter+" ";

						inter = "";
						interSize = 0;
						entity = token.getToken();
						entitySize = 1;
					}
				}
			}
			else //mot qui ne commence pas par une majuscule 
			{
				if(entity.equals(""))
				{
					if(token.getToken().matches("[0-9]+")) //est ce pertinent? est ce que char(0).isDigit n'est pas mieux?
						numberBefore = token.getToken();
					else
					{
						annotatedString += token.getToken()+" ";
						numberBefore = "";
					}
				}
				else
				{
					if(token.getToken().matches("[0-9]+") && interSize == 0)
						numberAfter = token.getToken();
					
					inter += " "+token.getToken();
					interSize++;
					
					if(interSize > 3) //si il y a déjà trop de mots qui suivent l'entité alors on la clôt
					{
						if(!numberAfter.equals("") && computeSCP(entity, entitySize, "", 0, numberAfter, 1).compareTo(gamma) >= 0)
						{
							entity = entity + " " + numberAfter;
							inter = inter.substring(inter.indexOf(numberAfter)+numberAfter.length());
						}
						
						annotatedString += "<entity> " + entity + " </entity>" + inter + " ";

						numberAfter = "";
						entity = "";
						entitySize = 0;
						inter = "";
						interSize = 0;
					}
				}
			}

			if(token.getPos().equals("SENT")) //ultra basique, à modifier lors de la prise en compte de la ponctuation dans les entités
			{
				sentenceBeginning = true;
				entity="";
				entitySize = 0;
				inter = "";
				interSize = 0;
			}
		}

		return annotatedString;
	}

	public Double computeSCP(String entity, Integer entitySize, String inter, Integer interSize, String token, Integer tokenSize)
	{
		Double denumerateur = 1., numerateur=0.;

		try {
			//Permet de récupérer l'id Indri au docno récupéré dans res
			Runtime runtime = Runtime.getRuntime();
			Process getCountProcess ;

			if(interSize > 0) //related entities dans l'article
			{
				getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"xcount","#1("+token+" "+inter+" "+entity+")"});
				getCountProcess.waitFor();
				String result = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream())).readLine();
				if( new Integer(result.substring(result.lastIndexOf(":")+1)) > 1)
					return 0.;
			}


			getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"stats"});
			getCountProcess.waitFor();

			BufferedReader br = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream()));
			String line;
			Double unigramTot = 1., documentsTot = 1.;
			while((line = br.readLine()) != null)
			{
				if(line.contains("documents"))
					documentsTot =  new Double(line.substring(line.lastIndexOf(":")+1));
				else if(line.contains("total terms"))
					unigramTot =  new Double(line.substring(line.lastIndexOf(":")+1));
			}

			getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"xcount","#1("+entity+")"});
			getCountProcess.waitFor();
			String result = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream())).readLine();
			denumerateur *= Math.max(new Double(result.substring(result.lastIndexOf(":")+1)), 1.) / (unigramTot - (documentsTot* (entitySize-1)));

			Double entityCount = new Double(result.substring(result.lastIndexOf(":")+1));
			System.out.println("entity : "+entity+" " + entityCount);

			getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"xcount","#1("+token+")"});
			getCountProcess.waitFor();
			result = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream())).readLine();
			denumerateur *= Math.max(new Double(result.substring(result.lastIndexOf(":")+1)), 1.) / (unigramTot - (documentsTot* (tokenSize-1)));

			Double tokenCount = new Double(result.substring(result.lastIndexOf(":")+1));
			System.out.println("token : " + token +" "+ tokenCount);

			if(!inter.equals(""))
			{
				getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"xcount","#1("+inter+")"});
				getCountProcess.waitFor();
				result = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream())).readLine();
				denumerateur *= Math.max(new Double(result.substring(result.lastIndexOf(":")+1)), 1.) / (unigramTot - (documentsTot* (interSize-1)));

				Double interCount = new Double(result.substring(result.lastIndexOf(":")+1));
				System.out.println(inter+" " + interCount);

				getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"xcount","#1("+entity+" "+inter+" "+token+")"});
				getCountProcess.waitFor();
				result = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream())).readLine();
				numerateur = Math.pow(Math.max(new Double(result.substring(result.lastIndexOf(":")+1)), 1.) / (unigramTot - (documentsTot* (entitySize+interSize+tokenSize-1))),3);
			}
			else
			{
				getCountProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("dumpIndexPath"),GetProperties.getInstance().getProperty("indexPath"),"xcount","#1("+entity+" "+token+")"});
				getCountProcess.waitFor();
				result = new BufferedReader(new InputStreamReader(getCountProcess.getInputStream())).readLine();
				numerateur = Math.pow(Math.max(new Double(result.substring(result.lastIndexOf(":")+1)), 1) / (unigramTot - (documentsTot* (entitySize+tokenSize-1))),2);

				Double interCount = new Double(result.substring(result.lastIndexOf(":")+1));
				System.out.println(entity+" "+token+" " +interCount);
			}

		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}

		System.out.println(numerateur/denumerateur);
		return (numerateur/denumerateur);
	}

	public static void main(String[] args) {
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");
		LEXp lex = new LEXp();
		System.out.println(lex.proceed("Hello President Obama and Bill Gates on Playstation 3 will call the Monday the 6 for a new minimum PlayStation 3 Reunion tax rate for individuals making more than $1 million a year to ensure that they pay at least the same percentage of their earnings as other taxpayers, according to administration officials."));
		
		StanfordNamedEntityRecognitionTool snert = new StanfordNamedEntityRecognitionTool();
		System.out.println(snert.proceed("Hello President Obama and Bill Gates on Playstation 3 will call the Monday the 6 for a new minimum PlayStation 3 Reunion tax rate for individuals making more than $1 million a year to ensure that they pay at least the same percentage of their earnings as other taxpayers, according to administration officials."));
	}
}
