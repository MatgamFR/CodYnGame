package com.codyngame.compiler;

import javafx.scene.control.TextArea;

public class LanguageChoice {
    public static IDEExecuteCode choice(String langage, TextArea textArea) {
        if (langage.equals("Python")) {
            return new PythonExecuteCode(textArea);
        }
        else if (langage.equals("Java")) {
            return new JavaCompilerExecuteCode(textArea); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else if (langage.equals("C")) {
            return new CcompilerExecuter(textArea); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else if (langage.equals("JavaScript")) {
            return new JavaScriptCompilerExecute(textArea); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else if (langage.equals("PHP")) {
            return new PhpCompilerExecute(textArea); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else {
            throw new IllegalArgumentException("Langage non supporté : " + langage);
        }
    }
}
