package token;

public class Token
{ 
	private String _token, _pos, _lemme;
	
	public Token(String token, String pos, String lemme)
	{
		_token = token;
		_pos = pos;
		if(lemme== null || lemme.equals(""))
			_lemme = token;
		else
			_lemme = lemme;
	}
	
	public String getToken() {
		return _token;
	}

	public void setToken(String token) {
		_token = token;
	}

	public String getPos() {
		return _pos;
	}

	public void setPos(String pos) {
		_pos = pos;
	}

	public String getLemme() {
		return _lemme;
	}

	public void setLemme(String lemme) {
		_lemme = lemme;
	}


	
}
