
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class ColorisationSyntaxique {

    // les mots clés de C et Java, faut que je rajoute les autres mais grosse flemme
    private static final String[] KEYWORDS = new String[] {
        "auto","break","case","char","const","continue","do","double","else","float","for","if","int","long","return","default",
        "short","switch","struct","typedef","sizeof","static","void","while","boolean","unsigned","volatile","union","register",
        "enum","extern","goto","null",
        "abstract","assert","byte","catch","class","extends","final","finally","implements","import","instanceof","interface","native",
        "new","package","private","protected","public","strictfp","super","synchronized","this","throw","throws","transient","try"
    };


    //differencier les mots clés
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = """
                                                  //[^
                                                  ]*|/\\*(.|\\R)*?\\*/""";
    private static final String NUMBER_PATTERN = "\\b\\d+\\b"; 

    private static final Pattern PATTERN = Pattern.compile(
    "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
        + "|(?<PAREN>" + PAREN_PATTERN + ")"
        + "|(?<BRACE>" + BRACE_PATTERN + ")"
        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
        + "|(?<STRING>" + STRING_PATTERN + ")"
        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
    );

    // Comment inserer un flux de texte dans un texte area avec des couleurs 
    // Comment faire pour chaanger le stule ou la couleur du texte 
    // Gerer la tabulation pour qu'il y ai les bons espaces a chaque retour a la ligne 

    public static CodeArea createCodeArea() {
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        codeArea.richChanges()
            .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
            .subscribe(change -> {
                codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
            });
        
        return codeArea;
    }


    // Parcours le texte pour savoir où il y a des motifs a changer
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
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
