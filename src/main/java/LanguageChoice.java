public class LanguageChoice {
    public static IDEExecuteCode choice(String langage) {
        if (langage.equals("Python")) {
            return new PythonExecuteCode();
        }
        else if (langage.equals("Java")) {
            return new JavaCompilerExecuteCode(); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else if (langage.equals("C")) {
            return new CcompilerExecuter(); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else if (langage.equals("JavaScript")) {
            return new JavaScriptCompilerExecute(); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else if (langage.equals("PHP")) {
            return new PhpCompilerExecute(); // Utiliser JavaCompilerExecuteCode pour Java
        }
        else {
            throw new IllegalArgumentException("Langage non supporté : " + langage);
        }
    }
}