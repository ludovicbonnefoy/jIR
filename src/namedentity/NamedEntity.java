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


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.GetProperties;
import util.SortKeysMapByNumberValues;
import web.searchenginequery.AbstractWebSearchEngineQuery;
import web.searchenginequery.WebSearchEngineQueryFactory;
import word.tokeniser.AbstractExternalTokeniser;
import word.tokeniser.ExternalTokeniserFactory;


/**
 * Forme la plus simple d'une entité nommée, contient uniquement le texte de l'EN.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NamedEntity implements Serializable
{
	private static final long serialVersionUID = -7492535373780960285L;
	
	protected String _namedEntity;
	protected String _canonicalForm;

	/**
	 * Initialise l'EN en mettant la string dans la première case du tableau.
	 * @param namedEntity
	 */
	public NamedEntity(String namedEntity)
	{
		_namedEntity = namedEntity;
		_canonicalForm = null;
	}
	
	public String getName()
	{
		return _namedEntity;
	}
	
	public void setName(String name)
	{
		_canonicalForm = null;
		_namedEntity = name;
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
	public String getCanonicalForm(String typeNamedEntity, String contextRequest)
	{
		if(_canonicalForm != null)
			return _canonicalForm;
		
		try
		{
			AbstractWebSearchEngineQuery qse = WebSearchEngineQueryFactory.get();
	        qse.query(_namedEntity+" "+contextRequest,10);
	        
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
		}
	        
		return _canonicalForm;
	}
	
	/**
	 * Essaye de corriger l'EN d'erreurs dans l'écriture.
	 * Interroge Boss et corrige si il y a une suggestion.
	 * @param namedEntity
	 * @return
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
