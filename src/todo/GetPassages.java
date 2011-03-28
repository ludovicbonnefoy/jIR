package todo;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import searchengine.indri.Indri;
import searchengine.indri.PassageIndri;
import util.GetProperties;
import web.util.html.HTML2Text;

/**
 * Pour remplacer la récupération de passages d'Indri.
 * La segmentation des mots est faites avec TreeTagger.
 * Une segmentation des passages est faite et les passages constituent un nouveau corpus, puis ensuite on interroge Indri pour récupérer les meilleurs passages.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class GetPassages
{
//	/**
//	 * Instantie la classe.
//	 */
//	public GetPassages()
//	{}
//	
//	/**
//	 * Récupère les passages les plus intéressants au vu d'une requête dans un corpus de documents.
//	 * @param corpusDirectory Chemin du corpus
//	 * @param nbrLine Taille en phrases des passages
//	 * @param requete Requête
//	 * @param count Nombre de passages
//	 * @return Liste des passages
//	 */
//    public ArrayList<PassageIndri> getPassages(File corpusDirectory, Integer nbrLine, String requete, Integer count)
//    {
//        ArrayList<PassageIndri> list = new ArrayList<PassageIndri>(); //liste des passages récupérés
//        
//        temporyCorpus(corpusDirectory, nbrLine); //création des passages
//        
//        try
//        {
//        	//Indexation des passages avec Indri
//        	Indri indri = new Indri(GetProperties.getInstance().getProperty("indri"));
//        	indri.setMemory("1G");
//        	indri.setCorpusPath(GetProperties.getInstance().getProperty("passagesCorpus")+"/");
//        	indri.setIndexPath(GetProperties.getInstance().getProperty("passagesIndex")+"/");
//        	indri.setCorpusClass("trectext");
//        	indri.index();
//        	
//        	HashMap<String,Double> scoreDocuments = indri.getDocuments(requete,count); //interrogation d'Indri et récupération des meilleurs
//        	for(String documentId : scoreDocuments.keySet()) //pour chaque passage recupéré
//        	{
//        		BufferedReader br = new BufferedReader(new InputStreamReader (new FileInputStream(GetProperties.getInstance().getProperty("passagesCorpus")+"/"+documentId),"UTF-8")); //on récupère son contenu
//        		String line = br.readLine();
//        		line = br.readLine();
//        		line = br.readLine();
//        		line = br.readLine(); //contenu texte du passage
//        		br.close();
//
//        		list.add(new PassageIndri(scoreDocuments.get(documentId),documentId,line)); //on créé un passage que l'on rajoute à la liste
//        	}
//    	}catch(Exception e){
//    		System.err.println(e.getMessage());
//    	}
//    	
//        return list;
//    }
//    
//    /**
//     * Créé un fichier temporaire dans lequel l'ensemble du corpus est contenu.
//     * Ce fichier est créé pour être lu par le TreeTagger.
//     * @param corpusDirectory Dossier du corpus
//     * @param nbrLine Nombre de phrases commposant un passage
//     */
//    public void temporyCorpus(File corpusDirectory, Integer nbrLine)
//    {
//        if(!corpusDirectory.exists()) //vérification du chemin du corpus
//            return;
//
//        try
//        {
//        	//création du fichier intermédiaire qui va regrouper tout les documents du corpus pour le traiter avec le TreeTagger
//            PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/corpus"),"ISO-8859-1")); //création du fichier
//
//            File[] files = corpusDirectory.listFiles(); //liste des document du corpus 
//            for(int i=0; i<files.length; i++)  //pour chaque document
//            {
//                if(files[i].toString().contains("/correspondances")) //si il ce nomme "correspondances"
//                    continue; //on le saute
//
//                HTML2Text parser = new HTML2Text(); //nettoyage des balises HTML
//                try{
//                	parser.parse(new InputStreamReader(new FileInputStream (files[i]),"UTF-8"));
//                
//                	String textCorpus = parser.getCleanText(); //récupération du texte nettoyé
//                
//                	String docno = files[i].toString().substring(files[i].toString().lastIndexOf("/")+1); //récupération de l'identifiant du document (son nom)
//                
//                	pw.println("<docno> "+docno+"  </docno>"); //écriture du nom du document
//                
//                	pw.println(textCorpus); //écriture de son contenu
//                }catch (Exception e) {}
//                catch (StackOverflowError sofe) {}
//            }
//            pw.close();
//            
//            passagesSegmentation(new File(GetProperties.getInstance().getProperty("tmpDirectory")+"/corpus"), nbrLine); //segmentation du corpus
//        }catch(Exception e){
//            System.err.println(e);
//        }
//    }
//    
//    /**
//     * Segmente un fichier en passages
//     * @param document Le document
//     * @param nbrLine Nombre de phrases commposant un passage
//     */
//    public void passagesSegmentation(File document, Integer nbrLine)
//    {
//    	try
//    	{
//    		//nettoyage du dossier où vont être stockés les passages
//    		File passagesDirectory = new File(GetProperties.getInstance().getProperty("passagesCorpus"));
//    		if(passagesDirectory.exists()) 
//    		{ 
//    			File[] passagesFiles = passagesDirectory.listFiles(); //liste des fichiers 
//    			for(int i=0; i<passagesFiles.length; i++)  //chaque fichier
//    				passagesFiles[i].delete(); //est supprimé
//    		} 
//
//    		//Utilisation du TreeTagger pour faire la segmentation en phrases
//    		//Préparation du TreeTagger
//    		PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger.sh"),"UTF-8"));
//    		pw.println("cat "+GetProperties.getInstance().getProperty("tmpDirectory")+"/corpus | "+GetProperties.getInstance().getProperty("treetaggerDirectory")+"/cmd/tree-tagger-english | iconv -f ISO-8859-1 -t UTF-8");
//    		pw.close();
//
//    		//Éxécution du TreeTagger
//    		Runtime runtime = Runtime.getRuntime();
//    		Process process = runtime.exec(new String[]{"/bin/sh","tmp/treetagger.sh"});
//
//    		boolean text = true; //lit-on le contenu d'un document?
//
//    		String docno = new String(); //identifiant du document en cours
//
//    		Integer numPassage = 0; //numéro du passage en création pour le document en cours
//
//    		String sentence = new String(); //phrase en train d'être construite (lue)
//
//    		ArrayList<String> sentences = new ArrayList<String>(nbrLine); //création d'un passage (avec recouvrement)
//    		for(int j = 0; j < nbrLine; ++j) //initialisation
//    			sentences.add("");
//
//    		BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream())); //va permettre de lire la sortie du TreeTagger
//    		String tokenLine = new String();
//
//    		while((tokenLine = brTokens.readLine()) != null) //pour chaque ligne de la sortie du TreeTagger (donc pour chaque token du texte)
//    		{
//    			if (tokenLine.equals("<docno>")) //si la ligne indique que l'on va obtenir l'id du document
//    			{
//    				text = false; //on signale que ce que l'on va lire n'est pas le contenu du document
//    				numPassage = 0; //et on initialise le compteur de passages
//    			}
//
//    			else if(tokenLine.equals("</docno>")) //si on sort de l'obtention de l'id
//    				text = true; //on indique que le texte va suivre
//
//    			else //lecture du texte
//    			{
//    				String[] elements = tokenLine.replaceAll("\t\t+","\t").split("\t"); //reformatage de la sortie du TreeTagger
//
//    				if(elements.length != 3) //vérification que l'élément est valide
//    					continue;
//
//    				if(text == false) //si on lit l'id
//    				{
//    					docno = elements[0].trim(); //on l'enregistre
//    					continue; //pas de traitement supplémentaires
//    				}
//
//    				if(elements[1].equals("SENT")) //si on a une marque de fin de phrase
//    				{
//    					sentences.remove(0); //on met à jour le passage
//    					sentences.add(sentence+elements[0]);
//
//    					if(!sentences.contains("")) //si on a un passage complet
//    					{
//    						//on l'enregistre
//    						pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("passagesCorpus")+"/"+docno+"_"+numPassage),"UTF-8")); //dans un fichier
//
//    						//formation du passage
//    						String passage = new String();
//    						for(String pass : sentences)
//    							passage += pass+" ";
//
//    						//Création du fichier
//    						pw.println("<DOC>");
//    						pw.println("<DOCNO>"+docno+"_"+numPassage+"</DOCNO>"); //identifiant du passage
//    						pw.println("<TEXT>");
//    						pw.println(passage.trim()); //texte du passage
//    						pw.println("</TEXT>");
//    						pw.println("</DOC>");
//    						pw.close();
//
//    						++numPassage; //mise à jour du numéro du passage
//    					}
//
//    					sentence = new String(); //mise à zéro pour la phrase suivante
//    				}
//    				else //on a là un mot d'une phrase
//    					sentence += elements[0]+" ";
//    			}
//    		}
//    		process.waitFor();
//    	}catch(Exception e){
//    		System.err.println(e.getMessage());
//    	}
//    }
}
