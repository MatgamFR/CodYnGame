import javafx.scene.control.TextArea;

public abstract class IDEExecuteCode {
    /**
     * Exécute le code Python donné en paramètre.
     * @param code Le code à exécuter
     */
    private TextArea textArea;
    public abstract void executeCode(String code, int id);
    public abstract void compileCode(String code);

    public IDEExecuteCode(TextArea textArea) {
        this.textArea = textArea;
    }

    public void printOutput(String output) {
        textArea.appendText(output+"\n");
    }
}