package corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import util.FileReader;
import util.GetProperties;
import web.page.downloader.AbstractExternalWebPageDownloader;
import web.page.downloader.ExternalWebPageDownloaderFactory;

/**
 * Corpus pour lequel les pages web correspondantes aux urls recherchées sont collectées sur le web via un outil système dédié.
 * Les pages web récupérées sont mises en pages selon la "norme" des campagnes d'évaluation TREC.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class WebPagesCorpusTrecForm extends AbstractWebPagesCorpus 
{
	private static final long serialVersionUID = -4608179288088729718L;

	public WebPagesCorpusTrecForm(File directory) throws FileNotFoundException
	{
		super(directory);
	}
	
	public WebPagesCorpusTrecForm(String corpusPath)
	{
		super(corpusPath);
	}

	public boolean add(String url)
	{
		try 
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
		} catch (InterruptedException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
}
