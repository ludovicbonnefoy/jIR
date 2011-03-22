package util.probabilitydistribution;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;


/**
 * Distribution de probabilités lissée avec Dirichlet.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class ProbabilityDistributionDirichletSmoothed extends ProbabilityDistribution 
{
	private static final long serialVersionUID = -369025182830636171L;
	
	/** Paramètre du lissage */
	private Double _mu; 
	
	/**
	 * Initialisation de l'instance.
	 */
	public ProbabilityDistributionDirichletSmoothed()
	{
		super();
		_mu = new Double(2000);
	}
	
	/**
	 * Initialise l'instance.
	 * @param frequencies Fréquences d'apparitions
	 */
	public ProbabilityDistributionDirichletSmoothed(HashMap<String,Long> frequencies)
	{
		super(frequencies);
	 
		_mu = new Double(2000);
	}
	
	public ProbabilityDistributionDirichletSmoothed(String path)
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
	
	public Double getMu()
	{
		return _mu;
	}
	
	public void setMu(Double mu)
	{
		_mu = mu;
	}
	
	/**
	 * Renvoie la valeur lissée correspondante à la clé.
	 * @param key Clé
	 * @return Valeur lissée
	 */
	public Double get(String key)
	{
		if(_freqs.containsKey(key))
			return DirichletSmoothing.getInstance().smooth(key, _freqs.get(key), _total, _mu);
		else
			return DirichletSmoothing.getInstance().smooth(key, new Long(0), _total, _mu);
	}
	
	protected void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ProbabilityDistributionDirichletSmoothed tmp = (ProbabilityDistributionDirichletSmoothed)(in.readObject());
		
		_freqs = tmp.getFrequenciesMap();
		_total = tmp.getVocabularySize();
		
		_mu = tmp.getMu(); 
	}
}
