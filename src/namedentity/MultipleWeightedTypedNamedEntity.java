package namedentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.graph.DefaultEdge;

/**
 * Forme d'entité nommée avec de multiple types hierarchisés et associés à des poids.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class MultipleWeightedTypedNamedEntity extends MultipleTypedNamedEntity 
{
	private static final long serialVersionUID = -7963686866828915203L;
	
	/** Poids des types pour l'entité */
	protected HashMap<String, Double> _typesWeight;
	
	/** 
	 * Création d'une entité nommée non typée.
	 * @param namedEntity Texte de l'entité.
	 */
	public MultipleWeightedTypedNamedEntity(String namedEntity) 
	{
		super(namedEntity);
		_typesWeight = new HashMap<String, Double>();
	}
	
	/**
	 * Création d'une entité nommée avec sa hiérarchie des types.
	 * @param namedEntity Texte de l'entité.
	 * @param types Liste des différents types de l'entité.
	 * @param orientedEdges Arcs sortants des types vers leurs sous-types.
	 */
	public MultipleWeightedTypedNamedEntity(String namedEntity, ArrayList<String> types, HashMap<String, ArrayList<String>> orientedEdges)
	{
		super(namedEntity, types, orientedEdges);
		_typesWeight = new HashMap<String, Double>();
	}
	
	/**
	 * Création d'une entité nommée avec sa hiérarchie des types associés à leur poids.
	 * @param namedEntity Texte de l'entité.
	 * @param types Liste des différents types de l'entité.
	 * @param orientedEdges Arcs sortants des types vers leurs sous-types.
	 * @param typesWeight Poids de chaque type pour l'entité.
	 */
	public MultipleWeightedTypedNamedEntity(String namedEntity, ArrayList<String> types, HashMap<String, ArrayList<String>> orientedEdges, HashMap<String, Double> typesWeight)
	{
		super(namedEntity, types, orientedEdges);
		_typesWeight = typesWeight;
	}
	
	/**
	 * Retourne les poids des types pour l'entité.
	 * @return Poids des types.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Double> getTypesWeight()
	{
		return (HashMap<String, Double>)_typesWeight.clone();
	}
	
	/**
	 * Retourne le poids du type pour l'entité.
	 * @param type Type dont le poids doit être retourné.
	 * @return Poids du type.
	 */
	public Double getTypeWeight(String type)
	{
		if(_typesWeight.containsKey(type))
			return _typesWeight.get(type);
		
		return 0.0;
	}
	
	/**
	 * Calcul du poids de chaque type pour l'entité.
	 * Un premier poids est attributé à chaque type qui égal au nombre de noeud dans le sous-abre ayant pour racine le type.
	 * Ensuite le score final attribué est : | premier_poids - max(premiers_poids) - 1 |
	 */
	public void computeWeights()
	{
		HashMap<String, Integer> typesWeight = new HashMap<String, Integer>();

		String racine = new String();
		
		for(String vertex : _typeHierarchy.vertexSet()) //recherche de la racine
			if(_typeHierarchy.inDegreeOf(vertex) == 0) //si un noeud n'a pas de père alors il est la racine
			{
				racine = vertex;
				break;
			}
		
		computeWeightRec(typesWeight, racine);

		Integer max = typesWeight.get(racine);
		_typesWeight = new HashMap<String, Double>();
		
		for(String vertex : _typeHierarchy.vertexSet())
			_typesWeight.put(vertex, new Double(Math.abs(typesWeight.get(vertex) - max - 1)));
	}
	
	/**
	 * Calcul récursif du poids des types pour l'entité.
	 * La récursivité permet de calculer le nombre d'élément dans le sous arbre ayant pour racine le type en paramètre.
	 * Le poids d'un type est égal au nombre de noeud dans le sous-arbre ayant pour racine type.
	 * @param typesWeight Poids des types.
	 * @param type Racine du sous-arbre étudié.
	 * @return Liste des éléments du sous-arbre de type.
	 */
	private ArrayList<String> computeWeightRec(HashMap<String, Integer> typesWeight, String type)
	{
		ArrayList<String> successors = new ArrayList<String>();
		
		if(_typeHierarchy.outDegreeOf(type) > 0)
		{
			DirectedNeighborIndex<String, DefaultEdge> dni= new DirectedNeighborIndex<String, DefaultEdge>(_typeHierarchy);
			Set<String> directSuccessors = dni.successorsOf(type);
			
			for(String successor : directSuccessors)
				successors.addAll(computeWeightRec(typesWeight, successor));
			
		}

		successors.add(type);
		
		Set<String> uniqueSuccessors = new HashSet<String>(successors);
		typesWeight.put(type, uniqueSuccessors.size());

		return successors;
	}
}