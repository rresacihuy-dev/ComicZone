import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/comiczonedatabase";
    private static final String user = "root";
    private static final String password = "";

    // Metode statis untuk memanggil koneksi dengan mudah dari JFrame mana saja
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Koneksi Database Gagal: " + e.getMessage());
        }
        return conn;
    }
}