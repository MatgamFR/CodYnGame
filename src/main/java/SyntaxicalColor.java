import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


public class SyntaxicalColor {
    
    // les mots clés de C et Java, faut que je rajoute les autres mais grosse flemme
    private static final Map<String,String[]> LANGUAGE_KEYWORDS = new HashMap<>();
        static{
            LANGUAGE_KEYWORDS.put("C", new String[] {
                "auto","break","case","char","const","continue","do","double","else","float","for","if","int","long","return","default",
                "short","switch","struct","typedef","sizeof","static","void","while","boolean","unsigned","volatile","union","register",
                "enum","extern","goto","null"
            });
            LANGUAGE_KEYWORDS.put("Java", new String[] {
                "abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else",
                "enum","extends","false","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native",
                "new","null","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw",
                "throws","transient","tru","try","void","volatile","while"
            });
            LANGUAGE_KEYWORDS.put("PHP", new String[] {
                "__halt_compiler()","and","abstract","array()","as","break","callable","case","catch","class","clone","const","continue","declare",
                "default","die()","do","echo","else","elseif","empty()","enddeclare","endfor","endforeach","endif","endswitch","endwhile","eval()",
                "exit()","extends","final","finally","fn","for","foreach","function","globa","got","if","implements","include","include_once",
                "insteadof","instanceof","interface","isset()","list()","match","namespace","new","or","print","private","protected","public",
                "require","readonly","require_once","return","static","switch","throw","trait","try","unset()","use","var","while","xor","yield","yield from",
            });
            LANGUAGE_KEYWORDS.put("Javascript", new String[] {
                "while","case","class","void" ,"function","instanceof","throw","export","delete","catch ","private","package","true","debugger","extends","default",
                "interface","super","with","enum","if","return","switch","try","let","yield","typeof","public","for","static","new","else","finally","false",
                "import","var","do","protected","null","in","implements","this","await","const","continue","break",
            });
            LANGUAGE_KEYWORDS.put("Python", new String[] {
                "and","as","assert","break","class","continue","def","del","elif","else","except","exec","finally","for","from","global","if","import","in","is",
                "lambda","not","or","pass","print","raise","return","try","while","with","yield"
            });
    }


    //differencier les mots clés
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = """
                                                  //[^
                                                  ]*|/\\*(.|\\R)*?\\*/""";
    private static final String NUMBER_PATTERN = "\\b\\d+\\b"; 

    private static Pattern currentPattern;
    private static String currentLanguage;

    // Comment inserer un flux de texte dans un texte area avec des couleurs 
    // Comment faire pour chaanger le stule ou la couleur du texte 
    // Gerer la tabulation pour qu'il y ai les bons espaces a chaque retour a la ligne 

    public static CodeArea createCodeArea() {
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        // Set a default language before applying highlighting
        if (currentLanguage == null || currentLanguage.isEmpty()) {
            setLanguage("Java"); // Default to Java syntax highlighting
        }
        
        codeArea.richChanges()
            .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
            .subscribe(change -> {
                codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
            });
        
        return codeArea;
    }

    // Si le langage est reconnu, on utilise les mots clés, sinon pas de mots clés
    public static void setLanguage(String language) {
        currentLanguage = language;
        if(LANGUAGE_KEYWORDS.containsKey(language)) {
            String keywordPattern = "\\b(" + String.join("|", LANGUAGE_KEYWORDS.get(language)) + ")\\b";
            currentPattern = Pattern.compile(
              "(?<KEYWORD>" + keywordPattern + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            );  
        } else {
            currentPattern = Pattern.compile(
            "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            );  
        }
    }


    // Parcours le texte pour savoir où il y a des motifs a changer
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    
        Matcher matcher = currentPattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */
            assert styleClass != null;

            
            // Ajoute le style pour la portion de texte correspondante
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();

        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


}


