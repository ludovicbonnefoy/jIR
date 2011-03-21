package util.probabilitydistribution;

import java.util.ArrayList;
import java.util.HashSet;

import util.probabilitydistribution.ProbabilityDistribution;

/**
 * Cette classe offre plusieures méthodes de calcul de similarité entre deux distribution de probabilités.
 * Pour l'instant sont implémentés : - KullbackLeibler. 
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class ProbabilityDistributionSimilarity
{
	/**
	 * Un des deux distribution de probabilités qui vont être comparées.
	 * L'intérêt de l'enregistrer est de ne pas avoir à la passer en paramètre lorsque différents calculs sont réalisés sur les mêmes distribution, ou que l'un d'elles reste là même.
	 */
	private ProbabilityDistribution _first, _second;

	/**
	 * Initialisation de l'instance.
	 */
	public ProbabilityDistributionSimilarity()
	{
		_first = new ProbabilityDistribution();
		_second = new ProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec une première distribution.
	 * @param first Distribution
	 */
	public ProbabilityDistributionSimilarity(ProbabilityDistribution first)
	{
		_first = first;
		_second = new ProbabilityDistribution();
	}

	/**
	 * Initialisation de l'instance avec deux distributions.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 */
	public ProbabilityDistributionSimilarity(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;
	}

	/**
	 * Permet de changer la première distribution de probabilités.
	 * @param first
	 */
	public void setFirst(ProbabilityDistribution first)
	{
		_first = first;
	}

	/**
	 * Permet de changer la seconde distribution de probabilités.
	 * @param second
	 */
	public void setSecond(ProbabilityDistribution second)
	{
		_second = second;
	}

	/**
	 * Calcul du score de similarité de Kullback-Leibler.
	 * Permet de définir les deux distributions qui vont être comparées.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double kullbackLeibler(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;

		return kullbackLeibler();
	}

	/**
	 * Calcul du score de similarité de Kullback-Leibler.
	 * Permet de définir la seconde des deux distributions qui vont être comparées (en supposant que la première l'a été précédemment).
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double kullbackLeibler(ProbabilityDistribution second)
	{
		_second = second;

		return kullbackLeibler();
	}

	/**
	 * Calcul du score de similarité de Kullback-Leibler.
	 * Suppose ici que les deux distributions ont été préalablement définies.
	 * Telle qu'elle est défini pour le moment, cette similarité évite le cas zéro en ignorant les cas n'apparaissant pas dans les deux distributions.
	 * @return Score de similarité
	 */
	public Double kullbackLeibler()
	{
		Double result = new Double(0); //score de similarité
		
		for(String key : _first.keySet()) //pour chaque élément de la première distribution
			result += _first.get(key) * Math.log(_first.get(key) / _second.get(key)); //mise à jour du score

		return result;
	}
	
	/**
	 * Calcul du score de similarité de Kullback-Leibler.
	 * Permet de définir les deux distributions qui vont être comparées.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double kullbackLeiblerUnion(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;

		return kullbackLeiblerUnion();
	}

	/**
	 * Calcul du score de similarité de Kullback-Leibler.
	 * Permet de définir la seconde des deux distributions qui vont être comparées (en supposant que la première l'a été précédemment).
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double kullbackLeiblerUnion(ProbabilityDistribution second)
	{
		_second = second;

		return kullbackLeiblerUnion();
	}

	/**
	 * Calcul du score de similarité de Kullback-Leibler.
	 * Suppose ici que les deux distributions ont été préalablement définies.
	 * Telle qu'elle est défini pour le moment, cette similarité évite le cas zéro en ignorant les cas n'apparaissant pas dans les deux distributions.
	 * @return Score de similarité
	 */
	public Double kullbackLeiblerUnion()
	{
		Double result = new Double(0); //score de similarité
		
		ArrayList<String> arrayVocabulary = new ArrayList<String>(_first.keySet());
		arrayVocabulary.addAll(_second.keySet());
		
		HashSet<String> Vocabulary = new HashSet<String>(arrayVocabulary);

		for(String key : Vocabulary) //pour chaque élément de la première distribution
			result += _first.get(key) * Math.log(_first.get(key) / _second.get(key)); //mise à jour du score

		return result;
	}


	/**
	 * Calcul du score de similarité de Jensen-Shannon.
	 * Permet de définir les deux distributions qui vont être comparées.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double jensenShannon(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;

		return jensenShannon();
	}


	/**
	 * Calcul du score de similarité de Jensen-Shannon.
	 * Permet de définir la seconde des deux distributions qui vont être comparées (en supposant que la première l'a été précédemment).
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double jensenShannon(ProbabilityDistribution second)
	{
		_second = second;

		return jensenShannon();
	}


	/**
	 * Calcul du score de similarité de Jensen-Shannon.
	 * Suppose ici que les deux distributions ont été préalablement définies.
	 * Telle qu'elle est défini pour le moment, cette similarité évite le cas zéro en ignorant les cas n'apparaissant pas dans les deux distributions.
	 * @return Score de similarité
	 */
	public Double jensenShannon()
	{
		Double result = new Double(0); //score de similarité

		for(String key : _first.keySet()) //pour chaque élément de la première distribution
		{
			Double pi = _first.get(key), qi = _second.get(key);
			Double mi = (pi + qi)/2;

			result += (pi * Math.log(pi / mi))/2 + (qi * Math.log(qi / mi))/2;
		}

		return result;
	}
	
	
	/**
	 * Calcul du score de similarité de Jensen-Shannon.
	 * Permet de définir les deux distributions qui vont être comparées.
	 * @param first Première distribution
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double jensenShannonUnion(ProbabilityDistribution first, ProbabilityDistribution second)
	{
		_first = first;
		_second = second;

		return jensenShannonUnion();
	}


	/**
	 * Calcul du score de similarité de Jensen-Shannon.
	 * Permet de définir la seconde des deux distributions qui vont être comparées (en supposant que la première l'a été précédemment).
	 * @param second Seconde distribution
	 * @return Score de similarité
	 */
	public Double jensenShannonUnion(ProbabilityDistribution second)
	{
		_second = second;

		return jensenShannonUnion();
	}


	/**
	 * Calcul du score de similarité de Jensen-Shannon.
	 * Suppose ici que les deux distributions ont été préalablement définies.
	 * Telle qu'elle est défini pour le moment, cette similarité évite le cas zéro en ignorant les cas n'apparaissant pas dans les deux distributions.
	 * @return Score de similarité
	 */
	public Double jensenShannonUnion()
	{
		Double result = new Double(0); //score de similarité

		ArrayList<String> arrayVocabulary = new ArrayList<String>(_first.keySet());
		arrayVocabulary.addAll(_second.keySet());
		
		HashSet<String> Vocabulary = new HashSet<String>(arrayVocabulary);

		for(String key : Vocabulary) //pour chaque élément de la première distribution
		{
			Double pi = _first.get(key), qi = _second.get(key);
			Double mi = (pi + qi)/2;

			result += (pi * Math.log(pi / mi))/2 + (qi * Math.log(qi / mi))/2;
		}

		return result;
	}
}
