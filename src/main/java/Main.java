import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
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

        int maxExo = Connexionbdd.maxexo();

        // Fenêtre principale (liste des exercices)
        BorderPane mainRoot = new BorderPane();
        Color backgroundColorMain = Color.web("#1E1E1E");

        // Titre de la page
        Label titleLabel = new Label("Le codyngame de la javadocance");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        // Description
        Label descriptionLabel = new Label("Bienvenue sur notre codyngame, veuillez choisir un exercice. Bon codage!");
        descriptionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        // Créer une liste d'exercices
        ListView<HBox> exerciseList = new ListView<>();
        exerciseList.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        for (int i = 1; i <= maxExo; i++) {
            String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
            Label exerciseNumber = new Label("Exercice " + i);
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

        // Organiser les composants dans un VBox
        VBox contentBox = new VBox(10, titleLabel, descriptionLabel, exerciseList);
        contentBox.setStyle("-fx-background-color: rgba(10, 10, 10, 0.95); -fx-padding: 25px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");
        contentBox.setAlignment(Pos.CENTER);

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

        Scene correctionStage = new Scene(correctionRoot, 600, 400);

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
                        if (!correction.isEmpty()) {
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
                                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle);
                                    exerciseItem.setSpacing(10);
                                    exerciseList.getItems().add(exerciseItem);
                                }

                                // Retourner à la scène principale
                                primaryStage.setScene(mainScene);
                            } catch (IOException ex) {
                                System.err.println("Erreur lors de l'enregistrement de la correction : " + ex.getMessage());
                            }
                        } else {
                            System.err.println("La correction ne peut pas être vide.");
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
        for (int i = 1; i <= maxExo; i++) {
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

        // Ajouter une zone de texte pour écrire du code
        TextArea codeArea = new TextArea();
        codeArea.setStyle("-fx-control-inner-background: rgba(10, 10, 10, 0.95); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        codeArea.setWrapText(false);

        // Ajouter un label pour afficher le nombre d'essais
        Text attemptsLabel = new Text();
        attemptsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Ajouter un bouton pour exécuter le code
        Button runButton = new Button("Exécuter");
        runButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        // Ajouter un bouton pour revenir à la liste des exercices
        Button backButton = new Button("Retour à la liste des exercices");
        backButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        backButton.setOnAction(event -> primaryStage.setScene(mainScene)); // Revenir à la scène principale

        ComboBox<String> languageSelector = new ComboBox<>();

        // Ajouter une boîte pour organiser les composants
        VBox codeBox = new VBox(10, codeArea, attemptsLabel);
        codeBox.setStyle("-fx-background-color: #1E1E1E;");
        VBox.setVgrow(codeArea, javafx.scene.layout.Priority.ALWAYS);

        HBox buttonBox = new HBox(10, backButton, languageSelector, runButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-padding: 15px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");

        secondaryRoot.setCenter(codeBox);
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
                    codeArea.setText("# " + consigne + "\n\nword = input()\n\nprint(word)");
                } 
                else if (language.equals("Java")) {
                    codeArea.setText(
                        "/* " + consigne + " */\n\n" +
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
                        "/* " + consigne + " */\n\n" +
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
                    codeArea.setText("// " + consigne + "\n\nconst readline = require('readline');\n" + //
                                                "const rl = readline.createInterface({\n" + //
                                                "  input: process.stdin,\n" + //
                                                "  output: process.stdout\n" + //
                                                "});\n" + //
                                                "\n" + //
                                                "// Lire une ligne d'entrée\n" + //
                                                "rl.question('', (word) => {\n" + //
                                                "  // Afficher la saisie\n" + //
                                                "  console.log(word);\n" + //
                                                "  \n" + //
                                                "  // Fermer l'interface readline\n" + //
                                                "  rl.close();\n" + //
                                                "});");
                }
                else if (language.equals("PHP")) {
                    codeArea.setText("<?php\n" +
                                     "// " + consigne + "\n\n" +
                                     "$word = trim(fgets(STDIN));\n" +
                                     "echo $word;\n");
                }

                // Récupérer et afficher le nombre d'essais
                int attempts = Connexionbdd.getExerciseAttempts(selectedExo);
                attemptsLabel.setText("Nombre d'essais : " + attempts);
                attemptsLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");

                primaryStage.setScene(secondaryScene); // Basculer vers la scène secondaire
            }
        });

        runButton.setOnAction(event -> {
            String code = codeArea.getText();
            String language = languageSelector.getValue();
            HBox selectedItem = exerciseList.getSelectionModel().getSelectedItem();
            Label exerciseNumberLabel = (Label) selectedItem.getChildren().get(0);
            String exerciseNumberText = exerciseNumberLabel.getText().replace("Exercice ", "").trim();
            int id = Integer.parseInt(exerciseNumberText); 
            IDEExecuteCode executor = LanguageChoice.choice(language);
            executor.executeCode(code, id);

            // Incrémenter le nombre d'essais dans la base de données
            Connexionbdd.incrementExerciseAttempts(id);

            // Mettre à jour l'affichage du nombre d'essais
            int updatedAttempts = Connexionbdd.getExerciseAttempts(id);
            attemptsLabel.setText("Nombre d'essais : " + updatedAttempts);
            attemptsLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");
        });

        languageSelector.setOnAction(event -> {
            String selectedLanguage = languageSelector.getValue();
            String consigne = Connexionbdd.showConsigne(exerciseList.getSelectionModel().getSelectedIndex() + 1);
            if (selectedLanguage == null) {
                return; // Ne rien faire si aucun langage n'est sélectionné
            }
            if (selectedLanguage.equals("Python")) {
                codeArea.setText("# " + consigne + "\n\nword = input()\n\nprint(word)");
            } 
            else if (selectedLanguage.equals("Java")) {
                codeArea.setText(
                    "/* " + consigne + " */\n\n" +
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
                    "/* " + consigne + " */\n\n" +
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
                codeArea.setText("// " + consigne + "\n\nconst readline = require('readline');\n" + //
                                        "const rl = readline.createInterface({\n" + //
                                        "  input: process.stdin,\n" + //
                                        "  output: process.stdout\n" + //
                                        "});\n" + //
                                        "\n" + //
                                        "// Lire une ligne d'entrée\n" + //
                                        "rl.question('', (word) => {\n" + //
                                        "  // Afficher la saisie\n" + //
                                        "  console.log(word);\n" + //
                                        "  \n" + //
                                        "  // Fermer l'interface readline\n" + //
                                        "  rl.close();\n" + //
                                        "});");
            }
            else if (selectedLanguage.equals("PHP")) {
                codeArea.setText("<?php\n" +
                                 "// " + consigne + "\n\n" +
                                 "$word = trim(fgets(STDIN));\n" +
                                 "echo $word;\n");
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