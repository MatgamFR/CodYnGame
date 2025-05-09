import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            Process process = Runtime.getRuntime().exec("javac " + tempFile.toAbsolutePath());
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
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("Main.java");
            Files.writeString(tempFile, code);

            Process compileProcess = Runtime.getRuntime().exec("javac " + tempFile.toAbsolutePath());
            if (compileProcess.waitFor() != 0) {
                System.err.println("Erreur de compilation :");
                compileProcess.getErrorStream().transferTo(System.err);
                return;
            }

            Process executeProcess = Runtime.getRuntime().exec("java -cp " + tempDir + " Main");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(executeProcess.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            }
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(executeProcess.getErrorStream()))) {
                errorReader.lines().forEach(System.err::println);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution : " + e.getMessage());
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
}
