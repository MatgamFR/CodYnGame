import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage){  
        BorderPane root = new BorderPane();
        Color backgroundColor = Color.web("#1E1E1E");
        
        // Add a text area to write code
        TextArea codeArea = new TextArea();
        codeArea.setStyle("-fx-control-inner-background: #1E1E1E; -fx-text-fill:rgb(255, 254, 254); -fx-background-color: #1E1E1E;");
        codeArea.setWrapText(false);
        
        
        // Add inital text
        codeArea.setText("word = input()\n\nprint(word)");
        
        // Organise composants in a HBox (ça sert pas à grand chose au début mais plus tard ça va être utile)
        HBox codeBox = new HBox(codeArea);
        codeBox.setStyle("-fx-background-color: #1E1E1E;");
        HBox.setHgrow(codeArea, javafx.scene.layout.Priority.ALWAYS);

        // Créer un bouton pour exécuter le code
        Button runButton = new Button("Exécuter");
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Add a action button with a lamdbda function
        runButton.setOnAction(event -> {
            executeCode(codeArea.getText());
        });
        
        // Créer une HBox pour contenir le bouton
        HBox buttonBox = new HBox(runButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: #2D2D2D;");

        // Set to the center
        root.setCenter(codeBox);
        
        // Ajouter la boîte de bouton en bas
        root.setBottom(buttonBox);
        
        // Create a scene with a background color
        Scene scene = new Scene(root, 1280, 720, backgroundColor);
        
        // Config the stage
        Image appIcon = new Image(getClass().getResourceAsStream("/icon.png"));
        primaryStage.getIcons().add(appIcon);
        primaryStage.setTitle("JAVADOCodYnGame"); // Set the title of the window
        primaryStage.setScene(scene); // Set the scene to the stage
        primaryStage.show(); // Show the stage
    }
    
    /**
     * Méthode qui exécute le code saisi par l'utilisateur
     * @param code Le code à exécuter
     */
    private void executeCode(String code) {
        try {
            // Créer un fichier temporaire avec extension .py
            Path tempFile = Files.createTempFile("codyngame", ".py");
            
            // Écrire le code dans le fichier temporaire
            Files.writeString(tempFile, code);
            
            // Définir les entrées prédéfinies
            String[] predefinedInputs = {"Valeur1", "Valeur2", "Valeur3"};
            
            // Créer un fichier temporaire pour les entrées
            Path inputFile = Files.createTempFile("inputs", ".txt");

            // Créer un fichier temporaire pour la sortie
            Path outputFile = Files.createTempFile("output", ".txt");
            
            // Écrire toutes les entrées dans le fichier, une par ligne
            Files.writeString(inputFile, String.join("\n", predefinedInputs));        

            // Créer un script shell pour gérer la redirection
            Path shellScript = Files.createTempFile("execute_", ".sh");
            String scriptContent = "#!/bin/bash\n" +
                                   "python3 " + tempFile.toAbsolutePath() + " < " + inputFile.toAbsolutePath() + " > " + outputFile.toAbsolutePath() + " 2>&1";
            Files.writeString(shellScript, scriptContent);
            
            // Rendre le script exécutable
            shellScript.toFile().setExecutable(true);
            
            // Exécuter le script shell (qui gère la redirection car Rutime.exec ne peut pas exécuter directement la commande)
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", shellScript.toString()});
            
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
            } else {
                int exitCode = process.exitValue();
                System.out.println("Programme terminé avec le code de sortie: " + exitCode);

                // Lire le contenu du fichier de sortie
                String output = Files.readString(outputFile);
                System.out.println(output);
            }
            
            // Nettoyer les fichiers temporaires
            try {
                Files.deleteIfExists(tempFile);
                Files.deleteIfExists(inputFile);
                Files.deleteIfExists(shellScript);
                Files.deleteIfExists(outputFile);
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression des fichiers temporaires: " + e.getMessage());
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exécution du code: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        int choice = 0;
        Connexionbdd dbService = new Connexionbdd();
        dbService.showExolist();
        choice = dbService.choiceExo();
        // Launch the JavaFX application
        launch(args);
    }
}
