package com.codyngame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
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
        codeArea.setText("word = input()\n\nprint(word);");
        
        // Organise composants in a HBox (ça sert pas à grand chose au début mais plus tard ça va être utile)
        HBox codeBox = new HBox(codeArea);
        codeBox.setStyle("-fx-background-color: #1E1E1E;");
        HBox.setHgrow(codeArea, javafx.scene.layout.Priority.ALWAYS);
        
        // Set to the center
        root.setCenter(codeBox);
        
        // Create a scene with a background color
        Scene scene = new Scene(root, 1280, 720, backgroundColor);
        
        // Config the stage
        primaryStage.setTitle("JAVADOCodYnGame"); // Set the title of the window
        primaryStage.setScene(scene); // Set the scene to the stage
        primaryStage.show(); // Show the stage
    }
    
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}