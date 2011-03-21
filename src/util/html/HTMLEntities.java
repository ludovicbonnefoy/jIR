package util.html;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * Map les caractères UTF-8 vers leur équivalent HTML et inversement.
 * @author unknown
 *
 */
public class HTMLEntities {
 
  /**
   * Entités HTML avec leur équivalent UTF-8.
   */
  private static Map<String, Character> HTML_ENTITIES;
 
  /**
   * Caractères UTF-8 avec leur équivalent entité.
   */
  private static Map<Character, String> UTF8_CHARS;
 
  /**
   * Initialise les Map au chargement de la classe.
   */
  private static void initEntities() {
    HTML_ENTITIES = new HashMap<String, Character>();
    HTML_ENTITIES.put("&acute;", '\u00B4');
    HTML_ENTITIES.put("&quot;", '\"');
    HTML_ENTITIES.put("&amp;", '\u0026');
    HTML_ENTITIES.put("&lt;", '\u003C');
    HTML_ENTITIES.put("&gt;", '\u003E');
    HTML_ENTITIES.put("&nbsp;", '\u00A0');
    HTML_ENTITIES.put("&iexcl;", '\u00A1');
    HTML_ENTITIES.put("&cent;", '\u00A2');
    HTML_ENTITIES.put("&pound;", '\u00A3');
    HTML_ENTITIES.put("&curren;", '\u00A4');
    HTML_ENTITIES.put("&yen;", '\u00A5');
    HTML_ENTITIES.put("&brvbar;", '\u00A6');
    HTML_ENTITIES.put("&sect;", '\u00A7');
    HTML_ENTITIES.put("&uml;", '\u00A8');
    HTML_ENTITIES.put("&copy;", '\u00A9');
    HTML_ENTITIES.put("&ordf;", '\u00AA');
    HTML_ENTITIES.put("&laquo;", '\u00AB');
    HTML_ENTITIES.put("&not;", '\u00AC');
    HTML_ENTITIES.put("&shy;", '\u00AD');
    HTML_ENTITIES.put("&reg;", '\u00AE');
    HTML_ENTITIES.put("&macr;", '\u00AF');
    HTML_ENTITIES.put("&deg;", '\u00B0');
    HTML_ENTITIES.put("&plusmn;", '\u00B1');
    HTML_ENTITIES.put("&sup2;", '\u00B2');
    HTML_ENTITIES.put("&sup3;", '\u00B3');
    HTML_ENTITIES.put("&acute;", '\u00B4');
    HTML_ENTITIES.put("&micro;", '\u00B5');
    HTML_ENTITIES.put("&para;", '\u00B6');
    HTML_ENTITIES.put("&middot;", '\u00B7');
    HTML_ENTITIES.put("&cedil;", '\u00B8');
    HTML_ENTITIES.put("&sup1;", '\u00B9');
    HTML_ENTITIES.put("&ordm;", '\u00BA');
    HTML_ENTITIES.put("&raquo;", '\u00BB');
    HTML_ENTITIES.put("&frac14;", '\u00BC');
    HTML_ENTITIES.put("&frac12;", '\u00BD');
    HTML_ENTITIES.put("&frac34;", '\u00BE');
    HTML_ENTITIES.put("&iquest;", '\u00BF');
    HTML_ENTITIES.put("&Agrave;", '\u00C0');
    HTML_ENTITIES.put("&Aacute;", '\u00C1');
    HTML_ENTITIES.put("&Acirc;", '\u00C2');
    HTML_ENTITIES.put("&Atilde;", '\u00C3');
    HTML_ENTITIES.put("&Auml;", '\u00C4');
    HTML_ENTITIES.put("&Aring;", '\u00C5');
    HTML_ENTITIES.put("&AElig;", '\u00C6');
    HTML_ENTITIES.put("&Ccedil;", '\u00C7');
    HTML_ENTITIES.put("&Egrave;", '\u00C8');
    HTML_ENTITIES.put("&Eacute;", '\u00C9');
    HTML_ENTITIES.put("&Ecirc;", '\u00CA');
    HTML_ENTITIES.put("&Euml;", '\u00CB');
    HTML_ENTITIES.put("&Igrave;", '\u00CC');
    HTML_ENTITIES.put("&Iacute;", '\u00CD');
    HTML_ENTITIES.put("&Icirc;", '\u00CE');
    HTML_ENTITIES.put("&Iuml;", '\u00CF');
    HTML_ENTITIES.put("&ETH;", '\u00D0');
    HTML_ENTITIES.put("&Ntilde;", '\u00D1');
    HTML_ENTITIES.put("&Ograve;", '\u00D2');
    HTML_ENTITIES.put("&Oacute;", '\u00D3');
    HTML_ENTITIES.put("&Ocirc;", '\u00D4');
    HTML_ENTITIES.put("&Otilde;", '\u00D5');
    HTML_ENTITIES.put("&Ouml;", '\u00D6');
    HTML_ENTITIES.put("&times;", '\u00D7');
    HTML_ENTITIES.put("&Oslash;", '\u00D8');
    HTML_ENTITIES.put("&Ugrave;", '\u00D9');
    HTML_ENTITIES.put("&Uacute;", '\u00DA');
    HTML_ENTITIES.put("&Ucirc;", '\u00DB');
    HTML_ENTITIES.put("&Uuml;", '\u00DC');
    HTML_ENTITIES.put("&Yacute;", '\u00DD');
    HTML_ENTITIES.put("&THORN;", '\u00DE');
    HTML_ENTITIES.put("&szlig;", '\u00DF');
    HTML_ENTITIES.put("&agrave;", '\u00E0');
    HTML_ENTITIES.put("&aacute;", '\u00E1');
    HTML_ENTITIES.put("&acirc;", '\u00E2');
    HTML_ENTITIES.put("&atilde;", '\u00E3');
    HTML_ENTITIES.put("&auml;", '\u00E4');
    HTML_ENTITIES.put("&aring;", '\u00E5');
    HTML_ENTITIES.put("&aelig;", '\u00E6');
    HTML_ENTITIES.put("&ccedil;", '\u00E7');
    HTML_ENTITIES.put("&egrave;", '\u00E8');
    HTML_ENTITIES.put("&eacute;", '\u00E9');
    HTML_ENTITIES.put("&ecirc;", '\u00EA');
    HTML_ENTITIES.put("&euml;", '\u00EB');
    HTML_ENTITIES.put("&igrave;", '\u00EC');
    HTML_ENTITIES.put("&iacute;", '\u00ED');
    HTML_ENTITIES.put("&icirc;", '\u00EE');
    HTML_ENTITIES.put("&iuml;", '\u00EF');
    HTML_ENTITIES.put("&eth;", '\u00F0');
    HTML_ENTITIES.put("&ntilde;", '\u00F1');
    HTML_ENTITIES.put("&ograve;", '\u00F2');
    HTML_ENTITIES.put("&oacute;", '\u00F3');
    HTML_ENTITIES.put("&ocirc;", '\u00F4');
    HTML_ENTITIES.put("&otilde;", '\u00F5');
    HTML_ENTITIES.put("&ouml;", '\u00F6');
    HTML_ENTITIES.put("&divide;", '\u00F7');
    HTML_ENTITIES.put("&oslash;", '\u00F8');
    HTML_ENTITIES.put("&ugrave;", '\u00F9');
    HTML_ENTITIES.put("&uacute;", '\u00FA');
    HTML_ENTITIES.put("&ucirc;", '\u00FB');
    HTML_ENTITIES.put("&uuml;", '\u00FC');
    HTML_ENTITIES.put("&yacute;", '\u00FD');
    HTML_ENTITIES.put("&thorn;", '\u00FE');
    HTML_ENTITIES.put("&yuml;", '\u00FF');
    HTML_ENTITIES.put("&OElig;", '\u008C');
    HTML_ENTITIES.put("&oelig;", '\u009C');
    HTML_ENTITIES.put("&euro;", '\u20AC');
 
    UTF8_CHARS = new HashMap<Character, String>();
    for (String key : HTML_ENTITIES.keySet()) {
      UTF8_CHARS.put(HTML_ENTITIES.get(key), key);
    }
  }
 
  /**
   * Encode les caractères UTF-8 spéciaux en entités HTML.
   *
   * @param s
   *            Chaine dont les caractères spéciaux doivent être encodés.
   * @return Chaine dont les caractères spéciaux ont été encodés.
   */
  public static String encode(String s) {
    if ((UTF8_CHARS == null) && (HTML_ENTITIES == null)) {
      HTMLEntities.initEntities();
    }
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      String htmlEntity = UTF8_CHARS.get(c);
      if (htmlEntity != null) {
        result.append(htmlEntity);
      } else {
        result.append(c);
      }
    }
    return (result.toString());
  }
 
  /**
   * Décode les entités HTML d'une chaine.
   *
   * @param s
   *            Chaine dont les entités doivent être décodées.
   * @return Chaine avec les entités reconnues converties.
   */
  public static String decode(String s) {
    if ((UTF8_CHARS == null) && (HTML_ENTITIES == null)) {
      HTMLEntities.initEntities();
    }
    StringBuffer result = new StringBuffer();
    int start = 0;
    Pattern p = Pattern.compile("&[a-zA-Z]+;");
    Matcher m = p.matcher(s);
    while (m.find(start)) {
      Character utf8Char = HTML_ENTITIES.get(s.substring(m.start(), m.end()));
      result.append(s.substring(start, m.start()));
      if (utf8Char != null) {
        result.append(utf8Char);
      } else {
        result.append(s.substring(m.start(), m.end()));
      }
      start = m.end();
    }
    return (result.append(s.substring(start)).toString());
  }
}