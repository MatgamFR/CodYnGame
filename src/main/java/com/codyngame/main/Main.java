package com.codyngame.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import com.codyngame.compiler.IDEExecuteCode;
import com.codyngame.compiler.LanguageChoice;
import com.codyngame.syntax.SyntaxicalColor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Main class for the Codyngame application.
 * This JavaFX application provides a coding exercise platform with features like:
 * - Exercise listing and filtering
 * - Code editor with syntax highlighting
 * - Code execution for multiple programming languages
 * - Exercise management (adding new exercises)
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 *
 */
public class Main extends Application {
ListView<HBox> exerciseList;
CheckBox filterPythonCheckBox;
CheckBox filterCCheckBox;
CheckBox filterJavaCheckBox;
CheckBox filterJSCheckBox;
CheckBox filterPHPCheckBox;

/**
 * Handles automatic indentation and bracket/parenthesis/quote completion in the code editor.
 * @param codeArea The CodeArea where the text is being edited
 * @param event The KeyEvent that triggered this method
 */
public void tabulationNumber(CodeArea codeArea, KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
        int caretPosition = codeArea.getCaretPosition();
        String text = codeArea.getText();
        String tab = "";
        int start = text.lastIndexOf("\n", caretPosition - 2) + 1;

        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\t' || c == ' ') {
                tab += c;
            } else {
                break;
            }
        }

        codeArea.insertText(caretPosition, tab);
        int verif = text.lastIndexOf("\n", caretPosition - 1) - 1;

        if (text.lastIndexOf("{", caretPosition - 1) == verif && text.lastIndexOf("}", caretPosition + 1) == verif + 2) {
            codeArea.deleteText(verif + 1, verif + 1);
            codeArea.insertText(caretPosition, "\t");
            codeArea.insertText(caretPosition + 1 + tab.length(), "\n" + tab);
            codeArea.moveTo(caretPosition + 1 + tab.length());
        } else if (text.lastIndexOf("{", caretPosition - 1) == verif) {
            codeArea.insertText(caretPosition, "\t");
            codeArea.moveTo(caretPosition + 1 + tab.length());
        }

        if (text.lastIndexOf(":", caretPosition - 1) == verif) {
            codeArea.insertText(caretPosition, "\t");
            codeArea.moveTo(caretPosition + 1 + tab.length());
        }
    }

    if (event.getText().equals("{")) {
        int position = codeArea.getCaretPosition();
        codeArea.insertText(position, "}");
        codeArea.moveTo(position);
    }

    if (event.getText().equals("(")) {
        int position = codeArea.getCaretPosition();
        codeArea.insertText(position, ")");
        codeArea.moveTo(position);
    }

    if (event.getText().equals("\"")) {
        int position = codeArea.getCaretPosition();
        codeArea.insertText(position, "\"");
        codeArea.moveTo(position);
    }
}

/**
 * Sets up the database connection by reading configuration from configue.txt
 */
public void setupBDD(){
    try {
        // Read configuration values from configue.txt
        Path configPath = Path.of("configue.txt");
        BufferedReader reader = new BufferedReader(new FileReader(configPath.toFile()));

        String dbUrl = null, dbUser = null, dbPassword = null;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("db_url=")) {
                dbUrl = line.split("=")[1];
            } else if (line.startsWith("db_user=")) {
                dbUser = line.split("=")[1];
            } else if (line.startsWith("db_password=")) {
                dbPassword = line.split("=")[1];
            }
        }
        reader.close();

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new IllegalArgumentException("Configuration invalide dans configue.txt");
        }

        // Initialize Connexionbdd with the read values
        new Connexionbdd(dbUrl, dbUser, dbPassword);
    } catch (IOException e) {
        System.err.println("Erreur lors de la lecture de la configuration :  " + e.getMessage());
        return;
    }
}

/**
 * Configures the search button functionality for filtering exercises by language
 * @param searchButton The search button to configure
 * @param overlay The overlay VBox containing the filter options
 * @param rootPane The root StackPane of the application
 */
public void setupSearchButton(Button searchButton, VBox overlay, StackPane rootPane) {
    searchButton.setOnAction(event -> {
        rootPane.getChildren().remove(overlay);

        // Get selected languages
        List<String> selectedLanguages = new ArrayList<>();
        if (filterPythonCheckBox.isSelected()) {
            selectedLanguages.add("Python");
        }
        if (filterJavaCheckBox.isSelected()) {
            selectedLanguages.add("Java");
        }
        if (filterCCheckBox.isSelected()) {
            selectedLanguages.add("C");
        }
        if (filterJSCheckBox.isSelected()) {
            selectedLanguages.add("JavaScript");
        }
        if (filterPHPCheckBox.isSelected()) {
            selectedLanguages.add("PHP");
        }
        filterPythonCheckBox.setSelected(false);
        filterJavaCheckBox.setSelected(false);
        filterCCheckBox.setSelected(false);
        filterJSCheckBox.setSelected(false);
        filterPHPCheckBox.setSelected(false);

        // Filter exercises
        List<Integer> filteredExerciseIds = Connexionbdd.getExercisesByLanguages(selectedLanguages);
        if (filteredExerciseIds.isEmpty()) {
            exerciseList.getItems().clear();
            int maxExobis = Connexionbdd.maxexo();
            for (int i = 1; i <= maxExobis; i++) {
                String titre = Connexionbdd.getExerciceTitle(i); // Get exercise title
                String difficulty = Connexionbdd.getExerciceDifficulty(i); // Get exercise difficulty
                int attempts = Connexionbdd.getExerciseAttempts(i); // Get number of attempts
                int successfulTries = Connexionbdd.getSuccessfulTries(i); // Get number of successful attempts
                String typeExo = Connexionbdd.getTypeExo(i); // Get exercise type
    
                Label exerciseNumber = new Label("Exercice " + i);
                exerciseNumber.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label exerciseTitle = new Label(titre);
                exerciseTitle.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label difficultyLabel = new Label("Difficulté : " + difficulty);
                difficultyLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                statsLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label typeLabel = new Label("Mode: " + typeExo);
                typeLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
    
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS); // Push exercise type to the right
    
                HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, difficultyLabel, statsLabel, spacer, typeLabel);
                exerciseItem.setSpacing(10);
                exerciseItem.setStyle(
                    "-fx-background-color: rgba(30, 30, 30, 0.9); " +
                    "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
                    "-fx-padding: 10px; " 
                );
                exerciseList.getItems().add(exerciseItem);
    
            }
    
            }
            else {
            exerciseList.getItems().clear();
            for (int id : filteredExerciseIds) {
                String titre = Connexionbdd.getExerciceTitle(id); // Get exercise title
                String difficulty = Connexionbdd.getExerciceDifficulty(id); // Get exercise difficulty
                int attempts = Connexionbdd.getExerciseAttempts(id); // Get number of attempts
                int successfulTries = Connexionbdd.getSuccessfulTries(id); // Get number of successful attempts
                String typeExo = Connexionbdd.getTypeExo(id); // Get exercise type
    
                Label exerciseNumber = new Label("Exercice " + id);
                exerciseNumber.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label exerciseTitle = new Label(titre);
                exerciseTitle.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label difficultyLabel = new Label("Difficulté : " + difficulty);
                difficultyLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                statsLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label typeLabel = new Label("Mode: " + typeExo);
                typeLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
    
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS); // Push exercise type to the right
    
                HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, difficultyLabel, statsLabel, spacer, typeLabel);
                exerciseItem.setSpacing(10);
                exerciseItem.setStyle(
                    "-fx-background-color: rgba(30, 30, 30, 0.9); " +
                    "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
                    "-fx-padding: 10px; " 
                );
                exerciseList.getItems().add(exerciseItem);
    
            }
    
            }
    });
    };


/**
 * Creates and returns the main scene with the exercise list
 * @return VBox containing the main scene components
 */
public VBox mainScene() {

    // Load custom fonts
    String pixelGamePath = getClass().getResource("/RessourceFonts/Pixel.otf").toExternalForm();
    
    // Load and register fonts
    Font.loadFont(pixelGamePath, 32); // Load Pixel Game font


    // Page title
    Label titleLabel = new Label("Liste d'exercices");
    titleLabel.setStyle("-fx-font-size: 70px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

    // Description
    Label descriptionLabel = new Label("Veuillez choisir un exercice. Bon codage!");
    descriptionLabel.setStyle("-fx-font-size: 39px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    VBox titleBox = new VBox(0, titleLabel, descriptionLabel); // Vertical spacing 0
    titleBox.setAlignment(Pos.CENTER); // Center labels

    
    // Create exercise list
    ListView<HBox> exerciseList = new ListView<>();
    exerciseList.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc);");
    
    // Set fixed size for exerciseList
    exerciseList.setPrefWidth(1300); // Preferred width
    exerciseList.setMinWidth(1300);  // Minimum width
    exerciseList.setMaxWidth(1300);  // Maximum width

    
    for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
        String titre = Connexionbdd.getExerciceTitle(i); // Get exercise title
        String difficulty = Connexionbdd.getExerciceDifficulty(i); // Get exercise difficulty
        int attempts = Connexionbdd.getExerciseAttempts(i); // Get number of attempts
        int successfulTries = Connexionbdd.getSuccessfulTries(i); // Get number of successful attempts
        String typeExo = Connexionbdd.getTypeExo(i); // Get exercise type

        Label exerciseNumber = new Label("Exercice " + i);
        exerciseNumber.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
        Label exerciseTitle = new Label(titre);
        exerciseTitle.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
        Label difficultyLabel = new Label("Difficulté : " + difficulty);
        difficultyLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
        Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
        statsLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
        Label typeLabel = new Label("Mode: " + typeExo);
        typeLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Push exercise type to the right

        HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, difficultyLabel, statsLabel, spacer, typeLabel);
        exerciseItem.setSpacing(10);
        exerciseItem.setStyle(
            "-fx-background-color: rgba(30, 30, 30, 0.9); " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
            "-fx-padding: 10px; " 
        );
        exerciseList.getItems().add(exerciseItem);

    }

    this.exerciseList = exerciseList;

    // Organize components in a VBox
    VBox contentBox = new VBox(10, titleBox, exerciseList);
    contentBox.setAlignment(Pos.CENTER);

    return contentBox;

    
}

/**
 * Main entry point for the JavaFX application
 * @param primaryStage The primary stage for this application
 */
@Override
public void start(Stage primaryStage) {
    setupBDD();
    filterPythonCheckBox = new CheckBox("Python");
    filterPythonCheckBox.setStyle("-fx-font-size: 25px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    filterJavaCheckBox = new CheckBox("Java");
    filterJavaCheckBox.setStyle("-fx-font-size: 25px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    filterCCheckBox = new CheckBox("C");
    filterCCheckBox.setStyle("-fx-font-size: 25px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    filterJSCheckBox = new CheckBox("JavaScript");
    filterJSCheckBox.setStyle("-fx-font-size: 25px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    filterPHPCheckBox = new CheckBox("PHP");
    filterPHPCheckBox.setStyle("-fx-font-size: 25px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    // Create home page scene
    BorderPane homePageRoot = new BorderPane();
    // Load custom fonts
    String PixelPath = getClass().getResource("/RessourceFonts/Pixel.otf").toExternalForm();
    // Load and register fonts
    Font.loadFont(PixelPath, 32); // Load Pixel Game font

    Label welcomeLabel = new Label("Le codyngame \n         de la \njavadocance");
    welcomeLabel.setStyle("-fx-font-size: 93px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    Label descriptionLabel = new Label("Tentez de braver \n   nos farouches \n exercices si vous \n\t  l'osez!");
    descriptionLabel.setStyle("-fx-font-size: 35px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    // Get absolute path of project base directory
    String basePath = new File("").getAbsolutePath(); // Project absolute path
    String imagePath = basePath + "/src/main/resources/RessourceImage/play.png"; // Relative path from project
    String backgroundPath = basePath + "/src/main/resources/RessourceImage/background.png"; // Relative path from project
    String addPath = basePath + "/src/main/resources/RessourceImage/add.png"; // Relative path from project
    String filterPath = basePath + "/src/main/resources/RessourceImage/filter.png"; // Relative path from project
    String homePath = basePath + "/src/main/resources/RessourceImage/home.png"; // Relative path from project
    String searchPath = basePath + "/src/main/resources/RessourceImage/search.png"; // Relative path from project
    String fermerPath = basePath + "/src/main/resources/RessourceImage/fermer.png"; // Relative path from project
    String returnPath = basePath + "/src/main/resources/RessourceImage/return.png"; // Relative path from project
    String executePath = basePath + "/src/main/resources/RessourceImage/execute.png"; // Relative path from project
    String savePath = basePath + "/src/main/resources/RessourceImage/save.png"; // Relative path from project    
    String nextPath = basePath + "/src/main/resources/RessourceImage/next.png"; // Relative path from project

    // Load image using absolute path
    File imageFile = new File(imagePath);
    File backgroundFile = new File(backgroundPath);
    File addFile = new File(addPath);
    File filterFile = new File(filterPath);
    File homeFile = new File(homePath);
    File searchFile = new File(searchPath);
    File fermerFile = new File(fermerPath);
    File returnFile = new File(returnPath);
    File executeFile = new File(executePath);
    File saveFile = new File(savePath);
    File nextFile = new File(nextPath);

    if (!imageFile.exists()) {
        System.err.println("Image introuvable : " + imageFile.getAbsolutePath());}
    else if(!backgroundFile.exists()){
        System.err.println("Image introuvable : " + backgroundFile.getAbsolutePath());
    } 
    
    else if(!addFile.exists()){
        System.err.println("Image introuvable : " + addFile.getAbsolutePath());
    }
    else if(!filterFile.exists()){
        System.err.println("Image introuvable : " + filterFile.getAbsolutePath());
    }
    else if(!homeFile.exists()){
        System.err.println("Image introuvable : " + homeFile.getAbsolutePath());
    }
    else if(!searchFile.exists()){
        System.err.println("Image introuvable : " + searchFile.getAbsolutePath());
    }
    else if(!fermerFile.exists()){
        System.err.println("Image introuvable : " + fermerFile.getAbsolutePath());
    }
    else if(!returnFile.exists()){
        System.err.println("Image introuvable : " + returnFile.getAbsolutePath());
    }
    else if(!executeFile.exists()){
        System.err.println("Image introuvable : " + executeFile.getAbsolutePath());
    }
    else if(!saveFile.exists()){
        System.err.println("Image introuvable : " + saveFile.getAbsolutePath());
    }
    else if(!nextFile.exists()){
        System.err.println("Image introuvable : " + nextFile.getAbsolutePath());
    }
    else {
    // Apply background image to main container
    homePageRoot.setStyle(
    "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
    "-fx-background-size: cover; " + // Adjust image to cover container
    "-fx-background-position: center center; " + // Center image
    "-fx-background-repeat: no-repeat;" // Don't repeat image
    );


    Image image = new Image(imageFile.toURI().toString());
    Image searchImage = new Image(searchFile.toURI().toString());
    Image fermerImage = new Image(fermerFile.toURI().toString());
    ImageView imageView = new ImageView(image);
    ImageView searchImageView = new ImageView(searchImage);
    ImageView fermerImageView = new ImageView(fermerImage);

    
    // Configure ImageView
    imageView.setFitWidth(240); // Image width
    imageView.setFitHeight(240); // Image height
    imageView.setPreserveRatio(true); // Preserve proportions

    // Create button with image
    Button goToExercisesButton = new Button();
    goToExercisesButton.setGraphic(imageView); // Add image to button
    goToExercisesButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
    // Add other elements to center of BorderPane
    VBox centerContent = new VBox(20, welcomeLabel, descriptionLabel, goToExercisesButton);
    centerContent.setAlignment(Pos.CENTER);
    homePageRoot.setCenter(centerContent);
    Scene homePageScene = new Scene(homePageRoot, 1600, 900);
    primaryStage.setTitle("Codyngame");
    primaryStage.setScene(homePageScene);
    primaryStage.show();

    // Main window (exercise list)
    BorderPane mainRoot = new BorderPane();
    mainRoot.setStyle(
        "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
        "-fx-background-size: cover; " + // Adjust image to cover container
        "-fx-background-position: center center; " + // Center image
        "-fx-background-repeat: no-repeat;" // Don't repeat image
        );

    VBox contentBox = mainScene();
    
    // Add content to center of main window
    mainRoot.setCenter(contentBox);
    // Add mainRoot to a StackPane
    StackPane rootPane = new StackPane(mainRoot);
    // Create scene for main window
    Scene mainScene = new Scene(rootPane, 1600, 900);

    goToExercisesButton.setOnAction(event -> {
        // Switch to main scene (exercise list)
        primaryStage.setScene(mainScene);
        mainScene.setCursor(Cursor.DEFAULT);
    });
    // Add "+" button to add exercises
    Image addImage = new Image(addFile.toURI().toString());
    ImageView addImageView = new ImageView(addImage);
    Button addButton = new Button();
    addButton.setGraphic(addImageView); // Add image to button
    addButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
    
    // Filter button
    Image filterImage = new Image(filterFile.toURI().toString());
    ImageView filterImageView = new ImageView(filterImage);
    Button filterButton = new Button();
    filterButton.setGraphic(filterImageView); // Add image to button
    filterButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
    
    // Home button
    Image homeImage = new Image(homeFile.toURI().toString());
    ImageView homeImageView = new ImageView(homeImage);
    Button homeButton = new Button();
    homeButton.setGraphic(homeImageView); // Add image to button
    homeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background

    // New scene for adding exercises
    BorderPane addExerciseRoot = new BorderPane();
    addExerciseRoot.setStyle(
        "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
        "-fx-background-size: cover; " + // Adjust image to cover container
        "-fx-background-position: center center; " + // Center image
        "-fx-background-repeat: no-repeat;" // Don't repeat image
        );
    Scene addExerciseScene = new Scene(addExerciseRoot, 1600, 900);
    addButton.setOnAction(event -> {
        primaryStage.setScene(addExerciseScene);
        addExerciseScene.setCursor(Cursor.DEFAULT);
    });
    filterButton.setOnAction(event -> {
        Label titleFilter = new Label("Filtrer: Choisissez les langages");
        titleFilter.setStyle("-fx-font-size: 34px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
        Button searchButton = new Button();
        searchButton.setGraphic(searchImageView); // Add image to button
        searchButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
        searchImageView.setFitWidth(180); // Image width
        searchImageView.setFitHeight(180); // Image height
        searchImageView.setPreserveRatio(true); // Preserve proportions
        // Add checkboxes and search button to HBox
        HBox filterBox = new HBox(10, filterPythonCheckBox, filterJavaCheckBox, filterCCheckBox, filterJSCheckBox, filterPHPCheckBox);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setStyle("-fx-padding: 10px;");

        // Create semi-transparent background
        VBox overlay = new VBox();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.83);");
        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setAlignment(Pos.CENTER);
        
        setupSearchButton(searchButton,overlay,rootPane);
        // Add filterBox to center
        VBox popupContent = new VBox(5,titleFilter, filterBox);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setStyle("-fx-padding: 20px; -fx-background-color: rgba(30, 30, 30, 0.9);");
    
        // Add button to close popup
        fermerImageView.setFitWidth(100); // Image width
        fermerImageView.setFitHeight(100); // Image height
        Button closeButton = new Button();
        closeButton.setGraphic(new ImageView(fermerImage));
        closeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
        closeButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            filterPythonCheckBox.setSelected(false);
            filterJavaCheckBox.setSelected(false);
            filterCCheckBox.setSelected(false);
            filterJSCheckBox.setSelected(false);
            filterPHPCheckBox.setSelected(false);
        });
    
        HBox closesearchButton = new HBox(10, searchButton, closeButton);
        closesearchButton.setAlignment(Pos.CENTER);

        popupContent.getChildren().add(closesearchButton);
        overlay.getChildren().add(popupContent);
    
        // Add overlay to StackPane
        rootPane.getChildren().add(overlay);
    });
    homeButton.setOnAction(event -> {
        primaryStage.setScene(homePageScene);
        homePageScene.setCursor(Cursor.DEFAULT);
    });
    VBox addExerciseBox = new VBox(40);
    addExerciseBox.setAlignment(Pos.CENTER);
    

    Label addExerciseLabel = new Label("Ajouter un nouvel exercice");
    addExerciseLabel.setStyle("-fx-font-size: 80px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

    TextArea titleInput = new TextArea();
    Label titleLabel = new Label("Titre de l'exercice");
    titleLabel.setStyle("-fx-font-size: 45px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    titleInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-font-size: 30px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    titleInput.setWrapText(true);
    titleInput.setPrefWidth(400); // Preferred width
    titleInput.setPrefHeight(50); // Preferred height

    TextArea questionInput = new TextArea();
    Label questionLabel = new Label("Question de l'exercice");
    questionLabel.setStyle("-fx-font-size: 45px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    questionInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-font-size: 30px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    questionInput.setWrapText(true);
    questionInput.setPrefWidth(400);
    questionInput.setPrefHeight(150);

    TextArea difficultyInput = new TextArea();
    Label difficultyLabel2 = new Label("Difficulté de l'exercice");
    difficultyLabel2.setStyle("-fx-font-size: 45px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    difficultyInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-font-size: 30px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    difficultyInput.setPrefWidth(400);
    difficultyInput.setPrefHeight(50);
    difficultyInput.setWrapText(true);

    // Add checkboxes to choose languages
    CheckBox pythonCheckBox = new CheckBox("Python");
    pythonCheckBox.setStyle("-fx-font-size: 25px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    CheckBox javaCheckBox = new CheckBox("Java");
    javaCheckBox.setStyle("-fx-font-size: 25px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    CheckBox CCheckBox = new CheckBox("C");
    CCheckBox.setStyle("-fx-font-size: 25px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    CheckBox jsCheckBox = new CheckBox("JavaScript");
    jsCheckBox.setStyle("-fx-font-size: 25px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    CheckBox phpCheckBox = new CheckBox("PHP");
    phpCheckBox.setStyle("-fx-font-size: 25px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

    HBox languageSelectionBox = new HBox(10, pythonCheckBox, javaCheckBox, CCheckBox, jsCheckBox, phpCheckBox);
    languageSelectionBox.setAlignment(Pos.CENTER);
    languageSelectionBox.setStyle("-fx-padding: 10px;");

    // Add ComboBox to choose exercise type
    ComboBox<String> typeComboBox = new ComboBox<>();
    typeComboBox.getItems().addAll("STDIN/STDOUT", "INCLUDE");
    typeComboBox.setValue("STDIN/STDOUT");
    Label comboBoxLabel = new Label("Mode :");
    comboBoxLabel.setStyle("-fx-font-size: 40px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    typeComboBox.setStyle("-fx-font-size: 25px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
    HBox typeBox = new HBox(10, comboBoxLabel, typeComboBox);
    typeBox.setAlignment(Pos.CENTER);
    typeComboBox.setOnAction(event -> {
        // Uncheck all checkboxes in languageSelectionBox
        pythonCheckBox.setSelected(false);
        javaCheckBox.setSelected(false);
        CCheckBox.setSelected(false);
        jsCheckBox.setSelected(false);
        phpCheckBox.setSelected(false);
    });

    // Add event handler for each CheckBox
    pythonCheckBox.setOnAction(event -> {
        if (typeComboBox.getValue().equals("INCLUDE")) {
            javaCheckBox.setSelected(false);
            CCheckBox.setSelected(false);
            jsCheckBox.setSelected(false);
            phpCheckBox.setSelected(false);
        }
    });

    Image nextImage = new Image(nextFile.toURI().toString());
    ImageView nextImageView = new ImageView(nextImage);
    nextImageView.setFitWidth(210); // Image width
    nextImageView.setFitHeight(210); // Image height
    nextImageView.setPreserveRatio(true); // Preserve proportions
    Button saveButton = new Button();
    saveButton.setGraphic(nextImageView);
    saveButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background

    ImageView fermerImageView2 = new ImageView(fermerImage);
    fermerImageView2.setFitWidth(175); // Image width
    fermerImageView2.setFitHeight(175); // Image height
    fermerImageView2.setPreserveRatio(true); // Preserve proportions
    Button cancelButton = new Button();
    cancelButton.setGraphic(fermerImageView2);
    cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
    cancelButton.setOnAction(event ->{
        primaryStage.setScene(mainScene);
        mainScene.setCursor(Cursor.DEFAULT);
    });// Return to main scene


    // Add output area (console)
    // Declare and configure outputArea once
    TextArea outputArea = new TextArea();
    outputArea.setEditable(false);
    outputArea.setWrapText(true);
    outputArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 1); " + 
                        "-fx-text-fill: #00FF00; " + // Green color like a terminal 
                        "-fx-font-family: 'Monospace'; " + 
                        "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc);" ); 




    // Create a BorderPane for the correction scene
    Label correctionLabel = new Label("Correction en Python :");
    correctionLabel.setStyle("-fx-font-size: 50px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

    CodeArea correctionInput = SyntaxicalColor.createCodeArea();
    correctionInput.setParagraphGraphicFactory(LineNumberFactory.get(correctionInput));
    correctionInput.setPlaceholder(new Label("Entrez la correction en Python pour cet exercice"));
    correctionInput.setPrefHeight(500); // Preferred height


    Image saveImage = new Image(saveFile.toURI().toString());
    ImageView saveImageView2 = new ImageView(saveImage);
    saveImageView2.setFitWidth(225); // Image width
    saveImageView2.setFitHeight(225); // Image height
    saveImageView2.setPreserveRatio(true); // Preserve proportions
    Button saveCorrectionButton = new Button();
    saveCorrectionButton.setGraphic(saveImageView2);
    saveCorrectionButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background

    Image returnImage = new Image(returnFile.toURI().toString());
    ImageView returnImageView2 = new ImageView(returnImage);
    Button backToAddExerciseButton = new Button();
    backToAddExerciseButton.setGraphic(returnImageView2);
    backToAddExerciseButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
    returnImageView2.setFitWidth(200); // Image width
    returnImageView2.setFitHeight(200); // Image height
    returnImageView2.setPreserveRatio(true); // Preserve proportions
    backToAddExerciseButton.setOnAction(event -> {
        primaryStage.setScene(addExerciseScene);
        addExerciseScene.setCursor(Cursor.DEFAULT);
    }); // Return to add exercise scene

    HBox correctionButtonBox = new HBox(10, backToAddExerciseButton, saveCorrectionButton);
    correctionButtonBox.setAlignment(Pos.CENTER);

    VBox correctionVBox = new VBox(10, correctionLabel, correctionInput, correctionButtonBox);
    correctionVBox.setStyle(
        "-fx-background-color: transparent;"+
        "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
        "-fx-background-size: cover; " + // Adjust image to cover container
        "-fx-background-position: center center; " + // Center image
        "-fx-background-repeat: no-repeat;" // Don't repeat image
        );
    correctionVBox.setAlignment(Pos.CENTER); // Center the VBox
    correctionVBox.setPrefWidth(800); // Reduce width to 800 pixels

    
    Scene correctionStage = new Scene(correctionVBox, 1600, 1400);

    List<String> languageBoxSelected = new ArrayList<>();
    
    // Modify "Next" button action to go to correction scene
    saveButton.setOnAction(event -> {
        String title = titleInput.getText();
        String question = questionInput.getText();
        String difficulty = difficultyInput.getText();
        String type = typeComboBox.getValue(); // Get selected type
        boolean isPythonSelected = pythonCheckBox.isSelected();
        boolean isJavaSelected = javaCheckBox.isSelected();
        boolean isCSelected = CCheckBox.isSelected();
        boolean isJSSelected = jsCheckBox.isSelected();
        boolean isPHPSelected = phpCheckBox.isSelected();

        if (!title.isEmpty() && !question.isEmpty() && !difficulty.isEmpty() && type != null &&
            (isPythonSelected || isCSelected || isJavaSelected || isJSSelected || isPHPSelected)) {
            if (Connexionbdd.isTitleExists(title)) {
                System.err.println("Le titre est déjà pris, veuillez à en choisir un autre.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Le titre est déjà pris, veuillez à en choisir un autre.");
                alert.showAndWait();
            } else {
                if(typeComboBox.getValue().equals("STDIN/STDOUT")){
                    correctionLabel.setText("Python Generator:"); 
                    correctionInput.replaceText("from random import *\n" + //
                                                    "import sys\n" + //
                                                    "\n" + //
                                                    "def main():\n" + //
                                                    "   seed(sys.argv[1])\n" + //
                                                    "\n" + //
                                                    "if __name__ == \"__main__\":\n" + //
                                                    "   main()");
                    languageBoxSelected.clear();
                    languageBoxSelected.add("Python");
                    languageBoxSelected.add("-1");
                    primaryStage.setScene(correctionStage);
                    correctionStage.setCursor(Cursor.DEFAULT);
                }
                else{
                    languageBoxSelected.clear();
                    if (isPythonSelected) {
                        languageBoxSelected.add("Python");
                    }
                    if (isJavaSelected) {
                        languageBoxSelected.add("Java");
                    }
                    if (isCSelected) {
                        languageBoxSelected.add("C");
                    }
                    if (isJSSelected) {
                        languageBoxSelected.add("JavaScript");
                    }
                    if (isPHPSelected) {
                        languageBoxSelected.add("PHP");
                    }
                    correctionInput.replaceText("from random import *\n" + //
                                                    "import sys\n" + //
                                                    "\n" + //
                                                    "def main():\n" + //
                                                    "   seed(sys.argv[1])\n\n" + //
                                                    "   # Votre code ici\n" + //
                                                    "\n" + //
                                                    "if __name__ == \"__main__\":\n" + //
                                                    "   main()");
                    languageBoxSelected.add("-1");
                    correctionLabel.setText("Python Generator:"); 
                    correctionLabel.setStyle("-fx-font-size: 40px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                    primaryStage.setScene(correctionStage);
                    correctionStage.setCursor(Cursor.DEFAULT);
                    
                }

                saveCorrectionButton.setOnAction(e -> {
                    String correction = correctionInput.getText();
                    if(typeComboBox.getValue().equals("STDIN/STDOUT")){
                        if (!correction.isEmpty()) {
                            try {
                                // Add exercise to database
                                int exerciseId;
                                int exo;
                                if(languageBoxSelected.get(languageBoxSelected.size()-1) == "-1"){
                                    exerciseId = Connexionbdd.addExercise(title, question, difficulty, type);
                                    languageBoxSelected.set(languageBoxSelected.size()-1, exerciseId+"");
                                    exo = 1;
                                }
                                else{
                                    exerciseId = Integer.parseInt(languageBoxSelected.get(languageBoxSelected.size()-1));
                                    languageBoxSelected.set(languageBoxSelected.size()-1, exerciseId+"");
                                    exo = 0;
                                }

                                // Add selected languages to database
                                if (isPythonSelected && exo==1) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "Python");
                                }
                                if (isJavaSelected && exo==1) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "Java");
                                }
                                if (isCSelected && exo==1) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "C");
                                }
                                if (isJSSelected && exo==1) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "JavaScript");
                                }
                                if (isPHPSelected && exo==1) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "PHP");
                                }

                                // Save correction to file
                                File exerciceFile;

                                if (exo == 0) {
                                    exerciceFile = new File("src/main/resources/Correction/Exercice" + exerciseId + ".py");
                                }
                                else {
                                    exerciceFile = new File("src/main/resources/Random/randomGeneration" + exerciseId + ".py");
                                }

                                if (exerciceFile.exists()) {
                                    exerciceFile.delete();
                                }
                                if (!exerciceFile.createNewFile()) {
                                    throw new IOException("Erreur lors de la création du fichier: " + exerciceFile.getAbsolutePath());
                                }

                                FileWriter fileWriter = new FileWriter(exerciceFile);
                                fileWriter.write(correction);
                                fileWriter.close();

                                // Update exercise list
                                exerciseList.getItems().clear();
                                for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
                                    String titre = Connexionbdd.getExerciceTitle(i); // Get exercise title
                                    String difficulty2 = Connexionbdd.getExerciceDifficulty(i); // Get exercise difficulty
                                    int attempts = Connexionbdd.getExerciseAttempts(i); // Get number of attempts
                                    int successfulTries = Connexionbdd.getSuccessfulTries(i); // Get number of successful attempts
                                    String typeExo = Connexionbdd.getTypeExo(i); // Get exercise type
                        
                                    Label exerciseNumber = new Label("Exercice " + i);
                                    exerciseNumber.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label exerciseTitle = new Label(titre);
                                    exerciseTitle.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label difficultyLabel = new Label("Difficulté : " + difficulty2);
                                    difficultyLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                                    statsLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label typeLabel = new Label("Mode: " + typeExo);
                                    typeLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

                                    Region spacer = new Region();
                                    HBox.setHgrow(spacer, Priority.ALWAYS); // Push exercise type to the right

                                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, difficultyLabel, statsLabel, spacer, typeLabel);
                                    exerciseItem.setSpacing(10);
                                    exerciseItem.setStyle(
                                        "-fx-background-color: rgba(30, 30, 30, 0.9); " +
                                        "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
                                        "-fx-border-radius: 15px; " +
                                        "-fx-background-radius: 15px; " +
                                        "-fx-padding: 10px; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
                                    );
                                    exerciseList.getItems().add(exerciseItem);
                                }

                                // Uncheck all filter checkboxes
                                filterPythonCheckBox.setSelected(false);
                                filterJavaCheckBox.setSelected(false);
                                filterCCheckBox.setSelected(false);
                                filterJSCheckBox.setSelected(false);
                                filterPHPCheckBox.setSelected(false);

                                // Return to main scene
                                if (languageBoxSelected.size() > 1) {
                                    correctionLabel.setText("Correction en " + languageBoxSelected.get(0) + " :");
                                    correctionInput.replaceText("");
                                    languageBoxSelected.remove(0);
                                    correctionInput.replaceText("word = input().replace(\"\\\\n" + //
                                                                                    "\", \"\\n" + //
                                                                                    "\").split(\"\\n" + //
                                                                                    "\") # liste des sorties de l'utilisateur \n" + //
                                                                                    "\n" + //
                                                                                    "if word[0] == \"hello world\": # Si c'est vrai\n" + //
                                                                                    "    print(1)\n" + //
                                                                                    "else: # Si c'est faux\n" + //
                                                                                    "    print(0)\n" + //
                                                                                    "    print(word[0]) # valeur de l'utilisateur\n" + //
                                                                                    "    print(\"hello world\") # valeur attendu\n" + //
                                                                                    "    print(1) # ligne de l'erreur");
                                } else {
                                    primaryStage.setScene(mainScene);
                                    mainScene.setCursor(Cursor.DEFAULT);
                                }

                            } catch (IOException ex) {
                                System.err.println("Erreur lors de l'enregistrement de la correction : " + ex.getMessage());
                            }
                        } else {
                            System.err.println("La correction ne peut pas être vide ou invalide.");
                        }
                    } else {
                        if (!correction.isEmpty()){
                            int exerciseId;
                            int exo;
                            
                            if(languageBoxSelected.get(languageBoxSelected.size()-1) == "-1"){
                                exerciseId = Connexionbdd.addExercise(title, question, difficulty, type);
                                languageBoxSelected.set(languageBoxSelected.size()-1, exerciseId+"");
                                exo = 1;
                            }
                            else{
                                exerciseId = Integer.parseInt(languageBoxSelected.get(languageBoxSelected.size()-1));
                                Connexionbdd.addLanguageToExercise(exerciseId, languageBoxSelected.get(0));
                                languageBoxSelected.set(languageBoxSelected.size()-1, exerciseId+"");
                                exo = 0;
                            }

                            // Save correction to file
                            try {
                                String end = "";
                                if (languageBoxSelected.get(0).equals("Python")) {
                                    end = ".py";
                                }
                                else if (languageBoxSelected.get(0).equals("Java")) {
                                    end = ".java";
                                }
                                else if (languageBoxSelected.get(0).equals("C")) {
                                    end = ".c";
                                }
                                else if (languageBoxSelected.get(0).equals("JavaScript")) {
                                    end = ".js";
                                }
                                else if (languageBoxSelected.get(0).equals("PHP")) {
                                    end = ".php";
                                }

                                File exerciceFile;

                                if (exo == 0) {
                                    exerciceFile = new File("src/main/resources/Correction/Exercice" + exerciseId + end);
                                }
                                else {
                                    exerciceFile = new File("src/main/resources/Random/randomGeneration" + exerciseId + ".py");
                                }

                                if (exerciceFile.exists()) {
                                    exerciceFile.delete();
                                }
                                if (!exerciceFile.createNewFile()) {
                                    throw new IOException("Erreur lors de la création du fichier: " + exerciceFile.getAbsolutePath());
                                }

                                FileWriter fileWriter = new FileWriter(exerciceFile);
                                fileWriter.write(correction);
                                fileWriter.close();

                                // Update exercise list
                                exerciseList.getItems().clear();
                                for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
                                    String titre = Connexionbdd.getExerciceTitle(i); // Get exercise title
                                    String difficulty2 = Connexionbdd.getExerciceDifficulty(i); // Get exercise difficulty
                                    int attempts = Connexionbdd.getExerciseAttempts(i); // Get number of attempts
                                    int successfulTries = Connexionbdd.getSuccessfulTries(i); // Get number of successful attempts
                                    String typeExo = Connexionbdd.getTypeExo(i); // Get exercise type
                        
                                    Label exerciseNumber = new Label("Exercice " + i);
                                    exerciseNumber.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label exerciseTitle = new Label(titre);
                                    exerciseTitle.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label difficultyLabel = new Label("Difficulté : " + difficulty2);
                                    difficultyLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                                    statsLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                                    Label typeLabel = new Label("Mode: " + typeExo);
                                    typeLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

                                    Region spacer = new Region();
                                    HBox.setHgrow(spacer, Priority.ALWAYS); // Push exercise type to the right

                                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, difficultyLabel, statsLabel, spacer, typeLabel);
                                    exerciseItem.setSpacing(10);
                                    exerciseItem.setStyle(
                                        "-fx-background-color: rgba(30, 30, 30, 0.9); " +
                                        "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
                                        "-fx-border-radius: 15px; " +
                                        "-fx-background-radius: 15px; " +
                                        "-fx-padding: 10px; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
                                    );
                                    exerciseList.getItems().add(exerciseItem);
                                }

                                // Uncheck all filter checkboxes
                                filterPythonCheckBox.setSelected(false);
                                filterJavaCheckBox.setSelected(false);
                                filterCCheckBox.setSelected(false);
                                filterJSCheckBox.setSelected(false);
                                filterPHPCheckBox.setSelected(false);

                                if (languageBoxSelected.size() > 1) {    
                                    if (exo == 0){
                                        languageBoxSelected.remove(0);
                                        if (languageBoxSelected.size() == 1){
                                            primaryStage.setScene(mainScene);
                                            mainScene.setCursor(Cursor.DEFAULT);
                                        }
                                    }
                                    correctionLabel.setText("Correction en " + languageBoxSelected.get(0) + " :");
                                    switch(languageBoxSelected.get(0)){
                                        case "Python":
                                            correctionInput.replaceText("import codyngame\n" + //
                                                                        "\n" + //
                                                                        "a = int(input())\n" + //
                                                                        "b = int(input())\n" + //
                                                                        "\n" + //
                                                                        "if codyngame.somme(a,b) == a+b:\n" + //
                                                                        "    print(1)\n" + //
                                                                        "else:\n" + //
                                                                        "    print(0)\n" + //
                                                                        "    print(codyngame.somme(a,b))\n" + //
                                                                        "    print(a+b)\n" + //
                                                                        "    print(1)");
                                            break;
                                        case "Java":
                                            correctionInput.replaceText("import java.util.Scanner;\n" + //
                                                                        "\n" + //
                                                                        "public class Exercice2 {\n" + //
                                                                        "    public static void main(String[] args) {\n" + //
                                                                        "        Scanner scanner = new Scanner(System.in);\n" + //
                                                                        "\n" + //
                                                                        "        int a = scanner.nextInt();\n" + //
                                                                        "        int b = scanner.nextInt();\n" + //
                                                                        "\n" + //
                                                                        "        if (Codyngame.somme(a, b) == a + b) {\n" + //
                                                                        "            System.out.println(1);\n" + //
                                                                        "        } else {\n" + //
                                                                        "            System.out.println(0);\n" + //
                                                                        "            System.out.println(Codyngame.somme(a, b));\n" + //
                                                                        "            System.out.println(a + b);\n" + //
                                                                        "            System.out.println(1);\n" + //
                                                                        "        }\n" + //
                                                                        "\n" + //
                                                                        "        scanner.close();\n" + //
                                                                        "    }\n" + //
                                                                        "}");
                                            break;
                                        case "C":
                                            correctionInput.replaceText("#include <stdio.h>\n" + //
                                                                        "\n" + //
                                                                        "// Fonction pour importer la fonction somme du fichier utilisateur\n" + //
                                                                        "int fonction(int a, int b);\n" + //
                                                                        "\n" + //
                                                                        "int main() {\n" + //
                                                                        "    int a, b;\n" + //
                                                                        "    \n" + //
                                                                        "    // Lecture des deux nombres depuis l'entrée standard\n" + //
                                                                        "    scanf(\"%d\", &a);\n" + //
                                                                        "    scanf(\"%d\", &b);\n" + //
                                                                        "    \n" + //
                                                                        "    // Vérification du résultat - correction de la condition\n" + //
                                                                        "    if (somme(a, b) == a + b) {  // Condition corrigée\n" + //
                                                                        "        printf(\"1\\n" + //
                                                                        "\");\n" + //
                                                                        "    } else {\n" + //
                                                                        "        printf(\"0\\n" + //
                                                                        "\");\n" + //
                                                                        "        printf(\"%d\\n" + //
                                                                        "\", somme(a, b));  // Résultat reçu\n" + //
                                                                        "        printf(\"%d\\n" + //
                                                                        "\", a + b);        // Résultat attendu\n" + //
                                                                        "        printf(\"1\\n" + //
                                                                        "\");                // Valeur de comparaison\n" + //
                                                                        "    }\n" + //
                                                                        "    \n" + //
                                                                        "    return 0;\n" + //
                                                                        "}");
                                            break;
                                        case "JavaScript":
                                            correctionInput.replaceText("// Inclure le fichier contenant la fonction somme\n" + //
                                                                        "const codyngame = require('./codyngame'); // Assurez-vous que codyngame.js contient la fonction somme\n" + //
                                                                        "\n" + //
                                                                        "const readline = require('readline');\n" + //
                                                                        "\n" + //
                                                                        "// Lire les entrées depuis la console\n" + //
                                                                        "const rl = readline.createInterface({\n" + //
                                                                        "    input: process.stdin,\n" + //
                                                                        "    output: process.stdout\n" + //
                                                                        "});\n" + //
                                                                        "\n" + //
                                                                        "let inputs = [];\n" + //
                                                                        "rl.on('line', (line) => {\n" + //
                                                                        "    inputs.push(parseInt(line.trim()));\n" + //
                                                                        "    if (inputs.length >= 2) {\n" + //
                                                                        "        const a = inputs[0];\n" + //
                                                                        "        const b = inputs[1];\n" + //
                                                                        "\n" + //
                                                                        "        if (codyngame.somme(a, b) === a + b) {\n" + //
                                                                        "            console.log(\"1\");\n" + //
                                                                        "            process.exit(0);\n" + //
                                                                        "        } else {\n" + //
                                                                        "            console.log(\"0\");\n" + //
                                                                        "            console.log(codyngame.somme(a, b));\n" + //
                                                                        "            console.log(a + b);\n" + //
                                                                        "            console.log(\"1\");\n" + //
                                                                        "            process.exit(0);\n" + //
                                                                        "        }\n" + //
                                                                        "\n" + //
                                                                        "        rl.close();\n" + //
                                                                        "    }\n" + //
                                                                        "});");
                                            break;
                                        case "PHP":
                                            correctionInput.replaceText("<?php\n" + //
                                                                        "\n" + //
                                                                        "include 'codyngame.php'; // Inclure le fichier contenant la fonction somme\n" + //
                                                                        "\n" + //
                                                                        "$a = intval(trim(fgets(STDIN))); // Lire la première entrée\n" + //
                                                                        "$b = intval(trim(fgets(STDIN))); // Lire la deuxième entrée\n" + //
                                                                        "\n" + //
                                                                        "if (codyngame_somme($a, $b) == $a + $b) {\n" + //
                                                                        "    echo \"1\\n" + //
                                                                        "\";\n" + //
                                                                        "} else {\n" + //
                                                                        "    echo \"0\\n" + //
                                                                        "\";\n" + //
                                                                        "    echo codyngame_somme($a, $b) . \"\\n" + //
                                                                        "\";\n" + //
                                                                        "    echo ($a + $b) . \"\\n" + //
                                                                        "\";\n" + //
                                                                        "    echo \"1\\n" + //
                                                                        "\";\n" + //
                                                                        "}\n" + //
                                                                        "?>");
                                            break;
                                    }
                                } else {
                                    primaryStage.setScene(mainScene);
                                    mainScene.setCursor(Cursor.DEFAULT);
                                }

                                
                            } catch (IOException ex) {
                                System.err.println("Erreur lors de l'enregistrement de la correction : " + ex.getMessage());
                            }
                        } else {
                            System.err.println("La correction ne peut pas être vide.");
                        }
                    }
                });
            }
        } else {
            System.err.println("Veuillez remplir tous les champs et sélectionner au moins une langue.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner au moins une langue.");
            alert.showAndWait();
        }
    });

    HBox buttonBoxAdd = new HBox(10, cancelButton, saveButton);
    buttonBoxAdd.setAlignment(Pos.CENTER);


    // Row for title
    HBox titleRow = new HBox(30); // Horizontal spacing of 10px
    titleRow.setAlignment(Pos.CENTER); // Align elements to left
    titleRow.getChildren().addAll(titleLabel, titleInput);

    // Row for question
    HBox questionRow = new HBox(20);
    questionRow.setAlignment(Pos.CENTER);
    questionRow.getChildren().addAll(questionLabel, questionInput);
    // Row for difficulty
    HBox difficultyRow = new HBox(20);
    difficultyRow.setAlignment(Pos.CENTER);
    difficultyRow.getChildren().addAll(difficultyLabel2, difficultyInput);
    
    titleRow.setPadding(new Insets(0, 0, 0, 40)); // 20px left padding
    questionRow.setPadding(new Insets(0, 10, 0, 0)); // 20px left padding
    difficultyRow.setPadding(new Insets(0, 26, 0, 0)); // 10px left padding
    // Group all rows in a VBox
    VBox formBox = new VBox(15, titleRow, questionRow, difficultyRow);
    formBox.setAlignment(Pos.CENTER);

    
    addExerciseBox.getChildren().addAll(addExerciseLabel,formBox, typeBox, languageSelectionBox, buttonBoxAdd);
    addExerciseRoot.setCenter(addExerciseBox);

        // Create a Region to separate buttons
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS); // Allows spacer to take all available space
        
        // Add buttons and spacer to HBox
        HBox topBar = new HBox(10, homeButton, space, filterButton, addButton);
        topBar.setAlignment(Pos.CENTER); // Center elements vertically
        topBar.setStyle("-fx-background-color: rgba(88, 69, 102, 0.95); -fx-padding: 15px;");

        // Add bar to top of BorderPane
        mainRoot.setTop(topBar);

        // Secondary window (code area)
        BorderPane secondaryRoot = new BorderPane();
        secondaryRoot.setStyle(
            "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
            "-fx-background-size: cover; " + // Adjust image to cover container
            "-fx-background-position: center center; " + // Center image
            "-fx-background-repeat: no-repeat;" // Don't repeat image
        );


        // Add text area to display instructions
        TextFlow instructionArea = new TextFlow();
        instructionArea.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-padding: 10px; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc);");
        instructionArea.setPrefWidth(400); // Preferred width for instructions
        
        // Title for code area and language selector
        ComboBox<String> languageSelector = new ComboBox<>();
        languageSelector.setStyle(" -fx-font-family: 'Pixel Game';-fx-font-size: 25px;");
        HBox.setMargin(languageSelector, new Insets(12, 0, 0, 10)); // 10px top and left margin
        Label codeAreaTitle = new Label("Zone de code");
        codeAreaTitle.setStyle("-fx-font-size: 50px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
        HBox codeAreaTitleBox = new HBox(10, codeAreaTitle, languageSelector);
        HBox.setMargin(codeAreaTitle, new Insets(10, 0, 0, 20)); // 50px right margin
        // Add text area to write code
        CodeArea codeArea = SyntaxicalColor.createCodeArea();

        codeArea.setWrapText(false);
        codeArea.setPrefHeight(400); // Preferred height of 400 pixels
        codeArea.setMinHeight(400);  // Minimum height of 300 pixels
        codeArea.setMaxHeight(400);  // Maximum height of 600 pixels
        VBox codeAreaBox = new VBox(10, codeAreaTitleBox, codeArea);
        codeAreaBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Black background with 90% opacity

        // Reuse outputArea without reconfiguring
        javafx.scene.control.SplitPane codeAndConsoleSplitPane = new javafx.scene.control.SplitPane();
        codeAndConsoleSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        codeAndConsoleSplitPane.getItems().addAll(codeAreaBox, outputArea);
        codeAndConsoleSplitPane.setDividerPositions(0.7);
        codeAndConsoleSplitPane.setStyle("-fx-background-color: transparent;");

        // Create SplitPane to divide instructions and code/console
        javafx.scene.control.SplitPane instructionAndCodeSplitPane = new javafx.scene.control.SplitPane();
        instructionAndCodeSplitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        instructionAndCodeSplitPane.getItems().addAll(instructionArea, codeAndConsoleSplitPane);
        instructionAndCodeSplitPane.setDividerPositions(0.3);
        instructionAndCodeSplitPane.setStyle("-fx-background-color: transparent;");
        // Add SplitPane to center of secondary window
        secondaryRoot.setCenter(instructionAndCodeSplitPane);

        // Add label to display number of attempts
        Label attemptsLabel = new Label();

        // Add label to display number of successful attempts
        Label successfulTriesLabel = new Label();

        VBox statsBox = new VBox(10, attemptsLabel, successfulTriesLabel);

        // Add button to execute code
        Image runImage = new Image(executeFile.toURI().toString());
        ImageView runImageView = new ImageView(runImage);
        Button runButton = new Button();
        runButton.setGraphic(runImageView); // Add image to button
        runButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
        // Add button to return to exercise list
        ImageView returnImageView = new ImageView(returnImage);
        Button backButton = new Button();
        backButton.setGraphic(returnImageView);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Transparent background
        backButton.setOnAction(event -> {
            // Update exercise list
            exerciseList.getItems().clear();
            for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
                String titre = Connexionbdd.getExerciceTitle(i); // Get exercise title
                String difficulty = Connexionbdd.getExerciceDifficulty(i); // Get exercise difficulty
                int attempts = Connexionbdd.getExerciseAttempts(i); // Get number of attempts
                int successfulTries = Connexionbdd.getSuccessfulTries(i); // Get number of successful attempts
                String typeExo = Connexionbdd.getTypeExo(i); // Get exercise type
    
                Label exerciseNumber = new Label("Exercice " + i);
                exerciseNumber.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label exerciseTitle = new Label(titre);
                exerciseTitle.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label difficultyLabel = new Label("Difficulté : " + difficulty);
                difficultyLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                statsLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                Label typeLabel = new Label("Mode: " + typeExo);
                typeLabel.setStyle("-fx-font-size: 23px;-fx-padding: 5px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS); // Push exercise type to the right

                HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, difficultyLabel, statsLabel, spacer, typeLabel);
                exerciseItem.setSpacing(10);
                exerciseItem.setStyle(
                    "-fx-background-color: rgba(30, 30, 30, 0.9); " +
                    "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
                    "-fx-border-radius: 15px; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-padding: 10px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
                );
                exerciseList.getItems().add(exerciseItem);
            }
            primaryStage.setScene(mainScene); // Return to main scene
            mainScene.setCursor(Cursor.DEFAULT);
        });


        // Reorganize components in HBox
        Region space2 = new Region();
        HBox.setHgrow(space2, Priority.ALWAYS); // Allows spacer to take all available space
        HBox buttonBox = new HBox(20, backButton, space2, statsBox, runButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle(("-fx-background-color: rgba(20, 20, 20, 0.6); -fx-padding: 10px; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc);"));
        
        
        // Add button and label to bottom
        secondaryRoot.setBottom(buttonBox);

        // Create scene for secondary window
        Scene secondaryScene = new Scene(secondaryRoot, 1600, 900);

        // Handle exercise click event
        exerciseList.setOnMouseClicked(event -> {
            HBox selectedItem = exerciseList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Get exercise number from first label in HBox
                Label exerciseNumberLabel = (Label) selectedItem.getChildren().get(0);
                String exerciseNumberText = exerciseNumberLabel.getText().replace("Exercice ", "").trim();
                int selectedExo;

                try {
                    selectedExo = Integer.parseInt(exerciseNumberText); // Convert to integer
                } catch (NumberFormatException e) {
                    System.err.println("Erreur : Impossible de convertir le numéro de l'exercice. Texte : " + exerciseNumberText);
                    return; // Stop if number is invalid
                }

                // Get and display instructions
                String consigne = Connexionbdd.showConsigne(selectedExo);
                // Clear previous TextFlow content
                instructionArea.getChildren().clear();

                // Add "Instructions:" text in large font
                Text consigneLabel = new Text("Consigne : \n");
                consigneLabel.setStyle("-fx-font-size: 60px; -fx-fill: white;-fx-font-family: 'Pixel Game';");

                // Add instruction text
                Text consigneText = new Text(consigne);
                consigneText.setStyle("-fx-font-size: 35px; -fx-fill: white;-fx-font-family: 'Pixel Game';");

                // Add texts to TextFlow
                instructionArea.getChildren().addAll(consigneLabel, new Text("\n"), consigneText);

                // Get available languages for selected exercise
                languageSelector.getItems().clear();
                languageSelector.getItems().addAll(Connexionbdd.getAvailableLanguages(selectedExo));

                // Set first available language as default
                if (!languageSelector.getItems().isEmpty()) {
                    languageSelector.setValue(languageSelector.getItems().get(0));
                }

                // Update code area based on selected language
                String language = languageSelector.getValue();
                if (language.equals("Python")) {
                    codeArea.replaceText("word = input()\n\nprint(word)");
                } 
                else if (language.equals("Java")) {
                    codeArea.replaceText(
                        "import java.util.Scanner;\n\n" +
                        "public class Codyngame {\n" +
                        "        public static void main(String[] args) {\n" +
                        "                Scanner sc = new Scanner(System.in);\n" +
                        "                String word = sc.nextLine();\n" +
                        "                System.out.println(word);\n" +
                        "        }\n" +
                        "}"
                    );
                }
                else if (language.equals("C")) {
                    codeArea.replaceText(
                        "#include <stdio.h>\n\n" +
                        "int main() {\n" +
                        "        char word[100];\n" +
                        "        scanf(\"%s\", word);\n" +
                        "        printf(\"%s\", word);\n" +
                        "        return 0;\n" +
                        "}"
                    );
                }
                else if (language.equals("JavaScript")) {
                    codeArea.replaceText(
                        "const readline = require('readline');\n" +
                        "const rl = readline.createInterface({\n" +
                        "        input: process.stdin,\n" +
                        "        output: process.stdout\n" +
                        "});\n" +
                        "\n" +
                        "rl.question('', (word) => {\n" +
                        "        console.log(word);\n" +
                        "        rl.close();\n" +
                        "});"
                    );
                }
                else if (language.equals("PHP")) {
                    codeArea.replaceText(
                        "<?php\n" +
                        "$word = trim(fgets(STDIN));\n" +
                        "echo $word;\n"
                    );
                }

                // Get and display number of attempts
                int attempts = Connexionbdd.getExerciseAttempts(selectedExo);
                attemptsLabel.setText("Nombre d'essais : " + attempts);
                attemptsLabel.setStyle("-fx-font-size: 35px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

                // Get and display number of successful attempts
                int successfulTries = Connexionbdd.getSuccessfulTries(selectedExo);
                successfulTriesLabel.setText("Nombre d'essais réussis : " + successfulTries);
                successfulTriesLabel.setStyle("-fx-font-size: 35px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
                primaryStage.setScene(secondaryScene); // Switch to secondary scene
                secondaryScene.setCursor(Cursor.DEFAULT);
            }
        });

        codeArea.setOnKeyPressed(event -> {
            tabulationNumber(codeArea, event);
        });

        correctionInput.setOnKeyPressed(event -> {
            tabulationNumber(correctionInput, event);
        });

        runButton.setOnAction(event -> {
            String code = codeArea.getText();
            String language = languageSelector.getValue();
            HBox selectedItem = exerciseList.getSelectionModel().getSelectedItem();
            Label exerciseNumberLabel = (Label) selectedItem.getChildren().get(0);
            String exerciseNumberText = exerciseNumberLabel.getText().replace("Exercice ", "").trim();
            int id = Integer.parseInt(exerciseNumberText); 
            IDEExecuteCode executor = LanguageChoice.choice(language, outputArea);
            outputArea.setText("");
            executor.executeCode(code, id);

            // Increment attempt count in database
            Connexionbdd.incrementExerciseAttempts(id);

            // Check if answer is correct
            if(outputArea.getText().contains("Le code est correct")){
                Connexionbdd.incrementSuccessfulTries(id);
            }

            // Update attempt count display
            int updatedAttempts = Connexionbdd.getExerciseAttempts(id);
            attemptsLabel.setText("Nombre d'essais : " + updatedAttempts);
            attemptsLabel.setStyle("-fx-font-size: 35px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

            // Update exercise list
           int updatedSuccessfulTries = Connexionbdd.getSuccessfulTries(id);
            successfulTriesLabel.setText("Nombre d'essais réussis : " + updatedSuccessfulTries);
            successfulTriesLabel.setStyle("-fx-font-size: 35px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
        });

        languageSelector.setOnAction(event -> {
            String selectedLanguage = languageSelector.getValue();
            String consigne = Connexionbdd.showConsigne(exerciseList.getSelectionModel().getSelectedIndex() + 1);
            if (selectedLanguage == null) {
                return; // Do nothing if no language selected
            }
            SyntaxicalColor.setLanguage(selectedLanguage);
            if (selectedLanguage.equals("Python")) {
                codeArea.replaceText("word = input()\n\nprint(word)");
            } 
            else if (selectedLanguage.equals("Java")) {
                codeArea.replaceText(
                    "import java.util.Scanner;\n\n" +
                    "public class Codyngame {\n" +
                    "        public static void main(String[] args) {\n" +
                    "                Scanner sc = new Scanner(System.in);\n" +
                    "                String word = sc.nextLine();\n" +
                    "                System.out.println(word);\n" +
                    "        }\n" +
                    "}"
                );
            }
            else if (selectedLanguage.equals("C")) {
                codeArea.replaceText(
                    "#include <stdio.h>\n\n" +
                    "int main() {\n" +
                    "        char word[100];\n" +
                    "        scanf(\"%s\", word);\n" +
                    "        printf(\"%s\", word);\n" +
                    "        return 0;\n" +
                    "}"
                );
            }
            else if (selectedLanguage.equals("JavaScript")) {
                codeArea.replaceText(
                    "const readline = require('readline');\n" +
                    "const rl = readline.createInterface({\n" +
                    "        input: process.stdin,\n" +
                    "        output: process.stdout\n" +
                    "});\n" +
                    "\n" +
                    "rl.question('', (word) => {\n" +
                    "        console.log(word);\n" +
                    "        rl.close();\n" +
                    "});"
                );
            }
            else if (selectedLanguage.equals("PHP")) {
                codeArea.replaceText(
                    "<?php\n" +
                    "$word = trim(fgets(STDIN));\n" +
                    "echo $word;\n"
                );
            }
        });
        
        // Syntax highlighting
        codeArea.getStylesheets().add(getClass().getResource("/SyntaxicalColor.css").toExternalForm());
        // Configure and display main window
    }
    }

    /**
     * Main method to launch the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}