package util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import namedentity.AbstractNamedEntityRecognitionTool;
import namedentity.NamedEntityRecognitionToolFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.GetProperties;
import util.SortKeysMapByNumberValues;
import util.web.searchenginequery.AbstractWebSearchEngineQuery;
import util.web.searchenginequery.WebSearchEngineQueryFactory;


/**
 * Forme la plus simple d'une entité nommée, contient uniquement le texte de l'EN dans un tableau de String (un mot par case).
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NamedEntity extends ArrayList<String> 
{
	private static final long serialVersionUID = -7492535373780960285L;

	/**
	 * Initialise l'EN 
	 */
	public NamedEntity()
	{
		super();
	}
	
	/**
	 * Initialise l'EN avec le tableau de string passé en paramètre.
	 * @param namedEntity
	 */
	public NamedEntity(ArrayList<String> namedEntity)
	{
		super(namedEntity);
	}
	
	/**
	 * Initialise l'EN en mettant la string dans la première case du tableau.
	 * @param namedEntity
	 */
	public NamedEntity(String namedEntity)
	{
		super();
		add(namedEntity);
	}
	
	/**
	 * Concatène les éléments du tableau, en les séparants d'un espace.
	 * @return L'EN
	 */
    public String getStringNamedEntity()
    {
        String namedEntity = new String(); //texte de l'EN
        for(String word : this) //pour chaque composante de l'array
            namedEntity += word + " "; //on concatène le terme au texte, séparé d'un espace

        return(namedEntity.trim()); //retour du texte avec suppression des espaces superflus
    }
    
	/**
	 * Essaye de récupérer la forme canonique d'une EN.
	 * Récupère l'EN contenant l'EN passée en référence qui à la plus forte occurence dans les dix premiers snippets récupéré en interrogeant Boss avec l'EN et le contexte.
	 * Ex : <br/>
	 * EN : massa <br/>
	 * on interroge le web avec "massa teammates michael schumacher" et on se rend compte que l'EN contenant "massa" et la plus fréquente dans les dix premiers snippets est "felipe massa". 
	 * @param namedEntity EN
	 * @param typeNamedEntity Type large de l'EN attendu
	 * @param contextRequest Supplément à la requête pour donner le contexte de l'EN à trouver.
	 * @return Retourne l'EN avec son écriture modifiée.
	 */
	public static NamedEntity canonicalForm(NamedEntity namedEntity, String typeNamedEntity, String contextRequest)
	{
		try
		{
			String textEN = namedEntity.getStringNamedEntity(); //texte de l'EN
			
			AbstractWebSearchEngineQuery qse = WebSearchEngineQueryFactory.get();
	        qse.query(textEN+" "+contextRequest,10);
	        
	        String text = new String(); //contient le texte des snippets
	        
	        for(String snippet : qse.getSnippets())
	        	text += snippet+" .";
	        
	        if(text.trim().equals(""))
	        	return namedEntity;
	        
	        AbstractNamedEntityRecognitionTool NER = NamedEntityRecognitionToolFactory.get();
			text = NER.proceed(text);
			
			//Préparation pour le TreeTagger
			PrintWriter pwPassages = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/stanford.xml"),"ISO-8859-1"));
			pwPassages.print(text);
			pwPassages.flush();
			pwPassages.close();
	        
	        //lecture de la sortie de la fonction précédente avec le TreeTagger
	        PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger.sh"),"UTF-8"));
	        pw.println("cat "+GetProperties.getInstance().getProperty("tmpDirectory")+"/stanford.xml | "+GetProperties.getInstance().getProperty("treetaggerDirectory")+"/cmd/tree-tagger-english | iconv -f ISO-8859-1 -t UTF-8");
	        pw.close();
	        
	        Runtime runtime = Runtime.getRuntime();
	        Process process = runtime.exec(new String[]{"/bin/sh","tmp/treetagger.sh"});
	
	        //lecture de la sortie du TreeTagger
	        BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
	        String tokenLine = new String();
	
	        String enType = null; //type attendu
	
	        HashMap<String, Integer> forms = new HashMap<String, Integer>();
	        String formNamedEntity = new String(); 
	        
	        while((tokenLine = brTokens.readLine()) != null) //pour chaque token
	        {
	            if(tokenLine.matches("<[^/][^>]*>")) //si on a une balise ouvrante, donc le type d'une EN
	            {
	                enType = tokenLine.substring(1,tokenLine.length()-1); //on récupère le type de l'EN
	
	                if(!enType.equals(typeNamedEntity)) //si ça ne correspond pas avec le type souhaité
	                    enType = null; //on passe cette EN
	                
	                formNamedEntity = new String();
	            }
	            else if(tokenLine.matches("</[^>]*>")) //si on a une balise fermante, donc la fin d'une EN
	            {
	                if(enType == null) //on vérifie qu'elle est du type souhaité
	                    continue;
	
	                enType = null; //on remet le type à zéro
	                
	                formNamedEntity = formNamedEntity.trim();
	                
	                if(formNamedEntity.contains(textEN))
	                {
	                	Integer count = forms.containsKey(formNamedEntity) ? forms.get(formNamedEntity) : 0;
	                	forms.put(formNamedEntity, count+1);
	                }
	            }
	            else //si on a affaire à un mot
	            {
	            	if(enType == null)
	            		continue;
	            	
	                String[] elements = tokenLine.replaceAll("\t\t+","\t").split("\t"); //on récupère les infos
	                
	                String word = elements[0].toLowerCase(); //le mot en minuscule
	                
	                if(!elements[2].equals("<unknown>")) //si on a le lemme on le prend
	                    word = elements[2].toLowerCase();
	                
	                if(word.equals(".")) //si on a un point on passe
	                    continue;
	                
	                formNamedEntity += word+" ";
	            }
	        }
	        
	        if(forms.size() > 0)
	        {
	        	List<Object> sortedNamedEntities = SortKeysMapByNumberValues.descendingSort(new HashMap<Object, Number>(forms)); //tri des ENs en fonction du score de compacité
				
	        	//modification de l'écriture
				namedEntity.clear();
				
				String[] elements = ((String)sortedNamedEntities.get(0)).trim().split(" ");
				for(String element : elements)
					namedEntity.add(element);
	        }
		}catch (Exception e) {
			System.err.println(e.getMessage());
		}
	        
		return namedEntity;
	}
	
	/**
	 * Essaye de corriger l'EN d'erreurs dans l'écriture.
	 * Interroge Boss et corrige si il y a une suggestion.
	 * @param namedEntity
	 * @return
	 */
	public static NamedEntity mistake(NamedEntity namedEntity)
	{
		try
		{
			String textEN = URLEncoder.encode(namedEntity.getStringNamedEntity(),"UTF-8"); //Transforme la requête en chaîne valide pour insérer dans une url

			//Interrogation
			URL url = new URL("http://boss.yahooapis.com/ysearch/spelling/v1/"+textEN+"?appid=OuxmJgjV34EhKxPJDY4HvcUwJyT_v4Ur4LdpW1f3QiLQXWAAiYhZPZvdJIck9Ik-&format=xml&lang=en");
			URLConnection con = url.openConnection ();

			//Extraction des réponses (en XML)
			DocumentBuilder parseur = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc =  parseur.parse(con.getInputStream());

			//Traitement des réponses
			if(Integer.parseInt(doc.getElementsByTagName("resultset_spell").item(0).getAttributes().getNamedItem("totalhits").getTextContent()) > 0)
			{
				NodeList suggestionList = doc.getElementsByTagName("suggestion");

				String correctTextEN = suggestionList.item(0).getTextContent();
				
				namedEntity.clear();
				
				String[] elements = correctTextEN.split(" ");
				for(String element : elements)
					namedEntity.add(element);
			}

		}catch(ParserConfigurationException pce){
			System.err.println("Erreur de configuration du parseur DOM");
			System.err.println("lors de l'appel à fabrique.newDocumentBuilder();");
		}catch(SAXException se){
			System.err.println("Erreur lors du parsing du document");
			System.err.println("lors de l'appel à construteur.parse(xml)");
		}catch(IOException ioe){
			System.err.println("Erreur d'entrée/sortie");
			System.err.println("lors de l'appel à construteur.parse(xml)");
		}
		
		return namedEntity;
	}
}
