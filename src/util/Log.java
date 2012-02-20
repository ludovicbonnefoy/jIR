package util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public final class Log
{
	private static volatile Log _log;
	private PrintWriter _logWriter;

	private Log()
	{
		try 
		{
			_logWriter = new PrintWriter(GetProperties.getInstance().getProperty("logFile"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public final static Log getInstance()
	{
		if(_log == null)
		{
			synchronized(Log.class) 
			{
				if (_log == null) 
					_log = new Log();
			}
		}
		return _log;
	}

	public void add(Exception e)
	{
		_logWriter.println(e.getMessage());
		e.printStackTrace(_logWriter);
//		_logWriter.println(e.getStackTrace().toString());
		_logWriter.flush();
	}
	
	public void close()
	{
		_logWriter.close();
	}
}
