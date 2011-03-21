package corpus;

import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Les classes implémentant cette inferface ont pour but de récupérer des pages web et de les rassembler dans un dossier.
 * Les documents dans le dossier seront numérotés de 0 à x (nombre de pages web).
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public interface WebPagesCorpusInterface
{
	
	/**
	 * Efface les documents du corpus
	 */
	public void destroy();
	
	
    /**
     * Construit le corpus de pages web dans le dossier local spécifié
     * @param directoryPath Dossier local où construire le corpus. Ne peut être spécifié qu'à la construction..
     * @param urls Liste des urls du corpus
     * @throws FileNotFoundException Si le dossier où enregister le corpus n'est pas trouvé (ou n'est pas un dossier)
     */
    public void build(File directory, Set<String> urls) throws FileNotFoundException, IOException, InterruptedException;
	
    
    /**
     * Ajoute d'une page web dans le corpus
     * @param url URL de la page à ajouter au corpus
     */
    public void add(String url) throws IOException, InterruptedException;
    
    /**
     * Ajoute les pages web dans le corpus
     * @param urls Ensemble des pages web à ajouter au corpus
     */
    public void add(Set<String> urls) throws IOException, InterruptedException;
    
    
    /**
     * Supprime la page web du corpus
     * @param url Page web à supprimer du corpus
     */
     public void delete(String url);
     
     /**
      * Supprime des pages web du corpus
      * @param files urls des pages web à supprimer
      */
     public void delete(Set<String> urls);
     
     /**
      * Retourne le fichier contenant la page web souhaitée
      * @return Fichier correspondant à l'url
      * @param url URL de la page à retourner
      */
     public File getWebPage(String url);
     
     /**
      * Retourne un ensemble de page webs
      * @param urls liste des pages webs souhaitées
      * @return Liste des documents correspondant aux urls
      */
     public Set<File> getWebPages(Set<String> urls);
     
     /**
      * Retourne la liste des urls des pages web dans le corpus
      * @return Liste des urls présente dans le corpus
      */
     public Set<String> getURLs();
     
     
     /**
      * Permet de récupèrer le dossier local où se trouve le corpus
      * @return
      */
     public File getCorpusDirectory();
     
     /**
      * Sérialisation de l'objet
      * @param path Chemin où va être sérialisé l'objet (doit aussi contenir le nom)
      */
     public void Save(String path);
 }
