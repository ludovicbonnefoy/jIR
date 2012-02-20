package namedentity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import util.FileReader;

/**
 * Classe abstraite implémentant la quasi-totalité des méthodes de l'interface correspondante.
 * La seule méthode non implémentée est la seule qui est particulière à la manière de récupérer
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractNamedEntityRecognitionTool implements NamedEntityRecognitionToolInterface 
{
	public String proceed(File file) throws FileNotFoundException,IOException
	{
		return proceed(FileReader.fileToString(file));
	}

}
