package namedentity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Entité nommée extraite de DBPedia avec de multiple types hierarchisés et associés à des poids et son uri dans DBPedia.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class DBPediaOwlNamedEntity extends MultipleWeightedTypedNamedEntity 
{
	private static final long serialVersionUID = 8294167777010805787L;
	
	/** Uri de l'entité dans DBPedia */
	private String _uri;
	
	/** 
	 * Création d'une entité nommée non typée.
	 * @param namedEntity Texte de l'entité.
	 * @param uri Uri de l'entité dans DBPedia
	 */
	public DBPediaOwlNamedEntity(String namedEntity, String uri)
	{
		super(namedEntity);
		_uri = uri;
	}
	
	/**
	 * Création d'une entité nommée avec sa hiérarchie des types associés à leur poids.
	 * @param namedEntity Texte de l'entité.
	 * @param types Liste des différents types de l'entité.
	 * @param orientedEdges Arcs sortants des types vers leurs sous-types.
	 * @param uri Uri de l'entité dans DBPedia
	 */
	public DBPediaOwlNamedEntity(String namedEntity, ArrayList<String> types, HashMap<String, ArrayList<String>> orientedEdges, String uri)
	{
		super(namedEntity, types, orientedEdges);
		_uri = uri;
	}
	
	/**
	 * Création d'une entité nommée avec sa hiérarchie des types associés à leur poids.
	 * @param namedEntity Texte de l'entité.
	 * @param types Liste des différents types de l'entité.
	 * @param orientedEdges Arcs sortants des types vers leurs sous-types.
	 * @param typesWeight Poids de chaque type pour l'entité.
 	 * @param uri Uri de l'entité dans DBPedia
	 */
	public DBPediaOwlNamedEntity(String namedEntity, ArrayList<String> types, HashMap<String, ArrayList<String>> orientedEdges, HashMap<String, Double> typesWeight, String uri)
	{
		super(namedEntity, types, orientedEdges, typesWeight);
		_uri = uri;
	}
	
	/**
	 * Permet de changer l'uri de l'entité dans DBPedia.
	 * @param uri Nouvelle uri de l'entité.
	 */
	public void setURI(String uri)
	{
		_uri = uri;
	}
	
	/**
	 * Retourne l'uri dans DBPedia de l'entité.
	 * @return URI de l'entité.
	 */
	public String getURI()
	{
		return _uri;
	}
}
