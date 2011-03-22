package web.page.classification;

import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;

import util.GetProperties;
import web.util.html.HTML2Text;

/**
 * Récupère plusieurs features dans une page web.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class ProcessFeatures
{
	/** Feature Texte : compte des mots dans l'ensemble du document: map[mot] = fréquence. 
	 * Tout les mots sont conservés car importants (d'après ...).*/
    HashMap<String,Integer> _wordsCount;
	
    /** Feature Texte: compte des POS (Part-of-Speech) dans l'ensemble du document : map[POS] = fréquence.
     * Un POS représente la fonction d'un terme (verbe, nom, adjéctif, ...). */
    HashMap<String,Integer> _posCount;
    
    /** Feature Structure : compte des balises HTML dans l'ensemble du document : map[balise] = fréquence. */
    HashMap<String,Integer>  _tagsCount;
    
    /** Features diverses : compte de nombreux paramètres différents : map[feature] = valeur
     * (Taille du document en mots, nombre de paragraphes, etc.). */
    HashMap<String,Double> _featuresCount;

    /** Features URL : liste des URLs sortantes de la page */
    ArrayList<String> _aHREF;
    
    /** Compte des mots présents dans l'URL de la page web : map[mot] = fréquence.
    * Tout les mots sont conservés car importants (d'après ...).*/
    HashMap<String,Integer> _urlWordsCount;


    /**
     * Initialise les conteneurs.
     */
    public ProcessFeatures()
    {
        _wordsCount = new HashMap<String,Integer>();
        _posCount = new HashMap<String,Integer>();
        
        _tagsCount = new HashMap<String,Integer>();
        
        _featuresCount = new HashMap<String,Double>();
        
        _aHREF = new ArrayList<String>();
        _urlWordsCount = new HashMap<String,Integer>();
    }

    /**
     * Calcul des valeurs des features sur un fichier.
     * Pas d'URL donnée pour cette page web.
     * Aucun calcul ne sera fait sur les URLs.
     * @param file Fichier
     */
    public void processFeatures(File file)
    {
        processFeatures(file,null);
    }

    /**
     * Calcul des valeurs des features sur un fichier.
     * L'URL de la page doit être précisée et sera analysée, ainsi que les URLs sortantes.
     * @param file Page web
     * @param url URL de la page
     */
    public void processFeatures(File file, String url)
    {
        _wordsCount = new HashMap<String,Integer>();
        _posCount = new HashMap<String,Integer>();
        
        _tagsCount = new HashMap<String,Integer>();
        
        _featuresCount = new HashMap<String,Double>();
        
        _aHREF = new ArrayList<String>();
        _urlWordsCount = new HashMap<String,Integer>();

        String webSite="", domain=""; //caractéristique de l'url
        
        if(url != null)  //si une URL est connue pour cette page
        {
            //travail sur l'url
            String[] urlParts = url.split("/"); //découpage de l'url
            
            webSite = urlParts[2]; //le nom du site web (http:// www.toto.fr / forum => www.toto.fr)
            domain = webSite.substring(webSite.lastIndexOf('.')+1); //domain (fr)

            _featuresCount.put("urlLength",new Double(url.length())); //taille de l'url

            int directoryLength = 0; //calcul de la taille de la position de la page dans le site
            for(int i = 3; i < urlParts.length-1; ++i)
                directoryLength += urlParts[i].length();
            _featuresCount.put("urlDirectoryPathLength",new Double(directoryLength));
           
            _featuresCount.put("urlDirectoryNbrElements",new Double(Math.max(urlParts.length-4,0))); // calcul de la taille de la position de la page dans la hiérarchie du site

            String[] urlTokens = url.split("[^a-zA-z0-9]"); //découpage de l'url en tokens
            for(String token : urlTokens) //pour chaque token
            {
            	Integer count = _urlWordsCount.containsKey(token) ? _urlWordsCount.get(token) : 0; //récupération du nombre de fois où a été rencontré l'élément.
                _urlWordsCount.put(token, count + 1);
            }
        }

        try{
            processTextFeaturesTreeTagger(new FileReader(file)); //calcul des features sur le texte (word, pos et autres)
            
            processHTMLFeatures(file.toString()); //calcul du compte pour les tags

            if(url != null) //si une URL est connue pour cette page
                analyseLinks(webSite,domain); //calcul de features sur les URLs sortantes
            
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }


    /**
     * Calcul de features liées au texte.
     * Il s'agit du compte des mots, des pos mais aussi de la taille du document en mots, ect.
     * Utilise le TreeTagger pour la segmentation des mots
     * @param in Reader vers le fichier à traiter
     */
    private void processTextFeaturesTreeTagger(FileReader in)
    {
        try{
            HTML2Text parser = new HTML2Text(); //va permettre la suppression des balises HTML
           	parser.parse(in);
            
            String text = parser.getCleanText(); //texte sans balises
            
            //Écriture dans un fichier pour le TreeTagger
            PrintWriter pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/tree_tagger"),"ISO-8859-1"));
            pw.println(text);
            pw.close();

            //Préparation du TreeTagger
            pw = new PrintWriter(new OutputStreamWriter (new FileOutputStream (GetProperties.getInstance().getProperty("tmpDirectory")+"/treetagger.sh"),"UTF-8"));
	        pw.println("cat "+GetProperties.getInstance().getProperty("tmpDirectory")+"/tree_tagger | "+GetProperties.getInstance().getProperty("treetaggerDirectory")+"/cmd/tree-tagger-english | iconv -f ISO-8859-1 -t UTF-8");
	        pw.close();
	        
	        //Éxécution du TreeTagger
            Runtime runtime = Runtime.getRuntime();
	        Process process = runtime.exec(new String[]{"/bin/sh","tmp/treetagger.sh"});

	        //déclaration de features
            Double sentenceLengthAverage = new Double(0), sentenceLengthMax = new Double(0), sentenceLengthMin = new Double(9999), sentenceLengthUnder13 = new Double(0), sentenceLengthUpper28 = new Double(0);
            Double wordLengthAverage = new Double(0);
            Double nbrWord = new Double(0), nbrSentence = new Double(0), nbrSentenceWord = new Double(0);
            
            BufferedReader brTokens = new BufferedReader(new InputStreamReader(process.getInputStream())); //permet de lire la sortie du TreeTagger
            String tokenLine = new String();
            
            String[] elements = new String[]{"","",""};//différentes parties d'une ligne du TreeTagger (terme POS lemme)
 
            while((tokenLine = brTokens.readLine()) != null) //lecture ligne à ligne de la sortie du TreeTagger (donc élément par élément)
            {
                ++nbrSentenceWord; //compte des mots dans la phrase

                elements = tokenLine.replaceAll("\t\t+","\t").split("\t"); //reformatage de la sortie

                wordLengthAverage += elements[2].length(); //taille moyenne des mots

                Integer count = _wordsCount.containsKey(elements[2]) ? _wordsCount.get(elements[2]) : 0; //récupération du nombre de fois où a été rencontré l'élément.
                _wordsCount.put(elements[2], count + 1);

                count = _posCount.containsKey(elements[1]) ? _posCount.get(elements[1]) : 0; //récupération du nombre de fois où a été rencontré l'élément.
                _posCount.put(elements[1], count + 1);

                if(elements[1].equals("SENT")) //si on a un marqueur de fin de phrase
                {
                    ++nbrSentence; //mise à jour du compte des phrases
                    sentenceLengthAverage += nbrSentenceWord; //mise à jour de la taille moyenne des phrases

                    //est-ce une phrase longue ou courte?
                    if(nbrSentenceWord.compareTo(sentenceLengthMax) > 0)
                        sentenceLengthMax = new Double(nbrSentenceWord);
                    if(nbrSentenceWord.compareTo(sentenceLengthMin) < 0)
                        sentenceLengthMin = nbrSentenceWord;
                    if(nbrSentenceWord.compareTo(13.) < 0)
                        ++sentenceLengthUnder13;
                    if(nbrSentenceWord.compareTo(28.) > 0)
                        ++sentenceLengthUpper28;

                    nbrWord += nbrSentenceWord;

                    nbrSentenceWord = new Double(0); //remise à zéro du compte de mots pour la nouvelle phrase
                }
            }

            //au cas où le texte ne se termine brusquement (sans ponctuation) Pour prendre en compte la dernière phrase
            //On considère alors que le texte constitue une phrase.
            if(!elements[1].equals("SENT"))
            { 
                ++nbrSentence; //mise à jour du compte des phrases
                sentenceLengthAverage += nbrSentenceWord; //mise à jour de la taille moyenne des phrases

                //est-ce une phrase longue ou courte?
                if(nbrSentenceWord.compareTo(sentenceLengthMax) > 0)
                    sentenceLengthMax = nbrSentenceWord;
                if(nbrSentenceWord.compareTo(sentenceLengthMin) < 0)
                    sentenceLengthMin = nbrSentenceWord;
                if(nbrSentenceWord.compareTo(13.) < 0)
                    ++sentenceLengthUnder13;
                if(nbrSentenceWord.compareTo(28.) > 0)
                    ++sentenceLengthUpper28;

                nbrWord += nbrSentenceWord;
            }

            _featuresCount.put("sentenceLengthAverage",sentenceLengthAverage/nbrSentence); //division par le nombre de phrases
            _featuresCount.put("sentenceLengthMax",sentenceLengthMax);
            _featuresCount.put("sentenceLengthMin",sentenceLengthMin);
            _featuresCount.put("sentenceLengthUnder13",sentenceLengthUnder13);
            _featuresCount.put("sentenceLengthUpper28",sentenceLengthUpper28);
            _featuresCount.put("sentenceCount",nbrSentence);
            _featuresCount.put("wordCount",nbrWord);
            _featuresCount.put("wordLengthAverage",wordLengthAverage/nbrWord); //division par le nombre de mots

            process.waitFor();
        }catch(Exception e){}
        catch (StackOverflowError sofe) {}
}

/**
 * Calcul des features liées au contenu HTML.
 * Par exemple le compte des balises html.
 * @param filename Nom du fichier
 */
private void processHTMLFeatures(String filename)
{
    try{
        DOMParser parser = new DOMParser(); //chargeur d'un document
        parser.parse(new InputSource(new FileInputStream(filename))); //chargement du document

        Node rootElement =  parser.getDocument().getElementsByTagName("html").item(0) ; //récupération de la racine de l'arbre dom du document chargé
        
        exploreDomTree(rootElement); //exploration de l'arbre dom et récupération des features
    }catch (Exception e){
        System.err.println(e.getMessage());
    }
}

/**
 * Fonction récursive qui va permettre de parcourir tout l'arbre dom d'un document et de récupérer le compte des balises HTML
 * @param node Noeud courant
 */
private void exploreDomTree (Node node)
{
    /*
    //Pourrait permettre de faire des comptes sur le type de noeud
    String type;
    switch (node.getNodeType()) //récupération du type de noeud
    {
        case Node.ATTRIBUTE_NODE :
            type = "attribut"; break;
        case Node.CDATA_SECTION_NODE :
            type = "CDATA"; break;
        case Node.COMMENT_NODE :
            type = "comment"; break;
        case Node.DOCUMENT_FRAGMENT_NODE :
            type = "document fragment"; break;
        case Node.DOCUMENT_NODE :
            type = "document"; break;
        case Node.DOCUMENT_TYPE_NODE :
            type = "document type"; break;
        case Node.ELEMENT_NODE :
            type = "node"; break;
        case Node.ENTITY_NODE :
            type = "entity"; break;
        case Node.ENTITY_REFERENCE_NODE :
            type = "entity reference"; break;
        case Node.NOTATION_NODE :
            type = "notation"; break;
        case Node.PROCESSING_INSTRUCTION_NODE :
            type = "processing instruction"; break;
        case Node.TEXT_NODE :
            type = "text"; break;
        default : 
            type = "none";
    }*/

    
    Integer count = _tagsCount.containsKey(node.getNodeName()) ? _tagsCount.get(node.getNodeName()) : 0; //récupération du nombre de fois où a été rencontré la balise.
    _tagsCount.put(node.getNodeName(), count + 1);
    
    if(node.getNodeName().equals("A") && node.getAttributes().getNamedItem("href") != null) //si on a un lien avec le champ "href" renseigné
        _aHREF.add(node.getAttributes().getNamedItem("href").getNodeValue()); //on ajoute dans la liste le lien sortant

    if (node.hasChildNodes()) //si le noeud à des fils
    {
        //On va parcourir les sous-abres des fils
        Node nextFils = node.getFirstChild(); //on commence par le premier fils
        
        while (nextFils != null) //tant que l'on a des fils 
        {
            exploreDomTree (nextFils); //exploration du sous arbre du fils
            nextFils = nextFils.getNextSibling(); //on passe au fils suivant
        }
    }
}

/**
 * Analyse des liens sortants.
 * Divers paramètres vont ici être étudié tel que le nombre de lien sortant du site, du domaine, etc.
 * !! Connaissance de l'URL du document pour pouvoir être éxécuté et que l'exploration du dom du document ai été réalisé!!
 * @param webSite Nom du site du document
 * @param domain Nom de domaine du document
 */
private void analyseLinks(String webSite,String domain)
{
    //les features
    int externalDomain = 0 ,externalWebSite = 0, ftp = 0, inDomain = 0, inWebSite = 0, mailto = 0, linkEvent = 0, other = 0;

    for(String href : _aHREF) //pour chaque lien sortant
    {
        String[] hrefParts = href.split("/"); //découpage
        
        if(hrefParts.length < 3) //si moins de trois champs, pas valide
            continue; //on la saute

        if(hrefParts[0].equals("ftp:")) //lien vers un ftp 
        {
            ++ftp;
            continue; //pas d'analyse complémentaire
        }
        else if(hrefParts[0].equals("mailto:")) //lien mail
        {
            ++mailto;
            continue; //pas d'analyse complémentaire
        }
        else if(hrefParts[0].contains("javascript")) //lien javascript
        {
            ++linkEvent;
            continue; //pas d'analyse complémentaire
            }
            else if(!hrefParts[0].equals("http:")) //pas une page web 
            {
                ++other;
                continue; //pas d'analyse complémentaire
            }

            String linkedWebSite = hrefParts[2]; //site pointé
            String linkedDomain = linkedWebSite.substring(linkedWebSite.lastIndexOf('.')+1); //nom de domain pointé

            if(linkedWebSite.equals(webSite)) //au sein du même site?
                ++inWebSite;
            else //vers l'extérieur
                ++externalWebSite;

            if(linkedDomain.equals(domain)) //même domaine?
                ++inDomain;
            else
                ++externalDomain;
        }

        //enregistrement des features
        _featuresCount.put("linksDomainExt",new Double(externalDomain));
        _featuresCount.put("linksDomainIn",new Double(inDomain));
        _featuresCount.put("linksWebsiteExt",new Double(externalWebSite));
        _featuresCount.put("linksWebsiteIn",new Double(inWebSite));
        _featuresCount.put("linksFtp",new Double(ftp));
        _featuresCount.put("linksMailto",new Double(mailto));
        _featuresCount.put("linksOthers",new Double(other));
        _featuresCount.put("linksEvents",new Double(linkEvent));
    }

    /**
     * Permet de récupérer la valeur d'une feature.
     * @param feature Nom de la feature
     * @return Sa Valeur (ou null)
     */
    public Double getFeaturesCount(String feature)
    {
    	return (_featuresCount.containsKey(feature) ? _featuresCount.get(feature) : null);
    }

    /**
     * Permet de récupérer l'ensemble des valeurs des features .
     * @return Les features
     */
    public HashMap<String,Double> getFeaturesCount()
    {
        return _featuresCount;
    }

    /**
     * Permet de récupérer le compte des mots.
     * @return Comptes des mots
     */
    public HashMap<String,Integer> getWordsCount()
    {
        return _wordsCount;
    }

    /**
     * Permet de récupérer le compte d'un mot
     * @param word Mot 
     * @return Compte du mot
     */
    public Integer getWordsCount(String word)
    {
        return _wordsCount.get(word);
    }

    /**
     * Permet de récupérer le compte des POSs.
     * @return Comptes des POSs
     */
    public HashMap<String,Integer> getPOSCount()
    {
        return _posCount;
    }

    /**
     * Permet de récupérer le compte d'un POS
     * @param pos POS 
     * @return Compte du POS
     */
    public Integer getPOSCount(String pos)
    {
        return _posCount.get(pos);
    }

    /**
     * Permet de récupérer le compte des balises HTML.
     * @return Comptes des balises
     */
    public HashMap<String,Integer> getTagsCount()
    {
        return _tagsCount;
    }

    /**
     * Permet de récupérer le compte d'une balise HTML
     * @param tag Balise
     * @return Compte de la balise
     */
    public Integer getTagsCount(String tag)
    {
        return _tagsCount.get(tag);
    }

    /**
     * Permet de récupérer le compte des mots de l'URL
     * @return Comptes des mots
     */
    public HashMap<String,Integer> getUrlWordsCount()
    {
        return _urlWordsCount;
    }

    /**
     * Permet de récupérer le compte d'un mot dans l'URL
     * @param word Mot 
     * @return Compte du mot dans l'URL
     */
    public Integer getUrlWordsCount(String word)
    {
        return _wordsCount.get(word);
    }
}
