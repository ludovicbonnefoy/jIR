package namedentity;

public class TypedNamedEntity extends NamedEntity 
{
	private static final long serialVersionUID = 8171610107921721381L;
	private String _type;
	
	public TypedNamedEntity(String namedEntity, String type)
	{
		super(namedEntity);
		_type = type;
	}
	
	public void setType(String type)
	{
		_type = type;
	}
	
	public String getType()
	{
		return _type;
	}
}
