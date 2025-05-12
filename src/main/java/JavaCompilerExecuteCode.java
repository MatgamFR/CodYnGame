import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaCompilerExecuteCode extends IDEExecuteCode {
    private Path tempClassDir; // Attribut pour stocker le chemin du répertoire des classes compilées

    @Override
    public void compileCode(String code) {
        try {
            if (!code.contains("public class Main")) {
                throw new IllegalArgumentException("Le code Java doit contenir une classe publique nommée 'Main'.");
            }
            
            // Créer un répertoire temporaire qui sera conservé jusqu'à l'exécution
            tempClassDir = Files.createTempDirectory("codyngame_classes");
            Path tempFile = tempClassDir.resolve("Main.java");
            Files.writeString(tempFile, code);

            Process process = Runtime.getRuntime().exec(new String[]{"javac", tempFile.toAbsolutePath().toString()});
            if (process.waitFor() != 0) {
                System.err.println("Erreur de compilation :");
                process.getErrorStream().transferTo(System.err);
            } else {
                System.out.println("Compilation réussie.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la compilation : " + e.getMessage());
        }
    }

    @Override
    public void executeCode(String code, int id) {
        try {
            // Compiler le code d'abord
            this.compileCode(code);
            
            // Vérifier que la compilation a bien fonctionné 
            if (tempClassDir == null || !Files.exists(tempClassDir.resolve("Main.class"))) {
                System.err.println("Erreur: Le fichier compilé Main.class n'a pas été trouvé");
                return;
            }

            // Créer des fichiers temporaires pour les sorties
            Path outputFile = Files.createTempFile("output", ".txt");
            Path outputFile2 = Files.createTempFile("output2", ".txt");
            
            long seed = System.currentTimeMillis();

            // Créer et exécuter le script principal (utilisant la classe compilée)
            Path shellScript = Files.createTempFile("execute_", ".sh");
            String scriptContent = "#!/bin/bash\n" +
                                  "python3 src/main/resources/randomGeneration.py " + seed + " " + id + " | java -cp " + tempClassDir.toAbsolutePath().toString() + " Main > " + outputFile.toAbsolutePath() + " 2>&1";
            Files.writeString(shellScript, scriptContent);

            // Créer et exécuter le script pour la correction Python
            Path shellScript2 = Files.createTempFile("execute2", ".sh");
            String scriptContent2 = "#!/bin/bash\n" +
                                   "python3 src/main/resources/randomGeneration.py " + seed + " " + id + " | python3 src/main/resources/exercice.py " + id + " > " + outputFile2.toAbsolutePath() + " 2>&1";
            Files.writeString(shellScript2, scriptContent2);
            
            // Rendre les scripts exécutables
            shellScript2.toFile().setExecutable(true);  
            shellScript.toFile().setExecutable(true);

            // Exécuter les deux scripts
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
            } else {
                int exitCode = executeProcess.exitValue();
                System.out.println("Programme terminé avec le code de sortie: " + exitCode);

                // Lire et comparer les sorties
                String output = Files.readString(outputFile);
                String output2 = Files.readString(outputFile2);
                if(output.equals(output2)) {
                    System.out.println("Le code est correct");
                } else {
                    System.out.println("Le code est incorrect");
                    System.out.println("Reçu : " + output);
                    System.out.println("Attendu : " + output2);
                }
            }

            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(shellScript);
                Files.deleteIfExists(shellScript2);
                Files.deleteIfExists(outputFile);
                Files.deleteIfExists(outputFile2);
                // Supprimer le répertoire temporaire et son contenu
                if (tempClassDir != null) {
                    Files.walk(tempClassDir)
                         .sorted(java.util.Comparator.reverseOrder())
                         .map(Path::toFile)
                         .forEach(file -> file.delete());
                }
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
