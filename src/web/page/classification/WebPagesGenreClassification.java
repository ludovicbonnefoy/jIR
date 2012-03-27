package web.page.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import util.GetProperties;
import util.Log;
import weka.WekaAttribute;
import weka.WekaLearner;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Détection du "genre" d'une page web.
 * Exemples de genres : "homepage", "shopping", "faq", ect.
 * Entraîne un classifieur SVM (SMO) pour cette tâche avec diverses features.
 * Cet entrainement est fait sur des corpus dont les chemins sont à fournir.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class WebPagesGenreClassification implements java.io.Serializable
{
	private static final long serialVersionUID = -249524828796629840L;

	/** Feature Texte : compte des mots dans l'ensemble des documents par genre : map[genre][mot] = fréquence. 
	 * Tout les mots sont conservés car importants (d'après ...).*/
	HashMap<String,HashMap<String,Integer>>  _globalWordsCount;

	/** Feature Texte: compte des POS (Part-of-Speech) dans l'ensemble des documents par genre : map[genre][POS] = fréquence.
	 * Un POS représente la fonction d'un terme (verbe, nom, adjéctif, ...). */
	HashMap<String,HashMap<String,Integer>>  _globalPOSCount;

	/** Feature Structure : compte des balises HTML dans l'ensemble des documents par genre : map[genre][balise] = fréquence. */
	HashMap<String,HashMap<String,Integer>>  _globalTagsCount;

	/** Features diverses : compte de nombreux paramètres différents pour chacun des documents par genre : map[genre][feature] = valeur
	 * (Taille du document en mots, nombre de paragraphes, etc.). */
	HashMap<String,HashMap<String,Double>>  _globalFeaturesCount;

	/** Objet Weka */
	WekaLearner _wL;

	public WebPagesGenreClassification() {
		_globalWordsCount = new HashMap<String, HashMap<String,Integer>>();
		_globalPOSCount = new HashMap<String, HashMap<String,Integer>>();
		_globalTagsCount = new HashMap<String, HashMap<String,Integer>>();
		_globalFeaturesCount = new HashMap<String, HashMap<String,Double>>();

		_wL = null;
	}
	
	/**
	 * Permet de récupérer le jeu d'instance d'entrainement.
	 * @return Instances d'entrainement
	 */
	public Instances getTrainingSet()
	{
		return _wL.GetTrainingSet();
	}
	

	/**Entrainement du classifieur SVM sur un corpus.
	 * Une map est passée en paramètre. La clé correspond à un des genres que l'on veut que le classifieur reconnaisse et la valeur correspondante est le chemin du corpus où les exemples pour ce genre se trouvent
	 * @param genrePaths Couple genre / chemin du corpus pour le genre
	 */
	public void train(HashMap<String, String> genrePaths)
	{
		//première phase de récupération des informations
		ProcessFeatures gtf = new ProcessFeatures(); //instance chargée de récupérer les informations d'un document

		ArrayList<String> genres = new ArrayList<String>(genrePaths.size());

		for(String genre : genrePaths.keySet()) //pour chaque genre
		{
			File corpusDirectory = new File(genrePaths.get(genre)); //Création d'un accesseur au dossier contenant le corpus
			if(!corpusDirectory.exists()) //si il y a une erreur dans le chemin
				continue; //on ignore ce genre

			genres.add(genre); //on enregistre la classe

			File[] files = corpusDirectory.listFiles(); //liste des fichiers du corpus 
			for(int i=0; i<files.length; i++) //pour chaque fichier
			{
				gtf.processFeatures(files[i]); //extraction des features sur le document

				//gestion des features diverses
				HashMap<String,Double> featuresCount = gtf.getFeaturesCount(); //récupération du compte des features "diverses"

				for(String feature : featuresCount.keySet()) //pour chaque feature calculée
				{
					if(_globalFeaturesCount.containsKey(feature)) //si on a déjà une clé pour cet élément
						_globalFeaturesCount.get(feature).put(files[i].toString(),featuresCount.get(feature)); //on rentre la valeur pour ce fichier
					else
					{
						HashMap<String,Double> tmp = new HashMap<String,Double>();
						tmp.put(files[i].toString(),featuresCount.get(feature)); //valeur pour le fichier

						_globalFeaturesCount.put(feature,tmp); //création de la valeur pour l'élément
					}
				}

				//gestion des tags
				HashMap<String,Integer> tagsCount = gtf.getTagsCount();  //récupération du compte des balises HTML

				for(String tag : tagsCount.keySet()) //pour chaque balise
				{
					if(_globalTagsCount.containsKey(tag)) //si on a déjà une clé pour cet élément
						_globalTagsCount.get(tag).put(files[i].toString(),tagsCount.get(tag)); //on rentre la valeur pour ce fichier
					else
					{
						HashMap<String,Integer> tmp = new HashMap<String,Integer>();
						tmp.put(files[i].toString(),tagsCount.get(tag)); //valeur pour le fichier

						_globalTagsCount.put(tag,tmp); //création de la valeur pour l'élément
					}
				}


				//gestion des mots
				HashMap<String,Integer> wordsCount = gtf.getWordsCount(); //récupération du compte des mots

				for(String word : wordsCount.keySet()) //pour chaque mot
				{
					if(_globalWordsCount.containsKey(word)) //si on a déjà une clé pour cet élément
						_globalWordsCount.get(word).put(files[i].toString(),wordsCount.get(word)); //on rentre la valeur pour ce fichier
					else
					{
						HashMap<String,Integer> tmp = new HashMap<String,Integer>();
						tmp.put(files[i].toString(),wordsCount.get(word)); //valeur pour le fichier

						_globalWordsCount.put(word,tmp); //création de la valeur pour l'élément
					}
				}

				//gestion des pos
				HashMap<String,Integer> posCount = gtf.getPOSCount(); //récupération du compte des POS

				for(String pos : posCount.keySet()) //pour chaque POS
				{
					if(_globalPOSCount.containsKey(pos)) //si on a déjà une clé pour cet élément
						_globalPOSCount.get(pos).put(files[i].toString(),posCount.get(pos)); //on rentre la valeur pour ce fichier
					else
					{
						HashMap<String,Integer> tmp = new HashMap<String,Integer>();
						tmp.put(files[i].toString(),posCount.get(pos)); //valeur pour le fichier

						_globalPOSCount.put(pos,tmp); //création de la valeur pour l'élément
					}
				}
			}
		}

		//création des attributs
		FastVector vals = new FastVector(19); //valeur possibles pour les rangs
		vals.addElement("0");
		vals.addElement("1");
		vals.addElement("2");
		vals.addElement("3");
		vals.addElement("4");
		vals.addElement("5");
		vals.addElement("10");
		vals.addElement("15");
		vals.addElement("20");
		vals.addElement("25");
		vals.addElement("30");
		vals.addElement("40");
		vals.addElement("50");
		vals.addElement("75");
		vals.addElement("100");
		vals.addElement("200");
		vals.addElement("300");
		vals.addElement("500");
		vals.addElement("1000");
		
		
		int nbrFeatures = _globalFeaturesCount.size() + _globalTagsCount.size() + _globalWordsCount.size() + _globalPOSCount.size();

		WekaAttribute[] pi_Attributes = new WekaAttribute[nbrFeatures];
		
		int numFeature = 0;

		for(String featureAttribute : _globalFeaturesCount.keySet()) //pour chaque feature
		{
			pi_Attributes[numFeature] = new WekaAttribute("f_" + featureAttribute); //on créer un attribut ayant ce nom
			++numFeature;
		}

		for(String tagAttribute : _globalTagsCount.keySet()) //pour chaque balise HTML
		{
			pi_Attributes[numFeature] = new WekaAttribute("t_" + tagAttribute, vals); //on créer un attribut
			++numFeature;
		}

		for(String wordAttribute : _globalWordsCount.keySet()) //pout chaque mot
		{
			/*if(_globalWordsCount.get(wordAttribute).size() == 1)
			{
				for(String file : _globalWordsCount.get(wordAttribute).keySet())
					if(_globalWordsCount.get(wordAttribute).get(file) == 1)
						continue;
			}*/
			pi_Attributes[numFeature] = new WekaAttribute("w_" + wordAttribute, vals); //on créer un attribut
			++numFeature;
		}

		for(String posAttribute : _globalPOSCount.keySet()) //pour chaque POS
		{
			pi_Attributes[numFeature] = new WekaAttribute("p_" + posAttribute, vals); //on créer un attribut
			++numFeature;
		}

		String[] pi_ClassAttributes = new String[genres.size()];

		for(int numGenre = 0; numGenre < genres.size(); ++numGenre)
			pi_ClassAttributes[numGenre] = genres.get(numGenre);

		_wL = new WekaLearner(pi_Attributes, pi_ClassAttributes);

		_wL.GetTrainingSet().setRelationName("Genre");


		//créations des instances
		for(String genre : genrePaths.keySet()) //pour chaque genre
		{
			File corpusDirectory = new File(genrePaths.get(genre)); //Création d'un accesseur au dossier contenant le corpus
			if(!corpusDirectory.exists()) //si il y a une erreur dans le chemin
				continue; //on ignore ce genre

			File[] files = corpusDirectory.listFiles(); //liste des fichiers du corpus 
			for(int i=0; i<files.length; i++) //pour chaque fichier
			{
				ArrayList<Object> pi_Instance = new ArrayList<Object>();

				//Faire bien attention à traiter les données dans le même ordre que lors de la définition

				//features diverses
				for(String featureAttribute : _globalFeaturesCount.keySet()) //pour chaque feature
				{
					if(_globalFeaturesCount.get(featureAttribute).containsKey(files[i].toString())) //si on a une valeur
						pi_Instance.add(_globalFeaturesCount.get(featureAttribute).get(files[i].toString())); //on note
					else
						pi_Instance.add(0); //sinon on met zéro
				}

				//tags count
				for(String tagAttribute : _globalTagsCount.keySet()) //pour chaque balise html
				{
					if(_globalTagsCount.get(tagAttribute).containsKey(files[i].toString())) //si on a une valeur
					{
						Double val = new Double(_globalTagsCount.get(tagAttribute).get(files[i].toString())); //récupération de la valeur pour cette balise
						
						if(val.equals(1.))
							pi_Instance.add("1");
						else if(val.equals(2.))
							pi_Instance.add("2");
						else if(val.equals(3.))
							pi_Instance.add("3");
						else if(val.equals(4.))
							pi_Instance.add("4");
						else if(val.equals(5.))
							pi_Instance.add("5");
						else if(val.compareTo(500.) > 0)
							pi_Instance.add("1000");
						else if(val.compareTo(300.) > 0)
							pi_Instance.add("500");
						else if(val.compareTo(200.) > 0)
							pi_Instance.add("300");
						else if(val.compareTo(100.) > 0)
							pi_Instance.add("200");
						else if(val.compareTo(75.) > 0)
							pi_Instance.add("100");
						else if(val.compareTo(50.) > 0)
							pi_Instance.add("75");
						else if(val.compareTo(40.) > 0)
							pi_Instance.add("50");
						else if(val.compareTo(30.) > 0)
							pi_Instance.add("40");
						else if(val.compareTo(25.) > 0)
							pi_Instance.add("30");
						else if(val.compareTo(20.) > 0)
							pi_Instance.add("25");
						else if(val.compareTo(15.) > 0)
							pi_Instance.add("20");
						else if(val.compareTo(10.) > 0)
							pi_Instance.add("15");
						else
							pi_Instance.add("10");
					}
					else
						pi_Instance.add("0"); //sinon on met zéro
				}

				//words count
				for(String wordAttribute : _globalWordsCount.keySet()) //pour chaque mot
				{
					Integer val =_globalWordsCount.get(wordAttribute).get(files[i].toString()); //récupération de la valeur pour ce mot

					if(val != null) //si on a une valeur
					{
						if(val.equals(1))
							pi_Instance.add("1");
						else if(val.equals(2))
							pi_Instance.add("2");
						else if(val.equals(3))
							pi_Instance.add("3");
						else if(val.equals(4))
							pi_Instance.add("4");
						else if(val.equals(5))
							pi_Instance.add("5");
						else if(val.compareTo(500) > 0)
							pi_Instance.add("1000");
						else if(val.compareTo(300) > 0)
							pi_Instance.add("500");
						else if(val.compareTo(200) > 0)
							pi_Instance.add("300");
						else if(val.compareTo(100) > 0)
							pi_Instance.add("200");
						else if(val.compareTo(75) > 0)
							pi_Instance.add("100");
						else if(val.compareTo(50) > 0)
							pi_Instance.add("75");
						else if(val.compareTo(40) > 0)
							pi_Instance.add("50");
						else if(val.compareTo(30) > 0)
							pi_Instance.add("40");
						else if(val.compareTo(25) > 0)
							pi_Instance.add("30");
						else if(val.compareTo(20) > 0)
							pi_Instance.add("25");
						else if(val.compareTo(15) > 0)
							pi_Instance.add("20");
						else if(val.compareTo(10) > 0)
							pi_Instance.add("15");
						else 
							pi_Instance.add("10");
					}
					else
						pi_Instance.add("0"); //sinon on met zéro
				}

				//pos count
				for(String posAttribute : _globalPOSCount.keySet()) //pour chaque POS
				{
					if(_globalPOSCount.get(posAttribute).containsKey(files[i].toString())) //si on a une valeur
					{
						Double val = new Double(_globalPOSCount.get(posAttribute).get(files[i].toString())); //récupération de la valeur pour cette balise
						if(val.equals(1.))
							pi_Instance.add("1");
						else if(val.equals(2.))
							pi_Instance.add("2");
						else if(val.equals(3.))
							pi_Instance.add("3");
						else if(val.equals(4.))
							pi_Instance.add("4");
						else if(val.equals(5.))
							pi_Instance.add("5");
						else if(val.compareTo(500.) > 0)
							pi_Instance.add("1000");
						else if(val.compareTo(300.) > 0)
							pi_Instance.add("500");
						else if(val.compareTo(200.) > 0)
							pi_Instance.add("300");
						else if(val.compareTo(100.) > 0)
							pi_Instance.add("200");
						else if(val.compareTo(75.) > 0)
							pi_Instance.add("100");
						else if(val.compareTo(50.) > 0)
							pi_Instance.add("75");
						else if(val.compareTo(40.) > 0)
							pi_Instance.add("50");
						else if(val.compareTo(30.) > 0)
							pi_Instance.add("40");
						else if(val.compareTo(25.) > 0)
							pi_Instance.add("30");
						else if(val.compareTo(20.) > 0)
							pi_Instance.add("25");
						else if(val.compareTo(15.) > 0)
							pi_Instance.add("20");
						else if(val.compareTo(10.) > 0)
							pi_Instance.add("15");
						else
							pi_Instance.add("10");
					}
					else
						pi_Instance.add("0"); //sinon on met zéro
					
				}
				_wL.AddTrainInstance(pi_Instance.toArray(), genre); //on créér l'instance correspondante à ce fichier
			}
		}

		_globalFeaturesCount = null;
		_globalPOSCount = null;
		_globalTagsCount = null;
		_globalWordsCount = null;
		

		
		_wL.CreateModel(new SMO()); //entrainement du SVM avec les données récupérées
	}

	/**
	 * Cette méthode effectue une validation croisée avec nbrFolds Folds.
	 * Elle retourne les informations de l'évaluation sous forme de texte.
	 * @param nbrFolds Nombre de Folds pour l'éval
	 * @return Résultats
	 */
	public String crossValidation(Integer nbrFolds)
	{
		if(_wL == null)
			return null;

		Evaluation eval = _wL.EstimateConfidence();
		return eval.toSummaryString("",false);
	}
	
	/**
	 * Classifie une page dont l'url est donnée en paramètre.
	 * @param url Url de la page
	 * @return La classe
	 */
	public String process(String url)
	{
        try{
        	Runtime.getRuntime().exec(new String[]{GetProperties.getInstance().getProperty("wget"),"-O",GetProperties.getInstance().getProperty("tmpDirectory")+"/page","-T","5","-t","2",url}).waitFor(); //récupération de la page web
        	return process(new File(GetProperties.getInstance().getProperty("tmpDirectory") + "/page")); //process
        }catch(Exception e){
			Log.getInstance().add(e);
			e.printStackTrace();
        }
        
        return null;
	}

	/**
	 * Classifie un fichier.
	 * @param file fichier
	 * @return Résultat de la classification
	 */
	public String process(File file)
	{
		if(_wL == null)
			return null;

		ArrayList<Object> pi_Instance = new ArrayList<Object>(_wL.GetTrainingSet().numAttributes() - 1); //Création d'une instance

		ProcessFeatures gtf = new ProcessFeatures(); //instance chargée de récupérer les informations d'un document
		gtf.processFeatures(file); //extraction des features sur le document

		HashMap<String,Double> featuresCount = gtf.getFeaturesCount(); //récupération du compte des features "diverses"
		HashMap<String,Integer> tagsCount = gtf.getTagsCount();  //récupération du compte des balises HTML
		HashMap<String,Integer> wordsCount = gtf.getWordsCount(); //récupération du compte des mots
		HashMap<String,Integer> posCount = gtf.getPOSCount(); //récupération du compte des POS

		for(int i = 0; i < _wL.GetTrainingSet().numAttributes() - 1; ++i)
		{
			String nameAttribut = _wL.GetTrainingSet().attribute(i).name();
			String name = nameAttribut.substring(2);
			
			if(nameAttribut.startsWith("f_")) //si on a un attribut quelconque
			{
				Double val = (Double)(featuresCount.get(name)); //récupération de la valeur pour ce mot

				if(val != null) //si on a une valeur
					pi_Instance.add(val); //on note
				else
					pi_Instance.add(0.); //sinon on met zéro
			}
			else if(nameAttribut.startsWith("t_")) //si on a un attribut sur des tags
			{
				Integer val = tagsCount.get(name); //récupération de la valeur pour ce mot

				if(val != null) //si on a une valeur
					pi_Instance.add(new Double(val)); //on note
				else
					pi_Instance.add(0.); //sinon on met zéro
			}
			else if(nameAttribut.startsWith("w_")) //si on a un attribut sur des mots
			{
				Integer val = wordsCount.get(name); //récupération de la valeur pour ce mot

				if(val != null) //si on a une valeur
					pi_Instance.add(new Double(val)); //on note
				else
					pi_Instance.add(0.); //sinon on met zéro
			}
			else if(nameAttribut.startsWith("p_")) //si on a un attribut sur des POS
			{
				Integer val = posCount.get(name); //récupération de la valeur pour ce mot

				if(val != null) //si on a une valeur
					pi_Instance.add(new Double(val)); //on note
				else
					pi_Instance.add(0.); //sinon on met zéro
			}
		}
		
		return _wL.GetTrainingSet().classAttribute().value((int)_wL.Classify(pi_Instance.toArray()));
	}
	
	
	public static void main(String[] args) 
	{
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");

		WebPagesGenreClassification wpgc = new WebPagesGenreClassification();
		
		File f = new File(properties.getProperty("webPagesGenreClassifier"));
		if(!f.exists())
		{
			File corpusDirectory = new File(args[0]);
			HashMap<String, String> genrePaths = new HashMap<String, String>();
	
			if(corpusDirectory.exists())
			{
				File[] genres = corpusDirectory.listFiles();
				for(int j=0; j<genres.length; j++)
				{
					if (!genres[j].isDirectory())
						continue;
	
					String genre = genres[j].toString().substring(genres[j].toString().lastIndexOf('/')+1);
					genrePaths.put(genre, genres[j].toString());
				}
			}
	
	
			wpgc = new WebPagesGenreClassification();
			wpgc.train(genrePaths);
			System.err.println("done");
		
		
			try 
	    	{
	    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(properties.getProperty("webPagesGenreClassifier"))); //Emplacement de l'objet sérialisé
	    		oos.writeObject(wpgc); //enregistrement
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
				System.err.println("Chargement classifieur");
	    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(properties.getProperty("webPagesGenreClassifier")));
	    		
	    		wpgc = (WebPagesGenreClassification)(ois.readObject());
	    	}catch(Exception e){
				Log.getInstance().add(e);
				e.printStackTrace();
	    	}
		}
		
    	//Ecriture du fichier Arff
		/*try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(GetProperties.getInstance().getProperty("tmpDirectory") + "/genre.arff")); //Écriture des résultats
			pw.println(wpgc.getTrainingSet());
			pw.close();
		}catch (Exception e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}*/
    	
		//System.err.println("Validation");
    	//System.out.println(wpgc.crossValidation(10));
    	System.out.println(wpgc.process("http://www.martinbrundle.com/"));
    	System.out.println(wpgc.process("http://www.riccardopatrese.com/"));
    	System.out.println(wpgc.process("http://www.formula1.com/teams_and_drivers/hall_of_fame/181/"));
		System.out.println(wpgc.process(new File("../Ludovic/7Web/spage/SPage_001.htm")));
		System.out.println(wpgc.process(new File("../Ludovic/7Web/blog/blog_augustine_0000004.htm")));
		System.out.println(wpgc.process(new File("../Ludovic/7Web/php/PHP_001.htm")));
	}
}
