import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Connexionbdd {

    // Informations de connexion
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    // Constructeur pour initialiser les informations de connexion
    public Connexionbdd(String dbUrl, String dbUser, String dbPassword) {
        DB_URL = dbUrl;
        DB_USER = dbUser;
        DB_PASSWORD = dbPassword;
    }

    // Méthode pour obtenir la connexion
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }

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

    public static void showExolist() {
        Connection conn = getConnection(); // Connexion à la BDD
        if (conn == null) {
            System.out.println("Impossible de se connecter à la base de données.");
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
    
                System.out.println("Exercice " + id +
                    ", Titre: " + Titre +
                    ", Difficulty: " + difficulty +
                    ", Question: " + question +
                    ", Try: " + tries +
                    ", SuccessfulTry: " + successfulTries +
                    ", Language disponible: ");
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
            System.err.println("Erreur lors de la récupération des exercices : " + e.getMessage());
        }
    }

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
        System.out.println("Entrez le numéro de l'exercice que vous souhaitez");
        int choice = scanner.nextInt();
        while(choice <1 || choice > id){
            System.out.println("Veuillez entrer un nombre entre 1 et " + id);
            choice = scanner.nextInt();
        }
        return choice;
    }

    public static String showConsigne(int choice) {
        Connection conn = getConnection(); 
        if (conn == null) {
            System.out.println("Impossible de se connecter à la base de données.");
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
                System.out.println("Aucune consigne trouvée pour l'exercice " + choice);
            }
    
            conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la consigne : " + e.getMessage());
        }
        return null;
    }

    public static List<String> getAvailableLanguages(int exerciseId) {
        List<String> languages = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT NomLanguage FROM LanguageCode WHERE Exerciceid = " + exerciseId)) {

            while (rs.next()) {
                languages.add(rs.getString("NomLanguage"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des langages disponibles : " + e.getMessage());
        }
        return languages;
    }

    public static String getExerciceTitle(int exerciseId) {
        String titre = "Titre non disponible";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Titre FROM Exercice WHERE Id = " + exerciseId)) {

            if (rs.next()) {
                titre = rs.getString("Titre");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du titre de l'exercice : " + e.getMessage());
        }
        return titre;
    }

    public static int addExercise(String title, String question, String difficulty) {
        String query = "INSERT INTO Exercice (Titre, Question, difficulty, Try, Successfulltry) VALUES (?, ?, ?, 0, 0)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, question);
            stmt.setString(3, difficulty);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Retourner l'ID généré
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'exercice : " + e.getMessage());
        }
        return -1; // Retourner -1 en cas d'erreur
    }

    public static void addLanguageToExercise(int exerciseId, String language) {
        String query = "INSERT INTO LanguageCode (Exerciceid, NomLanguage) VALUES (?, ?)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            stmt.setString(2, language);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du langage à l'exercice : " + e.getMessage());
        }
    }

    public static boolean isTitleExists(String title) {
        String query = "SELECT COUNT(*) FROM Exercice WHERE Titre = ?";
        try (Connection conn = getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne true si le titre existe
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du titre : " + e.getMessage());
        }
        return false; // Retourne false en cas d'erreur ou si le titre n'existe pas
    }

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
            System.err.println("Erreur lors de la récupération du nombre d'essais : " + e.getMessage());
        }
        return 0; // Retourne 0 en cas d'erreur
    }

    public static void incrementExerciseAttempts(int exerciseId) {
        String query = "UPDATE Exercice SET Try = Try + 1 WHERE Id = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exerciseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'incrémentation du nombre d'essais : " + e.getMessage());
        }
    }

}