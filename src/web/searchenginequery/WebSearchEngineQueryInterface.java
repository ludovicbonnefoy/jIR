package web.searchenginequery;

import java.util.ArrayList;

/**
 * Interface décrivant les méthodes à implémenter pour une classe permettant d'interroger un moteur de requête.
 * @author ludo
 */
public interface WebSearchEngineQueryInterface 
{
	/**
	 * Interroge le moteur de recherche avec query et récupère les count premiers éléments.
	 * @param query La requête.
	 * @param count Le nombre d'éléments à récupérer.
	 */
	public void query(String query, int count);
	
    /**
     * Permet de récupérer la liste des URLs correspondant aux résultats, dans l'ordre décroissant de pertinence.
     * @return Liste des URLs.
     */
    public ArrayList<String> getURLs();

    /**
     * Permet de récupérer la liste des Snippets correspondant aux résultats, dans l'ordre décroissant de pertinence.
     * @return Liste des Snippets.
     */
    public ArrayList<String> getSnippets();
    
    /**
     * Permet de récupérer le nombre total de résultats à cette requête.
     * @return Nombre total de résultats.
     */
    public Long getTotalHits();
    
}
