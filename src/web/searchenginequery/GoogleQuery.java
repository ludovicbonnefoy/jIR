package web.searchenginequery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import util.GetProperties;
import util.Log;

/**
 * Permet d'interroger le moteur de recherche Google.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class GoogleQuery extends AbstractWebSearchEngineQuery 
{
	public GoogleQuery()
	{
		super();
	}

	/**
	 * Interrogation de Google via Lynx pour éviter les limitations
	 * Attention totalHits correct si totalHits<count, sinon totalHits = count..
	 * @param query Requête.
	 * @param count Nombre de résultats souhaités.
	 */
	public void query(String query, int count)
	{
		_totalHits = new Long(0);
		_urls = new ArrayList<String>();
		_snippets = new ArrayList<String>();
		
		try {
			Runtime runtime = Runtime.getRuntime();
			String[] parameters = new String[]{GetProperties.getInstance().getProperty("lynx"),"-dump","http://www.google.com/search?source=ig&hl=en&num="+count+"&q=%22"+query.replaceAll(" ", "+")+"%22"};
			Process process = runtime.exec(parameters);
			//lecture de la sortie de lynx
			BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
			String tokenLine = new String();

			ArrayList<Integer> links = new ArrayList<Integer>();
			ArrayList<String> tmpSnippet = null;

			int phase = 0;
			while((tokenLine = brTokens.readLine()) != null) //pour chaque token
			{
				tokenLine = tokenLine.trim();
				if(tokenLine.equals(""))
					continue;

				if(phase==0) //entete
				{
					if(tokenLine.contains("About") && tokenLine.contains("results"))
						_totalHits = new Long(tokenLine.substring(tokenLine.indexOf("About")+6, tokenLine.indexOf("results")).replaceAll(",", "").trim());
					else if(tokenLine.equals("Search Results"))
					{
						if(_totalHits == 0)
							break;
						phase = 1;
					}
				}
				else if(phase==1) //numéros liens et snippets
				{
					if(tokenLine.contains("* Everything") || links.size() >= count)
					{
						phase = 2;
						continue;
					}
					try
					{
						
						Pattern p = Pattern.compile("[0-9]+\\.");
						Matcher m = p.matcher(tokenLine);
						if(m.find() && m.start() == 0)
						{
							if(tmpSnippet!=null)
							{
								String snippet = "";
								for(int i = 0; i < tmpSnippet.size()-2; i++) //-2 car la dernière phrase ne fait pas partie du snippet mais est un bout de l'adresse web
									snippet += " "+tmpSnippet.get(i);

								_snippets.add(snippet.trim());
							}
							tmpSnippet = new ArrayList<String>();

							String title = tokenLine.substring(m.end());
							Pattern p2 = Pattern.compile("\\[[0-9]+\\]");
							m = p2.matcher(title);
							if(m.find())
								links.add(new Integer(title.substring(m.start()+1, m.end()-1)));
						}
						else
							tmpSnippet.add(tokenLine);
					}catch(PatternSyntaxException pse){
						Log.getInstance().add(pse);
						pse.printStackTrace();
					}
				}
				else if(phase == 2) //pĥase 2: pied de page
				{
					if(tokenLine.equals("Références"))
						phase = 3;
				}
				else //phase 3: numéro lien => lien
				{
					String[] elements = tokenLine.split(" ");
					if(links.contains(new Integer(elements[0].replaceFirst("\\.", ""))))
						_urls.add(elements[1]);
				}
			}
			brTokens.close();
			process.destroy();
		} catch (UnsupportedEncodingException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
	}

	//	/**
	//	 * Interrogation de Google via son API
	//	 * Attention totalHits correct si totalHits<count, sinon totalHits = count..
	//	 * @param query Requête.
	//	 * @param count Nombre de résultats souhaités.
	//	 */
	//	public void query(String query, int count)
	//	{
	//		query = query.replaceAll("[^\\p{ASCII}]", "");
	//
	//		// Convert spaces to +, etc. to make a valid URL
	//		try {
	//			query = URLEncoder.encode(query, "UTF-8");
	//		} catch (UnsupportedEncodingException e) {
	//						Log.getInstance().add(e);
//	e.printStackTrace();
	//		}
	//
	//		_urls = new ArrayList<String>(0);
	//		_snippets = new ArrayList<String>(0);
	//		_totalHits = new Long(0);
	//
	//		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance("ABQIAAAAN01EmeM_E-n3GDBY_wnMWxQjY9LYFWCMM67ypO_BTNFHtzJ1KqBTMJIc0hiI0cTpdMOnvC4H9mEXc9s");
	//		WebSearchQuery wsquery = factory.newWebSearchQuery();
	//		wsquery.withQuery(query);
	//
	//		for(int i = 0; i < count; i+=4)
	//		{
	//			System.out.println(i);
	//			PagedList<WebResult> response = wsquery.withStartIndex(i).list();
	//			
	//			if(i==0)
	//				_totalHits = response.getEstimatedResultCount();
	//
	//			for(int j = 0; j < 4; j++)
	//			{
	//				WebResult result = response.get(j);
	//				
	//				if(i+j+1 > count)
	//					break;
	//				
	//				_urls.add(result.getUrl());
	//				_snippets.add(result.getContent());
	//			}
	//		}
	//	}

	public static void main(String[] args) 
	{
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");

		GoogleQuery qg = new GoogleQuery();
		qg.query("ludovic bonnefoy", 1);
	}
}
