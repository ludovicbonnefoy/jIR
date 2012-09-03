package util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Propose des options courantes sur les maps. 
 * @author "Ludovic Bonnefoy (ludovic.bonnefoy@etd.univ-avignon.fr)"
 */
public class Maps 
{
	/**
	 * Affichage d'une map.
	 * @param <K>
	 * @param map Map à afficher.
	 */
	public static <N, K> void printMap(HashMap<K, N> map)
	{
		for(K key : map.keySet())
			System.out.println(key+" "+map.get(key));
	}
	
	/**
	 * Ajoute les éléments de second à first.
	 * Additionne les valeurs si une clé est déjà présente dans first.
	 * @param <K> La clé peut être de n'importe quel type tant que la fonction equals est bien définie pour la classe.
	 * @param first Map à laquelle on ajoute des éléments.
	 * @param second Map que l'on ajoute à first.
	 */
	public static <K> void additionStringMaps(HashMap<K, Double> first, HashMap<K, Double> second)
	{
		for(K sKey : second.keySet())
			first.put(sKey, getDouble(first, sKey) + second.get(sKey));		
	}

	/**
	 * Transforme une liste (avec potentiellement des doublons) en map avec [élément] = nbr occurrences.
	 * @param array Liste à partir de laquelle la map est contruite.
	 * @return map avec [élément] = nbr occurrences.
	 */
	public static <E> HashMap<E, Integer> redundantArrayListToCountMap(ArrayList<E> array)
	{
		HashMap<E, Integer> map = new HashMap<E, Integer>();
		for(E element : array)
			map.put(element, getInt(map, element) + 1);

		return map;
	}

	/**
	 * Additionne "score" à la valeur déjà existante de "key" dans "map" et l'y ajoute.
	 * @param map Map à modifier.
	 * @param key Élément à rajouter ou à modifier dans map.
	 * @param score Score à additionner.
	 */
	public static <K> void addToMap(HashMap<K, Double> map, K key, Double score)
	{
		map.put(key, getDouble(map, key) + score);
	}

	/**
	 * Multiplie "score" à la valeur déjà existante de "key" dans "map" et l'y ajoute.
	 * @param map Map à modifier.
	 * @param key Élément à rajouter ou à modifier dans map.
	 * @param score Score à multiplier.
	 */
	public static <K> void timesToMap(HashMap<K, Double> map, K key, Double score)
	{
		map.put(key, getDouble(map, key) * score);
	}

	/**
	 * Diviser la valeur déjà existante de "key" dans "map" par "score".
	 * @param map Map à modifier.
	 * @param key Élément à rajouter ou à modifier dans map.
	 * @param score Score par lequel diviser la valeur existante.
	 * @param defaultValue Si score est égal à 0 alors le score est égal à defaultValue.
	 */
	public static <K> void divToMap(HashMap<K, Double> map, K key, Double score, Double defaultValue)
	{
		if(score == 0)
			map.put(key, defaultValue);
		else
			map.put(key, getDouble(map, key) / score);
	}
	
	/**
	 * Retourne 0. si map ne contient pas k sinon retourne map.get(k).
	 * @param map Map de laquelle on veut récupérer la valeur de key.
	 * @param key Clé de laquelle on veut récupérer la valeur.
	 * @return Valeur de key dans map (ou 0 si absent).
	 */
	public static <K> Double getDouble(HashMap<K, Double> map, K key)
	{
		return map.containsKey(key) ? map.get(key) : 0.;
	}
	
	/**
	 * Retourne 0 si map ne contient pas k sinon retourne map.get(k).
	 * @param map Map de laquelle on veut récupérer la valeur de key.
	 * @param key Clé de laquelle on veut récupérer la valeur.
	 * @return Valeur de key dans map (ou 0 si absent).
	 */
	public static <K> Integer getInt(HashMap<K, Integer> map, K key)
	{
		return map.containsKey(key) ? map.get(key) : 0;
	}
	
	/**
	 * Retourne 0 si map ne contient pas k sinon retourne map.get(k).
	 * @param map Map de laquelle on veut récupérer la valeur de key.
	 * @param key Clé de laquelle on veut récupérer la valeur.
	 * @return Valeur de key dans map (ou 0 si absent).
	 */
	public static <K> Long getLong(HashMap<K, Long> map, K key)
	{
		return map.containsKey(key) ? map.get(key) : 0;
	}
}
