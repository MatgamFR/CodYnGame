import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonExecuteCode extends IDEExecuteCode {
        /**
     * Méthode qui exécute le code saisi par l'utilisateur
     * @param code Le code à exécuter
     */
    @Override
    public void executeCode(String code, int id) {
        try {
            // Créer un fichier temporaire avec extension .py
            Path tempFile = Files.createTempFile("codyngame", ".py");
            
            // Écrire le code dans le fichier temporaire
            Files.writeString(tempFile, code);

            String output = "";
            String output2 = "";

            boolean valide = true;

            int exitCode = 1;

            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();
                
                // Exécuter le script shell (qui gère la redirection car Rutime.exec ne peut pas exécuter directement la commande)
                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), String.valueOf(id)});
                byte[] resultat = process.getInputStream().readAllBytes();

                Process process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/exercice.py", String.valueOf(id)});
                process2.getOutputStream().write(resultat);
                process2.getOutputStream().close();

                Process process3 = Runtime.getRuntime().exec(new String[]{"python3", tempFile.toAbsolutePath().toString()});
                process3.getOutputStream().write(resultat);
                process3.getOutputStream().close();
                
                // Définir un timeout global de 15 secondes
                boolean completed = process3.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
                
                if (!completed) {
                    System.out.println("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                    process3.destroy();
                    process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                    if (process3.isAlive()) {
                        process3.destroyForcibly();
                    }
                    System.out.println("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
                } else {
                    exitCode = process3.exitValue();

                    // Lire le contenu du fichier de sortie
                    output = new String(process2.getInputStream().readAllBytes());
                    output2 = new String(process3.getInputStream().readAllBytes());
                    if(!output.equals(output2)) {
                        valide = false;
                        break;
                    } 
                }
            }
            
            System.out.println("Programme terminé avec le code de sortie: " + exitCode);
            if(valide) {
                System.out.println("Le code est correct");
            } else {
                System.out.println("Le code est incorrect");
                System.out.println("Reçu : " + output2);
                System.out.println("Attendu : " + output);
            }
            
            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(tempFile);
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
