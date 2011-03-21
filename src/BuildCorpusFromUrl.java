import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import entityranking.CorresClueWeb;
import util.GetProperties;

/**
 * Cette classe récupère des pages html (soit sur le net, soit dans le ClueWeb) à partir d'une liste d'URLs afin d'obtenir un corpus. 
 * Les documents récupérés pour former le corpus sont mis au format TREC.
 * Un fichier correspondance est créé et contient les paires : nom du document (docno) / url
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class BuildCorpusFromUrl
{
	/**
	 * Efface un corpus
	 * @param path Le chemin du corpus
	 */
    public static void deleteCorpus(String path)
    {
        File corpusDirectory = new File(path);
        if( corpusDirectory.exists() ) 
        { 
            File[] files = corpusDirectory.listFiles(); 
            for(int i=0; i<files.length; i++) 
                files[i].delete(); 
        } 
    }
    
    /**
     * Construit un corpus en cherchant les pages web correspondantes aux urls dans le corpus ClueWeb09
     * @param path Le chemin du corpus
     * @param urls La liste des URLs du corpus
     */
    public static void buildCorpusClueWeb(String path, ArrayList<String> urls)
    {
        try{
            deleteCorpus(path); //écrase le précédant corpus (si il existe)

            //Récupération des fichiers
            Integer docno = 0; //numéro du document
            
            PrintWriter correspondances = new PrintWriter (new OutputStreamWriter (new FileOutputStream (path+"/correspondances"),"UTF-8")) ; //fichier de correspondance
            
            for(String url : urls) //parcours des URLs
            {
            	String text = CorresClueWeb.getFile(url); //récupération du texte du document dans le ClueWeb09 correspondant à cette url.
            		
                if(text == null) //si on ne trouve pas de page correspondante à cette url on passe à l'url suivante de la liste
                	continue;
                
                //System.err.print(docno+"\t");
                correspondances.println(docno+"\t"+url);

                //mise en forme du document suivante la norme de TREC
                PrintWriter doc = new PrintWriter(new OutputStreamWriter (new FileOutputStream (path+"/"+docno),"UTF-8"));
                doc.println("<DOC>");
                doc.println("<DOCNO>"+docno+"</DOCNO>");
                doc.println("<DOCHDR>\n"+url+"\n</DOCHDR>");
              	doc.println(text);
                doc.println("</DOC>");
                doc.close();

                docno++;
            }
            System.err.print(docno+"\n");
            correspondances.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Construit un corpus en récupérant les pages sur le web quand elles ne sont pas dans le ClueWeb09
     * @param path Chemin du corpus
     * @param urls Liste des URLs du corpus
     */
    public void buildCorpusClueWebWebTrecForm(String path, ArrayList<String> urls)
    {
        try{
            deleteCorpus(path); //écrase le corpus situé à cet emplacement (si il existe)

            //Récupération des fichiers
            Integer docno = 0;
            Runtime runtime = Runtime.getRuntime();
            PrintWriter correspondances = new PrintWriter (new OutputStreamWriter (new FileOutputStream (path+"/correspondances"),"UTF-8")); //fichier de correspondance
            
            for(String url : urls)//parcours des URLs
            {
            	String text = CorresClueWeb.getFile(url); //récupération du texte du document dans le ClueWeb09 correspondant à cette url.

                System.err.print(docno + "\t");
                correspondances.println(docno + "\t" + url);
            	
                if(text == null) //si on ne trouve pas de page correspondante à cette url on va récupérer la page sur internet
            		runtime.exec(new String[]{GetProperties.getInstance().getProperty("wget"),"-O",GetProperties.getInstance().getProperty("tmpDirectory")+"/page","-T","5","-t","2",url}).waitFor();

                
                //formatage suivant le format TREC
                PrintWriter doc = new PrintWriter(new OutputStreamWriter (new FileOutputStream (path + "/" + docno),"UTF-8"));
                doc.println("<DOC>");
                doc.println("<DOCNO>" + docno + "</DOCNO>");
                doc.println("<DOCHDR>\n" + url + "\n</DOCHDR>");

                if(text == null) //si on a trouvé la page sur le net 
                {
                	// on lit le document et on le retranscrit dans le document créé
                	BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(GetProperties.getInstance().getProperty("tmpDirectory") + "/page"))); 
                	String line;
                	while ((line=br.readLine())!=null)
                	    doc.println(line);
                	br.close();
                }
                else
                	doc.println(text);

                doc.println("</DOC>");
                doc.close();

                docno++;
            }

            System.err.print(docno+"\n");
            correspondances.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}
