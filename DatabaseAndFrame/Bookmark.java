import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class Bookmark extends JFrame {
    private String username;
    private int userId;
    private JTable tableBookmarks;
    private DefaultTableModel tableModel;
    
    private JTextField txtSearch;
    private JComboBox<String> cbType;

    private final Color bgColor = new Color(43, 45, 58);
    private final Color panelColor = new Color(55, 57, 73);
    private final Color accentColor = new Color(138, 114, 255);
    private final Color textColor = Color.WHITE;

    public Bookmark(String username, int userId) {
        this.username = username;
        this.userId = userId;

        setTitle("ComicZone - My Bookmarks");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(bgColor);
        panelTop.setBorder(new EmptyBorder(10, 15, 10, 15));

        ImageIcon originalLogo = new ImageIcon("assets/logo.png");
        Image scaledLogo = originalLogo.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(scaledLogo), SwingConstants.CENTER);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                backToHome();
            }
        });
        panelTop.add(lblLogo, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        JPanel panelContent = new JPanel(new BorderLayout());
        panelContent.setBackground(bgColor);
        panelContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel panelHeaderContent = new JPanel();
        panelHeaderContent.setLayout(new BoxLayout(panelHeaderContent, BoxLayout.Y_AXIS));
        panelHeaderContent.setBackground(bgColor);

        JLabel lblHeaderTitle = new JLabel("Daftar Bookmark: " + username, SwingConstants.CENTER);
        lblHeaderTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeaderTitle.setForeground(textColor);
        lblHeaderTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelHeaderContent.add(lblHeaderTitle);
        panelHeaderContent.add(Box.createVerticalStrut(20));

        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelFilterBar.setBackground(bgColor);
        
        JLabel lblSearch = new JLabel("Cari Judul:");
        lblSearch.setForeground(textColor);
        panelFilterBar.add(lblSearch);
        
        txtSearch = new JTextField(20);
        styleTextField(txtSearch);
        panelFilterBar.add(txtSearch);

        JLabel lblType = new JLabel("Tipe:");
        lblType.setForeground(textColor);
        panelFilterBar.add(lblType);
        
        cbType = new JComboBox<>(new String[]{"Semua", "Manga", "Manhwa", "Manhua"});
        panelFilterBar.add(cbType);

        JButton btnFilter = styleButton("Cari");
        panelFilterBar.add(btnFilter);
        
        panelHeaderContent.add(panelFilterBar);
        panelHeaderContent.add(Box.createVerticalStrut(15));

        panelContent.add(panelHeaderContent, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID Komik", "Gambar", "Judul Komik", "Tipe", "Total Chapter", "Chapter Dibaca", "Terakhir Update"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableBookmarks = new JTable(tableModel);
        styleTable(tableBookmarks);
        
        tableBookmarks.getColumnModel().getColumn(0).setMinWidth(0);
        tableBookmarks.getColumnModel().getColumn(0).setMaxWidth(0);
        tableBookmarks.getColumnModel().getColumn(1).setMinWidth(90);
        tableBookmarks.getColumnModel().getColumn(1).setMaxWidth(90);

        JScrollPane scrollPane = new JScrollPane(tableBookmarks);
        scrollPane.getViewport().setBackground(panelColor);
        scrollPane.setBorder(BorderFactory.createLineBorder(panelColor, 2));
        panelContent.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBottom.setBackground(bgColor);
        panelBottom.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton btnUpdateChapter = styleButton("Update Chapter Bacaan");
        
        JButton btnDeleteBookmark = styleButton("Hapus Bookmark");
        btnDeleteBookmark.setBackground(new Color(220, 53, 69));
        
        panelBottom.add(btnUpdateChapter);
        panelBottom.add(btnDeleteBookmark);
        panelContent.add(panelBottom, BorderLayout.SOUTH);

        add(panelContent, BorderLayout.CENTER);

        loadBookmarks();

        btnFilter.addActionListener(e -> loadBookmarks());
        cbType.addActionListener(e -> loadBookmarks());
        btnUpdateChapter.addActionListener(e -> updateReadingChapter());
        
        btnDeleteBookmark.addActionListener(e -> deleteBookmark());
    }

    private JButton styleButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(accentColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(65, 67, 85));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 110), 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(115);
        table.setBackground(panelColor);
        table.setForeground(textColor);
        table.setGridColor(bgColor);
        table.setSelectionBackground(accentColor);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 32, 44));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void backToHome() {
        HomeUser home = new HomeUser(username, userId);
        home.setExtendedState(this.getExtendedState());
        if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
            home.setBounds(this.getBounds());
        }
        home.setVisible(true);
        dispose();
    }

    private void loadBookmarks() {
        tableModel.setRowCount(0);
        
        StringBuilder sql = new StringBuilder(
            "SELECT c.id, c.image_path, c.title, c.type, c.chapters, c.last_update, b.current_chapter " +
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
                    
                    String finalPath = "image/" + rs.getString("image_path");
                    ImageIcon finalIcon = null;
                    try {
                        Image scaledImg = new ImageIcon(finalPath).getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH);
                        finalIcon = new ImageIcon(scaledImg);
                    } catch (Exception ex) {
                        finalIcon = new ImageIcon();
                    }
                    
                    row.add(finalIcon);
                    row.add(rs.getString("title"));
                    row.add(rs.getString("type"));
                    row.add(rs.getInt("chapters"));
                    row.add(rs.getInt("current_chapter")); 
                    row.add(rs.getTimestamp("last_update") != null ? rs.getTimestamp("last_update") : "-");
                    
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateReadingChapter() {
        int selectedRow = tableBookmarks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih komik dari daftar bookmark terlebih dahulu!");
            return;
        }

        int comicId = (int) tableModel.getValueAt(selectedRow, 0);
        int totalChapters = (int) tableModel.getValueAt(selectedRow, 4);
        
        String input = JOptionPane.showInputDialog(this, "Masukkan nomor chapter terakhir yang Anda baca:");
        if (input != null && !input.isEmpty()) {
            try {
                int newChapter = Integer.parseInt(input);
                if (newChapter < 1 || newChapter > totalChapters) {
                    JOptionPane.showMessageDialog(this, "Nomor chapter tidak valid (Max: " + totalChapters + ")!");
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
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void deleteBookmark() {
        int selectedRow = tableBookmarks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih komik yang ingin dihapus dari daftar bookmark terlebih dahulu!");
            return;
        }

        int comicId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus '" + title + "' dari bookmark?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            String deleteSql = "DELETE FROM bookmarks WHERE user_id = ? AND comic_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(deleteSql)) {
                
                pst.setInt(1, userId);
                pst.setInt(2, comicId);
                pst.executeUpdate();
                
                loadBookmarks();
                JOptionPane.showMessageDialog(this, "Komik '" + title + "' berhasil dihapus dari bookmark.");
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus komik dari bookmark.");
                ex.printStackTrace();
            }
        }
    }
}