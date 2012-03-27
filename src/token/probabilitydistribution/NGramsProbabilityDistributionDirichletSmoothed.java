package token.probabilitydistribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import token.probabilitydistribution.smoother.Dirichlet;
import util.Log;


/**
 * Effectue un lissage de Dirichlet sur les valeurs de la distribution.
 * La probabilité d'appirition d'u ngramme est estimé en fonction d'un corpus du monde.
 * Cette classe nécessite qu'une instance de la classe DirichletSmoothing est été initialisée.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class NGramsProbabilityDistributionDirichletSmoothed extends NGramsProbabilityDistribution 
{
	private static final long serialVersionUID = -369025182830636171L;
	
	/** Paramètre du lissage */
	private Double _mu; 
	
	/**
	 * Initialisation d'une nouvelle distribution de termes.
	 */
	public NGramsProbabilityDistributionDirichletSmoothed()
	{
		super();
		_mu = new Double(2000);
	}
	
	/**
	 * Copie d'une distribution de termes.
	 * @param ngpd Distribution à copier.
	 */
	public NGramsProbabilityDistributionDirichletSmoothed(NGramsProbabilityDistribution ngpd)
	{
		super(ngpd);
		_mu = new Double(2000);
	}
	
	/**
	 * Copie d'une distribution de termes lissée avec Dirichlet.
	 * @param ngpd Distribution à copier.
	 */
	public NGramsProbabilityDistributionDirichletSmoothed(NGramsProbabilityDistributionDirichletSmoothed ngpd)
	{
		super(ngpd);
	 
		_mu = ngpd.getMu();
	}
	
	/**
	 * Récupération d'une distribution sérialisée.
	 * @param serializedNGramsProbabilityDistributionDirichletSmoothed Fichier contenant la distribution sérialisée.
	 */
	@SuppressWarnings("unchecked")
	public NGramsProbabilityDistributionDirichletSmoothed(File serializedNGramsProbabilityDistributionDirichletSmoothed)
	{
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedNGramsProbabilityDistributionDirichletSmoothed));
			
			Object obj = ois.readObject();
			
			if(obj.getClass() == this.getClass())
			{
				NGramsProbabilityDistributionDirichletSmoothed tmp = (NGramsProbabilityDistributionDirichletSmoothed)(obj);
				_freqs = (HashMap<String, Long>) tmp.getFrequenciesMap().clone();
				_total = tmp.getVocabularySize();
				_mu = tmp.getMu(); 
			}
			else
			{
				NGramsProbabilityDistribution tmp = (NGramsProbabilityDistribution)(obj);
				_freqs = (HashMap<String, Long>) tmp.getFrequenciesMap().clone();
				_total = tmp.getVocabularySize();
				_mu = new Double(2000); 
			}
			
			ois.close();
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialisation d'une nouvelle distribution de ngrammes et spécification du paramètre de lissage.
	 */
	public NGramsProbabilityDistributionDirichletSmoothed(Double mu)
	{
		super();
		_mu = mu;
	}
	
	/**
	 * Initialisation d'une distribution lissée à partir d'une distribution non nécessairement lissée et spécification du paramètre de lissage.
	 */
	public NGramsProbabilityDistributionDirichletSmoothed(NGramsProbabilityDistribution ndpg, Double mu)
	{
		super(ndpg);
	 
		_mu = mu;
	}
	
	/**
	 * Renvoie le paramètre de lissage utilisé.
	 * @return Paramètre mu de lissage utilisé.
	 */
	public Double getMu()
	{
		return _mu;
	}
	
	/**
	 * Permet de modifier le paramètre de lissage utilisé.
	 * @param mu Nouveau paramètre de lissage.
	 */
	public void setMu(Double mu)
	{
		_mu = mu;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String name = "";
		for(int i = 0; i < args.length; i++)
			name += args[i]+" ";
		
		name= name.trim();
		
		String toto = name.substring(name.lastIndexOf("/")+1);
		System.out.println(toto+ " "+name);
		
		NGramsProbabilityDistributionDirichletSmoothed ngrams = new NGramsProbabilityDistributionDirichletSmoothed();
		ngrams.loadFromFreqFile(new File(name),1);
		ngrams.serialize("tmp/1NewGram/"+toto);
		
		//NGramsProbabilityDistribution ngrams = new NGramsProbabilityDistribution(new File("tmp/unigramWorld.ser"));
		//System.out.println(ngrams.getFrequenciesMap().get("the"));
	}
	
	/**
	 * Permet de récupérer la probabilité lissée d'apparition du ngramme.
	 * @param ngram Ngramme dont on veux la probabilité d'apparition.
	 * @return Probabilité lissée d'apparition du ngramme.
	 */
	public Double get(String ngram)
	{
		if(_freqs.containsKey(ngram))
			return Dirichlet.getInstance().smooth(ngram, _freqs.get(ngram), _total, _mu);
		else
			return Dirichlet.getInstance().smooth(ngram, new Long(0), _total, _mu);
	}
}
