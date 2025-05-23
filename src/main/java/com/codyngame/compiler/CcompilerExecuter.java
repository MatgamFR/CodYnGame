package com.codyngame.compiler;
import com.codyngame.main.Connexionbdd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.TextArea;

public class CcompilerExecuter extends IDEExecuteCode {
    private Path compiledExecutable = null;

    public CcompilerExecuter(TextArea textArea) {
        super(textArea);
    }

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
                this.printOutput("Timeout de compilation dépassé (10s)");
            }

            if (compileProcess.exitValue() != 0) {
                String errorOutputC = new String(compileProcess.getErrorStream().readAllBytes());
                if (errorOutputC.contains("error:")) {
                    this.printOutput("[ERREUR SYNTAXE]");
                } 
                if (errorOutputC.contains("warning:")) {
                    this.printOutput("[AVERTISSEMENT]");
                }
                
                this.printOutput(errorOutputC);
            }
            else {
                this.printOutput("Compilation C reussie.");
                this.compiledExecutable = tempDir.resolve("exe");  // Sauvegarde le chemin de l'exécutable
                String warningC = new String(compileProcess.getErrorStream().readAllBytes());
                if (warningC.contains("warning")) {
                    this.printOutput("Avertissement de compilation:");
                    this.printOutput(warningC);
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            this.printOutput("Erreur lors de la compilation : " + e.getMessage());
        } 
    }

    @Override
    public void executeCode(String code, int id) {
        this.compileCode(code, id);

        if (compiledExecutable == null || !Files.exists(compiledExecutable)) {
            System.err.println("Erreur: Aucun exécutable compilé trouvé. Compilez d'abord le code.");
            return;
        }

        try {        
            boolean valide = true;

            String[] output = {""};
            String resultat2 = "";

            int exitCode = 1;

            // Tester avec 10 seeds différentes
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                // Exécuter le script shell (qui gère la redirection car Rutime.exec ne peut pas exécuter directement la commande)
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
                        this.printOutput("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                        process3.destroy();
                        process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                        if (process3.isAlive()) {
                            process3.destroyForcibly();
                        }
                        this.printOutput("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
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
                        this.printOutput("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                        process3.destroy();
                        process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                        if (process3.isAlive()) {
                            process3.destroyForcibly();
                        }
                        this.printOutput("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
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
            
            this.printOutput("Programme terminé avec le code de sortie: " + exitCode);
            if(output.length > 4){
                this.printOutput("incorrect, vous avez fait un affichage au lieu d'un renvoi");
            }
            else if(valide) {
                this.printOutput("Le code est correct");
            } 
            else {
                this.printOutput("Le code est incorrect");
                this.printOutput("Reçu : '" + output[1] + "' valeur " + output[3]);
                this.printOutput("Attendu : '" + output[2] + "' valeur " + output[3]);
            }
            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(this.compiledExecutable);
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression des fichiers temporaires: " + e.getMessage());
            }
        } 
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
        }
    }
}

