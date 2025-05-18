import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.fxmisc.richtext.CodeArea;

public class CcompilerExecuter extends IDEExecuteCode {
    private Path compiledExecutable = null;

    public CcompilerExecuter(CodeArea textArea) {
        super(textArea);
    }

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
                this.printOutput("Timeout de compilation dépassé (10s)");
            }

            if (compileProcess.exitValue() != 0) {
                String errorOutputC = new String(compileProcess.getErrorStream().readAllBytes());
                if (errorOutputC.contains("error:")) {
                    this.printOutput("[ERREUR SYNTAXE]");
                } 
                if (errorOutputC.contains("warning:")) {
                    this.printOutput("[AVERTISSEMENT]");
                }
                
                this.printOutput(errorOutputC);
            }
            else {
                this.printOutput("Compilation C reussie.");
                this.compiledExecutable = tempDir.resolve("exe");  // Sauvegarde le chemin de l'exécutable
                String warningC = new String(compileProcess.getErrorStream().readAllBytes());
                if (warningC.contains("warning")) {
                    this.printOutput("Avertissement de compilation:");
                    this.printOutput(warningC);
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            this.printOutput("Erreur lors de la compilation : " + e.getMessage());
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
            boolean valide = true;
            int compt = 0;

            String[] output = {""};
            String[] output2 = {""};

            int exitCode = 1;

            // Tester avec 10 seeds différentes
            for (int i = 0; i < 10; i++) {
                long seed = System.currentTimeMillis();

                // Exécuter le script shell (qui gère la redirection car Rutime.exec ne peut pas exécuter directement la commande)
                Process process = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/randomGeneration.py", String.valueOf(seed), String.valueOf(id)});
                byte[] resultat = process.getInputStream().readAllBytes();

                Process process2 = Runtime.getRuntime().exec(new String[]{"python3", "src/main/resources/Correction/Exercice" + id +".py" });
                process2.getOutputStream().write(resultat);
                process2.getOutputStream().close();

                Process process3 = Runtime.getRuntime().exec(new String[]{compiledExecutable.toAbsolutePath().toString()});
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

            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(this.compiledExecutable);
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

