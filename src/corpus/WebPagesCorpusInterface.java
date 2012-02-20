package corpus;

import java.util.Set;
import java.io.File;

/**
 * Les classes implémentant cette inferface ont pour but de collecter des pages web à partir d'une liste d'urls et de les rassembler dans un même dossier.
 * Les documents dans le dossier seront représentés par un numéro (entiers >0) non nécessairement contigus.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public interface WebPagesCorpusInterface
{
	/**
	 * Efface les documents composant le corpus du dossier.
	 * Le dossier en lui-même doit rester intacte.
	 */
	public void destroy();
	
    /**
     * Collecte les pages web correspondantes aux urls et les stocke dans le dossier local spécifié.
     * @param urls Liste des urls des pages web à collecter pour former le corpus.
     * @return Liste des urls n'ayant pu être récupérées.
     * @throws FileNotFoundException Si le dossier où enregister le corpus n'est pas trouvé (ou n'est pas un dossier).
     */ 
    public Set<String> build(Set<String> urls);
    
    /**
     * Ajout d'une page web au corpus déjà récupéré.
     * @param url URL de la page à ajouter au corpus.
     * @return Retourne true si la page a été ajoutée correctement.
     */
    public boolean add(String url);
    
    /**
     * Ajout de plusieurs pages web dans le corpus.
     * @param urls Ensemble des urls des pages web à ajouter au corpus.
     * @return Liste des urls n'ayant pu être récupérées.
     */
    public Set<String> add(Set<String> urls);
    
    /**
     * Supprime du corpus la page web correspondante à l'url.
     * @param url Page web à supprimer du corpus.
     * @return Retourne true si la page a été correctement supprimée. false dans le cas contraire ou si aucun document ne correspond à cette url.  
     */
     public boolean delete(String url);
     
     /**
      * Supprime du corpus les pages web correspondantes aux urls.
      * @param urls urls des pages web à supprimer.
      * @return Retourne la liste des urls n'ayant pu être correctement supprimées. La raison peut être qu'aucun documents ne correspondent à ces urls. 
      */
     public Set<String> delete(Set<String> urls);
     
     /**
      * Retourne la page web du corpus correspondant à l'url ou null si aucun document correspondant n'est trouvé.
      * @param url URL de la page à retourner.
      * @return Fichier Page web correspondante à l'url ou null si aucun document correspondant n'est trouvé.
      */
     public File getWebPage(String url);
     
     /**
      * Retourne l'ensemble des pages web correspondantes aux urls.
      * @param urls Liste des pages webs à retourner.
      * @return Liste des documents correspondant aux urls. Certains éléments peuvent être à null si aucun document correspondant à l'url n'a été retrouvé.
      */
     public Set<File> getWebPages(Set<String> urls);
     
     /**
      * Retourne la liste des urls des pages web du corpus.
      * @return Liste des urls présente dans le corpus.
      */
     public Set<String> getURLs();
     
     /**
      * Retourne true si le corpus contient une page web correspondante à l'url. 
      * @param url URL de la page web à retrouver.
      * @return true si le corpus contient une page web correspondante à l'url. 
      */
     public boolean contains(String url);
     
     /**
      * Permet de récupèrer le dossier local où se trouve le corpus.
      * @return Dossier local où se trouve le corpus.
      */
     public File getCorpusDirectory();
     
     /**
      * Sérialisation de l'objet.
      * @param path Chemin complet où va être sérialisé l'objet (doit aussi contenir le nom)
      */
     public void serialize(String path);
 }
