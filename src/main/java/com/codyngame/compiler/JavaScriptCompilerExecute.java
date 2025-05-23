package com.codyngame.compiler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import com.codyngame.main.Connexionbdd;

import javafx.scene.control.TextArea;

/**
 * Class for executing JavaScript code in the Codyngame environment.
 * Handles compilation and execution of JavaScript code with input/output validation.
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
public class JavaScriptCompilerExecute extends IDEExecuteCode {
    /**
     * Constructor initializing with a text area for output display
     * @param textArea The text area to display execution results
     */
    public JavaScriptCompilerExecute(TextArea textArea) {
        super(textArea); // Call parent class constructor
    }
    
    /**
     * Executes the provided JavaScript code with the given exercise ID
     * @param code The JavaScript code to execute
     * @param id The exercise ID for validation
     */
    @Override
    public void executeCode(String code, int id) {
        // Create temporary file with .js extension
        File tempFile = new File("src/main/resources/Correction/codyngame.js");

        try {
            // Write code to temporary file
            FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(code);
            fileWriter.close();

            String[] output = {""};
            String resultat2 = "";

            boolean valide = true;

            int exitCode = 1;

            // Test with 10 different seeds
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();
                
                // Execute shell script (handles redirection as Runtime.exec can't execute command directly)
                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources//Random/randomGeneration" + id + ".py", String.valueOf(seed)});
                byte[] resultat = process.getInputStream().readAllBytes();

                Process process2;
                Process process3;

                boolean completed;

                if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                    process3 = Runtime.getRuntime().exec(new String[]{"node", tempFile.toPath().toString()});
                    process3.getOutputStream().write(resultat);
                    process3.getOutputStream().close();

                    completed = process3.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);

                    if (!completed) {
                        this.printOutput("Program exceeded maximum execution time of 15 seconds. Forced stop.");
                        process3.destroy();
                        process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                        if (process3.isAlive()) {
                            process3.destroyForcibly();
                        }
                        this.printOutput("Program probably tried to use more inputs than expected or has an infinite loop.");
                        return;
                    } 

                    resultat2 = new String(process3.getInputStream().readAllBytes());
                    String result = resultat2.replace("\n", "\\n");

                    process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                    process2.getOutputStream().write((result+"\n").getBytes());
                    process2.getOutputStream().write(resultat);
                    process2.getOutputStream().close();
                }
                else{
                process2 = Runtime.getRuntime().exec(new String[]{"node", tempFile.toPath().toString()});

                process3 = Runtime.getRuntime().exec(new String[]{"node", "src/main/resources/Correction/Exercice" + id +".js" });
                process3.getOutputStream().write(resultat);
                process3.getOutputStream().close();

                completed = process3.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);

                    if (!completed) {
                        this.printOutput("Program exceeded maximum execution time of 15 seconds. Forced stop.");
                        process3.destroy();
                        process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                        if (process3.isAlive()) {
                            process3.destroyForcibly();
                        }
                        this.printOutput("Program probably tried to use more inputs than expected or has an infinite loop.");
                        return;
                    } 
              
                }

                exitCode = process3.exitValue();
                if (exitCode != 0) {
                    this.printOutput(new String(process3.getErrorStream().readAllBytes()));
                    return;
                }
                else {
                    if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                        // Read output file content
                        output = new String(process2.getInputStream().readAllBytes()).split("\n");
                    }
                    else{
                        // Read output file content
                        output = new String(process3.getInputStream().readAllBytes()).split("\n");
                    }
                    if(!output[0].equals("1")){
                        valide = false;
                        break;
                    }
                }
            }
            
            this.printOutput("Program finished with exit code: " + exitCode);
            if(output.length > 4){
                this.printOutput("incorrect, you did a display instead of a return");
            }
            else if(valide) {
                this.printOutput("The code is correct");
            } 
            else {
                this.printOutput("The code is incorrect");
                this.printOutput("Received: '" + output[1] + "' value " + output[3]);
                this.printOutput("Expected: '" + output[2] + "' value " + output[3]);
            }
            
            // Clean temporary files
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                System.err.println("Error deleting temporary files: " + e.getMessage());
            }
                
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error executing code: " + e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                System.err.println("Error deleting temporary files: " + e.getMessage());
            }
        }
    }

    @Override
    public void compileCode(String code, int id) {
        // JavaScript doesn't require compilation
    }
}