import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.TextArea;

public class PhpCompilerExecute extends IDEExecuteCode {
        /**
     * Méthode qui exécute le code saisi par l'utilisateur
     * @param code Le code à exécuter
     */
    public PhpCompilerExecute(TextArea textArea) {
        super(textArea); // Appel du constructeur de la classe parente
    }
    @Override
    public void executeCode(String code, int id) {
        try {
            // Créer un fichier temporaire avec extension .php
            File tempFile = new File("src/main/resources/Correction/codyngame.php");
            
            // Écrire le code dans le fichier temporaire
            FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(code);
            fileWriter.close();

            String[] output = {""};
            String resultat2 = "";

            boolean valide = true;

            int exitCode = 1;

            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();
                
                // Exécuter le script shell (qui gère la redirection car Rutime.exec ne peut pas exécuter directement la commande)
                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), String.valueOf(id)});
                byte[] resultat = process.getInputStream().readAllBytes();

                Process process2;
                Process process3;

                if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                    process3 = Runtime.getRuntime().exec(new String[]{"php", tempFile.toPath().toString()});
                    process3.getOutputStream().write(resultat);
                    process3.getOutputStream().close();
                    resultat2 = new String(process3.getInputStream().readAllBytes());
                    String result = resultat2.replace("\n", "\\n");

                    process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                    process2.getOutputStream().write((result+"\n").getBytes());
                    process2.getOutputStream().write(resultat);
                    process2.getOutputStream().close();
                }
                else{
                    process2 = Runtime.getRuntime().exec(new String[]{"php", tempFile.toPath().toString()});

                    process3 = Runtime.getRuntime().exec(new String[]{"php", "src/main/resources/Correction/Exercice" + id +".php" });
                    process3.getOutputStream().write(resultat);
                    process3.getOutputStream().close();     
                }

                // Définir un timeout global de 15 secondes
                boolean completed = process3.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
                
                if (!completed) {
                    this.printOutput("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                    process3.destroy();
                    process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                    if (process3.isAlive()) {
                        process3.destroyForcibly();
                    }
                    this.printOutput("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
                } 
                else {
                    exitCode = process3.exitValue();
                    if (exitCode != 0) {
                        this.printOutput(new String(process3.getErrorStream().readAllBytes()));
                        return;
                    }
                    else {
                        if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                            // Lire le contenu du fichier de sortie
                            output = new String(process2.getInputStream().readAllBytes()).split("\n");
                        }
                        else{
                            // Lire le contenu du fichier de sortie
                            output = new String(process3.getInputStream().readAllBytes()).split("\n");
                        }
                        if(output[0].equals("0")){
                            valide = false;
                            break;
                        }
                    }
                }
            }
            
            this.printOutput("Programme terminé avec le code de sortie: " + exitCode);
            if(valide) {
                this.printOutput("Le code est correct");
            } 
            else {
                this.printOutput("Le code est incorrect");
                this.printOutput("Reçu : '" + output[1] + "' valeur " + output[3]);
                this.printOutput("Attendu : '" + output[2] + "' valeur " + output[3]);
            }
            
            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression des fichiers temporaires: " + e.getMessage());
            }
                
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
        }
    }

    public boolean verification(String code) {
        try {
            // Créer un fichier temporaire avec extension .py
            Path tempFile = Files.createTempFile("codyngame", ".py");
            
            // Écrire le code dans le fichier temporaire
            Files.writeString(tempFile, code);

            long seed = System.currentTimeMillis();

            Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), "1"});
            byte[] resultat = process.getInputStream().readAllBytes();

            Process process2 = Runtime.getRuntime().exec(new String[]{"python3", tempFile.toAbsolutePath().toString()});
            process2.getOutputStream().write(resultat);
            process2.getOutputStream().close();

            boolean completed = process2.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
                
            if (!completed) {
                this.printOutput("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                process2.destroy();
                process2.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                if (process2.isAlive()) {
                    process2.destroyForcibly();
                }
                this.printOutput("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
                return false;
            }

            int exitCode = process2.exitValue();

            if(exitCode != 0) {
                this.printOutput(new String(process2.getErrorStream().readAllBytes()));
                return false;
            }
            else {
                this.printOutput(new String(process2.getInputStream().readAllBytes()));
                return true;
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void compileCode(String code) {

    }

}
