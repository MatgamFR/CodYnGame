import org.fxmisc.richtext.CodeArea;

public abstract class IDEExecuteCode {
    /**
     * Exécute le code Python donné en paramètre.
     * @param code Le code à exécuter
     */
    private CodeArea textArea;
    public abstract void executeCode(String code, int id);
    public abstract void compileCode(String code);

    public IDEExecuteCode(CodeArea textArea) {
        this.textArea = textArea;
    }

    public void printOutput(String output) {
        textArea.appendText(output+"\n");
    }
}
