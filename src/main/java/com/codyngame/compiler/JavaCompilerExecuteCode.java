package com.codyngame.compiler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.codyngame.main.Connexionbdd;

import javafx.scene.control.TextArea;

/**
 * Class for compiling and executing Java code in the Codyngame environment.
 * Handles both STDIN/STDOUT and INCLUDE type exercises.
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
public class JavaCompilerExecuteCode extends IDEExecuteCode {
    private Path tempClassDir; // Attribute to store compiled classes directory path

    /**
     * Constructor initializing with a text area for output display
     * @param textArea The text area to display compilation/execution results
     */
    public JavaCompilerExecuteCode(TextArea textArea) {
        super(textArea);
    }

    /**
     * Compiles the provided Java code for the given exercise ID
     * @param code The Java code to compile
     * @param id The exercise ID for compilation context
     */
    @Override
    public void compileCode(String code, int id) {
        try {
            if (!code.contains("public class Codyngame")) {
                this.printOutput("Java code must contain a public class named 'Codyngame'.");
                return;
            }

            if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){     
                // Create temporary directory that will be kept until execution
                tempClassDir = Files.createTempDirectory("codyngame_classes");
                Path tempFile = tempClassDir.resolve("Codyngame.java");
                Files.writeString(tempFile, code);

                Process process = Runtime.getRuntime().exec(new String[]{"javac", tempFile.toAbsolutePath().toString()});
                if (process.waitFor() != 0) {
                    this.printOutput("Compilation error:\n" + new String(process.getErrorStream().readAllBytes()));
                } else {
                    this.printOutput("Compilation successful.");
                }
            } 
            else {
                tempClassDir = Files.createTempDirectory("codyngame_classes");
                Path tempFile = tempClassDir.resolve("Codyngame.java");
                Files.writeString(tempFile, code);
                Path tempFile2 = tempClassDir.resolve("Exercice" + id + ".java");
                File tempFile3 = new File("src/main/resources/Correction/Exercice" + id +".java");
                Files.writeString(tempFile2, Files.readString(tempFile3.toPath()));

                Process process = Runtime.getRuntime().exec(new String[]{"javac", tempFile2.toAbsolutePath().toString(), tempFile.toAbsolutePath().toString()});
                if (process.waitFor() != 0) {
                    this.printOutput("Compilation error:\n" + new String(process.getErrorStream().readAllBytes()));
                } else {
                    this.printOutput("Compilation successful.");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error during compilation: " + e.getMessage());
        }
    }

    /**
     * Executes the compiled Java code for the given exercise ID
     * @param code The Java code to execute
     * @param id The exercise ID for execution context
     */
    @Override
    public void executeCode(String code, int id) {
        try {
            // Compile code first
            this.compileCode(code, id);
            
            // Verify compilation was successful
            if (tempClassDir == null || !Files.exists(tempClassDir.resolve("Codyngame.class"))) {
                this.printOutput("Error: Compiled file Codyngame.class not found");
                return;
            }
            
            boolean valide = true;

            String[] output = {""};
            String resultat2 = "";

            int exitCode = 1;

            Process process2;
            Process process3;

            // Test with 10 different seeds
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                // Step 1: Generate random input data
                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources//Random/randomGeneration" + id + ".py", String.valueOf(seed)});
                byte[] resultat = process.getInputStream().readAllBytes();
                process.waitFor();

                boolean completed;

                if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){

                    process3 = Runtime.getRuntime().exec(new String[]{"java", "-cp", tempClassDir.toAbsolutePath().toString(), "Codyngame"});
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
                    
                    // Step 2: Execute correction code
                    process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                    process2.getOutputStream().write((result+"\n").getBytes());
                    process2.getOutputStream().write(resultat);
                    process2.getOutputStream().close();
                }
                else{
                    process2 = Runtime.getRuntime().exec(new String[] {"ls"});

                    process3 = Runtime.getRuntime().exec(new String[]{"java", "-cp", tempClassDir.toAbsolutePath().toString(), "Exercice" + id});
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
                    // Read output file content
                    if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                        output = new String(process2.getInputStream().readAllBytes()).split("\n");
                    }
                    else{
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

        } 
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error executing code: " + e.getMessage());
        }
    }
}