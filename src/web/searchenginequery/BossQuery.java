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

public class BossQuery extends AbstractWebSearchEngineQuery
{
	public BossQuery()
	{
		super();
	}

	public void query(String query, int count)
	{
		try
		{
			query = URLEncoder.encode(query,"UTF-8"); //Transforme la requête en chaîne valide pour insérer dans une url
		}catch(Exception e){
			System.err.println(e.getMessage());
		}

		_urls = new ArrayList<String>(0);
		_snippets = new ArrayList<String>(0);
		_totalHits = new Long(0);

		Integer nbrLoops = count/50;
		if(!(new Integer(count%50)).equals(0))
			++nbrLoops;

		for(Integer i = 0; i <= nbrLoops ; ++i)//Boss ne permet de récupérer que 50 résultats à la fois
		{
			try{
				//Interrogation
				URL url = new URL("http://boss.yahooapis.com/ysearch/web/v1/"+query+"?appid=OuxmJgjV34EhKxPJDY4HvcUwJyT_v4Ur4LdpW1f3QiLQXWAAiYhZPZvdJIck9Ik-&format=xml&count=50&filter=-hate,-porn&abstract=long&lang=en&strictlang=1&type=html,text&start="+i);
				URLConnection con = url.openConnection ();

				//Extraction des réponses (en XML)
				DocumentBuilder parseur = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc =  parseur.parse(con.getInputStream());


				//Traitement des réponses
				if(i == 0)
				{
					_totalHits = Long.parseLong(doc.getElementsByTagName("resultset_web").item(0).getAttributes().getNamedItem("deephits").getTextContent());
				}

				NodeList snippetsList = doc.getElementsByTagName("abstract");
				NodeList urlsList = doc.getElementsByTagName("url");
				for(int j = 0; j < snippetsList.getLength(); ++j) //récupération des snippets et des urls 
				{
					if((i*50)+j >= count) //stop si le nombre d'élément récupérer est celui souhaité
						break;

					_snippets.add(snippetsList.item(j).getTextContent().replaceAll("</?b>","")); //récupère le snippet du résultat (i*50)+j et supprime les balises <br>
					_urls.add(urlsList.item(j).getTextContent());
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
}
