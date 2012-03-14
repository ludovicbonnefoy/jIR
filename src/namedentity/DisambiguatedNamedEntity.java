package namedentity;

import java.util.HashMap;
import java.util.List;

import util.SortKeysMapByNumberValues;

public class DisambiguatedNamedEntity extends NamedEntity 
{
	private static final long serialVersionUID = -1818160604543074953L;
	
	private HashMap<String, Double> _nameVariants;
	
	public DisambiguatedNamedEntity(String namedEntity) 
	{
		super(namedEntity);
		_nameVariants = new HashMap<String, Double>();
		_nameVariants.put(namedEntity, 1.);
	}
	
	public String getBestNameVariant()
	{
		List<Object> sortedNameVariants = SortKeysMapByNumberValues.descendingSort(new HashMap<Object, Number>(_nameVariants));

		return (String)sortedNameVariants.get(0);
	}

	public void putNameVariant(String nameVariant, Double confidence)
	{
		_nameVariants.put(nameVariant, confidence);
	}
	
	public void addNameVariant(String nameVariant, Double confidence)
	{
		Double conf = _nameVariants.containsKey(nameVariant) ? _nameVariants.get(nameVariant) : 0.;
		_nameVariants.put(nameVariant, conf+confidence);
	}
	
	public void removeNameVariant(String nameVariant)
	{
		if(_nameVariants.containsKey(nameVariant))
			_nameVariants.remove(nameVariant);
	}
	
	public HashMap<String, Double> getNameVariants()
	{
		return _nameVariants;
	}
}
