package eval.type;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entityranking.QuerySearchEngine;

import util.GetProperties;
import util.NounTransformation;
import util.SortKeysMapByNumberValues;
import util.probabilitydistribution.DirichletSmoothing;
import util.probabilitydistribution.ProbabilityDistributionDirichletSmoothed;
import util.probabilitydistribution.ProbabilityDistributionLaplaceSmoothed;
import util.probabilitydistribution.ProbabilityDistributionSimilarity;

public class Eval {
	public static void main(String[] args)
	{
		try {
			evalDivergence();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void evalPatterns() throws UnsupportedEncodingException
	{
		PrintStream ps = new PrintStream(System.out,true,"UTF-8");
		System.setOut(ps);
		ps = new PrintStream(System.err,true,"UTF-8");
		System.setErr(ps);
		
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");

		ArrayList<String> types = new ArrayList<String>(); 
		try
		{
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream("Data/classes")));
			String type = new String();

			while((type = brTokens.readLine()) != null)
				types.add(type);
			
			brTokens.close();
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}

		System.err.println("Chargement classes terminé");


		ArrayList<String> instances = new ArrayList<String>();
		HashMap<String, DBPediaInstance> dbpInstances = GetInstancesSet.DBPediaInstancesSetDeserialization("data/DBPediaInstances");
		try
		{
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream("Data/instances")));
			String instance = new String();

			int i =0;
			while((instance = brTokens.readLine()) != null && i++ < 100)
			{
				if(!dbpInstances.containsKey(instance) || dbpInstances.get(instance).getTypeHierarchy().vertexSet().size() < 1)
				{
					i--;
					continue;
				}
				
				instances.add(instance);
			}
			
			brTokens.close();
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}



		System.err.println("Chargement instances terminé");

		try
		{
			PrintWriter pwResults = new PrintWriter (new OutputStreamWriter (new FileOutputStream (properties.getProperty("resultsDirectory")+"/results"))) ;
			PrintWriter pwQRels = new PrintWriter (new OutputStreamWriter (new FileOutputStream (properties.getProperty("resultsDirectory")+"/qrels"))) ;

			int compteur = 0;
			for(String instance : instances)
			{
				System.err.println(compteur++ +" "+instance);

				//Ecriture qrels
				HashMap<String, Double> typesWeight = dbpInstances.get(instance).getTypesWeight1();
				
				/*Double max = 0.;
				for(Double val : typesWeight.values())
					max = Math.max(val, max);
				*/
				for(String type : typesWeight.keySet())
					//if(typesWeight.get(type).equals(max))
					pwQRels.println(compteur +" 0 "+ type.replaceAll(" ", "_") +" "+ 1);
				
				//Calcul de l'appartenance aux types et écriture des résultats dans le fichier final
				HashMap<String, Double> hits = new HashMap<String, Double>();

				for(String type : types)
				{
					//System.err.println(type+" "+NounTransformation.pluralForm(type));
					QuerySearchEngine qse = new QuerySearchEngine();
					qse.queryBoss("\""+NounTransformation.pluralForm(type)+" * "+instance+"\"", 0); //Interrogation de Boss
					hits.put(type, new Double(qse.getTotalHits()));
				}

				List<Object> sortedKeys = SortKeysMapByNumberValues.descendingSort(new HashMap<Object, Number>(hits));
				int i = 1;
				for(Object oKey : sortedKeys)
				{
					String type = (String)oKey;
					pwResults.println(compteur +" Q0 "+ type.replaceAll(" ", "_") +" "+ i +" "+ hits.get(type)+" STANDARD");
					i++;
				}
			}
			pwResults.close();
			pwQRels.close();
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}
	}
	
	public static void evalDivergence() throws UnsupportedEncodingException
	{
		PrintStream ps = new PrintStream(System.out,true,"UTF-8");
		System.setOut(ps);
		ps = new PrintStream(System.err,true,"UTF-8");
		System.setErr(ps);
		
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");

		DirichletSmoothing ds = DirichletSmoothing.getInstance(); //Création de l'outil de smoothing
		ProbabilityDistributionLaplaceSmoothed pdlsWorld = new ProbabilityDistributionLaplaceSmoothed(properties.getProperty("unigramWorldSer"));
		ds.setReference(pdlsWorld); //donne le modèle du monde à Dirichlet
		System.err.println("Modèle du monde chargé");


		//chargement des distributions de probas pour tous les types
		HashMap<String, ProbabilityDistributionDirichletSmoothed> pddsTypes = new HashMap<String, ProbabilityDistributionDirichletSmoothed>();
		//HashMap<String, ProbabilityDistributionLaplaceSmoothed> pddsTypes = new HashMap<String, ProbabilityDistributionLaplaceSmoothed>();
		try
		{
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream("Data/classes")));
			String type = new String();

			while((type = brTokens.readLine()) != null)
			{
				ProbabilityDistributionDirichletSmoothed pdds = new ProbabilityDistributionDirichletSmoothed(properties.getProperty("1GramClassModelSnippets")+"/"+type);

				if(pdds.getVocabularySize() > 10)
				{
					pdds.setMu(2000.);
					pddsTypes.put(type, pdds);
				}
			}

			//pddsTypes.put(type, new ProbabilityDistributionLaplaceSmoothed(((ProbabilityDistributionDirichletSmoothed) NGrams.deserializedPDNGrams(properties.getProperty("1GramClassModelWikipedia")+"/"+type)).getFrequenciesMap()));

		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}

		System.err.println("Chargement classes terminé");


		HashMap<String, ProbabilityDistributionDirichletSmoothed> pddsInstances = new HashMap<String, ProbabilityDistributionDirichletSmoothed>();
		//HashMap<String, ProbabilityDistributionLaplaceSmoothed> pddsInstances = new HashMap<String, ProbabilityDistributionLaplaceSmoothed>();

		HashMap<String, DBPediaInstance> dbpInstances = GetInstancesSet.DBPediaInstancesSetDeserialization("data/DBPediaInstances");
		try
		{
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(new FileInputStream("Data/instances")));
			String instance = new String();

			int i =0;
			while((instance = brTokens.readLine()) != null && i++ < 100)
			{
				if(!dbpInstances.containsKey(instance) || dbpInstances.get(instance).getTypeHierarchy().vertexSet().size() < 1)
				{
					i--;
					continue;
				}
				
				ProbabilityDistributionDirichletSmoothed pdds = new ProbabilityDistributionDirichletSmoothed(properties.getProperty("1GramInstancesModelSnippets")+"/"+instance);

				if(pdds.getVocabularySize() > 10)
				{
					pdds.setMu(2000.);
					pddsInstances.put(instance, pdds);
				}
			}

			//pddsInstances.put(instance, new ProbabilityDistributionLaplaceSmoothed(((ProbabilityDistributionDirichletSmoothed) NGrams.deserializedPDNGrams(properties.getProperty("1GramInstancesModelWeb")+"/"+instance)).getFrequenciesMap()));

		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}



		System.err.println("Chargement instances terminé");

		try
		{
			PrintWriter pwResults = new PrintWriter (new OutputStreamWriter (new FileOutputStream (properties.getProperty("resultsDirectory")+"/results"))) ;
			PrintWriter pwQRels = new PrintWriter (new OutputStreamWriter (new FileOutputStream (properties.getProperty("resultsDirectory")+"/qrels"))) ;

			int compteur = 0;
			for(String instance : pddsInstances.keySet())
			{
				System.err.println(compteur++);
				System.err.println(instance);

				//Ecriture qrels
				HashMap<String, Double> typesWeight = dbpInstances.get(instance).getTypesWeight1();
				
				Double max = 0.;
				for(Double val : typesWeight.values())
					max = Math.max(val, max);
				
				for(String type : typesWeight.keySet())
					if(typesWeight.get(type).equals(max))
					pwQRels.println(compteur +" 0 "+ type.replaceAll(" ", "_") +" "+ 1);
				
				//Calcul de l'appartenance aux types et écriture des résultats dans le fichier final
				HashMap<String, Double> divergences = new HashMap<String, Double>();

				for(String type : pddsTypes.keySet())
				{
					//System.err.println("\t"+type);

					ProbabilityDistributionSimilarity pds = new ProbabilityDistributionSimilarity(); //va permettre de calculer la divergence de Kullback-Leibler
					Double divergence = Math.abs(pds.kullbackLeibler(pddsInstances.get(instance),pddsTypes.get(type)));

					divergences.put(type, divergence);
				}


				List<Object> sortedKeys = SortKeysMapByNumberValues.ascendingSort(new HashMap<Object, Number>(divergences));
				int i = 1;
				for(Object oKey : sortedKeys)
				{
					String type = (String)oKey;
					pwResults.println(compteur +" Q0 "+ type.replaceAll(" ", "_") +" "+ i +" "+ (1./divergences.get(type))+" STANDARD");
					i++;
				}
			}
			pwResults.close();
			pwQRels.close();
		}catch(Exception e){
			System.err.println(e.getMessage()); 
		}
	}
}
