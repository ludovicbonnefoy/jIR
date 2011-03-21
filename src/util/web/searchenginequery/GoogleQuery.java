package util.web.searchenginequery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleQuery extends AbstractWebSearchEngineQuery 
{
	public GoogleQuery()
	{
		super();
	}

	/**
	 * Interrogation de Google via son API
	 * Attention totalHits correct si totalHits<count, sinon totalHits = count..
	 * @param query Requête
	 * @param count Nombre de résultats souhaités
	 */
	public void query(String query, int count)
	{
		query = query.replaceAll("[^\\p{ASCII}]", "");

		URL urlR = null;
		_totalHits = new Long(0);

		try {
			// Convert spaces to +, etc. to make a valid URL
			query = URLEncoder.encode(query, "UTF-8");

			_urls = new ArrayList<String>(0);
			_snippets = new ArrayList<String>(0);
			_totalHits = new Long(0);

			Integer nbrLoops = count/8;
			if(!(new Integer(count%8)).equals(0))
				++nbrLoops;

			int nbr = 0;
			for(Integer i = 0; i <= nbrLoops ; ++i)//Yahoo ne permet de récupérer que 8 résultats à la fois
			{  
				StringBuffer buffer = new StringBuffer();

				urlR = new URL("http://ajax.googleapis.com/ajax/services/search/web?start="+ (i*8) + "&hl=en&rsz=large&v=1.0&q=" + query);
				URLConnection con = urlR.openConnection ();

				InputStreamReader isr = new InputStreamReader(con.getInputStream());
				Reader in = new BufferedReader(isr);
				int ch;
				while ((ch = in.read()) > -1)
				{
					buffer.append((char)ch);
				}


				Pattern p = Pattern.compile("\"responseData\": null");
				Matcher m = p.matcher(buffer.toString());
				if(m.find())
				{
					_totalHits = new Long(i*8);
					break;
				}

				String url = buffer.toString();

				Pattern purl = Pattern.compile("unescapedUrl\":\"[^\"]+");
				Matcher murl = purl.matcher(url);

				Pattern psnippet = Pattern.compile("content\":\"[^\"]+");
				Matcher msnippet = psnippet.matcher(url);

				while(murl.find())
				{
					if(nbr >= count)
						break;

					String tmp = url.substring(murl.start(),murl.end()).replaceAll("unescapedUrl\":\"","").replaceAll(".+/","");
					if(tmp.contains(".") && !tmp.contains("?"))//si ? alors page web
						if(!(tmp.contains("htm") || tmp.contains("php") || tmp.contains("aspx")))
							continue;

					_urls.add(url.substring(murl.start(),murl.end()).replaceAll("unescapedUrl\":\"",""));

					//pour les snippets essayer de faire décodage pour les caractères spéciaux
					msnippet.find();
					_snippets.add(url.substring(msnippet.start(),msnippet.end()).replaceAll("content\":\"",""));

					nbr++;
					_totalHits++; //bancale..
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Google.query ERROR. URL WAS " + urlR);
		}
	}

}
