package namedentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class MultipleTypedNamedEntity extends NamedEntity 
{
	private static final long serialVersionUID = 5942262853070465869L;
	
	protected DefaultDirectedGraph<String, DefaultEdge> _typeHierarchy;
	protected HashMap<String, Double> _typesWeight;

	
	public MultipleTypedNamedEntity(String namedEntity)
	{
		super(namedEntity);
		
		_typeHierarchy = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		_typesWeight = new HashMap<String, Double>();
	}
	
	public DefaultDirectedGraph<String, DefaultEdge> getTypeHierarchy()
	{
		return _typeHierarchy;
	}

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
	
	public HashMap<String, Double> getTypesWeight1()
	{
		HashMap<String, Integer> typesWeight = new HashMap<String, Integer>();

		String racine = new String();
		
		for(String vertex : _typeHierarchy.vertexSet())
			if(_typeHierarchy.inDegreeOf(vertex) == 0)
			{
				racine = vertex;
				break;
			}
		
		getTypesWeight1Rec(typesWeight, racine);

		Integer max = typesWeight.get(racine);
		_typesWeight = new HashMap<String, Double>();
		
		for(String vertex : _typeHierarchy.vertexSet())
			_typesWeight.put(vertex, new Double(Math.abs(typesWeight.get(vertex) - max - 1)));

		return _typesWeight;
	}
	
	private ArrayList<String> getTypesWeight1Rec(HashMap<String, Integer> typesWeight, String type)
	{
		ArrayList<String> successors = new ArrayList<String>();
		
		if(_typeHierarchy.outDegreeOf(type) > 0)
		{
			DirectedNeighborIndex<String, DefaultEdge> dni= new DirectedNeighborIndex<String, DefaultEdge>(_typeHierarchy);
			Set<String> directSuccessors = dni.successorsOf(type);
			
			for(String successor : directSuccessors)
				successors.addAll(getTypesWeight1Rec(typesWeight, successor));
			
		}

		successors.add(type);
		
		Set<String> uniqueSuccessors = new HashSet<String>(successors);
		typesWeight.put(type, uniqueSuccessors.size());

		return successors;
	}
}
