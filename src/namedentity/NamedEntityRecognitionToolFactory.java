package namedentity;

import util.GetProperties;

public class NamedEntityRecognitionToolFactory 
{
	public static AbstractNamedEntityRecognitionTool get()
	{
		if(GetProperties.getInstance().containsKey("namedEntityRecognitionTool"))
		{
			String type = GetProperties.getInstance().getProperty("namedEntityRecognitionTool");
			
			if(type.equals("stanfordNER"))
			{
				return new StanfordNamedEntityRecognitionTool();
			}
		}
		
		return new StanfordNamedEntityRecognitionTool();
	}
}
