import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaScriptCompilerExecute extends IDEExecuteCode {

    @Override
    public void executeCode(String code, int id) {
        try {
            // Créer un fichier temporaire avec extension .py
            Path tempFile = Files.createTempFile("codyngame", ".js");
            
            // Écrire le code dans le fichier temporaire
            Files.writeString(tempFile, code);

            // Créer un fichier temporaire pour la sortie
            Path outputFile = Files.createTempFile("output", ".txt");

            // Créer un fichier temporaire pour la sortie 2
            Path outputFile2 = Files.createTempFile("output2", ".txt");
            
            long seed = System.currentTimeMillis();

            // Créer un script shell pour gérer la redirection

            Path shellScript = Files.createTempFile("execute", ".sh");
            String scriptContent = "#!/bin/bash\n" +
                                   "python3 src/main/resources/randomGeneration.py " + seed + " " + id + " | node " + tempFile.toAbsolutePath() + " > " + outputFile.toAbsolutePath() + " 2>&1";
            Files.writeString(shellScript, scriptContent);

            Path shellScript2 = Files.createTempFile("execute2", ".sh");
            String scriptContent2 = "#!/bin/bash\n" +
                                   "python3 src/main/resources/randomGeneration.py " + seed + " " + id + " | python3 src/main/resources/exercice.py " + id + " > " + outputFile2.toAbsolutePath() + " 2>&1";
            Files.writeString(shellScript2, scriptContent2);
            
            // Rendre le script exécutable
            shellScript2.toFile().setExecutable(true);  
            shellScript.toFile().setExecutable(true);
            
            // Exécuter le script shell (qui gère la redirection car Rutime.exec ne peut pas exécuter directement la commande)
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript.toString()});
            Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript2.toString()}).waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
            
            // Définir un timeout global de 15 secondes
            boolean completed = process.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
            
            if (!completed) {
                System.out.println("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                process.destroy();
                process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
                System.out.println("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
            } else {
                int exitCode = process.exitValue();
                System.out.println("Programme terminé avec le code de sortie: " + exitCode);

                // Lire le contenu du fichier de sortie
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
                Files.deleteIfExists(tempFile);
                Files.deleteIfExists(shellScript);
                Files.deleteIfExists(shellScript2); // Ajouté
                Files.deleteIfExists(outputFile);
                Files.deleteIfExists(outputFile2); // Ajouté
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression des fichiers temporaires: " + e.getMessage());
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
        }
    }

    @Override
    public void compileCode(String code) {
    
    }
}
