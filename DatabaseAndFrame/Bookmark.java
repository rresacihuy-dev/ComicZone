import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Bookmark extends JFrame {
    private String username;
    private int userId;
    private JTable tableBookmarks;
    private DefaultTableModel tableModel;
    
    private JTextField txtSearch;
    private JComboBox<String> cbType;

    public Bookmark(String username, int userId) {
        this.username = username;
        this.userId = userId;

        setTitle("ComicZone - My Bookmarks");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblHeader = new JLabel("Bookmark milik: " + username, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTop.add(lblHeader);
        panelTop.add(Box.createVerticalStrut(10));

        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilterBar.add(new JLabel("Cari Judul:"));
        txtSearch = new JTextField(20);
        panelFilterBar.add(txtSearch);

        panelFilterBar.add(new JLabel("Tipe:"));
        cbType = new JComboBox<>(new String[]{"Semua", "Manga", "Manhwa", "Manhua"});
        panelFilterBar.add(cbType);

        JButton btnFilter = new JButton("Cari");
        panelFilterBar.add(btnFilter);
        
        panelTop.add(panelFilterBar);
        add(panelTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID Komik", "Gambar", "Judul Komik", "Total Chapter", "Chapter Dibaca", "Terakhir Update"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableBookmarks = new JTable(tableModel);
        tableBookmarks.setRowHeight(115);
        
        tableBookmarks.getColumnModel().getColumn(0).setMinWidth(0);
        tableBookmarks.getColumnModel().getColumn(0).setMaxWidth(0);
        tableBookmarks.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tableBookmarks.getColumnModel().getColumn(1).setMinWidth(90);
        tableBookmarks.getColumnModel().getColumn(1).setMaxWidth(90);
        tableBookmarks.getColumnModel().getColumn(1).setPreferredWidth(90);

        tableBookmarks.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tableBookmarks), BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new FlowLayout());
        JButton btnUpdateChapter = new JButton("Update Chapter Bacaan");
        JButton btnBack = new JButton("Kembali ke Home");
        
        panelBottom.add(btnUpdateChapter);
        panelBottom.add(btnBack);
        add(panelBottom, BorderLayout.SOUTH);

        loadBookmarks();

        btnFilter.addActionListener(e -> loadBookmarks());
        cbType.addActionListener(e -> loadBookmarks());

        btnUpdateChapter.addActionListener(e -> {
            int selectedRow = tableBookmarks.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih komik dari daftar bookmark terlebih dahulu!");
                return;
            }

            int comicId = (int) tableModel.getValueAt(selectedRow, 0);
            int totalChapters = (int) tableModel.getValueAt(selectedRow, 3);
            
            String input = JOptionPane.showInputDialog(this, "Masukkan nomor chapter terakhir yang Anda baca:");
            if (input != null && !input.isEmpty()) {
                try {
                    int newChapter = Integer.parseInt(input);
                    if (newChapter < 1 || newChapter > totalChapters) {
                        JOptionPane.showMessageDialog(this, "Nomor chapter tidak valid!");
                        return;
                    }
                    
                    String updateSql = "UPDATE bookmarks SET current_chapter = ? WHERE user_id = ? AND comic_id = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pst = conn.prepareStatement(updateSql)) {
                        pst.setInt(1, newChapter);
                        pst.setInt(2, userId);
                        pst.setInt(3, comicId);
                        pst.executeUpdate();
                        
                        loadBookmarks();
                        JOptionPane.showMessageDialog(this, "Progress membaca berhasil diperbarui!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Harap masukkan angka bulat!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnBack.addActionListener(e -> {
            new HomeUser(username, userId).setVisible(true);
            dispose();
        });
    }

    private void loadBookmarks() {
        tableModel.setRowCount(0);
        
        StringBuilder sql = new StringBuilder(
            "SELECT c.id, c.image_path, c.title, c.chapters, c.last_update, b.current_chapter " +
            "FROM bookmarks b JOIN comics c ON b.comic_id = c.id " +
            "WHERE b.user_id = ?"
        );
        
        String searchKeyword = txtSearch.getText().trim().toLowerCase();
        String selectedType = (String) cbType.getSelectedItem();
        
        if (selectedType != null && !selectedType.equals("Semua")) {
            sql.append(" AND c.type = ?");
        }
        if (!searchKeyword.isEmpty()) {
            sql.append(" AND LOWER(c.title) LIKE ?");
        }
                 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            pst.setInt(paramIndex++, userId);
            
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
                    row.add(rs.getInt("chapters"));
                    row.add(rs.getInt("current_chapter")); 
                    row.add(rs.getString("last_update"));
                    
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR DI BOOKMARK: " + e.getMessage());
            e.printStackTrace();
        }
    }
}