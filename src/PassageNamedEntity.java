

import java.util.ArrayList;

import util.NamedEntity;

/**
 * Définition d'une Entité Nommée adaptée à la piste EntityRanking sur le corpus ClueWeb 
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */

public class PassageNamedEntity extends NamedEntity
{
	private static final long serialVersionUID = 4782997108170808754L;

	/** Numéro du document dans le corpus ClueWeb */
    private String _fromDocno;
    
    /** Numéro du passage dans le document _docno */
    private Integer _numPassage;
    
    /** Mot à partir duquel commence l'EN*/
    private Integer _position;

    /**
     * Initialise l'EN
     * @param docno
     * @param numPassage
     * @param position
     */
    public PassageNamedEntity(String docno,  Integer numPassage, Integer position)
    {
        super();
        _fromDocno = docno;
        _numPassage = numPassage;
        _position = position;
    }
    
    /**
     * Initialise l'EN avec une EN
     * @param docno
     * @param numPassage
     * @param position
     * @param namedEntity
     */
    public PassageNamedEntity(String docno,  Integer numPassage, Integer position, ArrayList<String> namedEntity)
    {
        super(namedEntity);
        _fromDocno = docno;
        _numPassage = numPassage;
        _position = position;
    }
    
    /**
     * Initialise l'EN avec une string 
     * @param docno
     * @param numPassage
     * @param position
     * @param namedEntity
     */
    public PassageNamedEntity(String docno,  Integer numPassage, Integer position, String namedEntity)
    {
        super(namedEntity);
        _fromDocno = docno;
        _numPassage = numPassage;
        _position = position;
    }
    

    /**
     * Retourne le numéro de document dans le ClueWeb où se trouve l'EN
     * @return Le numéro du document
     */
    public String getDocno()
    {
        return _fromDocno;
    }

    /**
     * Retourne le numéro de passage dans le document
     * @return Le numéro de passage
     */
    public Integer getNumPassage()
    {
        return _numPassage;
    }

    /**
     * Retourne le numéro du mot à partir duquel commence l'EN
     * @return La position
     */
    public Integer getPosition()
    {
        return _position;
    }
 
    /**
     * Fonction qui calcul le hashcode de cet objet.
     * Nécessaire parce que plusieurs PassageNamedEntity aurait le même hashcode si le même texte (car ils héritent de ArrayList).
     * Le couple _numpassage _position est unique est donc le hashcode de la concaténation des deux chaines devrait être unique.
     * @return Hashcode
     */
    public int hashCode()
    {
    	return (_numPassage+" "+_position).hashCode();
    }
}
