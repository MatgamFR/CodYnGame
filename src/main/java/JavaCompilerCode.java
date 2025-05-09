import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaCompilerCode extends IDEExecuteCode {

    @Override
    public void compileCode(String code) {
        try {
            if (!code.contains("public class Main")) {
                throw new IllegalArgumentException("Le code Java doit contenir une classe publique nommée 'Main'.");
            }

            Path tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("Main.java");
            Files.writeString(tempFile, code);

            Process process = Runtime.getRuntime().exec("javac " + tempFile.toAbsolutePath());
            if (process.waitFor() != 0) {
                process.getErrorStream().transferTo(System.err);
            } else {
                System.out.println("Compilation réussie.");
            }

            // Nettoyer les fichiers temporaires
            Files.walk(tempDir).map(Path::toFile).forEach(file -> file.delete());
            tempDir.toFile().delete();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la compilation: " + e.getMessage());
        }
    }

    @Override
    public void executeCode(String code) {
        throw new UnsupportedOperationException("Utilisez JavaExecuteCode pour exécuter le code compilé.");
    }
}
