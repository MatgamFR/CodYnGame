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
                this.printOutput("Compilation réussie.");
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
            
            boolean valide = true;
            int compt = 0;

            String[] output = {""};
            String[] output2 = {""};

            int exitCode = 1;

            // Tester avec 10 seeds différentes
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                // Étape 1: Générer les données d'entrée aléatoires
                Process process = Runtime.getRuntime().exec(new String[]{
                    "python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), String.valueOf(id)
                });
                byte[] resultat = process.getInputStream().readAllBytes();
                process.waitFor();
                
                // Étape 2: Exécuter le code de correction
                Process process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });

                process2.getOutputStream().write(resultat);
                process2.getOutputStream().close();
                
                // Étape 3: Exécuter le code Java compilé de l'utilisateur
                Process process3 = Runtime.getRuntime().exec(new String[]{
                    "java", "-cp", tempClassDir.toAbsolutePath().toString(), "Main"
                });
                process3.getOutputStream().write(resultat);
                process3.getOutputStream().close();

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
                    valide = false;
                    break;
                } 
                else {
                    exitCode = process3.exitValue();

                    if (exitCode != 0) {
                        this.printOutput(new String(process3.getErrorStream().readAllBytes()));
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
            
            this.printOutput("Programme terminé avec le code de sortie: " + exitCode);
            if(valide) {
                if (output.length < output2.length) {
                    this.printOutput("Warning : Le code a produit plus de sorties que prévu.");
                }
                this.printOutput("Le code est correct");
            } 
            else {
                this.printOutput("Le code est incorrect");
                this.printOutput("Reçu : '" + output2[compt] + "' valeur " + (compt+1));
                this.printOutput("Attendu : '" + output[compt] + "' valeur " + (compt+1));
            }

        } 
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
        }
    }
}
