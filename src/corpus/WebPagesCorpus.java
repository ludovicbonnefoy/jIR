package corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import util.GetProperties;
import web.page.downloader.AbstractExternalWebPageDownloader;
import web.page.downloader.ExternalWebPageDownloaderFactory;

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
			AbstractExternalWebPageDownloader webPagesDownloader = ExternalWebPageDownloaderFactory.get(GetProperties.getInstance().getProperty("externalWebPagesDownloaderPath"));
			webPagesDownloader.download(_directory+"/"+_fileNumber,url);

			_webPages.put(url, new File(_directory+"/"+_fileNumber));
			_fileNumber++;
		} catch (InterruptedException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}
