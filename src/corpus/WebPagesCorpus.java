package corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import util.GetProperties;
import util.Log;
import web.page.downloader.AbstractExternalWebPageDownloader;
import web.page.downloader.ExternalWebPageDownloaderFactory;
import web.searchenginequery.AbstractWebSearchEngineQuery;
import web.searchenginequery.WebSearchEngineQueryFactory;

/**
 * Corpus pour lequel les pages web correspondantes aux urls recherchées sont collectées sur le web via un outil système dédié.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class WebPagesCorpus extends AbstractWebPagesCorpus 
{
	private static final long serialVersionUID = -6387970913933569000L;

	public WebPagesCorpus(File directory) throws FileNotFoundException
	{
		super(directory);
	}

	public WebPagesCorpus(String corpusPath)
	{
		super(corpusPath);
	}

	public boolean add(String url) 
	{
		try
		{
			if(url.endsWith(".pdf") || url.endsWith(".rdf") || url.endsWith(".doc") || url.endsWith(".ppt"))
				return false;
			AbstractExternalWebPageDownloader webPagesDownloader = ExternalWebPageDownloaderFactory.get(GetProperties.getInstance().getProperty("externalWebPagesDownloaderPath"));
			webPagesDownloader.download(_directory+"/"+_fileNumber,url);

			_webPages.put(url, new File(_directory+"/"+_fileNumber));
			_fileNumber++;
		} catch (InterruptedException e) {
			Log.getInstance().add(e);
			return false;
		} catch (IOException e) {
			Log.getInstance().add(e);
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) 
	{
		GetProperties properties = GetProperties.getInstance();
		properties.init("properties.properties");
		
		AbstractWebSearchEngineQuery qse = WebSearchEngineQueryFactory.get();
		qse.query("michael schumacher", 10); //Interrogation de Boss

		AbstractWebPagesCorpus webCorpus;
		try {
			webCorpus = WebPagesCorpusFactory.get(new File("tmpCorpus/"));
			Set<String> errors = webCorpus.build(new HashSet<String>(qse.getURLs())); //aspiration des pages
			for(String error : errors)
				System.out.println(error);
			
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
	}
}
