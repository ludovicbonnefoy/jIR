package searchengine.indri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import util.Log;

public class IndriQueryWriter 
{
	/** La mémoire maximum qu'Indri va pouvoir utiliser pour effectuer ses tâches */
	protected String _memory;

	/** Nombre de threads */
	protected Integer _nbrThreads;

	/** Nombre de résultats à ramener */
	protected Integer _count;

	/** Lissage à utiliser */
	protected String _smoothing;

	/** Trec Format */
	protected String _trecFormat;

	/** Les chemins des indexs à utiliser */
	protected ArrayList<String> _indexPaths;

	/** Liste des questions */
	protected ArrayList<String> _queries;

	/** Le modèle utilisé pour la recherche (cosine, okapie, indri ?) */
	protected String _retModel;

	/** Query Offset */
	protected Integer _queryOffset;

	/** Id du run */
	protected String _runID;

	/** Afficher la question */
	protected String _printQuery;

	/** Afficher les documents */
	protected String _printDocuments;

	/**
	 * Initialisation de la classe et de certains paramètres avec des valeurs par défaut.
	 */
	public IndriQueryWriter()
	{
		_memory="3G";
		_indexPaths = new ArrayList<String>(1);
		_queries = new ArrayList<String>(1);
		_retModel = "7";
		_nbrThreads = 2;
		_count = 100;
		_smoothing = "method:dirichlet,mu:2500";
		_trecFormat = "true";
		_queryOffset = 1;
		_runID = "default";
		_printQuery = "false";
		_printDocuments = "false";
	}

	/**
	 * Ecris la requête dans le fichier passé en paramètres.
	 * @param queryFile Chemin où va être écris la requête.	
	 */
	public void writeIndriQuery(File queryFile)
	{
		try 
		{
			PrintWriter parameters = new PrintWriter(new OutputStreamWriter (new FileOutputStream (queryFile),"UTF-8"));
			parameters.println("<parameters>");
			parameters.println("\t<memory>"+_memory+"</memory>");
			for(String index : _indexPaths)
				parameters.println("\t<index>"+index+"</index>");
			parameters.println("\t<count>"+_count+"</count>");
			parameters.println("\t<threads>"+_nbrThreads+"</threads>");
			parameters.println("\t<rule>"+_smoothing+"</rule>");
			parameters.println("\t<retmodel>"+_retModel+"</retmodel>");
			parameters.println("\t<trecFormat>"+_trecFormat+"</trecFormat>");
			parameters.println("\t<queryOffset>"+_queryOffset+"</queryOffset>");
			parameters.println("\t<runID>"+_runID+"</runID>");
			parameters.println("\t<printQuery>"+_printQuery+"</printQuery>");
			parameters.println("\t<printDocuments>"+_printDocuments+"</printDocuments>");

			int i = 1;
			for(String query : _queries)
			{
				parameters.println("\t<query>");
				parameters.println("\t\t<number>"+i+"</number>");
				parameters.println("\t\t<text>"+query+"</text>");
				parameters.println("\t</query>");
				i++;
			}
			
			parameters.println("</parameters>");
			parameters.close();
		} catch (UnsupportedEncodingException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
	}

	/**
	 * Permet de modifier la valeur par défaut de la mémoire maximum que peut utiliser Indri.
	 * @param memory Mémoire maximum allouée
	 */
	public void setMemory(String memory)
	{
		_memory = memory;
	}

	/**
	 * Réinitialise la liste des questions.
	 */
	public void eraseQueries()
	{
		_queries = new ArrayList<String>(0);
	}

	/**
	 * Permet de rajouter des questions.
	 * @param query Question
	 */
	public void addQuery(String query)
	{
		_queries.add(query);
	}

	/**
	 * Récupération de la liste des questions à partir de la liste en paramètre.
	 * @param queries Liste des questions
	 */
	public void setQueries(ArrayList<String> queries)
	{
		_queries = queries;
	}

	/**
	 * Réinitialise la liste des indexs.
	 */
	public void eraseIndexPaths()
	{
		_indexPaths = new ArrayList<String>(0);
	}

	/**
	 * Permet de rajouter un index.
	 * @param indexPath Chemin de l'index
	 */
	public void addIndexPaths(String indexPath)
	{
		_indexPaths.add(indexPath);
	}

	/**
	 * Récupération de la liste des indexs à partir de la liste en paramètre.
	 * @param indexPaths Liste des indexs.
	 */
	@SuppressWarnings("unchecked")
	public void setIndexPaths(ArrayList<String> indexPaths)
	{
		_indexPaths = (ArrayList<String>) indexPaths.clone();
	}

	/**
	 * Permet de modifier la valeur par défaut du modèle utilisé pour la recherche de documents ou de passages. 
	 * @param retModel Nom du modèle
	 */
	public void setRetModel(String retModel)
	{
		_retModel = retModel;
	}
	
	public void setNbrThreads(Integer nbrThreads)
	{
		_nbrThreads = nbrThreads;
	}
	
	public void setCount(Integer count)
	{
		_count = count;
	}
}
