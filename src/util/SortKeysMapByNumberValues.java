package util;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Cette classe retourne les clés d'une map triées.
 * Les clés des maps sont triées en fonction de leur valeur.
 * Les clés peuvent être de n'importe quel type et les valeurs de type nombre.
 * Ce n'est pas la map triée qui est retournée (une map ne peut avoir d'ordre) mais la liste des clés.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class SortKeysMapByNumberValues
{
	/**
	 * Retourne les clés dans l'ordre croissant des valeurs (de la plus petite à la plus grande).
	 * @param map La map à trier
	 * @return La liste des clés triées
	 */
    public static List<Object> ascendingSort(Map<Object,Number> map)
    {
        return listAscendingSorted(map);
    }

    /**
	 * Retourne les clés dans l'ordre décroissant des valeurs (de la plus grande à la plus petite).
	 * @param map La map à trier
	 * @return La liste des clés triées
     */
    public static List<Object> descendingSort(Map<Object,Number> map)
    {
        List<Object> sortedKeys = listAscendingSorted(map); //appel du tri ascendant

        //reverse de l'ordre ascendant
        List<Object> descendingSortedKeys = new ArrayList<Object>();
        for(int i = sortedKeys.size()-1; i >= 0; --i)
            descendingSortedKeys.add(sortedKeys.get(i));

        return descendingSortedKeys;
    }

    /**
     * Tri les clés dans l'ordre croissant.
     * @param map La map à trier
     * @return La liste des clés triées.
     */
    private static List<Object> listAscendingSorted(Map<Object,Number> map)
    {
        Set<Object> tmp = map.keySet(); //les clés de la map
        
        List<Object> keys = new ArrayList<Object>(); //va recueillir les clés
        
        for(Object key : tmp)
            keys.add(key);

        java.util.Collections.sort(keys, new ObjectComparator(map)); //tri des clés

        return keys;
    }


    /**
     * Classe qui permet de faire la comparaison entre deux nombres.
     */
    private static class ObjectComparator implements Comparator<Object>
    {
        private Map<Object, Number> _map;//pour garder une copie du Map que l'on souhaite traiter
          
        /**
         * Initialisation du comparateur.
         * @param map
         */
        public ObjectComparator(Map<Object,Number> map)
        {
            _map = map; //stocker la copie pour qu'elle soit accessible dans compare()
        }

        /**
         * Fonction de comparaison entre deux nombres
         */
        public int compare(Object key1, Object key2)
        {
            Double s1 = _map.get(key1).doubleValue();
            Double s2 = _map.get(key2).doubleValue();
            
			if(s1.compareTo(s2) < 0)
				return -1;
			else if (s1.equals(s2))
                return 0;
            else
                return 1;
        }
    }
}

