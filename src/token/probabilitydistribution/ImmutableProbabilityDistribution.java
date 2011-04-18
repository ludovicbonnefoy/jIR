package token.probabilitydistribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class ImmutableProbabilityDistribution implements Serializable, ProbabilityDistribution 
{
	private static final long serialVersionUID = -4128111925579928824L;
	
	/** Liste des éléments présents dans le corpus associé à leur proba. */
	protected HashMap<String, Double> _probas;
	
	/**
	 * Initialisation d'une nouvelle distribution de termes.
	 */
	public ImmutableProbabilityDistribution()
	{
		_probas = new HashMap<String, Double>();
	}

	/**
	 * Copie d'une distribution de termes.
	 * @param pd Distribution à copier.
	 */
	public ImmutableProbabilityDistribution(ImmutableProbabilityDistribution pd)
	{
		_probas = pd.getProbabilityMap();
	}

	/**
	 * Récupération d'une distribution sérialisée.
	 * @param serializedProbabilityDistribution Fichier contenant la distribution sérialisée.
	 */
	public ImmutableProbabilityDistribution(File serializedImmutableProbabilityDistribution)
	{
		try 
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedImmutableProbabilityDistribution));
			ImmutableProbabilityDistribution tmp = (ImmutableProbabilityDistribution)(ois.readObject());

			_probas = tmp.getProbabilityMap();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ajout d'un nombre d'occurence d'un terme. Va probablement modifier le total, qui va être différent de 1 et nécessiter une modification.
	 * @param term Terme dont on veut rajouter des occurences.
	 * @param occ Nombre d'occurences.
	 */
	public void add(String term, Double occ)
	{
		Double count = _probas.containsKey(term) ? _probas.get(term) : 0.; //on récupère le compte précédent de ce n-gramme
		_probas.put(term, count + occ);
	}

	/**
	 * Contrairement à add, cette méthode REMPLACE la valeur précédente associée à un terme par un nouveau nombre d'occurences.
	 * Même effet si le terme n'était pas encore présent.
	 * @param term Terme que l'on veux ajouter.
	 * @param occ Nombre d'occurences associé.
	 */
	public void put(String term, Double occ)
	{
		_probas.put(term, occ);
	}
	
	/**
	 * Nécessaire pour obtenir que la somme des probas fasse 1
	 * @param total Si on connait la somme exacte (pour éviter une approximation due à la précision des probas).
	 */
	public void normalize(Double total)
	{
		for(String token : _probas.keySet())
			_probas.put(token, _probas.get(token)/total);
	}
	
	/**
	 * Nécessaire pour obtenir que la somme des probas fasse 1 en cas d'insertion.
	 */
	public void normalize()
	{
		Double total = 0.;
		for(String token : _probas.keySet())
			total += _probas.get(token);
	}

	@Override
	public Double get(String term) 
	{
		if(_probas.containsKey(term))
			return _probas.get(term);
		else
			return 0.;
	}

	@Override
	public boolean containsKey(String term) 
	{
		return _probas.containsKey(term);
	}

	@Override
	public Set<String> keySet() 
	{
		return _probas.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Double> getProbabilityMap() 
	{
		return (HashMap<String, Double>) _probas.clone();
	}
	
	@Override
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
}
