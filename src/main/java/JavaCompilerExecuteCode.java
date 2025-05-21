import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.TextArea;

public class JavaCompilerExecuteCode extends IDEExecuteCode {
    private Path tempClassDir; // Attribut pour stocker le chemin du répertoire des classes compilées

    public JavaCompilerExecuteCode(TextArea textArea) {
        super(textArea);
    }

    @Override
    public void compileCode(String code, int id) {
        try {
            if (!code.contains("public class Codyngame")) {
                throw new IllegalArgumentException("Le code Java doit contenir une classe publique nommée 'Codyngame'.");
            }

            if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){     
                // Créer un répertoire temporaire qui sera conservé jusqu'à l'exécution
                tempClassDir = Files.createTempDirectory("codyngame_classes");
                Path tempFile = tempClassDir.resolve("Codyngame.java");
                Files.writeString(tempFile, code);

                Process process = Runtime.getRuntime().exec(new String[]{"javac", tempFile.toAbsolutePath().toString()});
                if (process.waitFor() != 0) {
                    System.err.println("Erreur de compilation :");
                    process.getErrorStream().transferTo(System.err);
                } else {
                    this.printOutput("Compilation réussie.");
                }
            } 
            else {
                tempClassDir = Files.createTempDirectory("codyngame_classes");
                Path tempFile = tempClassDir.resolve("Codyngame.java");
                Files.writeString(tempFile, code);
                Path tempFile2 = tempClassDir.resolve("Exercice" + id + ".java");
                File tempFile3 = new File("src/main/resources/Correction/Exercice" + id +".java");
                Files.writeString(tempFile2, Files.readString(tempFile3.toPath()));

                Process process = Runtime.getRuntime().exec(new String[]{"javac", tempFile2.toAbsolutePath().toString(), tempFile.toAbsolutePath().toString()});
                if (process.waitFor() != 0) {
                    System.err.println("Erreur de compilation :");
                    process.getErrorStream().transferTo(System.err);
                } else {
                    this.printOutput("Compilation réussie.");
                }
                
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
            this.compileCode(code, id);
            
            // Vérifier que la compilation a bien fonctionné 
            if (tempClassDir == null || !Files.exists(tempClassDir.resolve("Codyngame.class"))) {
                System.err.println("Erreur: Le fichier compilé Codyngame.class n'a pas été trouvé");
                return;
            }
            
            boolean valide = true;

            String[] output = {""};
            String resultat2 = "";

            int exitCode = 1;

            Process process2;
            Process process3;

            // Tester avec 10 seeds différentes
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                // Étape 1: Générer les données d'entrée aléatoires
                Process process = Runtime.getRuntime().exec(new String[]{
                    "python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), String.valueOf(id)
                });
                byte[] resultat = process.getInputStream().readAllBytes();
                process.waitFor();


                if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){

                    process3 = Runtime.getRuntime().exec(new String[]{"java", "-cp", tempClassDir.toAbsolutePath().toString(), "Codyngame"});
                    process3.getOutputStream().write(resultat);
                    process3.getOutputStream().close();
                    resultat2 = new String(process3.getInputStream().readAllBytes());
                    String result = resultat2.replace("\n", "\\n");
                    
                    // Étape 2: Exécuter le code de correction
                    process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                    process2.getOutputStream().write((result+"\n").getBytes());
                    process2.getOutputStream().write(resultat);
                    process2.getOutputStream().close();
                }
                else{
                    process2 = Runtime.getRuntime().exec(new String[] {"ls"});
                    process3 = Runtime.getRuntime().exec(new String[]{"java", "-cp", tempClassDir.toAbsolutePath().toString(), "Exercice" + id});
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
                        // Lire le contenu du fichier de sortie
                        if(Connexionbdd.getTypeExo(id).equals("STDIN/STDOUT")){
                            output = new String(process2.getInputStream().readAllBytes()).split("\n");
                        }
                        else{
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

        } 
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
        }
    }
}
