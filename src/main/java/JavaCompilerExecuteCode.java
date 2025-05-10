import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaCompilerExecuteCode extends IDEExecuteCode {

    @Override
    public void compileCode(String code) {
        Path tempDir = null;
        try {
            if (!code.contains("public class Main")) {
                throw new IllegalArgumentException("Le code Java doit contenir une classe publique nommée 'Main'.");
            }

            tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("Main.java");
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
        } finally {
            // Nettoyer les fichiers temporaires
            if (tempDir != null) {
                try {
                    Files.walk(tempDir).map(Path::toFile).forEach(file -> file.delete());
                    tempDir.toFile().delete();
                } catch (IOException e) {
                    System.err.println("Erreur lors du nettoyage des fichiers temporaires : " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void executeCode(String code) {
        try{
            Path tempDir = null;
            tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("Main.java");
            Files.writeString(tempFile, code);

            // Créer un fichier temporaire pour la sortie
            Path outputFile = Files.createTempFile("output", ".txt");
            
            long seed = System.currentTimeMillis();

            Path shellScript = Files.createTempFile("execute_", ".sh");
            String scriptContent = "#!/bin/bash\n" +
                                    "python3 src/main/resources/randomGeneration.py " + seed + " 1 | java " + tempFile.toAbsolutePath() + " > " + outputFile.toAbsolutePath() + " 2>&1";
            Files.writeString(shellScript, scriptContent);

            Process executeProcess = Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript.toString()});

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

                // Lire le contenu du fichier de sortie
                String output = Files.readString(outputFile);
                System.out.println(output);
            }

            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(tempFile);
                Files.deleteIfExists(shellScript);
                Files.deleteIfExists(outputFile);
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
