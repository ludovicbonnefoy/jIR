package web.util.html;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import util.Log;

/**
 * Supprime les balises HTML d'un texte.
 * Les balises du type "p" sont remplacées par un retour à la ligne, les autres par rien.
 * De plus cette classe ne retourner pas le texte contenu dans certaines balises (style, head, ...)
 * @author Ludovic Bonnefoy (ludovic.bonnefoy@gmail.com)
 */
public class HTML2Text extends HTMLEditorKit.ParserCallback 
{
	/** Contient le texte du document */
    StringBuffer _text;
    boolean _breakflow;
    
    /** Permet de ne pas prendre en compte le texte de certaines balises et de prendre en compte leur imbrication */
    Integer interdiction;
    
    /** Initialisation de la classe */
    public HTML2Text()
    {
        interdiction = new Integer(0);
    }

    /**
     * Fonction qui va traiter le texte passé en paramètre.
     * @param in Reader du texte
     * @throws IOException
     */
    public void parse(Reader in) throws IOException, StackOverflowError
    {
        interdiction = 0;
        
        _text = new StringBuffer();
        _breakflow = true;
        //init
        ParserDelegator delegator = new ParserDelegator();
        // the third parameter is TRUE to ignore charset directive
        delegator.parse(in, this, Boolean.TRUE); //traitement
        in.close();
    }

    /**
     * Fonction qui lit et gère les ouvertures de tags.
     */
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
    {
        if(t.breaksFlow() && !_breakflow) //gestion des retours à la ligne pour certains type de tags (p, ...)
        {
            _text.append(".\n");
            _breakflow = true;
        }

        if(t.equals(HTML.Tag.STYLE) || t.equals(HTML.Tag.HEAD) || t.equals(HTML.Tag.SCRIPT) || t.equals(HTML.Tag.SELECT))// || t.equals(HTML.Tag.TABLE)) //ne permet de signaler que l'on ne veut pas le texte de ces balises
            ++interdiction;
    }

    /**
     * Fonction qui lit et gère les fermetures de tags.
     */
    public void handleEndTag(HTML.Tag t,  int pos)
    {
        if(t.equals(HTML.Tag.STYLE) || t.equals(HTML.Tag.HEAD) || t.equals(HTML.Tag.SCRIPT) || t.equals(HTML.Tag.SELECT))// || t.equals(HTML.Tag.TABLE)) //met à jour le compteur
            --interdiction;
    }

    /**
     * Traite le texte.
     */
    public void handleText(char[] text, int pos)
    {
        if(interdiction <= 0) // Si on n'est pas dans une balise pour laquelle on ne veut pas conserver le texte
        {
            _text.append(" ").append(text); //ajout de la portion de texte à la suite de celui précédemment conservé.
            _breakflow = false;
        }
    }

    /**
     * Supprime un certain nombre de caractères du texte et le reformate avant de le retourner.
     * @return Texte "nettoyé"
     */
    public String getCleanText()
    {
    	return HTMLEntities.decode(getText()).replaceAll("(<[^>]*>)|(script[^<>]*>)","").replaceAll("\n+","\n").trim();
        //return HTMLEntities.decode(getText()).replaceAll("<[^>]*>","").replaceAll("\n+","\n");
//        return HTMLEntities.decode(getText()).replaceAll("<","").replaceAll(">","").replaceAll("\n+","\n");
    }


    /**
     * Retourne le texte sans traitement supplémentaires.
     * @return Texte
     */
    public String getText() 
    {
        return _text.toString();
    }

    /**
     * Main permettant de faire fonctionner la classe de manière autonome.
     * Attends en paramètre le chemin d'un fichier à traiter.
     * @param args Chemin du fichier contenant le texte.
     */
    public static void main (String[] args) 
    {
        try {
            // the HTML to convert
            FileReader in = new FileReader("0");            
            HTML2Text parser = new HTML2Text();
           	parser.parse(in);
            in.close();

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out")));
            //out.println("<Text>");
            out.println(parser.getCleanText());
            //out.println("</Text>");
            out.close();
        }
        catch (StackOverflowError e) {
        	e.printStackTrace();
        }
        catch (Exception e) {
			Log.getInstance().add(e);
			e.printStackTrace();
		}
    }
}
