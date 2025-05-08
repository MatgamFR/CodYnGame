import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaExecuteCode extends IDEExecuteCode {
    /**
     * Méthode qui exécute le code saisi par l'utilisateur
     * @param code Le code à exécuter
     */
    @Override
    public void executeCode(String code) {
        try {
            // Créer un fichier temporaire avec extension .java
            Path tempFile = Files.createTempFile("codyngame", ".java");

            // Écrire le code dans le fichier temporaire
            Files.writeString(tempFile, code);

            // Définir les entrées prédéfinies
            String[] predefinedInputs = {"Valeur1", "Valeur2", "Valeur3"};

            // Créer un fichier temporaire pour les entrées
            Path inputFile = Files.createTempFile("inputs", ".txt");

            // Créer un fichier temporaire pour la sortie
            Path outputFile = Files.createTempFile("output", ".txt");

            // Écrire toutes les entrées dans le fichier, une par ligne
            Files.writeString(inputFile, String.join("\n", predefinedInputs));

            // Créer un script shell pour gérer la redirection
            Path shellScript = Files.createTempFile("execute_", ".sh");
            String scriptContent = "#!/bin/bash\n" +
                    "javac " + tempFile.toAbsolutePath() + "\n" +  // Utilisation du fichier temporaire créé
                    "java codyngame < " + inputFile.toAbsolutePath() + " > " + outputFile.toAbsolutePath() + " 2>&1";  // Exécution avec les bons fichiers
            Files.writeString(shellScript, scriptContent);

            // Rendre le script exécutable
            shellScript.toFile().setExecutable(true);

            // Exécuter le script shell (qui gère la redirection)
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript.toString()});

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
                System.out.println(output);

                // Lire et afficher les erreurs éventuelles
                InputStream errorStream = process.getErrorStream();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
            }

            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(tempFile);
                Files.deleteIfExists(inputFile);
                Files.deleteIfExists(shellScript);
                Files.deleteIfExists(outputFile);
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
