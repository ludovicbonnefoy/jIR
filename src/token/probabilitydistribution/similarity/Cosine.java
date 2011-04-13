package token.probabilitydistribution.similarity;

import java.util.HashMap;

public class Cosine extends AbstractProbabilityDistributionSimilarity 
{
	/**
	 * Calcul du score de similarité cosinus entre deux distributions.
	 * On suppose que les deux distributions ont été définie précédemment.<br/>
	 * similarity(A,B) = cos θ = (A ⋅ B) / (|A| * |B|) <br/>
		  where:<br/>
		    A ⋅ B = Σ Ai * Bi<br/>
		    |A| = sqrt(Σ Ai²)<br/>
		    |B| = sqrt(Σ Bi²)<br/>
		  for i = [0..n-1], where n = number of terms in our term-document matrix.
	 * @return Score de similarité cosinus
	 */
	@Override
	public Double similarity() 
	{
		Double result = new Double(0); //score de la cimilarité cosine
		
		HashMap<String, Long> freqs1 = _first.getFrequenciesMap();
		HashMap<String, Long> freqs2 = _second.getFrequenciesMap();
		
		Double pAB = new Double(0); //le produit first * second (A.B) 
		Double d1c = new Double(0); //|A|²
		Double d2c = new Double(0); //|B|²

		for(String key : freqs1.keySet()) //pour chaque élément du premier (A)
		{
			Double freq1 = new Double(freqs1.get(key));
			
			d1c += Math.pow(freq1, 2);
			
			if(freqs2.containsKey(key)) //quand il n'y est pas alors freqs2(key) = 0 donc pas la peine de faire le calcul
				pAB += freq1 * freqs2.get(key);
		}
		
		for(String key : freqs2.keySet()) //pour chaque élément du second (B)
			d2c += Math.pow(freqs2.get(key),2);
		
		result = pAB / (Math.sqrt(d1c) * Math.sqrt(d2c));

		return result;
	}
}
