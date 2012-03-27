package web.searchenginequery;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.Log;

public class BingQuery extends AbstractWebSearchEngineQuery 
{
	private String _market;
	
	/** Liste des titres des résultats dans l'ordre décroissants de pertinence */
    protected ArrayList<String> _titles;
	
	public BingQuery()
	{
		super();
		_market = "en-US";
		_titles = new ArrayList<String>();
	}
	
	public BingQuery(String market)
	{
		super();
		_market = market;
		_titles = new ArrayList<String>();
	}

	@Override
	public void query(String query, int count) 
	{
		try
		{
			query = URLEncoder.encode(query,"UTF-8"); //Transforme la requête en chaîne valide pour insérer dans une url
		}catch(Exception e){
			Log.getInstance().add(e);
			e.printStackTrace();
		}

		_urls = new ArrayList<String>(0);
		_snippets = new ArrayList<String>(0);
		_titles = new ArrayList<String>(0);
		_totalHits = new Long(0);

		
		Integer nbrLoops = count/50;
		if(!(new Integer(count%50)).equals(0))
			++nbrLoops;
		
		for(Integer i = 0; i < nbrLoops ; ++i)//Bing ne permet de récupérer que 50 résultats à la fois
		{
			try{
				//Interrogation
				URL url = new URL("http://api.bing.net/xml.aspx?AppId=018E3B719F8E97A14762E0C045B841493D8FD68E&Version=2.2&Market="+_market+"&Query="+query+"&Sources=web&Adult=Strict&Web.FileType=htm&Web.Count=50&Web.Offset="+(50*i));
				
				URLConnection con = url.openConnection ();

				//Extraction des réponses (en XML)
				DocumentBuilder parseur = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc =  parseur.parse(con.getInputStream());
				if(doc.getElementsByTagName("web:Total").getLength() == 0)
				{
					_totalHits = new Long(0);
					break;
				}

				//Traitement des réponses
				if(i == 0)
					_totalHits = Long.parseLong(doc.getElementsByTagName("web:Total").item(0).getTextContent());

				NodeList results = doc.getElementsByTagName("web:WebResult");
				for(int j = 0; j < results.getLength(); ++j) //récupération des snippets et des urls 
				{
					if((i*50)+j >= count) //stop si le nombre d'élément récupérer est celui souhaité
						break;
					
					NodeList childs = results.item(j).getChildNodes();

					boolean findSnippet = false, findUrl = false, findTitle = false;
					for(int k = 0; k < childs.getLength(); ++k)
					{
						if(childs.item(k).getNodeName().equals("web:Title"))
						{
							if(findTitle == true)
								break;
							findTitle = true;
							_titles.add(childs.item(k).getTextContent());
						}
						else if(childs.item(k).getNodeName().equals("web:Description"))
						{
							if(findTitle)
							{
								_snippets.add(childs.item(k).getTextContent().replaceAll("</?b>","")); //récupère le snippet du résultat (i*50)+j et supprime les balises <br>
								findSnippet = true;
							}
						}
						else if(childs.item(k).getNodeName().equals("web:Url"))
						{
							if(findTitle && findSnippet)
							{
								_urls.add(childs.item(k).getTextContent());
								findUrl = true;
							}
							break;
						}
					}
					
					if(findTitle == true && (findSnippet == false || findUrl == false))
						_titles.remove(_titles.size()-1);
					if(findSnippet==true && findUrl==false)
						_snippets.remove(_snippets.size()-1);
				}
			}catch(ParserConfigurationException pce){
				Log.getInstance().add(pce);
				pce.printStackTrace();
				System.err.println("Erreur de configuration du parseur DOM");
				System.err.println("lors de l'appel à fabrique.newDocumentBuilder();");
			}catch(SAXException se){
				Log.getInstance().add(se);
				se.printStackTrace();
				System.err.println("Erreur lors du parsing du document");
				System.err.println("lors de l'appel à construteur.parse(xml)");
			}catch(IOException ioe){
				Log.getInstance().add(ioe);
				ioe.printStackTrace();
				System.err.println("Erreur d'entrée/sortie");
				System.err.println("lors de l'appel à construteur.parse(xml)");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTitles() 
	{
		return (ArrayList<String>)_titles.clone();
	}
}
