package com.codyngame.compiler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.codyngame.main.Connexionbdd;

import javafx.scene.control.TextArea;

/**
 * Class for compiling and executing C code in the Codyngame environment.
 * Handles both STDIN/STDOUT and INCLUDE type exercises.
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
public class CcompilerExecuter extends IDEExecuteCode {
    private Path compiledExecutable = null; // Path to compiled executable

    /**
     * Constructor initializing with a text area for output display
     * @param textArea The text area to display compilation/execution results
     */
    public CcompilerExecuter(TextArea textArea) {
        super(textArea);
    }

    /**
     * Compiles the provided C code for the given exercise ID
     * @param code The C code to compile
     * @param id The exercise ID for compilation context
     */
    @Override
    public void compileCode(String code, int id) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("codyngame.c");
            Files.writeString(tempFile, code);

            Process compileProcess;

            if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                compileProcess = Runtime.getRuntime().exec(
                new String[]{"gcc", tempFile.toAbsolutePath().toString(), "-o", tempDir.resolve("exe").toString()}
            );
            }
            else{
                Path tempFile2 = tempDir.resolve("Exercice" + id + ".c");
                File tempFile3 = new File("src/main/resources/Correction/Exercice" + id +".c");
                Files.writeString(tempFile2, Files.readString(tempFile3.toPath()));

                compileProcess = Runtime.getRuntime().exec(
                new String[]{"gcc", tempFile.toAbsolutePath().toString(), tempFile2.toAbsolutePath().toString(), "-o", tempDir.resolve("exe").toString()}
            );
            }

            if(!compileProcess.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
                compileProcess.destroyForcibly();
                this.printOutput("Compilation timeout exceeded (10s)");
            }

            if (compileProcess.exitValue() != 0) {
                String errorOutputC = new String(compileProcess.getErrorStream().readAllBytes());
                if (errorOutputC.contains("error:")) {
                    this.printOutput("[SYNTAX ERROR]");
                } 
                if (errorOutputC.contains("warning:")) {
                    this.printOutput("[WARNING]");
                }
                
                this.printOutput(errorOutputC);
            }
            else {
                this.printOutput("C compilation successful.");
                this.compiledExecutable = tempDir.resolve("exe");  // Save executable path
                String warningC = new String(compileProcess.getErrorStream().readAllBytes());
                if (warningC.contains("warning")) {
                    this.printOutput("Compilation warnings:");
                    this.printOutput(warningC);
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            this.printOutput("Error during compilation: " + e.getMessage());
        } 
    }

    /**
     * Executes the compiled C code for the given exercise ID
     * @param code The C code to execute
     * @param id The exercise ID for execution context
     */
    @Override
    public void executeCode(String code, int id) {
        this.compileCode(code, id);

        if (compiledExecutable == null || !Files.exists(compiledExecutable)) {
            System.err.println("Error: No compiled executable found. Compile the code first.");
            return;
        }

        try {        
            boolean valide = true;

            String[] output = {""};
            String resultat2 = "";

            int exitCode = 1;

            // Test with 10 different seeds
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                // Execute shell script (handles redirection as Runtime.exec can't execute command directly)
                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources//Random/randomGeneration" + id + ".py", String.valueOf(seed)});
                byte[] resultat = process.getInputStream().readAllBytes();

                Process process3;
                Process process2;

                boolean completed;

                if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                    process3 = Runtime.getRuntime().exec(new String[]{compiledExecutable.toAbsolutePath().toString()});
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
                    result.concat("\n");

                    process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                    process2.getOutputStream().write((result+"\n").getBytes());
                    process2.getOutputStream().write(resultat);
                    process2.getOutputStream().close();
                }
                else{
                    process3 = Runtime.getRuntime().exec(new String[]{compiledExecutable.toAbsolutePath().toString()});
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

                    process2 = Runtime.getRuntime().exec(new String[] {"ls"});
                }

                exitCode = process3.exitValue();
                
                if (exitCode != 0) {
                    this.printOutput(new String(process3.getErrorStream().readAllBytes()));
                    return;
                }
                else {
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
            // Clean temporary files
            try {
                Files.deleteIfExists(this.compiledExecutable);
            } catch (IOException e) {
                System.err.println("Error deleting temporary files: " + e.getMessage());
            }
        } 
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error executing code: " + e.getMessage());
        }
    }
}