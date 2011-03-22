package namedentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Forme d'entité nommée typée (multiple types hierarchisés).
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class MultipleTypedNamedEntity extends NamedEntity 
{
	private static final long serialVersionUID = 5942262853070465869L;
	
	/** Hierarchie des types, représentée sous forme d'arbre (graphe sans cylce). */
	protected DefaultDirectedGraph<String, DefaultEdge> _typeHierarchy;
	
	/** 
	 * Création d'une entité nommée non typée.
	 * @param namedEntity Texte de l'entité.
	 */
	public MultipleTypedNamedEntity(String namedEntity)
	{
		super(namedEntity);
		
		_typeHierarchy = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	}
	
	/**
	 * Création d'une entité nommée avec sa hiérarchie des types.
	 * @param namedEntity Texte de l'entité.
	 * @param types Liste des différents types de l'entité.
	 * @param orientedEdges Arcs sortants des types vers leurs sous-types.
	 */
	public MultipleTypedNamedEntity(String namedEntity, ArrayList<String> types, HashMap<String, ArrayList<String>> orientedEdges)
	{
		super(namedEntity);
		
		constructTypeHierarchy(types, orientedEdges);
	}
	
	/**
	 * Création de la hiérarchie des types de l'entité nommée.
	 * @param types Liste des types de l'entité.
	 * @param orientedEdges Arcs sortants des types vers leurs sous-types.
	 */
	public void constructTypeHierarchy(ArrayList<String> types, HashMap<String, ArrayList<String>> orientedEdges)
	{
		_typeHierarchy = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		for (String type : types) 
			_typeHierarchy.addVertex(type);
		
		Set<String> sources = orientedEdges.keySet();
		
		for (String source : sources)
			for (String dest : orientedEdges.get(source))
				_typeHierarchy.addEdge(source, dest);
	}
	
	/**
	 * Retourne l'arbre des types de l'entité.
	 * @return Arbre (Graph sans cycle) des types de l'entité.
	 */
	public DefaultDirectedGraph<String, DefaultEdge> getTypeHierarchy()
	{
		return _typeHierarchy;
	}

}
