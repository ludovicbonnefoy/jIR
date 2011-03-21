package corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;

import util.FileReader;
import util.GetProperties;
import util.web.page.downloader.AbstractExternalWebPageDownloader;
import util.web.page.downloader.ExternalWebPageDownloaderFactory;

public class WebPagesCorpusTrecForm extends AbstractWebPagesCorpus 
{
	public WebPagesCorpusTrecForm()
	{
		super();
	}
	
	public WebPagesCorpusTrecForm(File directory, Set<String> urls) throws FileNotFoundException, IOException, InterruptedException
	{
		super(directory, urls);
	}
	
	public void add(String url) throws IOException,InterruptedException
	{
		AbstractExternalWebPageDownloader webPagesDownloader = ExternalWebPageDownloaderFactory.get(GetProperties.getInstance().getProperty("externalWebPagesDownloaderPath"));
    	webPagesDownloader.download(url);
    	
    	File file = new File("/tmp/webPagesDownloaded"); //vérification de la taille de la page (si >500ko alors on ne la traite pas, au cas où ce ne soit pas une page web).
    	if(file.length() <= 500000)
    	{	
	    	//Écriture du fichier au format TREC
	    	PrintWriter doc = new PrintWriter(new OutputStreamWriter (new FileOutputStream (_directory+"/"+_fileNumber),"UTF-8"));
	    	doc.println("<DOC>");
	    	doc.println("<DOCNO>"+_fileNumber+"</DOCNO>");
	    	doc.println("<DOCHDR>\n"+url+"\n</DOCHDR>");
	
	    	doc.println(FileReader.fileToString(file));
	
	    	doc.println("</DOC>");
	    	doc.close();
	    	
	    	_webPages.put(url, new File(_directory+"/"+_fileNumber));
	    	_fileNumber ++;
    	}
	}

}
