package com.codyngame.compiler;

import javafx.scene.control.TextArea;

/**
 * Abstract base class for code execution in the Codyngame environment.
 * Provides common functionality for compiling and executing code in different languages.
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
public abstract class IDEExecuteCode {
    /**
     * Executes the given code for the specified exercise ID
     * @param code The code to execute
     * @param id The exercise ID
     */
    private TextArea textArea;
    public abstract void executeCode(String code, int id);
    public abstract void compileCode(String code, int id);

    /**
     * Constructor initializing with output text area
     * @param textArea The text area for displaying execution results
     */
    public IDEExecuteCode(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Prints output to the text area with appropriate styling
     * @param output The text to display
     */
    public void printOutput(String output) {
        textArea.appendText(output+"\n");
        if(output.equals("The code is correct")){
            textArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 1); " + 
            "-fx-text-fill: #00FF00; " + // Green terminal-like color 
            "-fx-font-family: 'Monospace'; " + 
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc);" ); 
        }
        else {
            textArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 1); " + 
            "-fx-text-fill:rgb(255, 0, 0); " + // Red color for errors
            "-fx-font-family: 'Monospace'; " + 
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc);" ); 
        }
    }
}