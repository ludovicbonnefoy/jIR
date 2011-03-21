package util;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * Propriétés, singleton.
 * Il contient les différentes valeurs et chemin des éléments nécessaires pour faire fonctionner le système.
 * Les différents éléments qui doivent être définis dans ce fichier sont :<br/>
 * topicCorpus : le chemin du dossier qui va contenir les fichiers récupérés pour le topic.<br/>
 * passagesCorpus : le chemin du dossier qui va contenir les passages intermédiaires créés.<br/>
 * referenceCorpus : le chemin du dossier qui va contenir les fichiers des ENs de références.<br/>
 * candidateCorpus : le chemin du dossier qui va contenir les fichiers d'une EN candidate.<br/>
 * clueWebEqCorpus : le chemin du dossier contenant les fichiers précisant l'équivalence url/idClueWeb.<br/>
 * clueWebRacine : racine du clueWeb.<br/>
 * 7Web : chemin du corpus 7Web.<br/>
 * indri : chemin des éxécutables Indri.<br/>
 * topicIndex : chemin de l'index pour le corpus du topic.<br/>
 * passagesIndex : chemin de l'index pour les passages.<br/>
 * clueWebEqIndex : chemin de l'index pour le corpus de correspondance url/idClueWeb.<br/>
 * resultsDirectory : chemin du dossier qui va contenir les résultats aux topics.<br/>
 * tmpDirectory : chemin du dossier qui va contenir tous le fichiers temporaires.<br/>
 * treetaggerDirectory : chemin du treeTagger.<br/>
 * wget : chemin du wget.<br/>
 * stanfordPath : chemin du dossier du stanford-NER.<br/>
 * clueWebPart1 : chemin de la première partie du clueWeb.<br/>
 * clueWebPart2 : chemin de la seconde partie du clueWeb.<br/>
 * unigramWorldSer : chemin de l'objet sérialisé des fréquences de monde.<br/>
 * unigramWorld : chemin du fichier contenant les fréquences du monde.<br/>
 * GoodArff : chemin du fichier arff contenantn l'apprentissage pour déterminer si une EN peut répondre à la question ou non.<br/>
 * qrels : chemin du fichier de référence pour l'évaluation.
 */
@SuppressWarnings("serial")
public class GetProperties extends Properties
{
    private static GetProperties instance;

    /**
     * À appeler pour récupérer l'instance en cours des propriétés
     * @return L'instance courante
     */
    public static GetProperties getInstance() 
    {
        if (null == instance) //si aucun élément n'existe alors on en créé un
            instance = new GetProperties();

        return instance; //si il en existe un alors on le retourne
    }

    private GetProperties()
    {}

    /**
     * Initialise l'instance. À appeler lors de la première invocation.
     * Charge un fichier de propriété.
     * @param path Chemin du fichier de propriété
     */
    public void init(String path)
    {
        try
        {
            load(new FileInputStream(path)); //chargement
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}

