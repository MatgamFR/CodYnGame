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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class Main extends Application {
    ListView<HBox> exerciseList;
    CheckBox filterPythonCheckBox;
    CheckBox filterCCheckBox;
    CheckBox filterJavaCheckBox;
    CheckBox filterJSCheckBox;
    CheckBox filterPHPCheckBox;
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

    public void setupSearchButton(Button searchButton, VBox overlay, StackPane rootPane) {
        searchButton.setOnAction(event -> {
            rootPane.getChildren().remove(overlay);
            filterPythonCheckBox.setSelected(false);
            filterJavaCheckBox.setSelected(false);
            filterCCheckBox.setSelected(false);
            filterJSCheckBox.setSelected(false);
            filterPHPCheckBox.setSelected(false);
    
            // Récupérer les langages sélectionnés
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
    
            // Filtrer les exercices
            List<Integer> filteredExerciseIds = Connexionbdd.getExercisesByLanguages(selectedLanguages);
            if (filteredExerciseIds.isEmpty()) {
                exerciseList.getItems().clear();
                int maxExobis = Connexionbdd.maxexo();
                for (int i = 1; i <= maxExobis; i++) {
                    String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
                    String difficulty = Connexionbdd.getExerciceDifficulty(i); // Récupérer la difficulté de l'exercice
                    int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
                    int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis
                    String typeExo = Connexionbdd.getTypeExo(i); // Récupérer le type de l'exercice
        
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
                    HBox.setHgrow(spacer, Priority.ALWAYS); // Pousse le type d'exercice à droite

                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, statsLabel, spacer, typeLabel);
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
            } else {
                exerciseList.getItems().clear();
                for (int id : filteredExerciseIds) {
                    String titre = Connexionbdd.getExerciceTitle(id);
                    int attempts = Connexionbdd.getExerciseAttempts(id);
                    int successfulTries = Connexionbdd.getSuccessfulTries(id);
                    String typeExo = Connexionbdd.getTypeExo(id); // Récupérer le type de l'exercice

                    Label exerciseNumber = new Label("Exercice " + id);
                    exerciseNumber.setStyle("-fx-font-size: 20px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                    Label exerciseTitle = new Label(titre);
                    exerciseTitle.setStyle("-fx-font-size: 20px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                    Label statsLabel = new Label("Essais : " + attempts + " | Réussis : " + successfulTries);
                    statsLabel.setStyle("-fx-font-size: 20px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
                    Label typeLabel = new Label(typeExo);
                    typeLabel.setStyle("-fx-font-size: 20px;-fx-padding: 23px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS); // Pousse le type d'exercice à droite

                    HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle, statsLabel, spacer, typeLabel);
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

        // Charger les polices personnalisées
        String pixelGamePath = getClass().getResource("/RessourceFonts/Pixel.otf").toExternalForm();
        
        // Charger et enregistrer les polices
        Font.loadFont(pixelGamePath, 32); // Charger Pixel Game


        // Titre de la page
        Label titleLabel = new Label("Liste d'exercices");
        titleLabel.setStyle("-fx-font-size: 70px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

        // Description
        Label descriptionLabel = new Label("Veuillez choisir un exercice. Bon codage!");
        descriptionLabel.setStyle("-fx-font-size: 39px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

        VBox titleBox = new VBox(0, titleLabel, descriptionLabel); // Espacement vertical à 0
        titleBox.setAlignment(Pos.CENTER); // Centrer les labels

        
        // Créer une liste d'exercices
        ListView<HBox> exerciseList = new ListView<>();
        exerciseList.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc);");
        
        // Fixer la taille de exerciseList
        exerciseList.setPrefWidth(1300); // Largeur préférée
        exerciseList.setMinWidth(1300);  // Largeur minimale
        exerciseList.setMaxWidth(1300);  // Largeur maximale

        
        for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
            String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
            String difficulty = Connexionbdd.getExerciceDifficulty(i); // Récupérer la difficulté de l'exercice
            int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
            int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis
            String typeExo = Connexionbdd.getTypeExo(i); // Récupérer le type de l'exercice

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
            HBox.setHgrow(spacer, Priority.ALWAYS); // Pousse le type d'exercice à droite

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

        // Organiser les composants dans un VBox
        VBox contentBox = new VBox(10, titleBox, exerciseList);
        contentBox.setAlignment(Pos.CENTER);

        return contentBox;

        
    }

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

        // Créer la scène de la page d'accueil
        BorderPane homePageRoot = new BorderPane();
        // Charger les polices personnalisées
        String PixelPath = getClass().getResource("/RessourceFonts/Pixel.otf").toExternalForm();
        // Charger et enregistrer les polices
        Font.loadFont(PixelPath, 32); // Charger Pixel Game

        Label welcomeLabel = new Label("Le codyngame \n         de la \njavadocance");
        welcomeLabel.setStyle("-fx-font-size: 93px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

        Label descriptionLabel = new Label("Tentez de braver \n   nos farouches \n exercices si vous \n\t  l'osez!");
        descriptionLabel.setStyle("-fx-font-size: 35px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");

        // Obtenir le chemin absolu du répertoire de base du projet
        String basePath = new File("").getAbsolutePath(); // Chemin absolu du projet
        String imagePath = basePath + "/src/main/resources/RessourceImage/play.png"; // Chemin relatif à partir du projet
        String backgroundPath = basePath + "/src/main/resources/RessourceImage/background.png"; // Chemin relatif à partir du projet
        String addPath = basePath + "/src/main/resources/RessourceImage/add.png"; // Chemin relatif à partir du projet
        String filterPath = basePath + "/src/main/resources/RessourceImage/filter.png"; // Chemin relatif à partir du projet
        String homePath = basePath + "/src/main/resources/RessourceImage/home.png"; // Chemin relatif à partir du projet
        String searchPath = basePath + "/src/main/resources/RessourceImage/search.png"; // Chemin relatif à partir du projet
        String fermerPath = basePath + "/src/main/resources/RessourceImage/fermer.png"; // Chemin relatif à partir du projet
        String returnPath = basePath + "/src/main/resources/RessourceImage/return.png"; // Chemin relatif à partir du projet
        String executePath = basePath + "/src/main/resources/RessourceImage/execute.png"; // Chemin relatif à partir du projet
        
        // Charger l'image en utilisant le chemin absolu
        File imageFile = new File(imagePath);
        File backgroundFile = new File(backgroundPath);
        File addFile = new File(addPath);
        File filterFile = new File(filterPath);
        File homeFile = new File(homePath);
        File searchFile = new File(searchPath);
        File fermerFile = new File(fermerPath);
        File returnFile = new File(returnPath);
        File executeFile = new File(executePath);

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
        else {
        // Appliquer l'image de fond au conteneur principal
        homePageRoot.setStyle(
        "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
        "-fx-background-size: cover; " + // Ajuster l'image pour couvrir tout le conteneur
        "-fx-background-position: center center; " + // Centrer l'image
        "-fx-background-repeat: no-repeat;" // Ne pas répéter l'image
        );


        Image image = new Image(imageFile.toURI().toString());
        Image searchImage = new Image(searchFile.toURI().toString());
        Image fermerImage = new Image(fermerFile.toURI().toString());
        ImageView imageView = new ImageView(image);
        ImageView searchImageView = new ImageView(searchImage);
        ImageView fermerImageView = new ImageView(fermerImage);

        
        // Configurer l'ImageView
        imageView.setFitWidth(240); // Largeur de l'image
        imageView.setFitHeight(240); // Hauteur de l'image
        imageView.setPreserveRatio(true); // Préserver les proportions

        // Créer un bouton avec l'image
        Button goToExercisesButton = new Button();
        goToExercisesButton.setGraphic(imageView); // Ajouter l'image au bouton
        goToExercisesButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
        // Ajouter les autres éléments au centre du BorderPane
        VBox centerContent = new VBox(20, welcomeLabel, descriptionLabel, goToExercisesButton);
        centerContent.setAlignment(Pos.CENTER);
        homePageRoot.setCenter(centerContent);
        Scene homePageScene = new Scene(homePageRoot, 1600, 900);
        primaryStage.setTitle("Accueil");
        primaryStage.setScene(homePageScene);
        primaryStage.show();
        

        // Fenêtre principale (liste des exercices)
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle(
            "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
            "-fx-background-size: cover; " + // Ajuster l'image pour couvrir tout le conteneur
            "-fx-background-position: center center; " + // Centrer l'image
            "-fx-background-repeat: no-repeat;" // Ne pas répéter l'image
            );

        VBox contentBox = mainScene();
        
        // Ajouter le contenu au centre de la fenêtre principale
        mainRoot.setCenter(contentBox);
        // Ajouter mainRoot dans un StackPane
        StackPane rootPane = new StackPane(mainRoot);
        // Créer une scène pour la fenêtre principale
        Scene mainScene = new Scene(rootPane, 1600, 900);

        goToExercisesButton.setOnAction(event -> {
            // Basculer vers la scène principale (liste des exercices)
            primaryStage.setScene(mainScene);
        });
        // Ajouter un bouton "+" pour ajouter des exercices
        Image addImage = new Image(addFile.toURI().toString());
        ImageView addImageView = new ImageView(addImage);
        Button addButton = new Button();
        addButton.setGraphic(addImageView); // Ajouter l'image au bouton
        addButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
        
        //Bouton filtrer
        Image filterImage = new Image(filterFile.toURI().toString());
        ImageView filterImageView = new ImageView(filterImage);
        Button filterButton = new Button();
        filterButton.setGraphic(filterImageView); // Ajouter l'image au bouton
        filterButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
        
        //Bouton acceuil
        Image homeImage = new Image(homeFile.toURI().toString());
        ImageView homeImageView = new ImageView(homeImage);
        Button homeButton = new Button();
        homeButton.setGraphic(homeImageView); // Ajouter l'image au bouton
        homeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent

        // Nouvelle scène pour ajouter des exercices
        BorderPane addExerciseRoot = new BorderPane();
        addExerciseRoot.setStyle(
            "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
            "-fx-background-size: cover; " + // Ajuster l'image pour couvrir tout le conteneur
            "-fx-background-position: center center; " + // Centrer l'image
            "-fx-background-repeat: no-repeat;" // Ne pas répéter l'image
            );
        Scene addExerciseScene = new Scene(addExerciseRoot, 1600, 900);
        addButton.setOnAction(event -> primaryStage.setScene(addExerciseScene));
        filterButton.setOnAction(event -> {
            Label titleFilter = new Label("Filtrer: Choisissez les langages");
            titleFilter.setStyle("-fx-font-size: 34px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc);-fx-font-family: 'Pixel Game';");
            Button searchButton = new Button();
            searchButton.setGraphic(searchImageView); // Ajouter l'image au bouton
            searchButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
            searchImageView.setFitWidth(180); // Largeur de l'image
            searchImageView.setFitHeight(180); // Hauteur de l'image
            searchImageView.setPreserveRatio(true); // Préserver les proportions
            // Ajouter les cases à cocher et le bouton de recherche dans une HBox
            HBox filterBox = new HBox(10, filterPythonCheckBox, filterJavaCheckBox, filterCCheckBox, filterJSCheckBox, filterPHPCheckBox);
            filterBox.setAlignment(Pos.CENTER);
            filterBox.setStyle("-fx-padding: 10px;");

            // Créer un fond semi-transparent
            VBox overlay = new VBox();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.83);");
            overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            overlay.setAlignment(Pos.CENTER);
            
            setupSearchButton(searchButton,overlay,rootPane);
            // Ajouter le filterBox au centre
            VBox popupContent = new VBox(5,titleFilter, filterBox);
            popupContent.setAlignment(Pos.CENTER);
            popupContent.setStyle("-fx-padding: 20px; -fx-background-color: rgba(30, 30, 30, 0.9);");
        
            // Ajouter un bouton pour fermer le pop-up
            fermerImageView.setFitWidth(100); // Largeur de l'image
            fermerImageView.setFitHeight(100); // Hauteur de l'image
            Button closeButton = new Button();
            closeButton.setGraphic(new ImageView(fermerImage));
            closeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
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
        
            // Ajouter l'overlay au StackPane
            rootPane.getChildren().add(overlay);
        });
        homeButton.setOnAction(event -> primaryStage.setScene(homePageScene));
        VBox addExerciseBox = new VBox(10);
        addExerciseBox.setAlignment(Pos.CENTER);
        

        Label addExerciseLabel = new Label("Ajouter un nouvel exercice");
        addExerciseLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100, 100, 100, 0.5), 4, 0.5, 0, 2);");



        TextArea titleInput = new TextArea();
        Label titleLabel = new Label("Titre de l'exercice");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100, 100, 100, 0.5), 4, 0.5, 0, 2);");
        titleInput.setStyle(
            "-fx-control-inner-background: rgba(20, 20, 20, 0.9); " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-prompt-text-fill: #BBBBBB; " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
        );

        TextArea questionInput = new TextArea();
        Label questionLabel = new Label("Question de l'exercice");
        questionLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100, 100, 100, 0.5), 4, 0.5, 0, 2);");
        questionInput.setStyle(
            "-fx-control-inner-background: rgba(20, 20, 20, 0.9); " +
            "-fx-text-fill: #FFFFFF;  " +
            "-fx-prompt-text-fill: #BBBBBB; " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);"
        );

        TextArea difficultyInput = new TextArea();
        Label difficultyLabel2 = new Label("Difficulté de l'exercice");
        difficultyLabel2.setStyle("-fx-font-size: 24px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(100, 100, 100, 0.5), 4, 0.5, 0, 2);");
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

        // Ajouter un ComboBox pour choisir le type d'exercice
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("STDIN/STDOUT", "INCLUDE");
        typeComboBox.setValue("STDIN/STDOUT");
        typeComboBox.setStyle(
            "-fx-control-inner-background: rgba(20, 20, 20, 0.9); " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-prompt-text-fill: #BBBBBB; " +
            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); " 
        );
        typeComboBox.setOnAction(event -> {
            // Décochez toutes les cases à cocher dans languageSelectionBox
            pythonCheckBox.setSelected(false);
            javaCheckBox.setSelected(false);
            CCheckBox.setSelected(false);
            jsCheckBox.setSelected(false);
            phpCheckBox.setSelected(false);
        });

        // Ajouter un gestionnaire d'événements pour chaque CheckBox
        pythonCheckBox.setOnAction(event -> {
            if (typeComboBox.getValue().equals("INCLUDE")) {
                javaCheckBox.setSelected(false);
                CCheckBox.setSelected(false);
                jsCheckBox.setSelected(false);
                phpCheckBox.setSelected(false);
            }
        });

        javaCheckBox.setOnAction(event -> {
            if (typeComboBox.getValue().equals("INCLUDE")) {
                pythonCheckBox.setSelected(false);
                CCheckBox.setSelected(false);
                jsCheckBox.setSelected(false);
                phpCheckBox.setSelected(false);
            }
        });

        CCheckBox.setOnAction(event -> {
            if (typeComboBox.getValue().equals("INCLUDE")) {
                pythonCheckBox.setSelected(false);
                javaCheckBox.setSelected(false);
                jsCheckBox.setSelected(false);
                phpCheckBox.setSelected(false);
            }
        });

        jsCheckBox.setOnAction(event -> {
            if (typeComboBox.getValue().equals("INCLUDE")) {
                pythonCheckBox.setSelected(false);
                javaCheckBox.setSelected(false);
                CCheckBox.setSelected(false);
                phpCheckBox.setSelected(false);
            }
        });

        phpCheckBox.setOnAction(event -> {
            if (typeComboBox.getValue().equals("INCLUDE")) {
                pythonCheckBox.setSelected(false);
                javaCheckBox.setSelected(false);
                CCheckBox.setSelected(false);
                jsCheckBox.setSelected(false);
            }
        });

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
        outputArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 1); " + 
                            "-fx-text-fill: #00FF00; " + // Couleur verte comme un terminal 
                            "-fx-font-family: 'Monospace'; " + 
                            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc);" ); 

        TextArea correctionArea = new TextArea();
        correctionArea.setEditable(false);
        correctionArea.setWrapText(true);
        correctionArea.setStyle("-fx-control-inner-background: rgba(0, 0, 0, 0.9); " +
                            "-fx-text-fill: #00FF00; " +  // Couleur verte comme un terminal
                            "-fx-font-family: 'Monospace'; " +
                            "-fx-border-color: linear-gradient(to right, #ffffff, #cccccc); ");
        correctionArea.setPrefHeight(200); // Hauteur préférée

        // Créer un BorderPane pour la scène de correction
        BorderPane correctionRoot = new BorderPane();
        VBox correctionBox = new VBox(10);
        correctionBox.setAlignment(Pos.CENTER);
        correctionBox.setStyle("-fx-padding: 20px; -fx-background-color: #1E1E1E;");

        Label correctionLabel = new Label("Correction en Python :");
        correctionLabel.setStyle("-fx-text-fill: white;");

        CodeArea correctionInput = SyntaxicalColor.createCodeArea();
        correctionInput.setParagraphGraphicFactory(LineNumberFactory.get(correctionInput));
        correctionInput.setPlaceholder(new Label("Entrez la correction en Python pour cet exercice"));

        Button saveCorrectionButton = new Button("Enregistrer la correction");
        saveCorrectionButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        Button backToAddExerciseButton = new Button("Retour");
        backToAddExerciseButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        backToAddExerciseButton.setOnAction(event -> primaryStage.setScene(addExerciseScene)); // Retour à la scène d'ajout d'exercice

        HBox correctionButtonBox = new HBox(10, backToAddExerciseButton, saveCorrectionButton);
        correctionButtonBox.setAlignment(Pos.CENTER);

        correctionBox.getChildren().addAll(correctionLabel, correctionInput, correctionButtonBox);
        correctionRoot.setCenter(correctionBox);

        VBox yes = new VBox(10, correctionRoot, correctionArea);

        Scene correctionStage = new Scene(yes, 1600, 900);

        // Modifier l'action du bouton "Enregistrer" pour aller à la scène de correction
        saveButton.setOnAction(event -> {
            String title = titleInput.getText();
            String question = questionInput.getText();
            String difficulty = difficultyInput.getText();
            String type = typeComboBox.getValue(); // Récupérer le type sélectionné
            boolean isPythonSelected = pythonCheckBox.isSelected();
            boolean isJavaSelected = javaCheckBox.isSelected();
            boolean isCSelected = CCheckBox.isSelected();
            boolean isJSSelected = jsCheckBox.isSelected();
            boolean isPHPSelected = phpCheckBox.isSelected();

            if (!title.isEmpty() && !question.isEmpty() && !difficulty.isEmpty() && type != null &&
                (isPythonSelected || isCSelected || isJavaSelected || isJSSelected || isPHPSelected)) {
                if (Connexionbdd.isTitleExists(title)) {
                    System.err.println("Un exercice avec ce titre existe déjà. Veuillez choisir un autre titre.");
                } else {
                    correctionInput.replaceText("word = input().replace('\\\\n', '\\n').split('\\n')");
                    // Transition vers la scène de correction
                    primaryStage.setScene(correctionStage);

                    saveCorrectionButton.setOnAction(e -> {
                        String correction = correctionInput.getText();
                        PythonExecuteCode pythonExecuteCode = new PythonExecuteCode(correctionArea);
                        correctionArea.setText("");
                        if (!correction.isEmpty() && pythonExecuteCode.verification(correction)) {
                            try {
                                // Ajouter l'exercice à la base de données
                                int exerciseId = Connexionbdd.addExercise(title, question, difficulty, type);

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
                                    String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
                                    String difficulty2 = Connexionbdd.getExerciceDifficulty(i); // Récupérer la difficulté de l'exercice
                                    int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
                                    int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis
                                    String typeExo = Connexionbdd.getTypeExo(i); // Récupérer le type de l'exercice
                        
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
                                    HBox.setHgrow(spacer, Priority.ALWAYS); // Pousse le type d'exercice à droite

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

        addExerciseBox.getChildren().addAll(addExerciseLabel,titleLabel, titleInput,questionLabel, questionInput,difficultyLabel2, difficultyInput, typeComboBox, languageSelectionBox, buttonBoxAdd);
        addExerciseRoot.setCenter(addExerciseBox);



        // Ajouter dans une barre les boutons "Accueil", "Filtrer" et "Ajouter"
        // Créer un Region pour séparer les boutons
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS); // Permet au spacer de prendre tout l'espace disponible
        
        // Ajouter les boutons et le spacer dans le HBox
        HBox topBar = new HBox(10, homeButton, space, filterButton, addButton);
        topBar.setAlignment(Pos.CENTER); // Centrer verticalement les éléments
        topBar.setStyle("-fx-background-color: rgba(88, 69, 102, 0.95); -fx-padding: 15px;");

        // Ajouter la barre au haut du BorderPane
        mainRoot.setTop(topBar);

        // Fenêtre secondaire (zone de code)
        BorderPane secondaryRoot = new BorderPane();
        secondaryRoot.setStyle(
            "-fx-background-image: url('" + backgroundFile.toURI().toString() + "'); " +
            "-fx-background-size: cover; " + // Ajuster l'image pour couvrir tout le conteneur
            "-fx-background-position: center center; " + // Centrer l'image
            "-fx-background-repeat: no-repeat;" // Ne pas répéter l'image
        );


        // Ajouter une zone de texte pour afficher la consigne
        TextFlow instructionArea = new TextFlow();
        instructionArea.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-padding: 10px; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc);");
        instructionArea.setPrefWidth(400); // Largeur préférée pour la consigne
        
        //Titre pour le code area et le language selector
        ComboBox<String> languageSelector = new ComboBox<>();
        languageSelector.setStyle(" -fx-font-family: 'Pixel Game';-fx-font-size: 25px;");
        HBox.setMargin(languageSelector, new Insets(12, 0, 0, 10)); // 10px en haut et à gauche
        Label codeAreaTitle = new Label("Zone de code");
        codeAreaTitle.setStyle("-fx-font-size: 50px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
        HBox codeAreaTitleBox = new HBox(10, codeAreaTitle, languageSelector);
        HBox.setMargin(codeAreaTitle, new Insets(10, 0, 0, 20)); // 50px de marge à droite
        // Ajouter une zone de texte pour écrire du code
        CodeArea codeArea = SyntaxicalColor.createCodeArea();
        codeArea.setWrapText(false);
        codeArea.setPrefHeight(400); // Hauteur préférée de 400 pixels
        codeArea.setMinHeight(400);  // Hauteur minimale de 300 pixels
        codeArea.setMaxHeight(400);  // Hauteur maximale de 600 pixels
        VBox codeAreaBox = new VBox(10, codeAreaTitleBox, codeArea);
        codeAreaBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Fond noir avec opacité 90%

        // Réutiliser outputArea sans reconfigurer
        javafx.scene.control.SplitPane codeAndConsoleSplitPane = new javafx.scene.control.SplitPane();
        codeAndConsoleSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        codeAndConsoleSplitPane.getItems().addAll(codeAreaBox, outputArea);
        codeAndConsoleSplitPane.setDividerPositions(0.7);
        codeAndConsoleSplitPane.setStyle("-fx-background-color: transparent;");

        // Créer un SplitPane pour diviser la consigne et la zone de code/console
        javafx.scene.control.SplitPane instructionAndCodeSplitPane = new javafx.scene.control.SplitPane();
        instructionAndCodeSplitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        instructionAndCodeSplitPane.getItems().addAll(instructionArea, codeAndConsoleSplitPane);
        instructionAndCodeSplitPane.setDividerPositions(0.3);
        instructionAndCodeSplitPane.setStyle("-fx-background-color: transparent;");
        // Ajouter le SplitPane au centre de la fenêtre secondaire
        secondaryRoot.setCenter(instructionAndCodeSplitPane);

        // Ajouter un label pour afficher le nombre d'essais
        Label attemptsLabel = new Label();

        // Ajouter un label pour afficher le nombre d'essais réussis
        Label successfulTriesLabel = new Label();

        VBox statsBox = new VBox(10, attemptsLabel, successfulTriesLabel);

        // Ajouter un bouton pour exécuter le code
        Image runImage = new Image(executeFile.toURI().toString());
        ImageView runImageView = new ImageView(runImage);
        Button runButton = new Button();
        runButton.setGraphic(runImageView); // Ajouter l'image au bouton
        runButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
        // Ajouter un bouton pour revenir à la liste des exercices
        Image returnImage = new Image(returnFile.toURI().toString());
        ImageView returnImageView = new ImageView(returnImage);
        Button backButton = new Button();
        backButton.setGraphic(returnImageView);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;"); // Rendre le fond transparent
        backButton.setOnAction(event -> {
            // Mettre à jour la liste des exercices
            exerciseList.getItems().clear();
            for (int i = 1; i <= Connexionbdd.maxexo(); i++) {
                String titre = Connexionbdd.getExerciceTitle(i); // Récupérer le titre de l'exercice
                String difficulty = Connexionbdd.getExerciceDifficulty(i); // Récupérer la difficulté de l'exercice
                int attempts = Connexionbdd.getExerciseAttempts(i); // Récupérer le nombre d'essais
                int successfulTries = Connexionbdd.getSuccessfulTries(i); // Récupérer le nombre d'essais réussis
                String typeExo = Connexionbdd.getTypeExo(i); // Récupérer le type de l'exercice
    
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
                HBox.setHgrow(spacer, Priority.ALWAYS); // Pousse le type d'exercice à droite

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
            primaryStage.setScene(mainScene); // Revenir à la scène principale
        });


        // Réorganiser les composants dans le HBox
        Region space2 = new Region();
        HBox.setHgrow(space2, Priority.ALWAYS); // Permet au spacer de prendre tout l'espace disponible
        HBox buttonBox = new HBox(20, backButton, space2, statsBox, runButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle(("-fx-background-color: rgba(20, 20, 20, 0.6); -fx-padding: 10px; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc);"));
        
        
        // Ajouter le bouton et le label en bas
        secondaryRoot.setBottom(buttonBox);

        // Créer une scène pour la fenêtre secondaire
        Scene secondaryScene = new Scene(secondaryRoot, 1600, 900);

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
                // Effacer le contenu précédent du TextFlow
                instructionArea.getChildren().clear();

                // Ajouter le texte "Consigne :" en grand
                Text consigneLabel = new Text("Consigne : \n");
                consigneLabel.setStyle("-fx-font-size: 60px; -fx-fill: white;-fx-font-family: 'Pixel Game';");

                // Ajouter le texte de la consigne
                Text consigneText = new Text(consigne);
                consigneText.setStyle("-fx-font-size: 35px; -fx-fill: white;-fx-font-family: 'Pixel Game';");

                // Ajouter les textes au TextFlow
                instructionArea.getChildren().addAll(consigneLabel, new Text("\n"), consigneText);

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

                // Récupérer et afficher le nombre d'essais
                int attempts = Connexionbdd.getExerciseAttempts(selectedExo);
                attemptsLabel.setText("Nombre d'essais : " + attempts);
                attemptsLabel.setStyle("-fx-font-size: 35px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");

                // Récupérer et afficher le nombre d'essais réussis
                int successfulTries = Connexionbdd.getSuccessfulTries(selectedExo);
                successfulTriesLabel.setText("Nombre d'essais réussis : " + successfulTries);
                successfulTriesLabel.setStyle("-fx-font-size: 35px;-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-font-family: 'Pixel Game';");
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
        //Colorisation syntaxique
        codeArea.getStylesheets().add(getClass().getResource("/SyntaxicalColor.css").toExternalForm());
        // Configurer et afficher la fenêtre principale
    }
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}