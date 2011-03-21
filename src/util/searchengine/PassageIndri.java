package util.searchengine;

/**
 * Cette classe contient les informations importantes liées à un passage retrouvé par Indri.  
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class PassageIndri
{
	/** Le texte du passage (en minuscule). */
    private String _text;
    
    /** L'identifiant du document dans lequel le passage est retrouvé. */
    private String _docno;
    
    /** Le score associé au passage. */
    private Double _score;

    /**
     * Inialisation de l'instance.
     * @param score Score du passage
     * @param docno Id du document associé
     * @param text Texte du passage
     */
    public PassageIndri(Double score, String docno, String text)
    {
        _score = score;
        _docno = docno;
        _text = text;
    }

    /**
     * Permet de récupérer le score du passage.
     * @return Score du passage
     */
    public Double getScore()
    {
        return _score;
    }

    /**
     * Permet de récupérer l'id du document duquel est extrait le passage.
     * @return Id du document d'origine
     */
    public String getDocno()
    {
        return _docno;
    }
    
    /**
     * Permet de récupérer le texte du passage (en minuscule).
     * @return Texte du passage
     */
    public String getText()
    {
        return _text;
    }
}
