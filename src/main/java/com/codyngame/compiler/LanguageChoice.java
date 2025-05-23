package com.codyngame.compiler;

import javafx.scene.control.TextArea;

/**
 * A factory class that creates appropriate code executor instances based on programming language.
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
public class LanguageChoice {
    /**
     * Creates and returns an appropriate code executor for the specified language.
     * @param langage The programming language ("Python", "Java", "C", "JavaScript", or "PHP")
     * @param textArea The TextArea where execution results will be displayed
     * @return An instance of IDEExecuteCode for the specified language
     * @throws IllegalArgumentException if the language is not supported
     */
    public static IDEExecuteCode choice(String langage, TextArea textArea) {
        if (langage.equals("Python")) {
            return new PythonExecuteCode(textArea);
        }
        else if (langage.equals("Java")) {
            return new JavaCompilerExecuteCode(textArea); // Use JavaCompilerExecuteCode for Java
        }
        else if (langage.equals("C")) {
            return new CcompilerExecuter(textArea); // Use CcompilerExecuter for C
        }
        else if (langage.equals("JavaScript")) {
            return new JavaScriptCompilerExecute(textArea); // Use JavaScriptCompilerExecute for JavaScript
        }
        else if (langage.equals("PHP")) {
            return new PhpCompilerExecute(textArea); // Use PhpCompilerExecute for PHP
        }
        else {
            throw new IllegalArgumentException("Unsupported language: " + langage);
        }
    }
}