package taskevaluation.topic.tac;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import searchengine.indri.Indri;
import util.GetProperties;
import util.Log;
import util.Serialization;

/**
 * Topic pour TAC KBP 2011 et année précédentes.
 * Permet aussi de charger les topics de 2012 mais deux champs ne sont pas pris en compte (begin et end offset).
 * @author "Ludovic Bonnefoy (ludovic.bonnefoy@etd.univ-avignon.fr)"
 */
public class TopicKBP11 implements Serializable
{
	private static final long serialVersionUID = 6959089507354897269L;

	/** Identifiant du topic. */
	private String _topicId;

	/** Nom de l'Entité Source (ES). */
	private String _sourceEntity;

	/** Id du document sourcd  */
	private String _docId;

	/** Texte du document correspondant à docId */
	private String _text;

	public TopicKBP11(String topicId,  String sourceEntity, String docId)
	{
		_topicId = topicId;
		_sourceEntity = sourceEntity;
		_docId = docId;
		_text = null;
	}

	public String getTopicId() 	{
		return _topicId;
	}

	public String getSourceEntity() {
		return _sourceEntity;
	}

	public String getDocId() {
		return _docId;
	}

	/**
	 * Charge les topics à partir d'un fichier XML (au format de celui fournit pour la tâche)
	 * @param topicFile Fichier XML contenant les topics
	 * @return Liste des topics
	 */
	public static ArrayList<TopicKBP11> loadTopics(File topicFile)
	{
		ArrayList<TopicKBP11> topics = new ArrayList<TopicKBP11>();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document queryDoc = builder.parse(topicFile);
			
			NodeList queryElements = queryDoc.getElementsByTagName("query");
			for (int i=0; i<queryElements.getLength(); i++) 
			{
				Element queryElement = (Element) queryElements.item(i);
				String topicId = queryElement.getAttribute("id");
				String sourceEntity = queryElement.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
				String docId = queryElement.getElementsByTagName("docid").item(0).getChildNodes().item(0).getNodeValue();

				topics.add(new TopicKBP11(topicId, sourceEntity, docId));
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Log.getInstance().add(e);
		} catch (SAXException e) {
			e.printStackTrace();
			Log.getInstance().add(e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.getInstance().add(e);
		}

		return topics;
	}
	
	@Override
	public String toString() {
		return _topicId+" "+_sourceEntity+" "+_docId;
	}

	/**
	 * Découpe la liste des topics en plus petites listes (avec maximum limit topics).
	 * Nom d'un sous groupe : path + numéro du sous groupe.
	 * @param topics Liste des topics à diviser.
	 * @param limit Nombre de topics par sous groupe
	 * @param path Où sont enregistré les sous groupes
	 */
	public static void divideUp(ArrayList<TopicKBP11> topics, Integer limit, String path)
	{
		ArrayList<TopicKBP11> topicPart = new ArrayList<TopicKBP11>();
		int num = 1, i = 1;
		for(TopicKBP11 topic : topics)
		{
			topicPart.add(topic);
			if(i % limit == 0)
			{
				Serialization.serialized(topicPart, path + num);
				num++;
				topicPart = new ArrayList<TopicKBP11>();
			}
			i++;
		}
		
		if(topicPart.size() > 0)
			Serialization.serialized(topicPart, path + num);
	}
	
	/**
	 * Retourne le texte du document source.
	 * Si le texte n'a pas été défini (null) alors le document va être recherché et le texte va en être extrait.
	 * @return Texte du document source.
	 */
	public String getText()
	{
		if (_text == null)
		{
			HashMap<String, String> fieldPerIndex = new HashMap<String, String>();
			fieldPerIndex.put(GetProperties.getInstance().getProperty("KBP_2009_Index"), "docid");
			fieldPerIndex.put(GetProperties.getInstance().getProperty("KBP_2010_Index"), "docid");
			fieldPerIndex.put(GetProperties.getInstance().getProperty("KBP_2012_WB_Index"), "docid");
			_text = Indri.getText(fieldPerIndex, " "+ _docId +" ");
			
			if(_text == null)
				_text = Indri.getText(GetProperties.getInstance().getProperty("KBP_2012_N_Index"), _docId, "docno");
			
			if(_text == null)
				System.out.println(_docId);
		}
		return _text;
	}
}
