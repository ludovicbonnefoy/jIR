package toto.eval.type;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import namedentity.DBPediaOwlNamedEntity;
import namedentity.TypedNamedEntity;

public class GetInstancesSet 
{
	public static void main(String[] args) 
	{
		//GetInstancesSet.DBPediaInstancesSetSerialization(GetInstancesSet.GetDBPediaInstancesSet(1000), "DBPediaInstances3");
		//GetInstancesSet.simpleInstancesSetToXML(GetInstancesSet.getCorrectSimpleInstancesSetFromDBPedia(10), "correctInstances.xml");
		//GetInstancesSet.simpleInstancesSetToXML(GetInstancesSet.getIncorrectSimpleInstancesSetFromDBPedia(1000), "incorrectInstances.xml");
	}
	
	
	public static HashMap<String, DBPediaOwlNamedEntity> GetDBPediaInstancesSet(Integer nbrInstances)
	{
		HashMap<String, DBPediaOwlNamedEntity> instances = new HashMap<String, DBPediaOwlNamedEntity>();

		while(instances.size() < nbrInstances)
		{
			try
			{
				Random rand = new Random();
				Integer offset = rand.nextInt(1200000);

				URL url = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=PREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+foaf%3A+%3Chttp%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2F%3E%0D%0APREFIX+dbpedia2%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2F%3E%0D%0APREFIX+dbpedia%3A+%3Chttp%3A%2F%2Fdbpedia.org%2F%3E%0D%0APREFIX+skos%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0D%0APREFIX+dbo%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2F%3E%0D%0A%0D%0Aselect+distinct+%3Fname+%3Furi+%3Fconcept%0D%0Awhere%0D%0A{%0D%0A+++%3Furi+rdf%3Atype+%3Fconcept+.%0D%0A+++%3Furi+rdf%3Atype+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23Thing%3E+.%0D%0A+++%3Furi+foaf%3Aname+%3Fname+.%0D%0A%0D%0A+++FILTER+regex%28%3Fconcept%2C%22ontology%22%29+.%0D%0A}%0D%0AOffset+"+offset+"%0D%0ALimit+100&debug=on&timeout=&format=text%2Fxml&save=display&fname=");
				URLConnection con = url.openConnection ();

				DocumentBuilder parseur = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc =  parseur.parse(con.getInputStream());

				NodeList uris = doc.getElementsByTagName("uri");
				NodeList names = doc.getElementsByTagName("literal");

				for (int i = 0; i < uris.getLength(); i+=2)// +=2 car il y a des couples d'uri : uri instance et uri concept (on saute l'uri du concept)
				{
					if (!instances.containsKey(names.item(i/2).getTextContent()))
						instances.put(names.item(i/2).getTextContent(), new DBPediaOwlNamedEntity(names.item(i/2).getTextContent(), uris.item(i).getTextContent()));

					if(instances.size() == nbrInstances)
						break;
				}

				//il faut maintenant pour chaque entité, construire le graphe de la hiérarchie de ses types
				//on commence pour chaque entité par récupérer la liste de ses types

				Set<String> nameInstances = instances.keySet();
				for (String nameInstance : nameInstances)
				{
					try 
					{
						ArrayList<String> types = new ArrayList<String>();
						HashMap<String, ArrayList<String>> orientedEdges = new HashMap<String, ArrayList<String>>();
	
						url = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=PREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+foaf%3A+%3Chttp%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2F%3E%0D%0APREFIX+dbpedia2%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2F%3E%0D%0APREFIX+dbpedia%3A+%3Chttp%3A%2F%2Fdbpedia.org%2F%3E%0D%0APREFIX+skos%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0D%0APREFIX+dbo%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2F%3E%0D%0A%0D%0Aselect+distinct+%3Fconcept%0D%0Awhere%0D%0A{%0D%0A+++%3C"+URLEncoder.encode(instances.get(nameInstance).getURI(),"utf-8")+"%3E+rdf%3Atype+%3Fconcept+.%0D%0A+++FILTER+regex%28%3Fconcept%2C%22http%3A%2F%2Fdbpedia.org%2Fontology%2F%22%29+.%0D%0A%0D%0A}%0D%0A&debug=on&timeout=&format=text%2Fxml&save=display&fname=");
						con = url.openConnection ();
	
						parseur = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						doc =  parseur.parse(con.getInputStream());
	
						NodeList typesList = doc.getElementsByTagName("uri");
	
						for (int i = 0; i < typesList.getLength(); ++i)
						{
							String type = typesList.item(i).getTextContent().replaceAll("http://dbpedia.org/ontology/", "").replaceAll("([^A-Z])([A-Z])", "$1 $2");
	
							types.add(type);                    	
	
							URL urlType = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=PREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0D%0APREFIX+rdfs%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0D%0APREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0APREFIX+foaf%3A+%3Chttp%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2F%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2F%3E%0D%0APREFIX+dbpedia2%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2F%3E%0D%0APREFIX+dbpedia%3A+%3Chttp%3A%2F%2Fdbpedia.org%2F%3E%0D%0APREFIX+skos%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0D%0APREFIX+dbo%3A+%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2F%3E%0D%0A%0D%0A%0D%0Aselect+distinct+%3Fconcept+where+%0D%0A{+%0D%0A++++%3C"+typesList.item(i).getTextContent()+"%3E+rdfs%3AsubClassOf+%3Fconcept+.%0D%0A+++FILTER+regex%28%3Fconcept%2C%22http%3A%2F%2Fdbpedia.org%2Fontology%2F%22%29+.%0D%0A}+%0D%0A&debug=on&timeout=&format=text%2Fxml&save=display&fname=");
							URLConnection con2 = urlType.openConnection ();
	
							DocumentBuilder pars = DocumentBuilderFactory.newInstance().newDocumentBuilder();
							Document document =  pars.parse(con2.getInputStream());
	
							NodeList parentsList = document.getElementsByTagName("uri");
	
							for (int j = 0; j < parentsList.getLength(); ++j)
							{
								String parent = parentsList.item(j).getTextContent().replaceAll("http://dbpedia.org/ontology/", "").replaceAll("([^A-Z])([A-Z])", "$1 $2");
								if(!orientedEdges.containsKey(parent))
									orientedEdges.put(parent,new ArrayList<String>());
	
								orientedEdges.get(parent).add(type);
							}
						}
						instances.get(nameInstance).constructTypeHierarchy(types, orientedEdges);
					}
					catch (MalformedURLException e) {
						System.err.println(e);
					}
					catch (IOException e) {
						System.err.println(e);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			catch (MalformedURLException e) {
				System.err.println(e);
			}
			catch (IOException e) {
				System.err.println(e);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return instances;
	}
	
	public static void DBPediaInstancesSetSerialization(HashMap<String,DBPediaOwlNamedEntity> instances, String nameFile)
	{
		try 
    	{
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nameFile)); //Emplacement de l'objet sérialisé
    		oos.writeObject(instances); //enregistrement
    		oos.flush();
    		oos.close();
    	}
    	catch (java.io.IOException e) {
    		e.getMessage();
    	}
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, DBPediaOwlNamedEntity> DBPediaInstancesSetDeserialization(String nameFile)
	{
		try 
    	{
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nameFile));
    		
    		return ((HashMap<String, DBPediaOwlNamedEntity>)(ois.readObject()));
    	}catch(Exception e){
    		e.getMessage();
    	}
    	
    	return new HashMap<String, DBPediaOwlNamedEntity>();
	}
	
	public static ArrayList<TypedNamedEntity> getCorrectSimpleInstancesSetFromDBPedia(Integer nbrInstances)
	{
		HashMap<String, DBPediaOwlNamedEntity> instances = GetInstancesSet.GetDBPediaInstancesSet(nbrInstances);
		
		ArrayList<TypedNamedEntity> simpleInstances = new ArrayList<TypedNamedEntity>();
		
		for(String nameInstance : instances.keySet())
		{
			//il faut trouver le type de plus bas niveau
			DefaultDirectedGraph<String, DefaultEdge> types = instances.get(nameInstance).getTypeHierarchy();
			for(String type : types.vertexSet())
			{
				if(types.outDegreeOf(type) == 0)
				{
					simpleInstances.add(new TypedNamedEntity(nameInstance, type));
					break;
				}
			}
		}
		
		return simpleInstances;
	}
	
	public static ArrayList<TypedNamedEntity> getIncorrectSimpleInstancesSetFromDBPedia(Integer nbrInstances)
	{
		HashMap<String, DBPediaOwlNamedEntity> instances = GetInstancesSet.GetDBPediaInstancesSet(nbrInstances);
		
		ArrayList<TypedNamedEntity> simpleInstances = new ArrayList<TypedNamedEntity>();
		
		//récupération de tous les types de plus bas niveau pour les entités
		ArrayList<String> fineGrainedTypes = new ArrayList<String>();
		for(String uriInstance : instances.keySet())
		{
			DefaultDirectedGraph<String, DefaultEdge> types = instances.get(uriInstance).getTypeHierarchy();
			for(String type : types.vertexSet())
			{
				if(types.outDegreeOf(type) == 0)
				{
					if(!fineGrainedTypes.contains(type))
						fineGrainedTypes.add(type);
					
					break;
				}
			}
		}
		
		//association d'un type qui ne correspond pas à l'entité
		for(String uriInstance : instances.keySet())
		{
			Random rand = new Random();
		
			boolean isAnIncorrectType = false;
			
			while(!isAnIncorrectType)
			{
				String type = fineGrainedTypes.get(rand.nextInt(fineGrainedTypes.size()));

				if(!instances.get(uriInstance).getTypeHierarchy().containsVertex(type))
				{
					simpleInstances.add(new TypedNamedEntity(instances.get(uriInstance).getName(), type));
					isAnIncorrectType = true;
				}
			}
		
		}
		
		return simpleInstances;
	}
	
	public static void simpleInstancesSetToXML(ArrayList<TypedNamedEntity> instances, String nameFile)
	{
		try 
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();

			Document doc = impl.createDocument(null,null,null);
			Element root = doc.createElement("instances");
			doc.appendChild(root);

			for(TypedNamedEntity simpleInstance : instances)
			{
				Element instanceNode = doc.createElement("instance");

				Element name = doc.createElement("name");
				name.setTextContent(simpleInstance.getName());
				instanceNode.appendChild(name);

				Element type = doc.createElement("type");
				type.setTextContent(simpleInstance.getType());
				instanceNode.appendChild(type);

				root.appendChild(instanceNode);
			}

			// transform the Document into a String
			DOMSource domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			java.io.StringWriter sw = new java.io.StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);

			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(nameFile)));
			pw.println(sw.toString());
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
