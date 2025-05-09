public class LanguageChoice {
    public static IDEExecuteCode choice(String langage) {
        if (langage.equals("Python")) {
            return new PythonExecuteCode();
        } else if (langage.equals("Java")) {
            return new JavaCompilerExecuteCode(); // Utiliser JavaCompilerExecuteCode pour Java
        } else {
            throw new IllegalArgumentException("Langage non supporté : " + langage);
        }
    }
}