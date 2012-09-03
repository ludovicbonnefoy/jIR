package searchengine.indri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import util.GetProperties;
import util.Log;
/**
 * API perso pour Indri. 
 * Trois fonctions sont proposées : Indexation, Récupération de documents et de passages.
 * Pour chacune, un fichier temporaire de configuration est créé et ensuite Indri est interrogé. 
 * Le chemin des éxécutables d'Indri doit être précisé dans un fichier de propriétés.
 * Pour plus d'infos sur Indri et sur chacun des paramètres privés de cette classe, un site web est disponible.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class Indri
{
		/** La mémoire maximum qu'Indri va pouvoir utiliser pour effectuer ses tâches */
		private String _memory;
	
		/** Le stemmer utilisé par Indri */
		private String _stemmer;
	
		/** Liste des champs présents dans le texte qui seront indéxés et qui pourront être questionnés. */
		private ArrayList<String> _fields;
	
		/** Chemin du corpus sur lequel travailler */
		private String _corpusPath;
	
		/** Classe du corpus (texte, Trec, ...?) */
		private String _corpusClass;
	
		/** Le chemin de l'index à créer ou utiliser */
		private String _indexPath;
	
		/** Le modèle utilisé pour la recherche (cosine, okapie, indri ?) */
		private String _retModel;
	
		private String _rule;
	
		/**Chemin du dossier où vont être trouvés les différents éxécutables d'Indri */
		private String _executableDirectoryPath;
	
		/**
		 * Initialisation de la classe et de certains paramètres avec des valeurs par défaut.
		 */
		public Indri(String executableDirectoryPath)
		{
			_memory="3G";
			_stemmer = "krovetz";
			_fields = new ArrayList<String>(0);
			_corpusPath = GetProperties.getInstance().getProperty("topicCorpus")+"/";
			_corpusClass = "trecweb";
			_indexPath = GetProperties.getInstance().getProperty("topicIndex")+"/";
			_retModel = "1";
			_rule = "method:dirichlet,mu:2500";
	
			_executableDirectoryPath = executableDirectoryPath;
		}
	
		/**
		 * Permet de modifier la valeur par défaut de la mémoire maximum que peut utiliser Indri.
		 * @param memory Mémoire maximum allouée
		 */
		public void setMemory(String memory) {
			_memory = memory;
		}
	
		public void setRule(String rule) {
			_rule = rule;
		}
	
		/**
		 * Permet de modifier la valeur par défaut du stemmer utilisé.
		 * @param stemmer Stemmer utilisé
		 */
		public void setStemmer(String stemmer)	{
			_stemmer = stemmer;
		}
	
		/**
		 * Réinitialise la liste des champs à indexer.
		 */
		public void eraseFields() {
			_fields = new ArrayList<String>(0);
		}
	
		/**
		 * Permet de rajouter des champs à indexer.
		 * @param field Nom du champ
		 */
		public void addField(String field) {
			_fields.add(field);
		}
	
		/**
		 * Récupération de la liste des champs à indexer à partir de la liste en paramètre.
		 * @param fields Liste des champs à indexer
		 */
		public void setFields(ArrayList<String> fields) {
			_fields = fields;
		}
	
		/**
		 * Permet de modifier la valeur par défaut du chemin du corpus. 
		 * @param corpusPath Chemin du corpus
		 */
		public void setCorpusPath(String corpusPath) {
			_corpusPath = corpusPath;
		}
	
		/**
		 * Permet de modifier la valeur par défaut du type du corpus. 
		 * @param corpusClass Type du corpus
		 */
		public void setCorpusClass(String corpusClass)	{
			_corpusClass = corpusClass;
		}
	
		/**
		 * Permet de modifier la valeur par défaut du chemin de l'index à créer ou à utiliser.
		 * @param indexPath Chemin de l'index
		 */
		public void setIndexPath(String indexPath)	{
			_indexPath = indexPath;
		}
	
		/**
		 * Permet de modifier la valeur par défaut du modèle utilisé pour la recherche de documents ou de passages. 
		 * @param retModel Nom du modèle
		 */
		public void setRetModel(String retModel) {
			_retModel = retModel;
		}

	/**
	 * Récupération des documents les plus pertinents par rapport à une requête dans un corpus préalablement indéxé.
	 * @param iqw Contient les paramètres de la requête
	 * @return Liste qui contient un map[doc] = score pour chaque query 
	 */
	public static ArrayList<ArrayList<IndriResult>> getDocuments(IndriQueryWriter iqw)
	{
		ArrayList<ArrayList<IndriResult>> results = new ArrayList<ArrayList<IndriResult>>();

		iqw.writeIndriQuery(new File(GetProperties.getInstance().getProperty("tmpDirectory")+"/indriQuery_"+Thread.currentThread().getId()));

		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		PumpStreamHandler psh = new PumpStreamHandler(stdout);
		CommandLine cl = new CommandLine(GetProperties.getInstance().getProperty("indriDirectory")+"/IndriRunQuery");
		cl.addArgument(GetProperties.getInstance().getProperty("tmpDirectory")+"/indriQuery_"+Thread.currentThread().getId());
		DefaultExecutor exec = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(Integer.parseInt(GetProperties.getInstance().getProperty("timeout"))*1000);
		exec.setWatchdog(watchdog);
		exec.setStreamHandler(psh);

		try{
			exec.execute(cl);

			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stdout.toString().getBytes("utf-8"))));
			String line;
			String text = null;
			while((line = br.readLine()) != null) //lecture de tous les résultats
			{
				Matcher mat = Pattern.compile("[0-9]+ Q0").matcher(line);
				if(mat.find() && mat.start() == 0)
				{
					if(text != null)
						results.get(results.size()-1).get(results.get(results.size()-1).size()-1).setText(text);
					text ="";

					//Récupération des éléments d'un résultat (identifiant du document et score associé)
					String res[] = line.split(" ");
					Integer queryId = Integer.parseInt(res[0]);
					if(results.size() < queryId)
						results.add(new ArrayList<IndriResult>());

					String docId = res[2];
					//				String id = res[2].substring(res[2].lastIndexOf("/")+1,res[2].lastIndexOf("."));
					Integer resultNumber = Integer.parseInt(res[3]);
					Double score = Double.parseDouble(res[4]);
					String runName = res[5];
					IndriResult ir = new IndriResult(docId, score, queryId, resultNumber, runName);
					results.get(queryId-1).add(ir);
				}
				text += line;
			}
			if(text != null)
				results.get(results.size()-1).get(results.get(results.size()-1).size()-1).setText(text);
			br.close();
		} catch (ExecuteException e) {
			Log.getInstance().add(e);
			System.err.println(Thread.currentThread().getName()+" "+Thread.currentThread().getId()+" too long");
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Retourne l'id interne du document à partir de l'id passé en paramètre et du champs dans lequel chercher pour l'index passé
	 * @param index Chemin de l'index
	 * @param id Id à rechercher
	 * @param field Champs dans lequel rechercher l'id
	 * @return L'id interne du document (si trouvé, null sinon)
	 */
	public static String getDocumentId(String index, String id, String field)
	{
		try {
			Runtime runtime = Runtime.getRuntime();
			Process pFilePath = runtime.exec(new String[]{GetProperties.getInstance().getProperty("indriDirectory")+"/dumpindex", index, "documentid", field, id});
			BufferedReader br = new BufferedReader(new InputStreamReader(pFilePath.getInputStream()));
			String internalId = br.readLine();
			pFilePath.waitFor();
			br.close();

			return internalId;
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retourne le texte correspondant au document internalId dans index.
	 * @param internalId Id interne à indri du document dont on veut le texte.
	 * @param index Index dans lequel rechercher le document.
	 * @return Texte du document (ou null si non trouvé).
	 */
	public static String getText(String internalId, String index)
	{
		try {
			Runtime runtime = Runtime.getRuntime();
			Process
			pFilePath = runtime.exec(new String[]{GetProperties.getInstance().getProperty("indriDirectory")+"/dumpindex", index, "dt", internalId});
			BufferedReader br = new BufferedReader(new InputStreamReader(pFilePath.getInputStream()));
			String line;
			String text="";
			while((line = br.readLine()) != null)//parcours du document de correspondances
				text += line;
			pFilePath.waitFor();
			br.close();
			return text;
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retourne le texte correpondant à l'id si trouvé dans le champs pour l'index passé en paramètre.
	 * @param index Index dans lequel rechercher le document.
	 * @param id Id à rechercher
	 * @param field Champs dans lequel rechercher l'id
	 * @return Texte du document (ou null si non trouvé).
	 */
	public static String getText(String index, String id, String field)
	{
		String internalId = getDocumentId(index, id, field);
		if(internalId != null)
			return getText(internalId, index);
		return null;
	}

	
	/**
	 * Retourne le texte correpondant à l'id si trouvé dans le champs pour chaque index passé en paramètre.
	 * @param fieldPerIndex [index] = champs dans lequel chercher id
	 * @param id
	 * @return Texte du document (ou null si non trouvé).
	 */
	public static String getText(HashMap<String, String> fieldPerIndex, String id)
	{
		for(String index : fieldPerIndex.keySet())
		{
			String text = getText(index, id, fieldPerIndex.get(index));
			if(text != null)
				return text;
		}
		return null;
	}

		/**
		 * Indexation du corpus de documents.
		 */
		public void index()
		{
			try
			{
				//Création du fichier de paramètres avec les valeurs des données membres
				PrintWriter parameters = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/indriIndexationParameters")));
				parameters.println("<parameters>");
				parameters.println("\t<index>"+_indexPath+"</index>");
				parameters.println("\t<memory>"+_memory+"</memory>");
				parameters.println("<rule>"+_rule+"</rule>");
				parameters.println("\t<corpus>");
				parameters.println("\t\t<path>"+_corpusPath+"</path>");
				parameters.println("\t\t<class>"+_corpusClass+"</class>");
				parameters.println("\t</corpus>");
				parameters.println("\t<stemmer><name>"+_stemmer+"</name></stemmer>");
	
				for(String field : _fields)
				{
					parameters.println("\t<field>");
					parameters.println("\t\t<name>"+field+"</name>");
					parameters.println("\t</field>");
				}
	
				parameters.println("</parameters>");
				parameters.close();
	
				File passagesDirectory = new File(_indexPath);
				if( passagesDirectory.exists() ) 
				{ 
					File[] passagesFiles = passagesDirectory.listFiles(); 
					for(int i=0; i<passagesFiles.length; i++) 
						passagesFiles[i].delete(); 
				} 
				else
					passagesDirectory.mkdir();
	
				//Indexation
				System.err.println("Indexation");
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(new String[]{_executableDirectoryPath+"/IndriBuildIndex","/tmp/indriIndexationParameters"});
	
				//Obligatoire pour que le système passe en revue les messages de sortie d'Indri et ne patiente pas
				BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while(brTokens.readLine() != null);
				process.waitFor();
	
				brTokens.close();
				process.destroy();
				System.err.println("Indexation terminée");
			}catch(Exception e){
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	
		/**
		 * Récupération des documents les plus pertinents par rapport à une requête dans un corpus préalablement indéxé.
		 * @param query La requête
		 * @param count Le nombre de document à retourner
		 * @return Paire Nom du document dans le corpus / Score du document.
		 */
		public HashMap<String,Double> getDocuments(String query, Integer count)
		{
			HashMap<String,Double> documents = new HashMap<String,Double>();
			System.err.println("Récupération des documents");
			try
			{
				//Création du fichier de paramètres
				PrintWriter parameters = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/indriDocumentQuery")));
				parameters.println("<parameters>");
				parameters.println("\t<memory>"+_memory+"</memory>");
				parameters.println("\t<index>"+_indexPath+"</index>");
				parameters.println("\t<count>"+count+"</count>");
				parameters.println("\t<threads>7</threads>");
				parameters.println("\t<trecFormat>true</trecFormat>");
				parameters.println("<rule>"+_rule+"</rule>");
				parameters.println("\t<retmodel>"+_retModel+"</retmodel>");
				parameters.println("\t<query>");
				parameters.println("\t\t<number>1</number>");
				//            parameters.println("\t\t<text>#combine("+query.replaceAll("[^0-9a-zA-Z]"," ")+")</text>");
				parameters.println("\t\t<text>"+query+"</text>");
				parameters.println("\t</query>");
				parameters.println("</parameters>");
				parameters.close();
	
				//Requete à Indri
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(new String[]{_executableDirectoryPath+"/IndriRunQuery","/tmp/indriDocumentQuery"});
				process.waitFor();
	
				//Lecture des résultats
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while((line = br.readLine()) != null) //lecture de tous les résultats
				{
					//Récupération des éléments d'un résultat (identifiant du document et score associé)
					String res[] = line.split(" ");
	
					/*//Récupération du nom du document en utilisant l'id retourné par Indri
	                Process getIDProcess = runtime.exec(new String[]{GetProperties.getInstance().getProperty("indri")+"/dumpindex",_indexPath,"di","docno",res[1]});
	                getIDProcess.waitFor();
	                String id = new BufferedReader(new InputStreamReader(getIDProcess.getInputStream())).readLine();
					 */
					documents.put(res[2],new Double(res[4])); //insertion dans la map de résultat
				}
				br.close();
				process.destroy();
			}catch(Exception e){
				System.err.println(e);
			}
			return documents;
		}
	
		/**
		 * Récupération de passages avec Indri.
		 * Permet de récupérer les passages des documents indéxés les plus pertinents au vu d'une requête.
		 * Les passages sont ordonnées dans l'ordre décroissants des scores, donc de l'intérêt.
		 * @param req La requête
		 * @param count Le nombre de passages à retourner
		 * @param length La longueur des passages (en nombre de mots)
		 * @param scale Le pas (en nombre de mots)
		 * @return Liste des Passages
		 */
		public ArrayList<PassageIndri> getPassages(String req, Integer count, Integer length, Integer scale)
		{
			ArrayList<PassageIndri> passages = new ArrayList<PassageIndri>(count);
			try
			{
				//Création du fichier de paramètres
				PrintWriter parameters = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/indriPassageQuery")));
				parameters.println("<parameters>");
				parameters.println("\t<index>"+_indexPath+"</index>");
				parameters.println("\t<retmodel>"+_retModel+"</retmodel>");
				parameters.println("\t<memory>"+_memory+"</memory>");
				parameters.println("\t<count>"+count+"</count>");        
				parameters.println("\t<rule>"+_rule+"</rule>");
	
	
				parameters.println("\t<query>");
				parameters.println("\t\t<number>1</number>");
				parameters.println("\t\t<text>#combine[passage"+length+":"+scale+"]("+req.replaceAll("[^0-9a-zA-Z]"," ")+")</text>"); //spécificité de la recherche de passages
				parameters.println("\t</query>");
				parameters.println("</parameters>");
				parameters.close();
	
				//Requete à Indri
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec(new String[]{_executableDirectoryPath+"/IndriRunQuery","/tmp/indriPassageQuery"});
				process.waitFor();
	
				//Lecture des résultats
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while((line = br.readLine()) != null)
				{
					//Récupération des éléments d'un résultat
					String res[] = line.split("\t");
	
					//Permet de récupérer l'id Indri au docno récupéré dans res
					Process getIDProcess = runtime.exec(new String[]{_executableDirectoryPath+"/dumpindex",_indexPath,"di","docno",res[1]});
					getIDProcess.waitFor();
					String id = new BufferedReader(new InputStreamReader(getIDProcess.getInputStream())).readLine();
	
					//Récupère le passage en sélectionnant les bons tokens dans le vecteur du document
					Process getPassageProcess = runtime.exec(new String[]{_executableDirectoryPath+"/dumpindex",_indexPath,"dv",id});
					BufferedReader brTokens = new BufferedReader(new InputStreamReader(getPassageProcess.getInputStream()));
					Integer numToken = 0;
					String passage = new String(), tokenLine;
					while((tokenLine = brTokens.readLine()) != null) //parcours des tokens du docupents
					{
						if(numToken >= Integer.parseInt(res[2]) && numToken < Integer.parseInt(res[3])) // si le mot est à récupérer
							passage += tokenLine.split(" ")[2]+" ";
						numToken++;
					}
					getPassageProcess.waitFor();
					passages.add(new PassageIndri(new Double(res[0]),res[1],passage)); //ajout du passage dans la liste
				}
				br.close();
				process.destroy();
			}catch(Exception e){
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			return passages;
		}
}
