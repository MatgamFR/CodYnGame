import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CcompilerExecuter extends IDEExecuteCode {
    private Path compiledExecutable = null;

    @Override
    public void compileCode(String code) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("main.c");
            Files.writeString(tempFile, code);

            Process compileProcess = Runtime.getRuntime().exec(
                new String[]{"gcc", tempFile.toAbsolutePath().toString(), "-o", tempDir.resolve("exe").toString()}
            );

            if(!compileProcess.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
                compileProcess.destroyForcibly();
                System.out.println("Timeout de compilation dépassé (10s)");
            }

            if (compileProcess.exitValue() != 0) {
                String errorOutputC = new String(compileProcess.getErrorStream().readAllBytes());
                if (errorOutputC.contains("error:")) {
                    System.out.println("[ERREUR SYNTAXE]");
                } 
                if (errorOutputC.contains("warning:")) {
                    System.out.println("[AVERTISSEMENT]");
                }
                
                System.out.println(errorOutputC);
            }
            else {
                System.out.println("Compilation C reussie.");
                this.compiledExecutable = tempDir.resolve("exe");  // Sauvegarde le chemin de l'exécutable
                String warningC = new String(compileProcess.getErrorStream().readAllBytes());
                if (warningC.contains("warning")) {
                    System.out.println("Avertissement de compilation:");
                    System.out.println(warningC);
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la compilation : " + e.getMessage());
        } 
    }

    @Override
    public void executeCode(String code, int id) {
        this.compileCode(code);

        if (compiledExecutable == null || !Files.exists(compiledExecutable)) {
            System.err.println("Erreur: Aucun exécutable compilé trouvé. Compilez d'abord le code.");
            return;
        }

        try {
            // Créer un fichier temporaire pour la sortie
            Path outputFile = Files.createTempFile("output", ".txt");
            Path outputFile2 = Files.createTempFile("output2", ".txt");
            
            Path shellScript = Files.createTempFile("execute_", ".sh");
            Path shellScript2 = Files.createTempFile("execute2", ".sh");
            
            boolean valide = false;
            String output = "";
            String output2 = "";
            int exitCode = 1;

            // Tester avec 10 seeds différentes
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                String scriptContent = "#!/bin/bash\n" +
                                    "python3 src/main/resources/randomGeneration.py " + seed + " " + id + 
                                    " | " + compiledExecutable.toAbsolutePath() + 
                                    " > " + outputFile.toAbsolutePath() + " 2>&1";
                Files.writeString(shellScript, scriptContent);

                String scriptContent2 = "#!/bin/bash\n" +
                                    "python3 src/main/resources/randomGeneration.py " + seed + " " + id + 
                                    " | python3 src/main/resources/exercice.py " + id + 
                                    " > " + outputFile2.toAbsolutePath() + " 2>&1";
                Files.writeString(shellScript2, scriptContent2);

                shellScript2.toFile().setExecutable(true);  
                shellScript.toFile().setExecutable(true);

                Process executeProcess = Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript.toString()});
                Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript2.toString()}).waitFor(15, java.util.concurrent.TimeUnit.SECONDS);

                // Définir un timeout global de 15 secondes
                boolean completed = executeProcess.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
                
                if (!completed) {
                    System.out.println("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                    executeProcess.destroy();
                    executeProcess.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                    if (executeProcess.isAlive()) {
                        executeProcess.destroyForcibly();
                    }
                    System.out.println("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
                    valide = false;
                    break;
                } else {
                    exitCode = executeProcess.exitValue();

                    // Vérifier les erreurs de segmentation
                    if (exitCode == 1) {
                        System.out.println("ERREUR DE SEGMENTATION");
                        valide = false;
                        break;
                    }

                    // Lire et comparer les sorties
                    output = Files.readString(outputFile);
                    output2 = Files.readString(outputFile2);
                    if (output.equals(output2)) {
                        valide = true;
                    } else {
                        valide = false;
                        break;
                    }
                }
            }
            
            System.out.println("Programme terminé avec le code de sortie: " + exitCode);
            if (valide) {
                System.out.println("Le code est correct");
            } else {
                System.out.println("Le code est incorrect");
                System.out.println("Reçu : " + output);
                System.out.println("Attendu : " + output2);
            }

            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(this.compiledExecutable);
                Files.deleteIfExists(shellScript);
                Files.deleteIfExists(shellScript2);
                Files.deleteIfExists(outputFile);
                Files.deleteIfExists(outputFile2);
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

