import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class HomeUser extends JFrame {
    private String username;
    private int userId;
    private JTable tableComics;
    private DefaultTableModel tableModel;

    public HomeUser(String username, int userId) {
        this.username = username;
        this.userId = userId;

        setTitle("ComicZone - Home");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblHi = new JLabel("Hi, " + username);
        lblHi.setFont(new Font("Arial", Font.BOLD, 18));
        panelTop.add(lblHi);
        add(panelTop, BorderLayout.NORTH);

        JPanel panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(Color.LIGHT_GRAY);
        panelSidebar.setPreferredSize(new Dimension(150, 0));

        JButton btnHome = new JButton("Home");
        JButton btnBookmark = new JButton("Bookmark");
        JButton btnAccount = new JButton("Akun");
        JButton btnLogout = new JButton("Logout");

        Component[] buttons = {btnHome, btnBookmark, btnAccount, btnLogout};
        for (Component btn : buttons) {
            ((JButton) btn).setAlignmentX(Component.CENTER_ALIGNMENT);
            ((JButton) btn).setMaximumSize(new Dimension(120, 40));
            panelSidebar.add(Box.createVerticalStrut(15));
            panelSidebar.add(btn);
        }
        add(panelSidebar, BorderLayout.WEST);

        JPanel panelContent = new JPanel(new BorderLayout());
        
        tableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Judul", "Tipe", "Genre", "Chapter", "Update Terakhir"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return ImageIcon.class; 
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableComics = new JTable(tableModel);
        
        tableComics.getColumnModel().getColumn(0).setMinWidth(0);
        tableComics.getColumnModel().getColumn(0).setMaxWidth(0);
        tableComics.setRowHeight(120);
        tableComics.getColumnModel().getColumn(1).setPreferredWidth(90);
        
        panelContent.add(new JScrollPane(tableComics), BorderLayout.CENTER);

        JButton btnAddBookmark = new JButton("Masukkan ke Bookmark");
        JPanel panelBottomContent = new JPanel();
        panelBottomContent.add(btnAddBookmark);
        panelContent.add(panelBottomContent, BorderLayout.SOUTH);

        add(panelContent, BorderLayout.CENTER);

        loadComics();

        btnAddBookmark.addActionListener(e -> addToBookmark());
        
        btnBookmark.addActionListener(e -> {
            //new Bookmark(username, userId).setVisible(true);
            dispose();
        });

        btnAccount.addActionListener(e -> {
            //new Account(username, userId).setVisible(true);
            dispose();
        });

        btnLogout.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadComics() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, image_path, title, type, genre, chapters, last_update FROM comics";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
             
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));

                String imagePath = rs.getString("image_path");
                ImageIcon finalIcon = null;
                
                try {
                    ImageIcon originalIcon = new ImageIcon(imagePath);
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

    private void addToBookmark() {
        int selectedRow = tableComics.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih komik terlebih dahulu!");
            return;
        }

        int comicId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);

        String checkSql = "SELECT * FROM bookmarks WHERE user_id = ? AND comic_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, comicId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Komik '" + title + "' sudah ada di bookmark Anda.");
                return;
            }

            String insertSql = "INSERT INTO bookmarks (user_id, comic_id) VALUES (?, ?)";
            PreparedStatement insertSt = conn.prepareStatement(insertSql);
            insertSt.setInt(1, userId);
            insertSt.setInt(2, comicId);
            insertSt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Komik '" + title + "' berhasil ditambahkan ke Bookmark!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan bookmark.");
        }
    }
}