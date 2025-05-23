/**
 * The Connexionbdd class provides methods to interact with the database for the Codyngame application.
 * It handles database connections, exercise management, and language-related operations.
 * 
 * @author Matheo,Younes,Remy,Leon,Tom
 * @version 1.0
 */
package com.codyngame.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Connexionbdd {

    // Database connection information
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    /**
     * Constructor to initialize database connection parameters.
     * 
     * @param dbUrl      The database URL
     * @param dbUser     The database username
     * @param dbPassword The database password
     */
    public Connexionbdd(String dbUrl, String dbUser, String dbPassword) {
        DB_URL = dbUrl;
        DB_USER = dbUser;
        DB_PASSWORD = dbPassword;
    }

    /**
     * Establishes a connection to the database.
     * 
     * @return A Connection object if successful, null otherwise
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the maximum exercise ID from the database.
     * 
     * @return The maximum exercise ID, or 0 if no exercises exist
     */
    public static int maxexo() {
        int id = 0;
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS max_id FROM Exercice");
    
            if (rs.next()) {
                id = rs.getInt("max_id");
            }
    
            rs.close();
            stmt.close();
            conn.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return id;
        }
    }

    /**
     * Displays the list of exercises with their details.
     */
    public static void showExolist() {
        Connection conn = getConnection();
        if (conn == null) {
            System.out.println("Unable to connect to the database.");
            return;
        }
    
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM Exercice";
            ResultSet rs = stmt.executeQuery(query);
    
            while (rs.next()) {
                int id = rs.getInt("Id");
                String Titre = rs.getString("Titre");
                String difficulty = rs.getString("difficulty");
                String question = rs.getString("Question");
                int tries = rs.getInt("Try");
                int successfulTries = rs.getInt("Successfulltry");
    
                System.out.println("Exercise " + id +
                    ", Title: " + Titre +
                    ", Difficulty: " + difficulty +
                    ", Question: " + question +
                    ", Attempts: " + tries +
                    ", Successful attempts: " + successfulTries +
                    ", Available languages: ");
                    Statement stmt2 = conn.createStatement();
                    String query2 = "SELECT NomLanguage FROM LanguageCode WHERE Exerciceid = "+id;
                    ResultSet rs2 = stmt2.executeQuery(query2);
                    while (rs2.next()) {
                        String language = rs2.getString("NomLanguage");
                        System.out.println("*"+language);
                    }
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving exercises: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to select an exercise from the list.
     * 
     * @return The selected exercise ID
     */
    public static int choiceExo() {
        int id = 0;
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS max_id FROM Exercice");
    
            if (rs.next()) {
                id = rs.getInt("max_id");
            }
    
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the exercise number you want");
        int choice = scanner.nextInt();
        while(choice <1 || choice > id){
            System.out.println("Please enter a number between 1 and " + id);
            choice = scanner.nextInt();
        }
        return choice;
    }

    /**
     * Retrieves the instructions for a specific exercise.
     * 
     * @param choice The exercise ID
     * @return The exercise instructions, or null if not found
     */
    public static String showConsigne(int choice) {
        Connection conn = getConnection(); 
        if (conn == null) {
            System.out.println("Unable to connect to the database.");
            return null;
        }
    
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT Question FROM Exercice WHERE Id = " + choice;
            ResultSet rs = stmt.executeQuery(query);
    
            if (rs.next()) {
                String question = rs.getString("Question");
                return question;
            } else {
                System.out.println("No instructions found for exercise " + choice);
            }
    
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving instructions: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gets the available languages for a specific exercise.
     * 
     * @param exerciseId The exercise ID
     * @return A list of available languages
     */
    public static List<String> getAvailableLanguages(int exerciseId) {
        List<String> languages = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT NomLanguage FROM LanguageCode WHERE Exerciceid = " + exerciseId)) {

            while (rs.next()) {
                languages.add(rs.getString("NomLanguage"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving available languages: " + e.getMessage());
        }
        return languages;
    }

    /**
     * Gets the title of a specific exercise.
     * 
     * @param exerciseId The exercise ID
     * @return The exercise title, or "Title not available" if not found
     */
    public static String getExerciceTitle(int exerciseId) {
        String titre = "Title not available";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Titre FROM Exercice WHERE Id = " + exerciseId)) {

            if (rs.next()) {
                titre = rs.getString("Titre");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving exercise title: " + e.getMessage());
        }
        return titre;
    }

    /**
     * Adds a new exercise to the database.
     * 
     * @param title       The exercise title
     * @param question    The exercise question/instructions
     * @param difficulty  The exercise difficulty level
     * @param type        The exercise type (STDIN/STDOUT or INCLUDE)
     * @return The generated exercise ID, or -1 if failed
     */
    public static int addExercise(String title, String question, String difficulty, String type) {
        String query = "INSERT INTO Exercice (Titre, Question, difficulty, TypeExo, Try, Successfulltry) VALUES (?, ?, ?, ?, 0, 0)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, question);
            stmt.setString(3, difficulty);
            stmt.setString(4, type);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the generated ID
            }
        } catch (SQLException e) {
            System.err.println("Error adding exercise: " + e.getMessage());
        }
        return -1; // Return -1 on error
    }

    /**
     * Adds a programming language to an exercise.
     * 
     * @param exerciseId The exercise ID
     * @param language   The programming language to add
     */
    public static void addLanguageToExercise(int exerciseId, String language) {
        String query = "INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (?, ?)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            stmt.setString(2, language);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding language to exercise: " + e.getMessage());
        }
    }

    /**
     * Checks if an exercise with the given title already exists.
     * 
     * @param title The title to check
     * @return true if the title exists, false otherwise
     */
    public static boolean isTitleExists(String title) {
        String query = "SELECT COUNT(*) FROM Exercice WHERE Titre = ?";
        try (Connection conn = getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if title exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking title: " + e.getMessage());
        }
        return false; // Returns false on error or if title doesn't exist
    }

    /**
     * Gets the difficulty level of a specific exercise.
     * 
     * @param exerciseId The exercise ID
     * @return The difficulty level, or null if not found
     */
    public static String getExerciceDifficulty(int exerciseId) {
        String query = "SELECT difficulty FROM Exercice WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("difficulty");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving difficulty: " + e.getMessage());
        }
        return null; // Returns null if difficulty not found or on error
    }

    /**
     * Gets the number of attempts for a specific exercise.
     * 
     * @param exerciseId The exercise ID
     * @return The number of attempts, or 0 if not found
     */
    public static int getExerciseAttempts(int exerciseId) {
        String query = "SELECT Try FROM Exercice WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Try");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving attempt count: " + e.getMessage());
        }
        return 0; // Returns 0 on error
    }

    /**
     * Gets the number of successful attempts for a specific exercise.
     * 
     * @param exerciseId The exercise ID
     * @return The number of successful attempts, or 0 if not found
     */
    public static int getSuccessfulTries(int exerciseId) {
        String query = "SELECT Successfulltry FROM Exercice WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Successfulltry");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving successful attempts: " + e.getMessage());
        }
        return 0; // Returns 0 on error
    }

    /**
     * Increments the attempt count for a specific exercise.
     * 
     * @param exerciseId The exercise ID
     */
    public static void incrementExerciseAttempts(int exerciseId) {
        String query = "UPDATE Exercice SET Try = Try + 1 WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error incrementing attempt count: " + e.getMessage());
        }
    }

    /**
     * Increments the successful attempt count for a specific exercise.
     * 
     * @param exerciseId The exercise ID
     */
    public static void incrementSuccessfulTries(int exerciseId) {
        String query = "UPDATE Exercice SET Successfulltry = Successfulltry + 1 WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error incrementing successful attempts: " + e.getMessage());
        }
    }

    /**
     * Deletes an exercise and its associated languages from the database.
     * 
     * @param exerciseId The exercise ID to delete
     */
    public static void deleteExercise(int exerciseId) {
        String deleteExerciseQuery = "DELETE FROM Exercice WHERE Id = ?";
        String deleteLanguageQuery = "DELETE FROM LanguageCode WHERE Exerciceid = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement deleteExerciseStmt = conn.prepareStatement(deleteExerciseQuery);
             java.sql.PreparedStatement deleteLanguageStmt = conn.prepareStatement(deleteLanguageQuery)) {

            // Delete associated languages
            deleteLanguageStmt.setInt(1, exerciseId);
            deleteLanguageStmt.executeUpdate();

            // Delete the exercise
            deleteExerciseStmt.setInt(1, exerciseId);
            deleteExerciseStmt.executeUpdate();

            System.out.println("Exercise with ID " + exerciseId + " successfully deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting exercise: " + e.getMessage());
        }
    }

    /**
     * Gets exercise IDs filtered by specified languages.
     * 
     * @param languages The list of languages to filter by
     * @return A list of exercise IDs that support all specified languages
     */
    public static List<Integer> getExercisesByLanguages(List<String> languages) {
        List<Integer> exerciseIds = new ArrayList<>();
        if (languages.isEmpty()) {
            return exerciseIds; // Return empty list if no languages are selected
        }

        StringBuilder query = new StringBuilder("SELECT DISTINCT Exerciceid FROM LanguageCode WHERE NomLanguage IN (");
        for (int i = 0; i < languages.size(); i++) {
            query.append("?");
            if (i < languages.size() - 1) {
                query.append(", ");
            }
        }
        query.append(")");

        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < languages.size(); i++) {
                stmt.setString(i + 1, languages.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                exerciseIds.add(rs.getInt("Exerciceid"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving exercises by language: " + e.getMessage());
        }
        return exerciseIds;
    }

    /**
     * Gets the type of a specific exercise.
     * 
     * @param id The exercise ID
     * @return The exercise type (STDIN/STDOUT or INCLUDE), or null if not found
     */
    public static String getTypeExo(int id) {
        String query = "SELECT TypeExo FROM Exercice WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("TypeExo");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving exercise type: " + e.getMessage());
        }
        return null; // Returns null if exercise type not found or on error
    }
}