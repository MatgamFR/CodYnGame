import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    ListView<HBox> exerciseList;
    CheckBox filterPythonCheckBox;
    CheckBox filterCCheckBox;
    CheckBox filterJavaCheckBox;
    CheckBox filterJSCheckBox;
    CheckBox filterPHPCheckBox;

    public void tabulationNumber(TextArea codeArea, KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            int caretPosition = codeArea.getCaretPosition();
            String text = codeArea.getText();
            String tab = "";
            int start = text.lastIndexOf("\n", caretPosition-2) + 1;
            for (int i=start; i < text.length(); i++){
                char c = text.charAt(i);
                if (c == '\t' || c == ' '){
                    tab += c;
                } else {
                    break;
                }
            }
            codeArea.insertText(caretPosition, tab);
            int verif = text.lastIndexOf("\n", caretPosition-1)-1;
            if (text.lastIndexOf("{", caretPosition-1) == verif && text.lastIndexOf("}", caretPosition+1) == verif+2){
                codeArea.deleteText(verif+1, verif+1);
                codeArea.insertText(caretPosition, "\t");
                codeArea.insertText(caretPosition+1+tab.length(), "\n"+tab);
                codeArea.positionCaret(caretPosition+1+tab.length());
            }
            else if (text.lastIndexOf("{", caretPosition-1) == verif){
                codeArea.insertText(caretPosition, "\t");
                codeArea.positionCaret(caretPosition+1+tab.length());
            }
            if (text.lastIndexOf(":", caretPosition-1) == verif){
                codeArea.insertText(caretPosition, "\t");
                codeArea.positionCaret(caretPosition+1+tab.length());
            }
            
        }
        if (event.getText().equals("{")) {
            int position = codeArea.getCaretPosition();
            codeArea.insertText(position, "}");
            codeArea.positionCaret(position);
        }
        if (event.getText().equals("(")) {
            int position = codeArea.getCaretPosition();
            codeArea.insertText(position, ")");
            codeArea.positionCaret(position);
        }
        if (event.getText().equals("\"")) {
            int position = codeArea.getCaretPosition();
            codeArea.insertText(position, "\"");
            codeArea.positionCaret(position);
        }
    }

    public void setupBDD(){
        try {
            // Lire les valeurs de configuration depuis configue.txt
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

            // Initialiser Connexionbdd avec les valeurs lues
            new Connexionbdd(dbUrl, dbUser, dbPassword);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de la configuration : " + e.getMessage());
            return;
        }
    }

    public void setupSearchButton(Button searchButton){
        searchButton.setOnAction(event -> {
            // Récupérer les langages sélectionnés
            List<String> selectedLanguages = new ArrayList<>();
            if (filterPythonCheckBox.isSelected()){
                selectedLanguages.add("Python");
            }
            if (filterJavaCheckBox.isSelected()){
                selectedLanguages.add("Java");
            }
            if (filterCCheckBox.isSelected()){ 
                selectedLanguages.add("C");
            }
            if (filterJSCheckBox.isSelected()){
                selectedLanguages.add("JavaScript");
            }
            if (filterPHPCheckBox.isSelected()){
                selectedLanguages.add("PHP");
            }
        
            // Filtrer les exercices
            List<Integer> filteredExerciseIds = Connexionbdd.getExercisesByLanguages(selectedLanguages);
            if (filteredExerciseIds.isEmpty()) {
                exerciseList.getItems().clear();
                int maxExobis = Connexionbdd.maxexo();
                for (int i = 1; i <= maxExobis; i++) {
                    String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
                    int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
                    int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis

                    Label exerciseNumber = new Label("Exercice " + i);
                    exerciseNumber.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    Label exerciseTitle = new Label(titre);
                    exerciseTitle.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");
                    Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                    statsLabel.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");

                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, statsLabel);
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
            }
            else {
                exerciseList.getItems().clear();
                for (int id : filteredExerciseIds) {
                    String titre = Connexionbdd.getExerciceTitle(id);
                    Label exerciseNumber = new Label("Exercice " + id);
                    exerciseNumber.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    Label exerciseTitle = new Label(titre);
                    exerciseTitle.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");
                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle);
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
            }
        });
    }

    public VBox mainScene() {
        // Titre de la page
        Label titleLabel = new Label("Le codyngame de la javadocance");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        // Description
        Label descriptionLabel = new Label("Bienvenue sur notre codyngame, veuillez choisir un exercice. Bon codage!");
        descriptionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");


        // Créer une liste d'exercices
        ListView<HBox> exerciseList = new ListView<>();
        exerciseList.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
            String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
            int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
            int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis

            Label exerciseNumber = new Label("Exercice " + i);
            exerciseNumber.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label exerciseTitle = new Label(titre);
            exerciseTitle.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");
            Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
            statsLabel.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");

            HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, statsLabel);
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

        this.exerciseList = exerciseList;

        // Ajouter des cases à cocher pour filtrer par langage
        CheckBox filterPythonCheckBox = new CheckBox("Python");
        filterPythonCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);");
        CheckBox filterJavaCheckBox = new CheckBox("Java");
        filterJavaCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);");
        CheckBox filterCCheckBox = new CheckBox("C");
        filterCCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);");
        CheckBox filterJSCheckBox = new CheckBox("JavaScript");
        filterJSCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);");
        CheckBox filterPHPCheckBox = new CheckBox("PHP");
        filterPHPCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);");

        this.filterCCheckBox = filterCCheckBox;
        this.filterJSCheckBox = filterJSCheckBox;
        this.filterJavaCheckBox = filterJavaCheckBox;
        this.filterPHPCheckBox = filterPHPCheckBox;
        this.filterPythonCheckBox = filterPythonCheckBox;
        
        Button searchButton = new Button("Rechercher");
        searchButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        // Ajouter les cases à cocher et le bouton de recherche dans une HBox
        HBox filterBox = new HBox(10, filterPythonCheckBox, filterJavaCheckBox, filterCCheckBox, filterJSCheckBox, filterPHPCheckBox, searchButton);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setStyle("-fx-padding: 10px;");

        // Organiser les composants dans un VBox
        VBox contentBox = new VBox(10, titleLabel, descriptionLabel, filterBox, exerciseList);
        contentBox.setStyle("-fx-background-color: rgba(10, 10, 10, 0.95); -fx-padding: 25px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");
        contentBox.setAlignment(Pos.CENTER);

        setupSearchButton(searchButton);

        return contentBox;

        
    }

    @Override
    public void start(Stage primaryStage) {
        setupBDD();

        // Fenêtre principale (liste des exercices)
        BorderPane mainRoot = new BorderPane();
        Color backgroundColorMain = Color.web("#1E1E1E");

        VBox contentBox = mainScene();

        // Ajouter le contenu au centre de la fenêtre principale
        mainRoot.setCenter(contentBox);

        // Créer une scène pour la fenêtre principale
        Scene mainScene = new Scene(mainRoot, 1600, 900, backgroundColorMain);

        // Ajouter un bouton "+" pour ajouter des exercices
        Button addButton = new Button("+");
        addButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-min-width: 50px; -fx-min-height: 50px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #ffffff); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-min-width: 50px; -fx-min-height: 50px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 8, 0.5, 0, 2);"));
        addButton.setOnMouseExited(e -> addButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-min-width: 50px; -fx-min-height: 50px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);"));

        // Nouvelle scène pour ajouter des exercices
        BorderPane addExerciseRoot = new BorderPane();
        Scene addExerciseScene = new Scene(addExerciseRoot, 600, 400);
        addButton.setOnAction(event -> primaryStage.setScene(addExerciseScene));
        VBox addExerciseBox = new VBox(10);
        addExerciseBox.setAlignment(Pos.CENTER);
        addExerciseBox.setStyle("-fx-background-color: rgba(10, 10, 10, 0.95); -fx-padding: 25px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");

        Label addExerciseLabel = new Label("Ajouter un nouvel exercice");
        addExerciseLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100, 100, 100, 0.5), 4, 0.5, 0, 2);");



        TextArea titleInput = new TextArea();
        titleInput.setPromptText("Titre de l'exercice");
        titleInput.setStyle(
            "-fx-control-inner-background: rgba(20, 20, 20, 0.9); " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-prompt-text-fill: #BBBBBB; " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
        );

        TextArea questionInput = new TextArea();
        questionInput.setPromptText("Question de l'exercice");
        questionInput.setStyle(
            "-fx-control-inner-background: rgba(20, 20, 20, 0.9); " +
            "-fx-text-fill: #FFFFFF;  " +
            "-fx-prompt-text-fill: #BBBBBB; " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
        );

        TextArea difficultyInput = new TextArea();
        difficultyInput.setPromptText("Difficulté de l'exercice (facile, moyen, difficile)");
        difficultyInput.setStyle(
            "-fx-control-inner-background: rgba(20, 20, 20, 0.9); " +
            "-fx-text-fill: #FFFFFF;  " +
            "-fx-prompt-text-fill: #BBBBBB; " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
        );

        // Ajouter des cases à cocher pour choisir les langages
        CheckBox pythonCheckBox = new CheckBox("Python");
        pythonCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100,100,100,0.5), 4, 0.5, 0, 2);");
        CheckBox javaCheckBox = new CheckBox("Java");
        javaCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100,100,100,0.5), 4, 0.5, 0, 2);");
        CheckBox CCheckBox = new CheckBox("C");
        CCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100,100,100,0.5), 4, 0.5, 0, 2);");
        CheckBox jsCheckBox = new CheckBox("JavaScript");
        jsCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100,100,100,0.5), 4, 0.5, 0, 2);");
        CheckBox phpCheckBox = new CheckBox("PHP");
        phpCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100,100,100,0.5), 4, 0.5, 0, 2);");

        HBox languageSelectionBox = new HBox(10, pythonCheckBox, javaCheckBox, CCheckBox, jsCheckBox, phpCheckBox);
        languageSelectionBox.setAlignment(Pos.CENTER);
        languageSelectionBox.setStyle("-fx-padding: 10px;");

        Button saveButton = new Button("Enregistrer");
        saveButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        cancelButton.setOnAction(event -> primaryStage.setScene(mainScene)); // Retour à la scène principale


        // Ajouter une zone de sortie (console)
        // Déclarer et configurer outputArea une seule fois
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 0.9); " +
                            "-fx-text-fill: #00FF00; " +  // Couleur verte comme un terminal
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
                            "-fx-border-radius: 15px; " +
                            "-fx-background-radius: 15px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        outputArea.setPrefHeight(200); // Hauteur préférée

        // Créer un BorderPane pour la scène de correction
        BorderPane correctionRoot = new BorderPane();
        VBox correctionBox = new VBox(10);
        correctionBox.setAlignment(Pos.CENTER);
        correctionBox.setStyle("-fx-padding: 20px; -fx-background-color: #1E1E1E;");

        Label correctionLabel = new Label("Correction en Python :");
        correctionLabel.setStyle("-fx-text-fill: white;");

        TextArea correctionInput = new TextArea();
        correctionInput.setPromptText("Entrez la correction en Python pour cet exercice");
        correctionInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #BBBBBB; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        Button saveCorrectionButton = new Button("Enregistrer la correction");
        saveCorrectionButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        Button backToAddExerciseButton = new Button("Retour");
        backToAddExerciseButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        backToAddExerciseButton.setOnAction(event -> primaryStage.setScene(addExerciseScene)); // Retour à la scène d'ajout d'exercice

        HBox correctionButtonBox = new HBox(10, backToAddExerciseButton, saveCorrectionButton);
        correctionButtonBox.setAlignment(Pos.CENTER);

        correctionBox.getChildren().addAll(correctionLabel, correctionInput, correctionButtonBox);
        correctionRoot.setCenter(correctionBox);

        VBox yes = new VBox(10, correctionRoot, outputArea);

        Scene correctionStage = new Scene(yes, 600, 400);

        // Modifier l'action du bouton "Enregistrer" pour aller à la scène de correction
        saveButton.setOnAction(event -> {
            String title = titleInput.getText();
            String question = questionInput.getText();
            String difficulty = difficultyInput.getText();
            boolean isPythonSelected = pythonCheckBox.isSelected();
            boolean isJavaSelected = javaCheckBox.isSelected();
            boolean isCSelected = CCheckBox.isSelected();
            boolean isJSSelected = jsCheckBox.isSelected();
            boolean isPHPSelected = phpCheckBox.isSelected();

            if (!title.isEmpty() && !question.isEmpty() && !difficulty.isEmpty() && (isPythonSelected || isCSelected || isJavaSelected || isJSSelected || isPHPSelected)) {
                if (Connexionbdd.isTitleExists(title)) {
                    System.err.println("Un exercice avec ce titre existe déjà. Veuillez choisir un autre titre.");
                } else {
                    // Transition vers la scène de correction
                    primaryStage.setScene(correctionStage);

                    saveCorrectionButton.setOnAction(e -> {
                        String correction = correctionInput.getText();
                        PythonExecuteCode pythonExecuteCode = new PythonExecuteCode(outputArea);
                        outputArea.setText("");
                        if (!correction.isEmpty() && pythonExecuteCode.verification(correction)) {
                            try {
                                // Ajouter l'exercice à la base de données
                                int exerciseId = Connexionbdd.addExercise(title, question, difficulty);

                                // Ajouter les langages sélectionnés à la base de données
                                if (isPythonSelected) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "Python");
                                }
                                if (isJavaSelected) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "Java");
                                }
                                if (isCSelected) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "C");
                                }
                                if (isJSSelected) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "JavaScript");
                                }
                                if (isPHPSelected) {
                                    Connexionbdd.addLanguageToExercise(exerciseId, "PHP");
                                }

                                // Sauvegarder la correction dans un fichier
                                File exerciceFile = new File("src/main/resources/Correction/Exercice" + exerciseId + ".py");
                                if (exerciceFile.exists()) {
                                    exerciceFile.delete();
                                }
                                if (!exerciceFile.createNewFile()) {
                                    throw new IOException("Erreur lors de la création du fichier : " + exerciceFile.getAbsolutePath());
                                }

                                FileWriter fileWriter = new FileWriter(exerciceFile);
                                fileWriter.write(correction);
                                fileWriter.close();
                                // Mettre à jour la liste des exercices
                                exerciseList.getItems().clear();
                                for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
                                    String titre = Connexionbdd.getExerciceTitle(i);
                                    Label exerciseNumber = new Label("Exercice " + i);
                                    exerciseNumber.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                                    Label exerciseTitle = new Label(titre);
                                    exerciseTitle.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");
                                    int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
                                    int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis
                                    Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                                    statsLabel.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");

                                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, statsLabel);
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

                                // Décocher toutes les cases de filtrage
                                filterPythonCheckBox.setSelected(false);
                                filterJavaCheckBox.setSelected(false);
                                filterCCheckBox.setSelected(false);
                                filterJSCheckBox.setSelected(false);
                                filterPHPCheckBox.setSelected(false);

                                // Retourner à la scène principale
                                primaryStage.setScene(mainScene);
                            } catch (IOException ex) {
                                System.err.println("Erreur lors de l'enregistrement de la correction : " + ex.getMessage());
                            }
                        } else {
                            System.err.println("La correction ne peut pas être vide ou invalide.");
                        }
                    });
                }
            } else {
                System.err.println("Veuillez remplir tous les champs et sélectionner au moins une langue.");
            }
        });

        HBox buttonBoxAdd = new HBox(10, cancelButton, saveButton);
        buttonBoxAdd.setAlignment(Pos.CENTER);

        addExerciseBox.getChildren().addAll(addExerciseLabel, titleInput, questionInput, difficultyInput, languageSelectionBox, buttonBoxAdd);
        addExerciseRoot.setCenter(addExerciseBox);


        // Ajouter un bouton "-" pour supprimer des exercices
        Button removeButton = new Button("-");
        removeButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6666, #ff3333); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-min-width: 50px; -fx-min-height: 50px; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.5), 6, 0.5, 0, 2);");
        removeButton.setOnMouseEntered(e -> removeButton.setStyle("-fx-background-color: linear-gradient(to right, #ff3333, #ff6666); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-min-width: 50px; -fx-min-height: 50px; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.8), 8, 0.5, 0, 2);"));
        removeButton.setOnMouseExited(e -> removeButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6666, #ff3333); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-min-width: 50px; -fx-min-height: 50px; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.5), 6, 0.5, 0, 2);"));

        // Nouvelle scène pour supprimer un exercice
        BorderPane removeExerciseRoot = new BorderPane();
        VBox removeExerciseBox = new VBox(10);
        removeExerciseBox.setAlignment(Pos.CENTER);
        removeExerciseBox.setStyle("-fx-background-color: rgba(10, 10, 10, 0.95); -fx-padding: 25px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");

        Label removeExerciseLabel = new Label("Supprimer un exercice");
        removeExerciseLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100,100,100,0.5), 4, 0.5, 0, 2);");

        // Liste déroulante pour sélectionner l'exercice à supprimer
        ComboBox<String> exerciseSelector = new ComboBox<>();
        exerciseSelector.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");
        for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
            String titre = Connexionbdd.getExerciceTitle(i);
            exerciseSelector.getItems().add("Exercice " + i + ": " + titre);
        }

        // Boutons pour confirmer ou annuler la suppression
        Button confirmRemoveButton = new Button("Confirmer");
        confirmRemoveButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6666, #ff3333); -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.5), 6, 0.5, 0, 2);");

        Button cancelRemoveButton = new Button("Annuler");
        cancelRemoveButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        cancelRemoveButton.setOnAction(event -> primaryStage.setScene(mainScene)); // Retour à la scène principale

        confirmRemoveButton.setOnAction(event -> {
            String selectedExercise = exerciseSelector.getValue();
            if (selectedExercise != null) {
                int exerciseId = Integer.parseInt(selectedExercise.split(" ")[1].replace(":", ""));
                Connexionbdd.deleteExercise(exerciseId); // Suppression de l'exercice dans la base de données
                exerciseList.getItems().removeIf(item -> {
                    Label label = (Label) item.getChildren().get(0);
                    return label.getText().equals("Exercice " + exerciseId);
                });
                exerciseSelector.getItems().remove(selectedExercise); // Suppression de l'exercice de la liste déroulante
                primaryStage.setScene(mainScene); // Retour à la scène principale
            } else {
                System.err.println("Veuillez sélectionner un exercice à supprimer.");
            }
        });

        HBox buttonBoxRemove = new HBox(10, cancelRemoveButton, confirmRemoveButton);
        buttonBoxRemove.setAlignment(Pos.CENTER);

        removeExerciseBox.getChildren().addAll(removeExerciseLabel, exerciseSelector, buttonBoxRemove);
        removeExerciseRoot.setCenter(removeExerciseBox);

        Scene removeExerciseScene = new Scene(removeExerciseRoot, 600, 400);

        removeButton.setOnAction(event -> primaryStage.setScene(removeExerciseScene));

        // Ajouter le bouton "-" à gauche du bouton "+"
        HBox topBar = new HBox(10, removeButton, addButton);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-padding: 15px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");
        mainRoot.setTop(topBar);

        // Fenêtre secondaire (zone de code)
        BorderPane secondaryRoot = new BorderPane();
        Color backgroundColorSecondary = Color.web("#1E1E1E");

        // Ajouter une zone de texte pour afficher la consigne
        TextArea instructionArea = new TextArea();
        instructionArea.setEditable(false);
        instructionArea.setWrapText(true);
        instructionArea.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.95); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        instructionArea.setPrefWidth(400); // Largeur préférée pour la consigne

        // Ajouter une zone de texte pour écrire du code
        TextArea codeArea = new TextArea();
        codeArea.setStyle("-fx-control-inner-background: rgba(10, 10, 10, 0.95); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        codeArea.setWrapText(false);

        
        // Réutiliser outputArea sans reconfigurer
        javafx.scene.control.SplitPane codeAndConsoleSplitPane = new javafx.scene.control.SplitPane();
        codeAndConsoleSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        codeAndConsoleSplitPane.getItems().addAll(codeArea, outputArea);
        codeAndConsoleSplitPane.setDividerPositions(0.7);

        // Créer un SplitPane pour diviser la consigne et la zone de code/console
        javafx.scene.control.SplitPane instructionAndCodeSplitPane = new javafx.scene.control.SplitPane();
        instructionAndCodeSplitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        instructionAndCodeSplitPane.getItems().addAll(instructionArea, codeAndConsoleSplitPane);
        instructionAndCodeSplitPane.setDividerPositions(0.3);

        // Ajouter le SplitPane au centre de la fenêtre secondaire
        secondaryRoot.setCenter(instructionAndCodeSplitPane);

        // Ajouter un label pour afficher le nombre d'essais
        Text attemptsLabel = new Text();
        attemptsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Ajouter un label pour afficher le nombre d'essais réussis
        Text successfulTriesLabel = new Text();
        successfulTriesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Ajouter un bouton pour exécuter le code
        Button runButton = new Button("Exécuter");
        runButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        // Ajouter un bouton pour revenir à la liste des exercices
        Button backButton = new Button("Retour à la liste des exercices");
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        backButton.setOnAction(event -> primaryStage.setScene(mainScene)); // Revenir à la scène principale

        ComboBox<String> languageSelector = new ComboBox<>();

        // Réorganiser les composants dans le HBox
        HBox buttonBox = new HBox(10, attemptsLabel, successfulTriesLabel, languageSelector, runButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-padding: 15px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");
        
        
        // Ajouter le bouton et le label en bas
        secondaryRoot.setBottom(buttonBox);

        // Créer une scène pour la fenêtre secondaire
        Scene secondaryScene = new Scene(secondaryRoot, 1280, 720, backgroundColorSecondary);

        // Gestion de l'événement de clic sur un exercice
        exerciseList.setOnMouseClicked(event -> {
            HBox selectedItem = exerciseList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Récupérer le numéro de l'exercice à partir du premier label dans l'HBox
                Label exerciseNumberLabel = (Label) selectedItem.getChildren().get(0);
                String exerciseNumberText = exerciseNumberLabel.getText().replace("Exercice ", "").trim();
                int selectedExo;

                try {
                    selectedExo = Integer.parseInt(exerciseNumberText); // Convertir en entier
                } catch (NumberFormatException e) {
                    System.err.println("Erreur : Impossible de convertir le numéro de l'exercice. Texte : " + exerciseNumberText);
                    return; // Arrêter si le numéro est invalide
                }

                // Récupérer et afficher la consigne
                String consigne = Connexionbdd.showConsigne(selectedExo);
                instructionArea.setText(consigne);

                // Récupérer les langages disponibles pour l'exercice sélectionné
                languageSelector.getItems().clear();
                languageSelector.getItems().addAll(Connexionbdd.getAvailableLanguages(selectedExo));

                // Définir le premier langage disponible comme valeur par défaut
                if (!languageSelector.getItems().isEmpty()) {
                    languageSelector.setValue(languageSelector.getItems().get(0));
                }

                // Mettre à jour la zone de code en fonction du langage sélectionné
                String language = languageSelector.getValue();
                if (language.equals("Python")) {
                    codeArea.setText("word = input()\n\nprint(word)");
                } 
                else if (language.equals("Java")) {
                    codeArea.setText(
                        "import java.util.Scanner;\n\n" +
                        "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Scanner sc = new Scanner(System.in);\n" +
                        "        String word = sc.nextLine();\n" +
                        "        System.out.println(word);\n" +
                        "    }\n" +
                        "}"
                    );
                }
                else if (language.equals("C")) {
                    codeArea.setText(
                        "#include <stdio.h>\n\n" +
                        "int main() {\n" +
                        "    char word[100];\n" +
                        "    scanf(\"%s\", word);\n" +
                        "    printf(\"%s\", word);\n" +
                        "    return 0;\n" +
                        "}"
                    );
                }
                else if (language.equals("JavaScript")) {
                    codeArea.setText(
                        "const readline = require('readline');\n" +
                        "const rl = readline.createInterface({\n" +
                        "  input: process.stdin,\n" +
                        "  output: process.stdout\n" +
                        "});\n" +
                        "\n" +
                        "rl.question('', (word) => {\n" +
                        "  console.log(word);\n" +
                        "  rl.close();\n" +
                        "});"
                    );
                }
                else if (language.equals("PHP")) {
                    codeArea.setText(
                        "<?php\n" +
                        "$word = trim(fgets(STDIN));\n" +
                        "echo $word;\n"
                    );
                }

                // Récupérer et afficher le nombre d'essais
                int attempts = Connexionbdd.getExerciseAttempts(selectedExo);
                attemptsLabel.setText("Nombre d'essais : " + attempts);
                attemptsLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");

                // Récupérer et afficher le nombre d'essais réussis
                int successfulTries = Connexionbdd.getSuccessfulTries(selectedExo);
                successfulTriesLabel.setText("Nombre d'essais réussis : " + successfulTries);
                successfulTriesLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");

                primaryStage.setScene(secondaryScene); // Basculer vers la scène secondaire
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

            // Incrémenter le nombre d'essais dans la base de données
            Connexionbdd.incrementExerciseAttempts(id);

            // Vérification de l'exactitude de la réponse
            if(outputArea.getText().contains("Le code est correct")){
                Connexionbdd.incrementSuccessfulTries(id);
            }

            // Mettre à jour l'affichage du nombre d'essais
            int updatedAttempts = Connexionbdd.getExerciseAttempts(id);
            attemptsLabel.setText("Nombre d'essais : " + updatedAttempts);
            attemptsLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");

            // Mettre à jour la liste des exercices
           int updatedSuccessfulTries = Connexionbdd.getSuccessfulTries(id);
            successfulTriesLabel.setText("Nombre d'essais réussis : " + updatedSuccessfulTries);
            successfulTriesLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");
        });

        languageSelector.setOnAction(event -> {
            String selectedLanguage = languageSelector.getValue();
            String consigne = Connexionbdd.showConsigne(exerciseList.getSelectionModel().getSelectedIndex() + 1);
            if (selectedLanguage == null) {
                return; // Ne rien faire si aucun langage n'est sélectionné
            }
            if (selectedLanguage.equals("Python")) {
                codeArea.setText("word = input()\n\nprint(word)");
            } 
            else if (selectedLanguage.equals("Java")) {
                codeArea.setText(
                    "import java.util.Scanner;\n\n" +
                    "public class Main {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        Scanner sc = new Scanner(System.in);\n" +
                    "        String word = sc.nextLine();\n" +
                    "        System.out.println(word);\n" +
                    "    }\n" +
                    "}"
                );
            }
            else if (selectedLanguage.equals("C")) {
                codeArea.setText(
                    "#include <stdio.h>\n\n" +
                    "int main() {\n" +
                    "    char word[100];\n" +
                    "    scanf(\"%s\", word);\n" +
                    "    printf(\"%s\", word);\n" +
                    "    return 0;\n" +
                    "}"
                );
            }
            else if (selectedLanguage.equals("JavaScript")) {
                codeArea.setText(
                    "const readline = require('readline');\n" +
                    "const rl = readline.createInterface({\n" +
                    "  input: process.stdin,\n" +
                    "  output: process.stdout\n" +
                    "});\n" +
                    "\n" +
                    "rl.question('', (word) => {\n" +
                    "  console.log(word);\n" +
                    "  rl.close();\n" +
                    "});"
                );
            }
            else if (selectedLanguage.equals("PHP")) {
                codeArea.setText(
                    "<?php\n" +
                    "$word = trim(fgets(STDIN));\n" +
                    "echo $word;\n"
                );
            }
        });

        // Configurer et afficher la fenêtre principale
        primaryStage.setTitle("Liste des Exercices");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}