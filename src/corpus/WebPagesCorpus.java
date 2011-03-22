package corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import util.GetProperties;
import web.page.downloader.AbstractExternalWebPageDownloader;
import web.page.downloader.ExternalWebPageDownloaderFactory;

public class WebPagesCorpus extends AbstractWebPagesCorpus 
{
	public WebPagesCorpus()
	{
		super();
	}
	
	public WebPagesCorpus(File directory, Set<String> urls) throws FileNotFoundException, IOException, InterruptedException
	{
		super(directory, urls);
	}
	
	public void add(String url) throws IOException, InterruptedException 
	{
		AbstractExternalWebPageDownloader webPagesDownloader = ExternalWebPageDownloaderFactory.get(GetProperties.getInstance().getProperty("externalWebPagesDownloaderPath"));
    	webPagesDownloader.download(_directory+"/"+_fileNumber,url);
    	
    	_webPages.put(url, new File(_directory+"/"+_fileNumber));
    	_fileNumber++;
	}

}
