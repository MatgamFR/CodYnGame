import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PhpCompilerExecute extends IDEExecuteCode {
        /**
     * Méthode qui exécute le code saisi par l'utilisateur
     * @param code Le code à exécuter
     */
    @Override
    public void executeCode(String code, int id) {
        try {
            // Créer un fichier temporaire avec extension .py
            Path tempFile = Files.createTempFile("codyngame", ".php");

            // Écrire le code dans le fichier temporaire
            Files.writeString(tempFile, code);

            boolean valide = true;
            int compt = 0;

            String[] output = {""};
            String[] output2 = {""};

            int exitCode = 1;

            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), String.valueOf(id)});
                byte[] resultat = process.getInputStream().readAllBytes();

                Process process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                process2.getOutputStream().write(resultat);
                process2.getOutputStream().close();

                Process process3 = Runtime.getRuntime().exec(new String[]{"php", tempFile.toAbsolutePath().toString()});
                process3.getOutputStream().write(resultat);
                process3.getOutputStream().close();
                
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
                } 
                else {
                    exitCode = process3.exitValue();

                    if (exitCode != 0) {
                        System.out.println(new String(process3.getErrorStream().readAllBytes()));
                        return;
                    }
                    else {
                        // Lire le contenu du fichier de sortie
                        output = new String(process2.getInputStream().readAllBytes()).split("\n");
                        output2 = new String(process3.getInputStream().readAllBytes()).split("\n");
                    }

                    for (int j = 0; j < output.length; j++) {
                        if (j >= output2.length) {
                            valide = false;
                            compt = j-1;
                            break;
                        }
                        if(!output[j].equals(output2[j])) {
                            valide = false;
                            compt = j;
                            break;
                        }
                    }

                    if(!valide) {
                        break;
                    } 
                }
            }
            
            System.out.println("Programme terminé avec le code de sortie: " + exitCode);
            if(valide) {
                if (output.length < output2.length) {
                    System.out.println("Warning : Le code a produit plus de sorties que prévu.");
                }
                System.out.println("Le code est correct");
            } 
            else {
                System.out.println("Le code est incorrect");
                System.out.println("Reçu : '" + output2[compt] + "' valeur " + (compt+1));
                System.out.println("Attendu : '" + output[compt] + "' valeur " + (compt+1));
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
