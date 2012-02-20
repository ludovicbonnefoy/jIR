package namedentity;

/**
 * Forme d'entité nommée typée (un seul type).
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class TypedNamedEntity extends NamedEntity 
{
	private static final long serialVersionUID = 8171610107921721381L;
	
	/** Type de l'entité nommée. */
	private String _type;
	
	/**
	 * Construction d'une entité nommée avec son texte et son type.
	 * @param namedEntity Texte de l'entité.
	 * @param type Type de l'entité.
	 */
	public TypedNamedEntity(String namedEntity, String type)
	{
		super(namedEntity);
		_type = type;
	}
	

	/**
	 * Permet d'attribuer un nouveau type à l'entité nommée.
	 * @param type Nouveau type de l'entité.
	 */
	public void setType(String type)
	{
		_type = type;
	}
	
	/**
	 * Retourne le type de l'entité nommée.
	 * @return Type de l'entité. 
	 */
	public String getType()
	{
		return _type;
	}
}
