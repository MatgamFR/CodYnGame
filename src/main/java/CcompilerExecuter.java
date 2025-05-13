import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CcompilerExecuter extends IDEExecuteCode {
    private Path compiledExecutable = null;

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
                System.out.println("Timeout de compilation dépassé (10s)");
            }

            if (compileProcess.exitValue() != 0) {
                String errorOutputC = new String(compileProcess.getErrorStream().readAllBytes());
                if (errorOutputC.contains("error:")) {
                    System.out.println("[ERREUR SYNTAXE]");
                } 
                if (errorOutputC.contains("warning:")) {
                    System.out.println("[AVERTISSEMENT]");
                }
                
                System.out.println(errorOutputC);
            }
            else {
                System.out.println("Compilation C reussie.");
                this.compiledExecutable = tempDir.resolve("exe");  // Sauvegarde le chemin de l'exécutable
                String warningC = new String(compileProcess.getErrorStream().readAllBytes());
                if (warningC.contains("warning")) {
                    System.out.println("Avertissement de compilation:");
                    System.out.println(warningC);
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la compilation : " + e.getMessage());
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

            String output = "";
            String output2 = "";

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
                    System.out.println("Le programme a dépassé la durée d'exécution maximale de 15 secondes. Arrêt forcé.");
                    process3.destroy();
                    process3.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                    if (process3.isAlive()) {
                        process3.destroyForcibly();
                    }
                    System.out.println("Le programme a probablement essayé d'utiliser plus d'entrées que prévu ou une boucle infinie.");
                    valide = false;
                    break;
                } else {
                    exitCode = process3.exitValue();

                    // Vérifier les erreurs de segmentation
                    if (exitCode == 1) {
                        System.out.println("ERREUR DE SEGMENTATION");
                        valide = false;
                        break;
                    }

                    // Lire et comparer les sorties
                    output = new String(process2.getInputStream().readAllBytes());
                    output2 = new String(process3.getInputStream().readAllBytes());
                    if (!output.equals(output2)) {
                        valide = false;
                        break;
                    }
                }
            }
            
            System.out.println("Programme terminé avec le code de sortie: " + exitCode);
            if (valide) {
                System.out.println("Le code est correct");
            } else {
                System.out.println("Le code est incorrect");
                System.out.println("Reçu : " + output2);
                System.out.println("Attendu : " + output);
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

