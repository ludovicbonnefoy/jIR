package util;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Initialise la format de la sortie, le http agent (firefox) et charge le fichier des propriétés.  
 * @author "Ludovic Bonnefoy (ludovic.bonnefoy@etd.univ-avignon.fr)"
 */
public class InitConfig 
{
	public static void init()
	{
		try 
		{ 
			PrintStream ps; ps = new PrintStream(System.out,true,"UTF-8"); //Configuration de la sortie en utf-8
			System.setOut(ps);
			ps = new PrintStream(System.err,true,"UTF-8");
			System.setErr(ps);
			System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; rv:15.0) Gecko/20120716 Firefox/15.0a2");

			GetProperties.getInstance().init("properties.properties");
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
	}
	
}
