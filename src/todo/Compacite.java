package todo;

import java.util.ArrayList;
import java.util.HashMap;

import namedentity.AbstractNamedEntityRecognitionTool;
import namedentity.NamedEntityRecognitionToolFactory;

import util.GetProperties;
import util.TreeTagger;


/**
 * Détection des Entités Nommées Candidates (ECs) et calcul de leur compacité.
 * La compacité mesure la densité des mots d'une question dans l'entourage d'une EC.
 * Plus il y a de mots de la question (uniques) et plus ils en sont proches, meilleur est le score associé.
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */

public class Compacite
{
    /**
     * Détecte les ECs et leur attribue un score de compacité.
     * @param text Texte à analyser. Fichier XML, doit contenir une partie "query" dans laquelle des mots clés sont présents, une partie "enSource", une partie "passages" contenant des "passage" contenant eux mêmes "docno" et le texte du passage. 
     * @param targetType Type large des ECs à détecter
     * @return Couple EC / score de compacité
     */
    public static HashMap<PassageNamedEntity,Double> process(String text, String targetType)
    {
        try{
        	AbstractNamedEntityRecognitionTool NER = NamedEntityRecognitionToolFactory.get();
        	text = NER.proceed(text);
        	
        	TreeTagger treeTagger = new TreeTagger(GetProperties.getInstance().getProperty("treeTaggerPath"));
        	ArrayList<ArrayList<String>> tokensTag = treeTagger.tag(text);

            ArrayList<String> query = new ArrayList<String>(); 
            HashMap<String,HashMap<Integer,ArrayList<Integer>>>  indexTerms = new HashMap<String, HashMap<Integer,ArrayList<Integer>>>();//indexTerms[word][numPassage] = positions;
            ArrayList<PassageNamedEntity> namedEntities = new ArrayList<PassageNamedEntity>();
            HashMap<PassageNamedEntity,Double> scoreEN = new HashMap<PassageNamedEntity,Double>(); // scoreEN[PassageNamedEntity] = score EN
           
            String balise = new String(); //indicateur de la balise en cours

            String docno = new String(); //numéro du document duquel est extrait le passage en cours
            String enSource = new String(); //en Source
            String enType = null; //type attendu
            boolean prodProcessing = false; //est on en train de construite une EN de type produit?
            String currentNoProductEN = new String(); //EN non produit courante (pour vérifier que le candidat produit n'est pas exactement un non produit
            Integer position = 0, numPassage = 0; //position de l'EN et numéro du passage

            for(ArrayList<String> tokenTag : tokensTag)
            {
            	String token = tokenTag.get(0);
            	
            	if(token.equals("<passages>") || tokenTag.get(0).equals("</passages>")) //ignore ces lignes
                    continue;
                else if(token.equals("<enSource>")) //prévient que l'on est en train de récupérer le nom de l'EN source
                    balise = "enSource";
                else if(token.equals("</enSource>")) //on a finit de récupérer l'EN source
                {
                    balise = new String(); //on remet à zéro l'indicateur de la balise en cours
                    enSource = enSource.trim();
                }
                if(token.equals("<query>"))
                    balise = "query";
                else if(token.equals("</query>"))
                    balise = new String();
                else if(token.equals("<docno>"))
                    balise = "docno";
                else if(token.equals("</docno>"))
                    balise = new String();
                else if(token.equals("<passage>")) //début d'un nouveau passage
                    position = 0; //on remet la position à 0
                else if(token.equals("</passage>"))
                    ++numPassage;
                else if(token.matches("<[^/][^>]*>")) //si on a une balise ouvrante, donc le type d'une EN
                {
                    if(!balise.equals(""))//si on est dans une balise spécifique autre que "passage"
                        continue; //on passe

                    enType = token.substring(1,token.length()-1); //on récupère le type de l'EN

                    if(!enType.equals(targetType)) //si ça ne correspond pas avec le type souhaité
                    {
                    	if(targetType.equals("PROD")) //si on recherche un produit
                    		currentNoProductEN = new String();
                    	else
                    		enType = null; //on passe cette EN
                    }
                    else                    
                    	namedEntities.add(new PassageNamedEntity(docno,numPassage,position)); //on rajoute l'EN à la position correspondante
                }
                else if(token.matches("</[^>]*>")) //si on a une balise fermante, donc la fin d'une EN
                {
                    if(enType == null || !balise.equals("")) //on vérifie qu'elle est du type souhaité
                        continue;

                    enType = null; //on remet le type à zéro

                    if(!targetType.equals("PROD"))//si on ne recherche pas un produit
                    {
                    	String en = namedEntities.get(namedEntities.size()-1).getStringNamedEntity().toLowerCase().trim(); //récupération du texte de l'EN
                    	if(en.equals(enSource) || en.contains(enSource) || enSource.contains(en)) //si l'EN est l'ES ou qu'elle la contient ou l'inverse
                    		namedEntities.remove(namedEntities.size()-1); //on ne la prends pas en compte
                    }
                }
                else //si on a affaire à un mot
                {
                    String word = token.toLowerCase(); //le mot en minuscule
                    
                    if(!tokenTag.get(2).equals("<unknown>") && !tokenTag.get(2).equals("@card@")) //si on a le lemme on le prend
                        word = tokenTag.get(2).toLowerCase();
                    
                    if(word.equals(".")) //si on a un point on passe
                        continue;

                    if(balise.equals("enSource"))
                        enSource += word+" ";
                    else if(balise.equals("query"))
                        query.add(word);
                    else if(balise.equals("docno"))
                        docno = token.toLowerCase();
                    else //si on a soit le texte d'une EN soit un mot
                    {
                        if(targetType.equals("PROD")) //on cherche un produit
                        {
                            if((!tokenTag.get(2).equals("<unknown>") && tokenTag.get(2).substring(0,0).matches("/[A-Z]/")) || tokenTag.get(1).equals("NP") || (tokenTag.get(1).equals("CD") && prodProcessing))//on à ici un élément à récupérer
                            {
                            	if(!prodProcessing) //si on commence une EN
                            	{
                            		namedEntities.add(new PassageNamedEntity(docno,numPassage,position)); //on rajoute l'EN à la position correspondante
                            		prodProcessing = true; //on est en train de traiter une EN
                            	}

                            	namedEntities.get(namedEntities.size()-1).add(word); //on ajoute le mot
                            }
                            else if(prodProcessing)//si on a un mot qui ne se rajoute pas dans l'EN, alors on a finit de construire cette EN si il y en a une en cours. 
                            {
                            	prodProcessing = false; //aucune EN en cours
                                String en = namedEntities.get(namedEntities.size()-1).getStringNamedEntity().toLowerCase().trim(); //récupération du texte de l'EN
                                if(en.equals(enSource) || en.contains(enSource) || enSource.contains(en) || en.equals(currentNoProductEN.trim())) //si l'EN est l'ES ou qu'elle la contient ou l'inverse
                                    namedEntities.remove(namedEntities.size()-1); //on ne la prends pas en compte
                            }
                        }

                        if(query.contains(word)) //si le mot est présent dans la question
                        {
                            if(indexTerms.containsKey(word)) //si on a déjà ce mot
                            {
                                if(indexTerms.get(word).containsKey(numPassage)) //dans ce passage
                                    indexTerms.get(word).get(numPassage).add(position); //on rajoute la position de cette occurence
                                else //mais pas dans ce passage
                                {
                                    indexTerms.get(word).put(numPassage,new ArrayList<Integer>()); //on ajoute cette première occurence
                                    indexTerms.get(word).get(numPassage).add(position);
                                }
                            }
                            else //si c'est la première fois qu'on le rencontre
                            {
                                indexTerms.put(word,new HashMap<Integer, ArrayList<Integer>>()); //on ajoute cette première occurence
                                indexTerms.get(word).put(numPassage,new ArrayList<Integer>());
                                indexTerms.get(word).get(numPassage).add(position);
                            }
                        }

                        if(enType != null)//si on a une EN en cours de construction
                        {
                        	if(!targetType.equals("PROD"))//si on ne cherche pas un produit
                        		namedEntities.get(namedEntities.size()-1).add(word);
                        	else
                        		currentNoProductEN += word + " ";
                        }

                        ++position;
                    }
                }
            }

            //Calcul de la compacité pour chaque EN : meilleur occurence prise en compte
            for(PassageNamedEntity namedEntity: namedEntities)
            {
                HashMap<String,Double> contribWord = new HashMap<String,Double>();
                Double compacite = new Double(0); //\frac{1}{|QW|} \sum_{w \in QW}ContribCompacite(w)ERc_i

                Integer debut = namedEntity.getPosition();
                Integer fin = namedEntity.getPosition() + namedEntity.size() - 1;

                ArrayList<Integer> posOcc = new ArrayList<Integer>(); //contient toute les positions de tout les mots de la question
                for(String qWord : query)
                {
                	if(!indexTerms.containsKey(qWord) || !indexTerms.get(qWord).containsKey(namedEntity.getNumPassage()))//si le mot n'est pas présent dans le passage
                        continue;
                
                	for(Integer pos : indexTerms.get(qWord).get(namedEntity.getNumPassage())) //parcours des occurences de ce mot
                		posOcc.add(pos);
                }
                
                for(String qWord : query)
                {
                	if(!indexTerms.containsKey(qWord) || !indexTerms.get(qWord).containsKey(namedEntity.getNumPassage()))//si le mot n'est pas présent dans le passage
                        continue;
                	
                	contribWord.put(qWord, 0.); //contribution du mot
                	for(Integer pos : indexTerms.get(qWord).get(namedEntity.getNumPassage())) //pour chacune de ses positions
                	{
                		Double contrib = 2.;//contribution de l'EN et du mot
                		if(pos.compareTo(fin) > 0) //si on est dans la partie droite
                		{
                			for(int i = (fin+1); i < pos.intValue(); ++i) //pour chaque position entre lui et l'EN
                				if(posOcc.contains(i) && !indexTerms.get(qWord).get(namedEntity.getNumPassage()).contains(i)) //on regarde si il y a un mot de la question différent de celui traité 
                					++contrib; //si oui alors on rajoute 1 à la contribution
                			
                			contrib /= (pos - fin + 1);
                		}
                		else if(pos.compareTo(debut) < 0)
                		{
                			for(int i = (pos+1); i < debut.intValue(); ++i)
                				if(posOcc.contains(i) && !indexTerms.get(qWord).get(namedEntity.getNumPassage()).contains(i))
                					++contrib;
                			
                			contrib /= (debut - pos + 1);
                		}
                		else
                			continue;
                		if(contrib.compareTo(contribWord.get(qWord)) > 0)
                			contribWord.put(qWord, contrib);
                	}
                }
                
                for(String qWord : contribWord.keySet())
                	compacite += contribWord.get(qWord);
                
                scoreEN.put(namedEntity, compacite / query.size());
            }
            return scoreEN;
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }
}
