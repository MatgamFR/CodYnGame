import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                String difficulty = rs.getString("difficulty");
                String question = rs.getString("Question");
                String answer = rs.getString("Answer");
                int tries = rs.getInt("Try");
                int successfulTries = rs.getInt("Successfulltry");
                String language = rs.getString("language");
    
                System.out.println("Exercice " + id +
                    ", difficulty: " + difficulty +
                    ", Question: " + question +
                    ", Answer: " + answer +
                    ", Try: " + tries +
                    ", SuccessfulTry: " + successfulTries +
                    ", Language disponible: " + language);
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

    public void choiceExo(int choice){
        Connection conn = getConnection(); // Récupère la connexion
        if (conn == null) {
            System.out.println("Impossible de se connecter à la base de données.");
        }
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT Question FROM Exercice WHERE Id = choice";
            ResultSet rs = stmt.executeQuery(query);


            conn.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
        }
    }

}