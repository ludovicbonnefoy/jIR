package token.probabilitydistribution.similarity;

import java.util.HashMap;

import token.probabilitydistribution.NGramsProbabilityDistribution;

public class Jaccard extends AbstractProbabilityDistributionSimilarity 
{
	public Jaccard()
	{
		super();
	}
	
	public Jaccard(NGramsProbabilityDistribution first)
	{
		super(first);
	}
	
	public Jaccard(NGramsProbabilityDistribution first, NGramsProbabilityDistribution second)
	{
		super(first, second);
	}
		
	/**
	 * Calcul du score de similarité de Jaccard entre deux distributions. Cette version de Jaccard ne prend pas juste présent ou absent mais utilise les fréquences
	 * On suppose que les deux distributions ont été définie précédemment.<br/>
	 * similarity(A,B) = n(A ∩ B) / n(A) + n(B) - n(A ∩ B) <br/>
  		where:<br/>
    	n(A ∩ B) = Σ min(Ai, Bi)<br/>
    	n(A) = Σ Ai<br/>
    	n(B) = Σ Bi<br§>
  		for i = [0..n-1], where n = number of terms in our term-document matrix.
	 * @return Score de similarité de Jaccard
	 */
	@Override
	public Double similarity() 
	{
		Double result = new Double(0); //score de la cimilarité cosine
		
		HashMap<String, Long> freqs1 = _first.getFrequenciesMap();
		HashMap<String, Long> freqs2 = _second.getFrequenciesMap();
		
		Double nAB = new Double(0); // n(A ∩ B)
		Double nA = new Double(0); 
		Double nB = new Double(0);

		for(String key : freqs1.keySet()) //pour chaque élément du premier (A)
		{
			Double freq1 = new Double(freqs1.get(key));
			
			nA += freq1;
			
			if(freqs2.containsKey(key)) //quand il n'y est pas alors freqs2(key) = 0 donc pas la peine de faire le calcul
				nAB += Math.min(freq1, freqs2.get(key));			
		}
		
		for(String key : freqs2.keySet()) //pour chaque élément du second (B)
			nB += freqs2.get(key);
		
		result = nAB / (nA + nB - nAB);

		return result;
	}

}
