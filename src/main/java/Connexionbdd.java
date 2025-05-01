import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexionbdd {

    // Informations de connexion
    private static final String DB_URL = "jdbc:mysql://localhost:3306/codyngame";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "cytech0001";

    /**
     * Méthode pour obtenir une connexion à la base de données
     * @return Connection ou null si erreur
     */
    public static Connection getConnection() {
        try {
            // Charger le driver JDBC (optionnel avec JDBC 4+)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connexion réussie à la base de données !");
            return connection;

        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC non trouvé : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
        }

        return null;
    }

    public static void main(String[] args) {
        getConnection(); // Juste pour tester
    }
} 
