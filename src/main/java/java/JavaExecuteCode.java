import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaExecuteCode extends IDEExecuteCode {

    @Override
    public void executeCode(String code) {
        try {
            Path tempDir = Files.createTempDirectory("codyngame");
            Path tempFile = tempDir.resolve("Main.java");
            Files.writeString(tempFile, code);

            Process compileProcess = Runtime.getRuntime().exec("javac " + tempFile.toAbsolutePath());
            if (compileProcess.waitFor() != 0) {
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

            // Nettoyer les fichiers temporaires
            Files.walk(tempDir).map(Path::toFile).forEach(file -> file.delete());
            tempDir.toFile().delete();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution: " + e.getMessage());
        }
    }

    @Override
    public void compileCode(String code) {
        throw new UnsupportedOperationException("Utilisez JavaCompilerCode pour compiler le code.");
    }
}
