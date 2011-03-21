package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import util.html.HTML2Text;
import util.probabilitydistribution.ProbabilityDistribution;
import util.probabilitydistribution.ProbabilityDistributionDirichletSmoothed;

/**
 * Propose des calculs de ngrammes sur divers type de corpus.
 * Sur le ClueWeb, des pages webs, un fichier texte, ou d'une chaîne,  
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NGrams
{
	/**
	 * Calcul des fréquences des n-grammes sur un fichier.
	 * @param filePath Le chemin du fichier
	 * @param n La taille des n-grammes
	 * @return Les n-grammes: map[n-gramme] = frequence
	 */
    public static HashMap<String,Long> freqNGramsFile(String filePath, Integer n)
    {
        HashMap<String,Long> ngrams = new HashMap<String,Long>();
        try
        {
            System.err.println("Acquisition modèle de langage");
            
            //préparation du TreeTagger
            PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger.sh"),"UTF-8"));
            pw.println("cat "+filePath+" | "+GetProperties.getInstance().getProperty("treetaggerDirectory")+"/cmd/tree-tagger-english | iconv -f ISO-8859-1 -t UTF-8");
            pw.close();
            
            //Execution du TreeTagger
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[]{"/bin/sh","tmp/treetagger.sh"});

            BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream())); //lecture de sa sortie
            String tokenLine = new String();

            ArrayList<String> ngramList = new ArrayList<String>(n); //préparation de la structure qui contient le n-grammes courant
            for(int i=0; i< n; ++i)
                ngramList.add("");

            Pattern p = Pattern.compile("[a-zA-Z0-9]");
            while((tokenLine = brTokens.readLine()) != null) //lecture de la sortie du TreeTagger élément par élément
            {
                String[] elements = tokenLine.replaceAll("\t\t+","\t").split("\t"); //reformatage de la sortie

                if(elements.length != 3) //est-elle valide?
                    continue; //si non on passe à la ligne suivante

                String word = elements[0].toLowerCase(); //passage en minuscule
                
                if(elements[1].equals("CD")) //si on a un chiffre  
                    word = "@card@"; //on remplace la valeur par @card@, on ne prend pas en compte la valeur
                
                if(!elements[2].equals("<unknown>")) //si on a le lemme
                    word = elements[2].toLowerCase(); //on prend le lemme

                Matcher m = p.matcher(word); //on vérifie que il y est au moins une lettre ou chiffre dans le terme (pas juste des caractères spéciaux)
                if(!m.find()) //si on n'en trouve pas on ignore le terme en cours
                    continue;

                //on met à jour le n-grammes
                ngramList.remove(0); //pour cela on supprime le premier élément
                ngramList.add(word); //et on rajoute ce terme à la fin

                if(!ngramList.contains("")) //permet de vérifier que l'on est bien un n-gramme complet (pour les premiers cas)
                {
                    String ngram = new String(); //on va mettre "à plat" le n-gramme
                    
                    for(String ngramWord : ngramList) //pour chaque élément du n-gramme
                        ngram += ngramWord+" "; //on concatène le terme
                    
                    ngram = ngram.trim(); //on supprime l'espace superflu

                    Long count = ngrams.containsKey(ngram) ? ngrams.get(ngram) : 0; //on récupère le compte précédent de ce n-gramme
                    ngrams.put(ngram, count + 1);  //on lui ajoute 1
                }
            }
            
            process.waitFor();
        }catch(Exception e){
            System.err.println(e.getMessage()); 
        }
        
        return ngrams;
    }
	
	
	/** 
	 * Calcul des fréquences des n-grammes sur une chaîne.
	 * @param text La chaîne à analyser
	 * @param n La taille des n-grammes
	 * @return Les n-grammes: map[n-gramme] = frequence
	 */
    public static HashMap<String,Long> freqNGramsString(String text, Integer n)
    {
        try
        {
        	//Ecrit le texte dans un fichier
            PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger"),"ISO-8859-1"));
            pw.print(text);
            pw.flush();
            pw.close();

        }catch(Exception e){
            System.err.println(e);
        }

        return freqNGramsFile(GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger", n); //retourne le résultat de la fonction qui gère les fichiers
    }
    
    /**
     * Calcul des fréquences des n-grammes dans un corpus composé de pages web.
     * @param corpusDirectory Le corpus
     * @param n La taille des n-grammes
     * @return Les n-grammes: map[n-gramme] = frequence
     */
    public static HashMap<String,Long> freqNGramsWebCorpus(File corpusDirectory, Integer n)
    {
        try
        {
            if(corpusDirectory.exists())  //vérifie que le dossier du corpus existe bien
            { 
                File[] files = corpusDirectory.listFiles(); //chargement de la liste des fichiers composant le corpus
                
                PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger"),"ISO-8859-1")); //fichier qui va contenir l'ensemble du texte "propre" du corpus
                
                for(int i=0; i<files.length; ++i) //pour chaque fichier 
                {
                    if(files[i].toString().contains("/correspondances")) //si le fichier se nomme correspondance on le saute
                        continue;

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
        
        return freqNGramsFile(GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger", n); //retourne le résultat de la fonction qui gère les fichier
    }	
	
    /**
     * Calcul des fréquences des n-grammes présents dans un fichier warc.
     * Ce genre de fichier est composé de pages web mais contient aussi des informations inutiles ici.
     * Cette fonction gère des fichiers warc gzippé uniquement.
     * @param n La taille des n-grammes.
     * @param warcFile Le fichier à traiter.
     * @return Les n-grammes: map[n-gramme] = frequence
     */
    public static HashMap<String, Long> freqNGramsWarc(File warcFile, Integer n)
    {
    	try{
    		PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/tree_tagger"),"ISO-8859-1")); //écriture du contenu des documents dans un fichier
    		
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
	    
	    return freqNGramsFile(GetProperties.getInstance().getProperty("tmpDirectory")+"/tree_tagger", n);  //retourne le résultat de la fonction qui gère les fichier
    }
    
	/**
	 * Calcul la probabilité d'apparition des n-grammes dans un fichier.
	 * @param filePath Le chemin du fichier
	 * @param n La taille des n-grammes
	 * @return Les n-grammes: map[n-gramme] = probabilité d'apparition du n-gramme
	 */
    public static ProbabilityDistribution pdNGramsFile(String filePath, Integer n)
    {
    	return (new ProbabilityDistribution(freqNGramsFile(filePath, n)));
    }
    
	/**
	 * Calcul la probabilité d'apparition des n-grammes dans une chaîne.
	 * @param text Le chemin du fichier
	 * @param n La taille des n-grammes
	 * @return Les n-grammes: map[n-gramme] = probabilité d'apparition du n-gramme
	 */
    public static ProbabilityDistribution pdNGramsString(String text, Integer n)
    {
    	return (new ProbabilityDistribution(freqNGramsString(text, n)));
    }
    
	/**
	 * Calcul la probabilité d'apparition des n-grammes dans un corpus de pages web.
	 * @param corpusDirectory Le chemin du fichier
	 * @param n La taille des n-grammes
	 * @return Les n-grammes: map[n-gramme] = probabilité d'apparition du n-gramme
	 */
    public static ProbabilityDistribution pdNGramsWebCorpus(File corpusDirectory, Integer n)
    {
    	return (new ProbabilityDistribution(freqNGramsWebCorpus(corpusDirectory, n)));
    }
    
    /**
     * Calcul la probabilité d'apparition des n-grammes présents dans un fichier warc.
     * Ce genre de fichier est composé de pages web mais contient aussi des informations inutiles ici.
     * Cette fonction gère des fichiers warc gzippé uniquement.
	 * @param warcFile Le chemin du fichier
	 * @param n La taille des n-grammes
	 * @return Les n-grammes: map[n-gramme] = probabilité d'apparition du n-gramme
	 */
    public static ProbabilityDistribution pdNGramsWarc(File warcFile, Integer n)
    {
    	return (new ProbabilityDistribution(freqNGramsWarc(warcFile, n)));
    }

    /**
     * Sérialise la map contenant des fréquences de ngrammes.
     * @param path Emplacement de l'objet
     * @param freqNGrams L'objet à sérialiser
     */
    public static void serializedFreqNGrams(String path, HashMap<String, Long> freqNGrams)
    {
    	try 
    	{
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)); //Emplacement de l'objet sérialisé
    		oos.writeObject(freqNGrams); //enregistrement
    		oos.flush();
    		oos.close();
    	}
    	catch (java.io.IOException e) {
    		e.getMessage();
    	}
    }
    
    /**
     * Désérialise une map contenant des fréquences de ngrammes.
     * @param path Le chemin de l'objet sérialisé
     * @return La map des fréquences
     */
	@SuppressWarnings("unchecked")
	public static HashMap<String, Long> deserializedFreqNGrams(String path)
    {
    	try 
    	{
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
    		
    		return ((HashMap<String, Long>)(ois.readObject()));
    	}catch(Exception e){
    		e.getMessage();
    	}
    	
    	return new HashMap<String, Long>();
    }
	
	/**
	 * Charge les fréquences à partir d'un fichier.
	 * Ce fichier est composé pour chaque ligne "mot fréquence".
	 * @param file Fichier à charger
	 * @return Map des fréquences
	 */
	public static HashMap<String, Long> loadFreqNGrams(File file)
	{
		if(!file.exists()) //Vérification que le fichier existe
			return null;

		try
		{
			HashMap<String, Long> freqNGrams = new HashMap<String, Long>(); //map des fréquences
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8")); //lecture du fichier
			String line;

			while(( line= br.readLine()) != null) //pour chaque ligne (donc ngramme)
			{
				String[] elements = line.split(" "); //découpage
				
				freqNGrams.put(elements[0], new Long(elements[1])); //enregistrement
			}
			
			return freqNGrams;
		}catch (Exception e) {
			e.getMessage();
		}
		
		return null;
	}
	
    /**
     * Sérialise la map contenant des probabilités d'apparition de ngrammes.
     * @param path Emplacement de l'objet
     * @param pd L'objet à sérialiser
     */
    public static void serializedPDNGrams(String path, ProbabilityDistribution pd)
    {
    	try 
    	{
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)); //Emplacement de l'objet sérialisé
    		oos.writeObject(pd); //enregistrement
    		oos.flush();
    		oos.close();
    	}
    	catch (java.io.IOException e) {
    		e.getMessage();
    	}
    }
    
    /**
     * Désérialise une map contenant des probabilités d'apparition de ngrammes.
     * @param path Le chemin de l'objet sérialisé
     * @return La map des fréquences
     */
	public static ProbabilityDistribution deserializedPDNGrams(String path)
    {
    	try 
    	{
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
    		
    		return ((ProbabilityDistribution)(ois.readObject()));
    	}catch(Exception e){
    		e.getMessage();
    	}
    	
    	return null;
    }
	
	public static void main(String[] args) 
	{
			//ProbabilityDistribution pd = new ProbabilityDistribution(NGrams.loadFreqNGrams(new File(args[0])));
			
		/*if(args[0].equals("WARC"))
			pd = NGrams.pdNGramsWarc(new File(args[2]), new Integer(args[1]));
		else if(args[0].equals("File"))
			pd = NGrams.pdNGramsFile(args[2], new Integer(args[1]));
		else if(args[0].equals("WebCorpus"))
			pd = NGrams.pdNGramsWebCorpus(new File(args[2]), new Integer(args[1]));
		*/
		
		//NGrams.serializedPDNGrams(args[3], pd);
			//NGrams.serializedPDNGrams(args[1], pd);
		
		
				ProbabilityDistribution pd = NGrams.deserializedPDNGrams(args[0]);
				System.out.println(pd.get("@card@"));
				ProbabilityDistributionDirichletSmoothed pdls = new ProbabilityDistributionDirichletSmoothed(pd.getFrequenciesMap());
				System.out.println(pdls.get("@card@"));
				System.out.println(pdls.get("baracaraafafa"));
	}
}
