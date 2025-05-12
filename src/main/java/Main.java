import java.io.IOException;
import java.nio.file.Files;
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
        Connexionbdd dbService = new Connexionbdd();
        int maxExo = dbService.maxexo();

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
        VBox addExerciseBox = new VBox(10);
        addExerciseBox.setAlignment(Pos.CENTER);
        addExerciseBox.setStyle("-fx-background-color: rgba(10, 10, 10, 0.95); -fx-padding: 25px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0.5, 0, 2);");

        Label addExerciseLabel = new Label("Ajouter un nouvel exercice");
        addExerciseLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        TextArea titleInput = new TextArea();
        titleInput.setPromptText("Titre de l'exercice");
        titleInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        TextArea questionInput = new TextArea();
        questionInput.setPromptText("Question de l'exercice");
        questionInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        TextArea difficultyInput = new TextArea();
        difficultyInput.setPromptText("Difficulté de l'exercice (facile, moyen, difficile)");
        difficultyInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        // Ajouter des cases à cocher pour choisir les langages
        CheckBox pythonCheckBox = new CheckBox("Python");
        pythonCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");
        CheckBox javaCheckBox = new CheckBox("Java");
        javaCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");
        CheckBox CCheckBox = new CheckBox("C");
        CCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");
        CheckBox jsCheckBox = new CheckBox("JavaScript");
        jsCheckBox.setStyle("-fx-text-fill: linear-gradient(to right, #ffffff, #cccccc); -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

        HBox languageSelectionBox = new HBox(10, pythonCheckBox, javaCheckBox, CCheckBox, jsCheckBox);
        languageSelectionBox.setAlignment(Pos.CENTER);
        languageSelectionBox.setStyle("-fx-padding: 10px;");

        Button saveButton = new Button("Enregistrer");
        saveButton.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #cccccc); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");

        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: linear-gradient(to right, #cccccc, #999999); -fx-text-fill: black; -fx-font-weight: bold; -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 6, 0.5, 0, 2);");
        cancelButton.setOnAction(event -> primaryStage.setScene(mainScene)); // Retour à la scène principale

        saveButton.setOnAction(event -> {
            String title = titleInput.getText();
            String question = questionInput.getText();
            String difficulty = difficultyInput.getText();
            boolean isPythonSelected = pythonCheckBox.isSelected();
            boolean isJavaSelected = javaCheckBox.isSelected();
            boolean isCSelected = CCheckBox.isSelected();
            boolean isJSSelected = jsCheckBox.isSelected();

            if (!title.isEmpty() && !question.isEmpty() && !difficulty.isEmpty() && (isPythonSelected || isCSelected || isJavaSelected || isJSSelected)) {
                if (dbService.isTitleExists(title)) {
                    System.err.println("Un exercice avec ce titre existe déjà. Veuillez choisir un autre titre.");
                } else {
                    // Demander la correction en Python
                    TextArea correctionInput = new TextArea();
                    correctionInput.setPromptText("Entrez la correction en Python pour cet exercice");
                    correctionInput.setStyle("-fx-control-inner-background: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

                    // Fenêtre pour saisir la correction
                    Stage correctionStage = new Stage();
                    VBox correctionBox = new VBox(10, new Label("Correction en Python :"), correctionInput);
                    correctionBox.setAlignment(Pos.CENTER);
                    correctionBox.setStyle("-fx-padding: 20px; -fx-background-color: #1E1E1E;");

                    Button saveCorrectionButton = new Button("Enregistrer la correction");
                    saveCorrectionButton.setOnAction(e -> {
                        String correction = correctionInput.getText();
                        if (!correction.isEmpty()) {
                            try {
                                // Ajouter la correction au fichier exercice.py
                                Path exerciceFile = Path.of("src/main/resources/exercice.py");
                                String content = Files.readString(exerciceFile);
                                                    // Ajouter l'exercice à la base de données
                                int exerciseId = dbService.addExercise(title, question, difficulty);

                                // Ajouter le langage Python à la base de données
                                if (isPythonSelected) {
                                    dbService.addLanguageToExercise(exerciseId, "Python");
                                }
                                if (isJavaSelected) {
                                    dbService.addLanguageToExercise(exerciseId, "Java");
                                }
                                if (isCSelected) {
                                    dbService.addLanguageToExercise(exerciseId, "C");
                                }
                                if (isJSSelected) {
                                    dbService.addLanguageToExercise(exerciseId, "JavaScript");
                                }
                                
                                // Formater la nouvelle correction avec l'indentation correcte
                                String newCase = String.format(
                                    "        case %d:\n            # exercice corrigé\n            %s\n",
                                    exerciseId,
                                    correction.replace("\n", "\n            ")
                                );
                                
                                // Trouver l'endroit où insérer le nouveau cas
                                int matchBlock = content.indexOf("    match int(sys.argv[1]):");
                                if (matchBlock == -1) {
                                    throw new IOException("Structure du fichier exercice.py invalide: bloc match non trouvé");
                                }
                                
                                // Vérifier si le case existe déjà et le supprimer
                                String casePattern = String.format("        case %d:", exerciseId);
                                int existingCasePos = content.indexOf(casePattern);
                                
                                if (existingCasePos != -1) {
                                    // Le case existe déjà, trouver où il se termine
                                    int nextCasePos = content.indexOf("        case ", existingCasePos + casePattern.length());
                                    int ifNamePos = content.indexOf("if __name__", existingCasePos);
                                    
                                    int endOfExistingCase;
                                    if (nextCasePos != -1) {
                                        // Il y a un autre case après celui-ci
                                        endOfExistingCase = nextCasePos;
                                    } else if (ifNamePos != -1) {
                                        // Il n'y a pas d'autre case, mais il y a le bloc if __name__
                                        // Reculer jusqu'à la ligne vide avant if __name__
                                        endOfExistingCase = content.lastIndexOf("\n\n", ifNamePos);
                                        if (endOfExistingCase == -1) {
                                            // Si pas de ligne vide, utiliser la position juste avant if __name__
                                            endOfExistingCase = content.lastIndexOf("\n", ifNamePos);
                                        }
                                    } else {
                                        // Ni d'autre case, ni de bloc if __name__, aller jusqu'à la fin du fichier
                                        endOfExistingCase = content.length();
                                    }
                                    
                                    // Supprimer l'ancien case
                                    content = content.substring(0, existingCasePos) + content.substring(endOfExistingCase);
                                    
                                    // Insérer le nouveau case au même endroit
                                    content = content.substring(0, existingCasePos) + newCase + content.substring(existingCasePos);
                                    
                                    System.out.println("Case existant remplacé pour l'exercice " + exerciseId);
                                } else {
                                    // Le case n'existe pas, l'ajouter normalement
                                    // Chercher la dernière instruction case correctement indentée
                                    int lastCasePos = content.lastIndexOf("        case ", content.indexOf("if __name__"));
                                    if (lastCasePos == -1) {
                                        // Pas de case existant, ajouter après la ligne match
                                        int matchLineEnd = content.indexOf('\n', matchBlock) + 1;
                                        content = content.substring(0, matchLineEnd) + newCase + content.substring(matchLineEnd);
                                    } else {
                                        // Trouver la fin du dernier case
                                        int endOfLastCase = content.indexOf("\n\n", lastCasePos);
                                        if (endOfLastCase == -1) {
                                            // Si pas de ligne vide après le dernier case, chercher le bloc if __name__
                                            endOfLastCase = content.indexOf("if __name__", lastCasePos);
                                            if (endOfLastCase == -1) {
                                                // Si if __name__ non trouvé, utiliser la fin du fichier
                                                endOfLastCase = content.length();
                                            } else {
                                                // Remonter à la ligne précédant if __name__
                                                endOfLastCase = content.lastIndexOf("\n", endOfLastCase);
                                            }
                                        }
                                        
                                        // Insérer le nouveau cas après le dernier cas existant
                                        content = content.substring(0, endOfLastCase) + "\n" + newCase + content.substring(endOfLastCase);
                                    }
                                    System.out.println("Nouveau case ajouté pour l'exercice " + exerciseId);
                                }
                                
                                // Écrire le contenu mis à jour
                                Files.writeString(exerciceFile, content);
                                System.out.println("Correction ajoutée au fichier exercice.py");
                            } catch (IOException ex) {
                                System.out.println("Erreur lors de l'écriture dans exercice.py : " + ex.getMessage());
                            }
                            correctionStage.close();
                        } else {
                            System.out.println("La correction ne peut pas être vide.");
                        }
                    });

                    correctionBox.getChildren().add(saveCorrectionButton);
                    correctionStage.setScene(new Scene(correctionBox, 400, 300));
                    correctionStage.showAndWait();

                    // Mettre à jour la liste des exercices
                    exerciseList.getItems().clear();
                    for (int i = 1; i <= dbService.maxexo(); i++) {
                        String titre = dbService.getExerciceTitle(i);
                        Label exerciseNumber = new Label("Exercice " + i);
                        exerciseNumber.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                        Label exerciseTitle = new Label(titre);
                        exerciseTitle.setStyle("-fx-text-fill: white; -fx-padding: 0 0 0 10px;");
                        HBox exerciseItem = new HBox(exerciseNumber, exerciseTitle);
                        exerciseItem.setSpacing(10);
                        exerciseList.getItems().add(exerciseItem);
                    }
                    primaryStage.setScene(mainScene); // Retour à la scène principale
                }
            } else {
                // Afficher un message d'erreur si les champs sont incomplets ou aucun langage n'est sélectionné
                System.err.println("Veuillez remplir tous les champs et sélectionner au moins une langue");
            }
        });

        HBox buttonBoxAdd = new HBox(10, cancelButton, saveButton);
        buttonBoxAdd.setAlignment(Pos.CENTER);

        addExerciseBox.getChildren().addAll(addExerciseLabel, titleInput, questionInput, difficultyInput, languageSelectionBox, buttonBoxAdd);
        addExerciseRoot.setCenter(addExerciseBox);

        Scene addExerciseScene = new Scene(addExerciseRoot, 600, 400);

        addButton.setOnAction(event -> primaryStage.setScene(addExerciseScene));

        // Ajouter le bouton "+" en haut de la scène principale
        HBox topBar = new HBox(10, addButton);
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
        languageSelector.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-text-fill: white; -fx-border-color: linear-gradient(to right, #ffffff, #cccccc); -fx-border-radius: 15px; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 4, 0.5, 0, 2);");

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
                String consigne = dbService.showConsigne(selectedExo);

                // Récupérer les langages disponibles pour l'exercice sélectionné
                languageSelector.getItems().clear();
                languageSelector.getItems().addAll(dbService.getAvailableLanguages(selectedExo));

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

                // Récupérer et afficher le nombre d'essais
                int attempts = dbService.getExerciseAttempts(selectedExo);
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
            dbService.incrementExerciseAttempts(id);

            // Mettre à jour l'affichage du nombre d'essais
            int updatedAttempts = dbService.getExerciseAttempts(id);
            attemptsLabel.setText("Nombre d'essais : " + updatedAttempts);
            attemptsLabel.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-text-fill: white;");
        });

        languageSelector.setOnAction(event -> {
            String selectedLanguage = languageSelector.getValue();
            String consigne = dbService.showConsigne(exerciseList.getSelectionModel().getSelectedIndex() + 1);
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