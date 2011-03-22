package web.page.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WgetWebPageDownloader extends AbstractExternalWebPageDownloader 
{
	public WgetWebPageDownloader(String executablePath)
	{
		super(executablePath);
	}
	
	public void download(String filePath, String url) throws InterruptedException,IOException
	{
        Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(new String[]{_executablePath,"-O",filePath,"-T","5","-t","2",url}); //récupération de la page web
    	
		BufferedReader brP = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    	while(brP.readLine() != null);
    	process.waitFor();
	}

}
