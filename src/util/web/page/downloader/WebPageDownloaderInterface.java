package util.web.page.downloader;

import java.io.IOException;

public interface WebPageDownloaderInterface 
{
	/**
	 * Récupère la page web correspondante à l'url et la stocke dans file
	 * @param filePath chemin complet (nom compris) où va être stocké la page web
	 * @param url URL de la page web à récupérer
	 */
	void download(String filePath, String url) throws InterruptedException,IOException;
	
	/**
	 *  Récupère la page web correspondante à l'url et la stocke dans /tmp/webPageDownloaded
	 * @param url URl de la page web à récupérer
	 */
	void download(String url) throws InterruptedException,IOException;
}
