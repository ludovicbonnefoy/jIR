package namedentity.proximities;

/**
 * Les classes implémentant cette inferface ont pour but de calculer la proximité sémantique de deux éléments.
 * Permet par exemple de calculer la proximité de deux entités ou d'une entité à un type (le degré d'appartenance d'une entité à un type). 
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public interface ProximityInterface 
{
	/**
	 * Permet de définir le premier objet.
	 * @param first Premier objet.
	 */
	public void setFirst(Object first);

	/**
	 * Permet de définir le second objet.
	 * @param second Second objet.
	 */
	public void setSecond(Object second);
	
	/**
	 * Calcul de la proximité de deux objets.
	 * Permet de définir les deux éléments qui vont être comparées.
	 * @param first Premier objet.
	 * @param second Second objet.
	 * @return Score de proximité.
	 */
	public Double proximity(Object first, Object second);
	
	/**
	 * Calcul de la proximité de deux objets.
	 * Permet de définir le second élément (on suppose que le premier a été défini).
	 * @param second Second objet.
	 * @return Score de proximité.
	 */
	public Double proximity(Object second);
	
	/**
	 * Calcul de la proximité de deux objets.
	 * On suppose que les deux éléments ont été définis.
	 * @return Score de proximité.
	 */
	public Double proximity();
}
