package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization 
{
	public static <E> void serialized(E object, String path)
	{
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
			out.writeObject(object);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
	}
	
	public static Object deserialized(String path)
	{
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			Object object = ois.readObject();
			ois.close();

			return object;
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
		return null;
	}
}
