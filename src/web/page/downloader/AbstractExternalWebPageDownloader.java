package web.page.downloader;

import java.io.IOException;

/**
 * Les classes dérivant de celle ci permettent de télécharger des pages web en utilisant des programmes installés sur la machine.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractExternalWebPageDownloader implements WebPageDownloaderInterface 
{
	/** Chemin complet où l'on va trouver l'éxécutable. */
	protected String _executablePath;
	
	/**
	 * Construit un objet et enregistre le chemin pour trouver l'éxécutable.
	 * @param executablePath Chemin de l'éxécutable.
	 */
	public AbstractExternalWebPageDownloader(String executablePath)
	{
		_executablePath = executablePath;
	}
	
	public void download(String url) throws InterruptedException,IOException
	{
		download("/tmp/webPagesDownloaded", url);
	}
}
