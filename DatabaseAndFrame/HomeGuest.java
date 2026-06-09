import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class HomeGuest extends JFrame {
    private JTable tableComics;
    private DefaultTableModel tableModel;

    public HomeGuest() {
        setTitle("ComicZone - Home Page (Guest)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(Color.LIGHT_GRAY);
        panelSidebar.setPreferredSize(new Dimension(150, 0));

        JButton btnLogin = new JButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(120, 40));
        panelSidebar.add(Box.createVerticalStrut(20));
        panelSidebar.add(btnLogin);
        add(panelSidebar, BorderLayout.WEST);

        JPanel panelContent = new JPanel(new BorderLayout());
        JLabel lblHeader = new JLabel("Daftar Komik (Login untuk menambahkan ke Bookmark)", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panelContent.add(lblHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Gambar", "Judul", "Tipe", "Genre", "Chapter", "Update Terakhir"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return ImageIcon.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableComics = new JTable(tableModel);
        
        tableComics.setRowHeight(120);
        tableComics.getColumnModel().getColumn(0).setPreferredWidth(90);
        
        panelContent.add(new JScrollPane(tableComics), BorderLayout.CENTER);
        add(panelContent, BorderLayout.CENTER);

        loadComics();

        btnLogin.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadComics() {
        tableModel.setRowCount(0);
        String sql = "SELECT image_path, title, type, genre, chapters, last_update FROM comics";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
             
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String imagepath = rs.getString("image_path");
                ImageIcon finalIcon = null;
                
                try {
                    ImageIcon originalIcon = new ImageIcon(imagepath);
                    Image scaledImg = originalIcon.getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH);
                    finalIcon = new ImageIcon(scaledImg);
                } catch (Exception ex) {
                    finalIcon = new ImageIcon();
                }
                
                row.add(finalIcon);
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
        SwingUtilities.invokeLater(() -> {
            new HomeGuest().setVisible(true);
        });
    }
}