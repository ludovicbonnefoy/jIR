package toto.eval.type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


import corpus.AbstractWebPagesCorpus;
import corpus.WebPagesCorpusFactory;

import util.GetProperties;
import word.probabilitydistribution.NGramsProbabilityDistributionDirichletSmoothed;
import entityranking.QuerySearchEngine;

public class BuildReferenceModels 
{
	
	public static void main(String[] args) 
	{
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");
		
		
		/*HashMap<String, DBPediaInstance> instances = GetInstancesSet.DBPediaInstancesSetDeserialization("Data/DBPediaInstances");
		
		try {
			PrintWriter pw = new PrintWriter (new OutputStreamWriter (new FileOutputStream ("Data/instances"))) ;


			for(String instance : instances.keySet())
			{
				pw.println(instance);
			}
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*Vérif fonctionnement ngrams
		DirichletSmoothing ds = DirichletSmoothing.getInstance(); //Création de l'outil de smoothing
		ProbabilityDistributionLaplaceSmoothed pdlsWorld = new ProbabilityDistributionLaplaceSmoothed(NGrams.deserializedPDNGrams(properties.getProperty("unigramWorldSer")).getFrequenciesMap()); //récupération du modèle du monde, lissé avec Laplace
		ds.setReference(pdlsWorld); //donne le modèle du monde à Dirichlet
		ProbabilityDistributionDirichletSmoothed pdds = (ProbabilityDistributionDirichletSmoothed)NGrams.deserializedPDNGrams(properties.getProperty("1GramClassModelSnippets")+"/Activity");
		*/

		
		BuildWikipediaModel("Data/classes",100, properties.getProperty("1GramClassModelWikipedia"));
		BuildSnippetModel("Data/classes",100, properties.getProperty("1GramClassModelSnippets"));
		BuildWebModel("Data/classes",100, properties.getProperty("1GramClassModelWeb"));
		
		BuildSnippetModel("Data/instances",10, properties.getProperty("1GramInstancesModelSnippets10"));
		BuildWebModel("Data/instances",10, properties.getProperty("1GramInstancesModelWeb10"));
		
		BuildSnippetModel("Data/instances",100, properties.getProperty("1GramInstancesModelSnippets"));
		BuildWebModel("Data/instances",100, properties.getProperty("1GramInstancesModelWeb"));
	}
	
	public static void BuildWikipediaModel(String classesFilePath, Integer nbrDocs, String saveDirectory) 
	{
		try
		{
			GetProperties properties = GetProperties.getInstance();
			properties.init("properties.properties");
			
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream(classesFilePath))); //lecture de sa sortie
			String type = new String();
			
			while((type = brTokens.readLine()) != null) //lecture de la sortie du TreeTagger élément par élément
            {
				System.out.println(type);
			
				URL url = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=select+distinct+%3Fpage%0D%0Awhere%0D%0A{%0D%0A+++%3Furi+rdf%3Atype+<http%3A%2F%2Fdbpedia.org%2Fontology%2F"+type.replaceAll(" ","")+">+.%0D%0A+++%3Furi+foaf%3Apage+%3Fpage+.%0D%0A}%0D%0ALimit+"+nbrDocs+"&debug=on&timeout=&format=text%2Fxml&save=display&fname=");
				URLConnection con = url.openConnection ();

				DocumentBuilder parseur = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc =  parseur.parse(con.getInputStream());

				NodeList uris = doc.getElementsByTagName("uri");
				
				ArrayList<String> urls = new ArrayList<String>();
				
				for (int i = 0; i < uris.getLength(); i++)
					urls.add(uris.item(i).getTextContent());
				
				AbstractWebPagesCorpus webCorpus = WebPagesCorpusFactory.get(new File(properties.getProperty("tmpDirectory")+"/tmpCorpus/"));
				webCorpus.build(new HashSet<String>(urls)); //aspiration des pages
				
				NGramsProbabilityDistributionDirichletSmoothed pdds = new NGramsProbabilityDistributionDirichletSmoothed();
				pdds.fromFile(new File(properties.getProperty("tmpDirectory")+"/tmpCorpus/"), 1);

				pdds.serialize(saveDirectory+"/"+type);
            }
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}

	}
	
	public static void BuildSnippetModel(String classesFilePath, Integer nbrDocs, String saveDirectory) 
	{
		try
		{
			GetProperties properties = GetProperties.getInstance();
			properties.init("properties.properties");
			
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream(classesFilePath))); //lecture de sa sortie
			String type = new String();
			
			while((type = brTokens.readLine()) != null) //lecture de la sortie du TreeTagger élément par élément
            {
				System.out.println(type);
				QuerySearchEngine qse = new QuerySearchEngine();
				qse.queryBoss(type, nbrDocs); //Interrogation de Boss
			
				String snippets = new String();
				for(String snippet : qse.getSnippets())
					snippets += snippet;
				
				NGramsProbabilityDistributionDirichletSmoothed pdds = new NGramsProbabilityDistributionDirichletSmoothed();
				pdds.fromString(snippets, 1);
				pdds.serialize(saveDirectory+"/"+type);
            }
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}

	}
	
	public static void BuildWebModel(String classesFilePath, Integer nbrDocs, String saveDirectory) 
	{
		try
		{
			GetProperties properties = GetProperties.getInstance();
			properties.init("properties.properties");
			
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream(classesFilePath))); //lecture de sa sortie
			String type = new String();
			
			while((type = brTokens.readLine()) != null) //lecture de la sortie du TreeTagger élément par élément
            {
				QuerySearchEngine qse = new QuerySearchEngine();
				qse.queryBoss(type, nbrDocs); //Interrogation de Boss
				System.out.println(type);
			
				AbstractWebPagesCorpus webCorpus = WebPagesCorpusFactory.get(new File(properties.getProperty("tmpDirectory")+"/tmpCorpus/"));
				webCorpus.build(new HashSet<String>(qse.getURLs())); //aspiration des pages
				
				
				NGramsProbabilityDistributionDirichletSmoothed pdds = new NGramsProbabilityDistributionDirichletSmoothed();
				pdds.fromFile(new File(properties.getProperty("tmpDirectory")+"/tmpCorpus/"), 1);
				pdds.serialize(saveDirectory+"/"+type);
            }
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}

	}
}
