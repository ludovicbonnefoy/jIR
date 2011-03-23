package word.probabilitydistribution.similarity;

import util.GetProperties;

public class ProbabilityDistributionSimilarityFactory 
{
	public static AbstractProbabilityDistributionSimilarity get()
	{
		if(GetProperties.getInstance().containsKey("probabilityDistributionSimilarity"))
		{
			String similarity = GetProperties.getInstance().getProperty("probabilityDistributionSimilarity");
			if(similarity.equals("kullbackLeibler"))
				return new KullbackLeibler();
			else if (similarity.equals("kullbackLeiblerUnion"))
				return new KullbackLeiblerUnion();
			else if (similarity.equals("jensenShannon"))
				return new JensenShannon();
			else if (similarity.equals("jensenShannonUnion"))
				return new JensenShannonUnion();
		}

		return new KullbackLeibler();
	}
}
