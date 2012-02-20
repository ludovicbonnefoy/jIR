package namedentity.proximities;

/**
 * Classe abstraite implémentant la quasi-totalité des méthodes de l'interface correspondante.
 * La seule méthode non implémentée est celle du calcul de la proximité.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public abstract class AbstractProximity implements ProximityInterface 
{
	/** Deux objets dont on veut connaitre la proximité sémantique. */
	Object _first, _second;
	
	/**
	 * Initilisation de l'objet avec deux objets null.
	 */
	public AbstractProximity()
	{
		_first = null;
		_second = null;
	}
	
	/**
	 * Initilisation de l'objet avec passage du premier objet.
	 * @param first Premier objet.
	 */
	public AbstractProximity(Object first)
	{
		_first = first;
		_second = null;
	}
	
	/**
	 * Initilisation de l'objet avec passage des deux objets.
	 * @param first Premier objet.
	 * @param second Second objet.
	 */
	public AbstractProximity(Object first, Object second)
	{
		_first = first;
		_second = second;
	}
	
	@Override
	public void setFirst(Object first) 
	{
		_first = first;
	}

	@Override
	public void setSecond(Object second) 
	{
		_second = second;
	}

	@Override
	public Double proximity(Object first, Object second) 
	{
		_first = first;
		_second = second;
		
		return proximity();
	}

	@Override
	public Double proximity(Object second) 
	{
		_second = second;
		
		return proximity();
	}
}
