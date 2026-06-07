import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class HomeGuest extends JFrame {
    private JTable tableComics;
    private DefaultTableModel tableModel;

    public HomeGuest() {
        setTitle("ComicZone - Home (Guest)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(Color.LIGHT_GRAY);
        panelSidebar.setPreferredSize(new Dimension(150, 0));

        JButton btnLogin = new JButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(100, 40));
        panelSidebar.add(Box.createVerticalStrut(20));
        panelSidebar.add(btnLogin);
        add(panelSidebar, BorderLayout.WEST);

        // Main Content Panel (Daftar Komik)
        JPanel panelContent = new JPanel(new BorderLayout());
        JLabel lblHeader = new JLabel("Daftar Komik (Login untuk menambahkan ke Bookmark)", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panelContent.add(lblHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Gambar", "Judul", "Tipe", "Genre", "Chapter", "Update Terakhir"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mencegah user mengedit tabel secara langsung
            }
        };
        tableComics = new JTable(tableModel);
        panelContent.add(new JScrollPane(tableComics), BorderLayout.CENTER);
        add(panelContent, BorderLayout.CENTER);

        loadComics();

        // Action Listener
        btnLogin.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadComics() {
        String sql = "SELECT image_path, title, type, genre, chapters, last_update FROM comics";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("image_path")); // Di tahap lanjut, ini bisa di-render sebagai ImageIcon
                row.add(rs.getString("title"));
                row.add(rs.getString("type"));
                row.add(rs.getString("genre"));
                row.add(rs.getInt("chapters"));
                row.add(rs.getTimestamp("last_update"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new HomeGuest().setVisible(true);
    }
}