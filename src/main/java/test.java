import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane(); // Conteneur vide
        Scene scene = new Scene(root, 400, 300); // Fenêtre de 400x300 pixels

        primaryStage.setTitle("Fenêtre JavaFX"); // Titre de la fenêtre
        primaryStage.setScene(scene); // Associer la scène à la fenêtre
        primaryStage.show(); // Afficher la fenêtre
    }

    public static void main(String[] args) {
        launch(args); // Lancer l'application JavaFX
    }
}


