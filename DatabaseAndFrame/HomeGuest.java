import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class HomeGuest extends JFrame {
    private JTable tableComics;
    private DefaultTableModel tableModel;
    
    private JTextField txtSearch;
    private JComboBox<String> cbType;

    public HomeGuest() {
        setTitle("ComicZone - Home Page (Guest)");
        setSize(950, 650);
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
        
        JPanel panelTopContent = new JPanel();
        panelTopContent.setLayout(new BoxLayout(panelTopContent, BoxLayout.Y_AXIS));
        panelTopContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblHeader = new JLabel("Daftar Komik (Login untuk menambahkan ke Bookmark)", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTopContent.add(lblHeader);
        panelTopContent.add(Box.createVerticalStrut(10));

        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        panelFilterBar.add(new JLabel("Cari Judul:"));
        txtSearch = new JTextField(20);
        panelFilterBar.add(txtSearch);

        panelFilterBar.add(new JLabel("Tipe:"));
        cbType = new JComboBox<>(new String[]{"Semua", "Manga", "Manhwa", "Manhua"});
        panelFilterBar.add(cbType);

        JButton btnFilter = new JButton("Cari");
        panelFilterBar.add(btnFilter);
        
        panelTopContent.add(panelFilterBar);
        panelContent.add(panelTopContent, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Judul", "Tipe", "Genre", "Chapter", "Update Terakhir"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
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
        tableComics.setRowHeight(115); 
        
        tableComics.getColumnModel().getColumn(0).setMinWidth(0);
        tableComics.getColumnModel().getColumn(0).setMaxWidth(0);
        tableComics.getColumnModel().getColumn(0).setPreferredWidth(0);

        tableComics.getColumnModel().getColumn(1).setMinWidth(90);
        tableComics.getColumnModel().getColumn(1).setMaxWidth(90);
        tableComics.getColumnModel().getColumn(1).setPreferredWidth(90);
        
        tableComics.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        panelContent.add(new JScrollPane(tableComics), BorderLayout.CENTER);
        add(panelContent, BorderLayout.CENTER);

        loadComics();

        btnFilter.addActionListener(e -> loadComics());
        cbType.addActionListener(e -> loadComics());

        btnLogin.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadComics() {
        tableModel.setRowCount(0);
        
        StringBuilder sql = new StringBuilder("SELECT id, image_path, title, type, genre, chapters, last_update FROM comics WHERE 1=1");
        
        String searchKeyword = txtSearch.getText().trim().toLowerCase();
        String selectedType = (String) cbType.getSelectedItem();
        
        if (selectedType != null && !selectedType.equals("Semua")) {
            sql.append(" AND type = ?");
        }
        if (!searchKeyword.isEmpty()) {
            sql.append(" AND LOWER(title) LIKE ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {
             
            int paramIndex = 1;
            if (selectedType != null && !selectedType.equals("Semua")) {
                pst.setString(paramIndex++, selectedType);
            }
            if (!searchKeyword.isEmpty()) {
                pst.setString(paramIndex++, "%" + searchKeyword + "%");
            }
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));

                    String imagePath = rs.getString("image_path");
                    String finalPath = "image/" + imagePath;
                    ImageIcon finalIcon = null;
                    
                    try {
                        ImageIcon originalIcon = new ImageIcon(finalPath);
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