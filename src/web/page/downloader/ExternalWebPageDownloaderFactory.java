package web.page.downloader;

import util.GetProperties;

/**
 * Classe ayant pour but de fournir un outil pour récupérer des pages web à partir d'une url. 
 * Elle permet de ne pas à avoir à modifier le code en profondeur lorsque l'on souhaite modifier l'outil utilisé.
 * L'outil à utiliser est recherché dans un fichier properties. Dans le cas où rien n'est trouvé, un outil par défaut est créé.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class ExternalWebPageDownloaderFactory 
{
	public static AbstractExternalWebPageDownloader get(String executablePath)
	{
		if(GetProperties.getInstance().containsKey("externalWebPagesDownloader"))
		{
			String external = GetProperties.getInstance().getProperty("externalWebPagesDownloader");
			if(external.equals("wget"))
				return new WgetWebPageDownloader(executablePath);
		}

		return new WgetWebPageDownloader(executablePath);
	}
}
