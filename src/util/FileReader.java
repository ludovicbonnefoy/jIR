package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader 
{
	public static String fileToString(File file) throws IOException, FileNotFoundException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		StringBuffer result = new StringBuffer();
		
		while ((line = br.readLine()) != null)
			result.append(line);

		br.close();
		
		return result.toString();
	}
}
