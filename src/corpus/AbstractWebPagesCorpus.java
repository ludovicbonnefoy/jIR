package corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe abstraite implémentant la quasi-totalité des méthodes de l'interface correspondante.
 * La seule méthode non implémentée est la seule qui est particulière à la manière de récupérer
 * les pages web correspondantes aux urls pour chaque corpus différent, à savoir la fonction add.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractWebPagesCorpus implements WebPagesCorpusInterface, Serializable
{
	private static final long serialVersionUID = 7571330063113173296L;

	/** Association url/ document dans le corpus. */
	protected HashMap<String, File> _webPages;

	/** Dossier où est contenu le corpus. */
	protected File _directory;

	/** Numéro à associer à la prochaine page web pour stockage dans le dossier. */
	protected Integer _fileNumber;

	/**
	 * Initialisation d'un corpus vide.
	 * @param directory Dossier où vont être stockés les pages web.
	 */
	AbstractWebPagesCorpus(File directory) throws FileNotFoundException
	{
		if(!directory.exists())
			throw (new FileNotFoundException("directory not found"));

		_webPages = new HashMap<String, File>();
		_fileNumber = 0;
		_directory = new File(directory.getAbsolutePath());
	}
	
	/**
	 * Construit un corpus comme une copie d'un autre.
	 * @param wpc Corpus à copier.
	 */
	public AbstractWebPagesCorpus(AbstractWebPagesCorpus wpc) 
	{
		_directory = new File(wpc.getCorpusDirectory().getAbsolutePath());
		_fileNumber = wpc.getFileNumber();
		
		_webPages = new HashMap<String, File>();
		Set<String> urls = wpc.getURLs();
		for(String url : urls)
			_webPages.put(url, wpc.getWebPage(url));
	}

	/**
	 * Récupère un corpus sérialisé.
	 * @param corpusPath Chemin complet du corpus sérialisé.
	 */
	AbstractWebPagesCorpus(String corpusPath)
	{
		try {
			readObject(new ObjectInputStream(new FileInputStream(corpusPath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	public Set<String> build(Set<String> urls)
	{
		destroy();

		HashSet<String> errors = new HashSet<String>();

		_webPages = new HashMap<String, File>();
		_fileNumber = 0;

		errors = (HashSet<String>) add(urls);

		return errors;
	}

	public Set<String> add(Set<String> urls)
	{
		HashSet<String> errors = new HashSet<String>();

		for(String url : urls)
		{
			boolean success = add(url);

			if(!success)
				errors.add(url);
		}

		return errors;
	}

	public boolean delete(String url)
	{
		if(_webPages.containsKey(url))
		{
			_webPages.get(url).delete();
			_webPages.remove(url);
			return true;
		}

		return false;
	}

	public Set<String> delete(Set<String> urls)
	{
		HashSet<String> errors = new HashSet<String>();

		for(String url : urls)
		{
			boolean success = delete(url);

			if(!success)
				errors.add(url);
		}

		return errors;
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

	public boolean contains(String url)
	{
		return _webPages.containsKey(url);
	}

	public File getCorpusDirectory()
	{
		return new File(_directory.getAbsolutePath());
	}

	public Integer getFileNumber()
	{
		return _fileNumber;
	}

	public void serialize(String path)
	{
		try 
		{
			writeObject(new ObjectOutputStream(new FileOutputStream(path)));
		}
		catch (java.io.IOException e) {
			e.getMessage();
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(this);
		out.flush();
		out.close();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		AbstractWebPagesCorpus tmp = (AbstractWebPagesCorpus)(in.readObject());

		for(String url : tmp.getURLs())
			_webPages.put(url, tmp.getWebPage(url));

		_directory = tmp.getCorpusDirectory();

		_fileNumber = tmp.getFileNumber();
	}
}
