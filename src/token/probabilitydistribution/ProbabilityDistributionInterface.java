package token.probabilitydistribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public interface ProbabilityDistributionInterface 
{
	/**
	 * Permet de récupérer la probabilité d'apparition du terme.
	 * @param term Terme dont on veux la probabilité d'apparition.
	 * @return Probabilité d'apparition du terme.
	 */
	public Double get(String term);
	
	/**
	 * Ajout d'un nombre d'occurence d'un terme.
	 * @param term Terme dont on veut rajouter des occurences.
	 * @param occ Nombre d'occurences.
	 */
	public void add(String term, Long occ);
	
	/**
	 * Ajout d'occurences de termes.
	 * @param terms Association termes/nombre d'occurences.
	 */
	public void add(HashMap<String, Long> terms);

	/**
	 * Contrairement à add, cette méthode REMPLACE la valeur précédente associée à un terme par un nouveau nombre d'occurences.
	 * Même effet si le terme n'était pas encore présent.
	 * @param term Terme que l'on veux ajouter.
	 * @param occ Nombre d'occurences associé.
	 */
	public void put(String term, Long occ);

	/**
	 * Contrairement à add, cette méthode REMPLACE les valeurs précédentes associées à des termes par de nouveaux nombres d'occurences.
	 * Même effet si les termes n'étaient pas encore présents.
	 * @param terms Association terme/nombre d'occurences.
	 */
	public void put(HashMap<String, Long> terms);

	/**
	 * Retourne true si le terme est présent.
	 * @param term Terme recherché.
	 * @return true si le terme est présent.
	 */
	public boolean containsKey(String term);

	/**
	 * Permet de récupérer la liste des termes présents.
	 * @return Liste des termes présents.
	 */
	public Set<String> keySet();

	/**
	 * Renvoie l'ensemble des termes présents avec leur nombre d'occurences.
	 * @return Ensemble des couples termes/nombre d'occurences.
	 */
	public HashMap<String, Long> getFrequenciesMap();

	/**
	 * Retourne le nombre total de termes présents (somme du nombre d'occurences pour chaque terme). 
	 * @return Nombre total de termes présents.
	 */
	public Long getVocabularySize();
	
	/**
	 * Ajoute les termes et leur nombre d'occurences d'une distribution à l'instance.
	 * @param pd Distribution de laquelle on va récupérer des termes et leur nombre d'occurences et les ajouter à l'instance.
	 */
	public void merge(ProbabilityDistributionInterface pd);
	
	/**
	 * Sérialise l'objet au chemin indiqué (chemin complet = contenant le nom).
	 * @param path Chemin complet où doit être stocké l'objet sérialisé.
	 */
	public void serialize(String path);
	
	/**
	 * Construction à partir d'un fichier ne contenant des fréquences.
	 * Un terme par ligne, associé à sa fréquence.
	 * @param file Fichier contenant les fréquences.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadFromFreqFile(File file) throws FileNotFoundException, IOException;
}
