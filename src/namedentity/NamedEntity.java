package namedentity;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import namedentity.ner.AbstractNamedEntityRecognitionTool;
import namedentity.ner.NamedEntityRecognitionToolFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import token.tokeniser.AbstractExternalTokeniser;
import token.tokeniser.ExternalTokeniserFactory;
import util.GetProperties;
import util.SortKeysMapByNumberValues;
import web.searchenginequery.AbstractWebSearchEngineQuery;
import web.searchenginequery.WebSearchEngineQueryFactory;

/**
 * Forme la plus simple d'une entité nommée, contient uniquement son nom et sa forme canonique.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NamedEntity implements Serializable
{
	private static final long serialVersionUID = -7492535373780960285L;
	
	/** Texte de l'entité nommée */
	protected String _namedEntity;
	
	/** Forme canonique de l'entité nommée */
	protected String _canonicalForm;

	/**
	 * Construction d'une entité nommée avec un texte.
	 * @param namedEntity Texte de l'entité.
	 */
	public NamedEntity(String namedEntity)
	{
		_namedEntity = namedEntity;
		_canonicalForm = null;
	}
	
	/**
	 * Retourne le texte de l'entité nommée (nom par abus).
	 * @return Texte de l'entité nommée.
	 */
	public String getName()
	{
		return _namedEntity;
	}
	
	/**
	 * Retourne le texte de l'entité nommée (nom par abus).
	 * @return Texte de l'entité nommée.
	 */
	public String toString()
	{
		return _namedEntity;
	}
	
	/**
	 * Permet de changer le texte de l'entité nommée.
	 * @param name Nouveau texte de l'entité nommée.
	 */
	public void setName(String name)
	{
		_namedEntity = name;
		_canonicalForm = null; //mis à nul car le texte de l'en a changé
	}
	
	/**
	 * Récupère la forme canonique d'une entité nommées.
	 * Principe : Récupère la forme contenant le texte actuel de l'entité nommée passée en référence qui à la plus forte 
	 * occurence dans les dix premiers snippets récupéré en interrogeant un moteur de recherche sur le web avec pour requête l'entité nommée et un contexte.
	 * <br/> Ex : <br/>
	 * EN : massa <br/>
	 * on interroge le web avec "massa teammates michael schumacher" et on se rend compte que l'EN contenant "massa" et la plus fréquente dans les dix premiers snippets est "felipe massa". 
	 * @param context Supplément à la requête pour être sur de récupérer des snippets traitant de l'entité nommée (et pas d'une homonyme).
	 */
	public String getCanonicalForm(String context)
	{
		if(_canonicalForm != null) //Si la forme canonique a déjà été trouvée
			return _canonicalForm; //on la retourne
		
		try
		{
			AbstractWebSearchEngineQuery qse = WebSearchEngineQueryFactory.get();
	        qse.query(_namedEntity+" "+context,10);
	        
	        String text = new String(); //contient le texte des snippets
	        
	        for(String snippet : qse.getSnippets())
	        	text += snippet+" .";
	        
	        if(text.trim().equals(""))
	        	return _namedEntity;
	        
	        AbstractNamedEntityRecognitionTool NER = NamedEntityRecognitionToolFactory.get();
			text = NER.proceed(text);
			
			AbstractExternalTokeniser treeTagger = ExternalTokeniserFactory.get(GetProperties.getInstance().getProperty("externalTokeniserPath"));
			ArrayList<String> tokens = treeTagger.tokenise(text);
	
	        HashMap<String, Integer> forms = new HashMap<String, Integer>();
	        String formNamedEntity = new String(); 
	        
	        for(String token : tokens) //pour chaque token
	        {
	            if(token.matches("<[^/][^>]*>")) //si on a une balise ouvrante, donc le type d'une EN
	                formNamedEntity = new String();
	            else if(token.matches("</[^>]*>")) //si on a une balise fermante, donc la fin d'une EN
	            {
	                formNamedEntity = formNamedEntity.trim();
	                
	                if(formNamedEntity.contains(_namedEntity))
	                {
	                	Integer count = forms.containsKey(formNamedEntity) ? forms.get(formNamedEntity) : 0;
	                	forms.put(formNamedEntity, count+1);
	                }
	            }
	            else if(!token.equals(".")) //si on a un point on passe
	            	formNamedEntity += token+" ";
	        }
	        
	        if(forms.size() > 0)
	        {
	        	List<Object> sortedNamedEntities = SortKeysMapByNumberValues.descendingSort(new HashMap<Object, Number>(forms)); //tri des ENs en fonction du score de compacité
				_canonicalForm = ((String)sortedNamedEntities.get(0)).trim();
	        }
		}catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	        
		return _canonicalForm;
	}
	
	/**
	 * Vérifie et corrige le texte de l'entité nommée d'erreurs dans l'écriture.
	 * Interroge Boss et corrige si il y a une suggestion.
	 */
	public void correctSpelling()
	{
		try
		{
			String textEN = URLEncoder.encode(_namedEntity,"UTF-8"); //Transforme la requête en chaîne valide pour insérer dans une url

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

				_namedEntity = suggestionList.item(0).getTextContent();
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
	}
}
