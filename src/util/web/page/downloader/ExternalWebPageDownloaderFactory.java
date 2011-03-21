package util.web.page.downloader;

import util.GetProperties;

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
