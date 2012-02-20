package util;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * Propriétés, singleton.
 * Il contient les différentes valeurs et chemin des éléments nécessaires pour faire fonctionner le système.
 * Les différents éléments qui doivent être définis dans ce fichier sont :<br/>
 * webPagesCorpusType : Type de corpus utilisé. Valeurs possibles : trec, web.
 * webSearchEngine : moteur de recherche à interroger. Valeurs possibles : boss, google.
 * externalWebPagesDownloader : nom de l'outil à utiliser pour récupérer les pages web. Valeurs possibles : wget.
 * externalWebPagesDownloaderPath : chemin complet de l'executable pour récupérer les pages Web.
 * externalTokeniser : nom de l'outil à utiliser. Valeurs possibles : treeTagger.
 * externalTokeniserPath : chemin complet de l'executable pour tokeniser du texte.
 * namedEntityRecognitionTool : Outil qui va être utilisé pour la reconnaissance d'entités nommées. Valeurs possibles : stanfordNER.
 * stanfordClassifierPath : chemin vers le dossier où l'on va pouvoir trouver le classifier à utiliser pour la reco d'entités.
 * probabilityDistributionSimilarity : type de similarité à utiliser. Valeurs possibles : kullbackLeibler, kullbackLeiblerUnion, jensenShannon, jensenShannonUnion.
 * webPagesGenreClassifier : spécifie un chemin vers un classifieur de pages web sérialisé (et aussi là où celui créer va l'être).
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
			e.printStackTrace();
        }
    }
}

