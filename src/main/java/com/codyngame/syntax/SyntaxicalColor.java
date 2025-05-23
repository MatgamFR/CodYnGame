package com.codyngame.syntax;

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

/**
 * A class that provides syntax highlighting functionality for various programming languages.
 * It supports keywords recognition and syntax patterns for languages like C, Java, PHP, JavaScript, and Python.
 * 
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
public class SyntaxicalColor {
    
    // Keywords for all languages
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
                "throws","transient","true","try","void","volatile","while"
            });
            LANGUAGE_KEYWORDS.put("PHP", new String[] {
                "__halt_compiler()","and","abstract","array()","as","break","callable","case","catch","class","clone","const","continue","declare",
                "default","die()","do","echo","else","elseif","empty()","enddeclare","endfor","endforeach","endif","endswitch","endwhile","eval()",
                "exit()","extends","final","finally","fn","for","foreach","function","globa","got","if","implements","include","include_once",
                "insteadof","instanceof","interface","isset()","list()","match","namespace","new","or","print","private","protected","public",
                "require","readonly","require_once","return","static","switch","throw","trait","try","unset()","use","var","while","xor","yield","yield from",
            });
            LANGUAGE_KEYWORDS.put("JavaScript", new String[] {
                "while","case","class","void" ,"function","instanceof","throw","export","delete","catch ","private","package","true","debugger","extends","default",
                "interface","super","with","enum","if","return","switch","try","let","yield","typeof","public","for","static","new","else","finally","false",
                "import","var","do","protected","null","in","implements","this","await","const","continue","break",
            });
            LANGUAGE_KEYWORDS.put("Python", new String[] {
                "and","as","assert","break","class","continue","def","del","elif","else","except","exec","finally","for","from","global","if","import","in","is",
                "lambda","not","or","pass","print","raise","return","try","while","with","yield"
            });
    }

    // Patterns for syntax elements
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

    // How to insert a text stream into a text area with colors
    // How to change the style or color of the text
    // Handle tabulation to have proper spacing on each line return

    /**
     * Creates and configures a CodeArea with syntax highlighting capabilities.
     * 
     * @return A configured CodeArea instance with syntax highlighting enabled
     */
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

    /**
     * Sets the programming language for syntax highlighting.
     * If the language is recognized, its keywords will be used for highlighting.
     * 
     * @param language The programming language to set (e.g., "Java", "C", "Python")
     */
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

    /**
     * Computes the syntax highlighting spans for the given text.
     * 
     * @param text The text to analyze for syntax highlighting
     * @return StyleSpans object containing the highlighting information
     */
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

            
            // Add style for the corresponding text portion
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();

        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}