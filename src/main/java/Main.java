import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Connexionbdd dbService = new Connexionbdd();
        int maxExo = dbService.maxexo();

        // Fenêtre principale (liste des exercices)
        BorderPane mainRoot = new BorderPane();
        Color backgroundColorMain = Color.web("#1E1E1E");

        // Titre de la page
        Label titleLabel = new Label("Le codyngame de la javadocance");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Description
        Label descriptionLabel = new Label("Bienvenue sur notre codyngame, veuillez choisir un exercice. Bon codage!");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // Créer une liste d'exercices
        ListView<HBox> exerciseList = new ListView<>();
        exerciseList.setStyle("-fx-control-inner-background: #2D2D2D; -fx-text-fill: white;");
        for (int i = 1; i <= maxExo; i++) {
            String titre = dbService.getExerciceTitle(i); // Récupérer le titre de l'exercice
            Label exerciseNumber = new Label("Exercice " + i);
            exerciseNumber.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label exerciseTitle = new Label(titre);
            exerciseTitle.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");

            HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle);
            exerciseItem.setSpacing(10);
            exerciseList.getItems().add(exerciseItem);
        }

        // Organiser les composants dans un VBox
        VBox contentBox = new VBox(10, titleLabel, descriptionLabel, exerciseList);
        contentBox.setStyle("-fx-background-color: #1E1E1E;");
        contentBox.setAlignment(Pos.CENTER);

        // Ajouter le contenu au centre de la fenêtre principale
        mainRoot.setCenter(contentBox);

        // Créer une scène pour la fenêtre principale
        Scene mainScene = new Scene(mainRoot, 1600, 900, backgroundColorMain);

        // Fenêtre secondaire (zone de code)
        BorderPane secondaryRoot = new BorderPane();
        Color backgroundColorSecondary = Color.web("#1E1E1E");

        // Ajouter une zone de texte pour écrire du code
        TextArea codeArea = new TextArea();
        codeArea.setStyle("-fx-control-inner-background: #1E1E1E; -fx-text-fill:rgb(255, 254, 254); -fx-background-color: #1E1E1E;");
        codeArea.setWrapText(false);

        // Ajouter un bouton pour exécuter le code
        Button runButton = new Button("Exécuter");
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        // Ajouter un bouton pour revenir à la liste des exercices
        Button backButton = new Button("Retour à la liste des exercices");
        backButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(event -> primaryStage.setScene(mainScene)); // Revenir à la scène principale

        ComboBox<String> languageSelector = new ComboBox<>();
        languageSelector.setStyle("-fx-background-color:rgb(236, 227, 227); -fx-text-fill: white;");

        // Ajouter une boîte pour organiser les composants
        HBox codeBox = new HBox(codeArea);
        codeBox.setStyle("-fx-background-color: #1E1E1E;");
        HBox.setHgrow(codeArea, javafx.scene.layout.Priority.ALWAYS);

        HBox buttonBox = new HBox(10, backButton, languageSelector, runButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: #2D2D2D;");

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
                String consigne = dbService.showConsigne(selectedExo);
                if (consigne != null) {
                    codeArea.setText("/* " + consigne + " */\n\nword = input()\n\nprint(word)");
                } else {
                    codeArea.setText("/* Aucune consigne disponible pour cet exercice. */\n\nword = input()\n\nprint(word)");
                }

                // Récupérer les langages disponibles pour l'exercice sélectionné
                languageSelector.getItems().clear();
                languageSelector.getItems().addAll(dbService.getAvailableLanguages(selectedExo));

                // Définir le premier langage disponible comme valeur par défaut
                if (!languageSelector.getItems().isEmpty()) {
                    languageSelector.setValue(languageSelector.getItems().get(0));
                    // Mettre à jour la zone de code en fonction du langage sélectionné
                    String language = languageSelector.getValue();
                    if (language.equals("Python")) {
                        codeArea.setText("# " + consigne + "\n\nword = input()\n\nprint(word)");
                    } else if (language.equals("Java")) {
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
                }

                primaryStage.setScene(secondaryScene); // Basculer vers la scène secondaire
            }
        });

        runButton.setOnAction(event -> {
            String code = codeArea.getText();
            String language = languageSelector.getValue();
            
            if (language.equals("Java")) {
                JavaCompilerExecuteCode compiler = new JavaCompilerExecuteCode();
                compiler.executeCode(code); // Utiliser JavaCompilerExecuteCode pour compiler et exécuter le code
            } else {
                IDEExecuteCode executor = LanguageChoice.choice(language);
                executor.executeCode(code); // Exécuter le code Python
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
