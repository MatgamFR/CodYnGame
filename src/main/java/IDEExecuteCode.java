import javafx.scene.control.TextArea;

public abstract class IDEExecuteCode {
    /**
     * Exécute le code Python donné en paramètre.
     * @param code Le code à exécuter
     */
    private TextArea textArea;
    public abstract void executeCode(String code, int id);
    public abstract void compileCode(String code, int id);

    public IDEExecuteCode(TextArea textArea) {
        this.textArea = textArea;
    }

    public void printOutput(String output) {
        textArea.appendText(output+"\n");
        if(output.equals("Le code est correct")){
            textArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 1); " + 
            "-fx-text-fill: #00FF00; " + // Couleur verte comme un terminal 
            "-fx-font-family: 'Monospace'; " + 
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc);" ); 
        }
        else if(output.equals("Le code est incorrect")){
            textArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 1); " + 
            "-fx-text-fill:rgb(255, 0, 0); " + // Couleur verte comme un terminal 
            "-fx-font-family: 'Monospace'; " + 
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc);" ); 
        }
    }
}
