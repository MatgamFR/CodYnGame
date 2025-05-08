import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

        ComboBox<String> languageSelector = new ComboBox<>();
        languageSelector.getItems().addAll("Python", "Java");
        languageSelector.setValue("Python"); // Sélection par défaut
        languageSelector.setStyle("-fx-background-color:rgb(236, 227, 227); -fx-text-fill: white;");   

        String consigne = setDatabase(codeArea);

        // Add a action button with a lamdbda function
        runButton.setOnAction(event -> {
            String code = codeArea.getText();
            String language = languageSelector.getValue();
            
            if (language.equals("Java")) {
                JavaCompilerCode compiler = new JavaCompilerCode();
                compiler.compileCode(code); // Compile le code Java
                JavaExecuteCode executor = new JavaExecuteCode();
                executor.executeCode(code); // Exécute le code compilé
            } else {
                IDEExecuteCode executor = LanguageChoice.choice(language);
                executor.executeCode(code); // Exécute le code Python
            }
        });

        languageSelector.setOnAction(event -> {
            String language = languageSelector.getValue();     
            if (language.equals("Python")) {
                codeArea.setText("#" + consigne + "\n\nword = input()\n\nprint(word)");
            } else if (language.equals("Java")) {  
                codeArea.setText(
                    "/* " + consigne + " */\n\n" +
                    "import java.util.Scanner;\n\n" +
                    "public class Main {\n" +
                    "    public static void main(String[] args) {\n" +       
                    "        System.out.println(\"word\");\n" +
                    "    }\n" +
                    "}"
                );
            }
        });
        
        // Créer une HBox pour contenir le bouton
        HBox buttonBox = new HBox(10, languageSelector, runButton);
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

    private String setDatabase(TextArea textArea){
        int choice = 0;
        Connexionbdd dbService = new Connexionbdd();
        dbService.showExolist();
        choice = dbService.choiceExo();
        String Consigne = dbService.showConsigne(choice);
        System.out.println("Consigne: " + Consigne);
        textArea.setText("#"+Consigne+"\n\nword = input()\n\nprint(word)");
        return Consigne;
    }
    

    
    public static void main(String[] args) {
        
        // Launch the JavaFX application
        launch(args);
        
    }
}
