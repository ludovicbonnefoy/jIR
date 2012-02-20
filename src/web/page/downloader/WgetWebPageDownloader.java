package web.page.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Permet de récupérer des pages web en utilisant l'outil wget.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class WgetWebPageDownloader extends AbstractExternalWebPageDownloader 
{
	public WgetWebPageDownloader(String executablePath)
	{
		super(executablePath);
	}
	
	public void download(String filePath, String url) throws InterruptedException,IOException
	{
        Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(new String[]{_executablePath,"-O",filePath,"--user-agent","Mozilla/5.0X11Linuxi686rv7.0.1Gecko/20100101Firefox/7.0.1","-T","5","-t","2",url}); //récupération de la page web
    	
		BufferedReader brP = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    	while(brP.readLine() != null);
    	process.waitFor();
    	brP.close();
    	process.destroy();
	}

}
