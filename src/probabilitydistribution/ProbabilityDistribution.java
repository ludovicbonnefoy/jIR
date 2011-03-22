package probabilitydistribution;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * Représente une distribution de probabilité.
 * Elle est du type pD[element] = proba d'apparition.
 * Propose un constructeur permettant de créer cette distribution à partir de fréquences.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class ProbabilityDistribution implements java.io.Serializable
{
	private static final long serialVersionUID = -2120076421809371259L;

	/** Nombre d'éléments total dans la collection */
	protected Long _total;
	
	/** Liste des éléments présents dans le corpus associé à son nombre d'occurences. */
	protected HashMap<String, Long> _freqs; 

	/**
	 * Initialise l'instance.
	 */
	public ProbabilityDistribution()
	{
		_total = new Long(0);
		_freqs = new HashMap<String, Long>();
	}

	/**
	 * Initialise l'instance.
	 * @param frequencies Fréquences d'apparitions
	 */
	public ProbabilityDistribution(HashMap<String,Long> frequencies)
	{
		_total = new Long(0);

		_freqs = frequencies;

		for(String element : _freqs.keySet()) //calcul des occurences totales
			_total += _freqs.get(element);
	}
	
	public ProbabilityDistribution(String path)
	{
		try {
			readObject(new ObjectInputStream(new FileInputStream(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de récupérer la probabilité associé à la clé.
	 * @param key Clé
	 * @return Valeur
	 */
	public Double get(String key)
	{
		if(_freqs.containsKey(key))
			return new Double(_freqs.get(key) / new Double(_total));
		else
			return new Double(0);
	}

	/**
	 * Associe la valeur freq à la clé key.
	 * Met à jour le compteur total et la map.
	 * @param key Clé
	 * @param freq Valeur associée
	 */
	public void put(String key, Long freq)
	{
		if(_freqs.containsKey(key)) //si une valeur est déjà dans la map
			_total -= _freqs.get(key); //retire la valeur du compteur

		_freqs.put(key, freq);

		_total += freq; //rajoute la valeur au total 
	}

	/**
	 * Retourne vrai si une valeur est associée à cette clé.
	 * @param key Clé recherchée
	 * @return Une valeur pour cette clé?
	 */
	public boolean containsKey(String key)
	{
		return _freqs.containsKey(key);
	}

	/**
	 * Permet de récupérer la liste des clés présentes.
	 * @return Liste des clés
	 */
	public Set<String> keySet()
	{
		return _freqs.keySet();
	}

	/**
	 * Permet de récupérer la map des fréquences.
	 * @return Map des fréquences
	 */
	public HashMap<String, Long> getFrequenciesMap()
	{
		return _freqs;
	}

	public Long getVocabularySize()
	{
		return _total;
	}

	public void serialize(String path)
	{
		try {
			writeObject(new ObjectOutputStream(new FileOutputStream(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(this); //enregistrement
		out.flush();
		out.close();
	}


	protected void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ProbabilityDistribution tmp = (ProbabilityDistribution)(in.readObject());
		
		_freqs = tmp.getFrequenciesMap();
		_total = tmp.getVocabularySize();
	}
}
