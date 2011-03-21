package namedentity;

public class DBPediaOwlNamedEntity extends MultipleTypedNamedEntity 
{
	private static final long serialVersionUID = 8294167777010805787L;
	private String _uri;
	
	public DBPediaOwlNamedEntity(String namedEntity, String uri)
	{
		super(namedEntity);
		_uri = uri;
	}
	
	public void setURI(String uri)
	{
		_uri = uri;
	}
	
	public String getURI()
	{
		return _uri;
	}
}
