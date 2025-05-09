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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/codegame";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "cytech0001";

    // 1. Méthode pour obtenir la connexion
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }

    public int maxexo(){
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

    // 2. Méthode pour afficher les exos
    public void showExolist() {
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

    public int choiceExo() {
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



    public String showConsigne(int choice) {
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

    public List<String> getAvailableLanguages(int exerciseId) {
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

    public String getExerciceTitle(int exerciseId) {
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
}