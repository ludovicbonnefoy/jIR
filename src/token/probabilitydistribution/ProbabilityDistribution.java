package token.probabilitydistribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public abstract class ProbabilityDistribution implements Serializable, ProbabilityDistributionInterface
{
	private static final long serialVersionUID = 3365020124806085622L;

	/** Nombre d'éléments total dans la collection */
	protected Long _total;

	/** Liste des éléments présents dans le corpus associé à son nombre d'occurences. */
	protected HashMap<String, Long> _freqs; 

	/**
	 * Initialisation d'une nouvelle distribution de termes.
	 */
	public ProbabilityDistribution()
	{
		_freqs = new HashMap<String, Long>();
	}

	/**
	 * Copie d'une distribution de termes.
	 * @param pd Distribution à copier.
	 */
	@SuppressWarnings("unchecked")
	public ProbabilityDistribution(ProbabilityDistribution pd)
	{
		_freqs = (HashMap<String, Long>) pd.getFrequenciesMap().clone();
		_total = pd.getVocabularySize();
	}

	/**
	 * Récupération d'une distribution sérialisée.
	 * @param serializedProbabilityDistribution Fichier contenant la distribution sérialisée.
	 */
	@SuppressWarnings("unchecked")
	public ProbabilityDistribution(File serializedProbabilityDistribution)
	{
		try 
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedProbabilityDistribution));
			NGramsProbabilityDistribution tmp = (NGramsProbabilityDistribution)(ois.readObject());

			_freqs = (HashMap<String, Long>) tmp.getFrequenciesMap().clone();
			_total = tmp.getVocabularySize();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de récupérer la probabilité d'apparition du terme.
	 * @param term Terme dont on veux la probabilité d'apparition.
	 * @return Probabilité d'apparition du terme.
	 */
	public Double get(String term)
	{
		if(_freqs.containsKey(term))
			return new Double(_freqs.get(term) / new Double(_total));
		else
			return new Double(0);
	}

	/**
	 * Ajout d'un nombre d'occurence d'un terme.
	 * @param term Terme dont on veut rajouter des occurences.
	 * @param occ Nombre d'occurences.
	 */
	public void add(String term, Long occ)
	{
		Long count = _freqs.containsKey(term) ? _freqs.get(term) : 0; //on récupère le compte précédent de ce n-gramme
		_freqs.put(term, count + occ);
		_total += occ;
	}

	/**
	 * Ajout d'occurences de termes.
	 * @param terms Association termes/nombre d'occurences.
	 */
	public void add(HashMap<String, Long> terms)
	{
		for(String term : terms.keySet())
			add(term, terms.get(term));
	}

	/**
	 * Contrairement à add, cette méthode REMPLACE la valeur précédente associée à un terme par un nouveau nombre d'occurences.
	 * Même effet si le terme n'était pas encore présent.
	 * @param term Terme que l'on veux ajouter.
	 * @param occ Nombre d'occurences associé.
	 */
	public void put(String term, Long occ)
	{
		if(_freqs.containsKey(term)) //si une valeur est déjà dans la map
			_total -= _freqs.get(term); //retire la valeur du compteur

		_freqs.put(term, occ);
		_total += occ;
	}

	/**
	 * Contrairement à add, cette méthode REMPLACE les valeurs précédentes associées à des termes par de nouveaux nombres d'occurences.
	 * Même effet si les termes n'étaient pas encore présents.
	 * @param terms Association terme/nombre d'occurences.
	 */
	public void put(HashMap<String, Long> terms)
	{
		for(String term : terms.keySet())
			put(term, terms.get(term));
	}

	/**
	 * Retourne true si le terme est présent.
	 * @param term Terme recherché.
	 * @return true si le terme est présent.
	 */
	public boolean containsKey(String term)
	{
		return _freqs.containsKey(term);
	}

	/**
	 * Permet de récupérer la liste des termes présents.
	 * @return Liste des termes présents.
	 */
	public Set<String> keySet()
	{
		return _freqs.keySet();
	}

	/**
	 * Renvoie l'ensemble des termes présents avec leur nombre d'occurences.
	 * @return Ensemble des couples termes/nombre d'occurences.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Long> getFrequenciesMap()
	{
		return (HashMap<String, Long>)_freqs.clone();
	}

	/**
	 * Retourne le nombre total de termes présents (somme du nombre d'occurences pour chaque terme). 
	 * @return Nombre total de termes présents.
	 */
	public Long getVocabularySize()
	{
		return _total;
	}

	/**
	 * Ajoute les termes et leur nombre d'occurences d'une distribution à l'instance.
	 * @param pdi Distribution de laquelle on va récupérer des termes et leur nombre d'occurences et les ajouter à l'instance.
	 */
	public void merge(ProbabilityDistributionInterface pdi)
	{
		if(pdi.getClass().equals(getClass()))
		{
			HashMap<String, Long> pdFreqs = pdi.getFrequenciesMap();

			Set<String> termes = pdFreqs.keySet();
			for(String terme : termes)
				add(terme, pdFreqs.get(terme));

			_total += pdi.getVocabularySize();
		}
	}

	/**
	 * Sérialise l'objet au chemin indiqué (chemin complet = contenant le nom).
	 * @param path Chemin complet où doit être stocké l'objet sérialisé.
	 */
	public void serialize(String path)
	{
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construction à partir d'un fichier ne contenant des fréquences.
	 * Un terme par ligne, associé à sa fréquence.
	 * @param file Fichier contenant les fréquences.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadFromFreqFile(File file) throws FileNotFoundException, IOException
	{
		_freqs = new HashMap<String, Long>();
		_total = new Long(0);

		BufferedReader lines = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		while((line = lines.readLine()) != null) //pour chaque token
		{
			String[] elements = line.split(" "); //on récupère les infos

			String term = "";
			for(int i = 0; i < elements.length-2; i++)
				term += elements[i];

			_freqs.put(term, new Long(elements[elements.length-1])); 
			_total += new Long(elements[elements.length-1]);
		}
	}
}
