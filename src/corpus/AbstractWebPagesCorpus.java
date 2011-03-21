package corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractWebPagesCorpus implements WebPagesCorpusInterface
{
	protected HashMap<String, File> _webPages;
	protected File _directory;
	
	/** Numéro à associer à la prochaine page web*/
	protected Integer _fileNumber;

	/**
	 * Construit un corpus vide
	 */
	AbstractWebPagesCorpus()
	{
		_webPages = new HashMap<String, File>();
		_fileNumber = 0;
	}

	/**
	 * Build corpus with files
	 * @param files Files
	 */
	AbstractWebPagesCorpus(File directory, Set<String> urls) throws FileNotFoundException, IOException, InterruptedException
	{
		build(directory, urls);
	}
	
	public void destroy()
	{
		File[] files = _directory.listFiles(); 
		for(int i=0; i<files.length; i++) 
			files[i].delete(); 
		
		_directory = null;
		_webPages = new HashMap<String, File>();
		_fileNumber = 0;
	}

	public void build(File directory, Set<String> urls) throws FileNotFoundException, IOException, InterruptedException
	{
		destroy();
		
		if( directory.exists() )
		{
			_directory = directory;
			_webPages = new HashMap<String, File>();
			_fileNumber = 0;

			add(urls);
		}
		else
			throw (new FileNotFoundException("directory not found"));
	}
	
	public void add(Set<String> urls) throws IOException,InterruptedException
	{
		for(String url : urls)
			add(url);
	}
	
	public void delete(String url)
	{
		if(_webPages.containsKey(url))
		{
			_webPages.get(url).delete();
			_webPages.remove(url);
		}
	}
	
	public void delete(Set<String> urls)
	{
		for(String url : urls)
		{
			if(_webPages.containsKey(url))
			{
				_webPages.get(url).delete();
				_webPages.remove(url);
			}
		}
	}
	
	public File getWebPage(String url)
	{
		if(_webPages.containsKey(url))
			return _webPages.get(url);
		
		return null;
	}
	
	public Set<File> getWebPages(Set<String> urls)
	{
		HashSet<File> webPagesSet = new HashSet<File>();
		
		for(String url : urls)
		{
			if(_webPages.containsKey(url))
				webPagesSet.add(_webPages.get(url));
			else
				webPagesSet.add(null);
		}
		
		return webPagesSet;
	}
	
	public Set<String> getURLs()
	{
		return _webPages.keySet();
	}

	public File getCorpusDirectory()
	{
		return _directory;
	}
	
	public void Save(String path)
	{
		try 
    	{
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)); //Emplacement de l'objet sérialisé
    		oos.writeObject(this); //enregistrement
    		oos.flush();
    		oos.close();
    	}
    	catch (java.io.IOException e) {
    		e.getMessage();
    	}
	}
	
	public static AbstractWebPagesCorpus Load(String path)
	{
		try 
    	{
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
    		
    		return ((AbstractWebPagesCorpus)(ois.readObject()));
    	}catch(Exception e){
    		e.getMessage();
    	}
    	
    	return null;
	}
	
}
