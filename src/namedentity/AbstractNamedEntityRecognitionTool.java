package namedentity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import util.FileReader;

public abstract class AbstractNamedEntityRecognitionTool implements NamedEntityRecognitionToolInterface 
{
	public String proceed(File file) throws FileNotFoundException,IOException
	{
		return proceed(FileReader.fileToString(file));
	}

}
