package web.page.downloader;

import java.io.IOException;

/**
 * Les classes implémentant cette inferface ont pour but de récupèrer la page web correspondante à l'url et la stocker dans un fichier.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
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
