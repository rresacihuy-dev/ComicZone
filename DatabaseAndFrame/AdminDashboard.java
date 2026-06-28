import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class AdminDashboard extends JFrame {
    private String adminUsername;
    
    // Tema Warna Gelap
    private final Color bgColor = new Color(43, 45, 58);
    private final Color panelColor = new Color(55, 57, 73);
    private final Color accentColor = new Color(138, 114, 255);
    private final Color textColor = Color.WHITE;

    // Komponen Komik
    private JTable tableComics;
    private DefaultTableModel comicsTableModel;
    private JTextField txtTitle, txtType, txtGenre, txtImagePath, txtChapters, txtSearchComic;
    private JComboBox<String> cbTypeFilter;

    // Komponen Akun (Users)
    private JTable tableUsers;
    private DefaultTableModel usersTableModel;
    private JTextField txtSearchUser;

    public AdminDashboard(String username) {
        this.adminUsername = username;
        setTitle("ComicZone - Admin Dashboard");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- TOP BAR (LOGO & HEADER) ---
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(bgColor);
        panelTop.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel lblHi = new JLabel("Admin: " + adminUsername);
        lblHi.setFont(new Font("Arial", Font.BOLD, 16));
        lblHi.setForeground(textColor);
        panelTop.add(lblHi, BorderLayout.WEST);

        // Logo
        ImageIcon originalLogo = new ImageIcon("assets/logo.png");
        Image scaledLogo = originalLogo.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(scaledLogo), SwingConstants.CENTER);
        panelTop.add(lblLogo, BorderLayout.CENTER);

        JButton btnLogout = styleButton("Logout");
        btnLogout.addActionListener(e -> logout());
        panelTop.add(btnLogout, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // --- TABBED PANE UTAMA ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(panelColor);
        tabbedPane.setForeground(textColor);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        tabbedPane.addTab("Comic", createComicPanel());
        tabbedPane.addTab("Account", createUsersPanel());
        
        add(tabbedPane, BorderLayout.CENTER);

        // Load data awal
        loadComicData();
        loadUserData();
    }

    // ==========================================
    // PANEL KELOLA KOMIK
    // ==========================================
    private JPanel createComicPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Filter Bar Komik
        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilterBar.setBackground(bgColor);
        
        JLabel lblSearch = new JLabel("Cari Judul:");
        lblSearch.setForeground(textColor);
        panelFilterBar.add(lblSearch);
        
        txtSearchComic = new JTextField(15);
        styleTextField(txtSearchComic);
        panelFilterBar.add(txtSearchComic);

        JLabel lblType = new JLabel("Tipe:");
        lblType.setForeground(textColor);
        panelFilterBar.add(lblType);
        
        cbTypeFilter = new JComboBox<>(new String[]{"Semua", "Manga", "Manhwa", "Manhua"});
        panelFilterBar.add(cbTypeFilter);

        JButton btnSearchComic = styleButton("Cari");
        btnSearchComic.addActionListener(e -> loadComicData());
        panelFilterBar.add(btnSearchComic);

        panel.add(panelFilterBar, BorderLayout.NORTH);

        // Tabel Komik
        comicsTableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Judul", "Tipe", "Genre", "Chapter", "Tanggal Masuk"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableComics = new JTable(comicsTableModel);
        styleTable(tableComics);
        tableComics.setRowHeight(120);
        tableComics.getColumnModel().getColumn(0).setMinWidth(0);
        tableComics.getColumnModel().getColumn(0).setMaxWidth(0);
        tableComics.getColumnModel().getColumn(1).setMinWidth(90);
        tableComics.getColumnModel().getColumn(1).setMaxWidth(90);
        
        JScrollPane scrollComics = new JScrollPane(tableComics);
        scrollComics.getViewport().setBackground(panelColor);
        scrollComics.setBorder(BorderFactory.createLineBorder(panelColor, 2));
        panel.add(scrollComics, BorderLayout.CENTER);

        // Form Input Komik
        JPanel panelBottom = new JPanel(new BorderLayout(5, 5));
        panelBottom.setBackground(bgColor);
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBackground(panelColor);
        panelInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentColor), " Tambah/Edit Data Komik ", 0, 0, new Font("Arial", Font.BOLD, 12), textColor));
        
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(5, 5, 5, 5);
        grid.fill = GridBagConstraints.HORIZONTAL;

        txtTitle = new JTextField(); styleTextField(txtTitle);
        txtType = new JTextField(); styleTextField(txtType);
        txtGenre = new JTextField(); styleTextField(txtGenre);
        txtImagePath = new JTextField(); styleTextField(txtImagePath);
        txtChapters = new JTextField(); styleTextField(txtChapters);
        
        addFormRow(panelInput, "Judul Komik:", txtTitle, grid, 0);
        addFormRow(panelInput, "Tipe:", txtType, grid, 1);
        addFormRow(panelInput, "Genre:", txtGenre, grid, 2);
        addFormRow(panelInput, "Image Path:", txtImagePath, grid, 3);
        addFormRow(panelInput, "Total Chapter:", txtChapters, grid, 4);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelButtons.setBackground(bgColor);
        
        JButton btnAdd = styleButton("Tambah Komik");
        JButton btnUpdate = styleButton("Update Chapter");
        JButton btnDelete = styleButton("Hapus Komik");

        btnAdd.addActionListener(e -> addComic());
        btnUpdate.addActionListener(e -> updateChapter());
        btnDelete.addActionListener(e -> deleteComic());

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);

        panelBottom.add(panelInput, BorderLayout.CENTER);
        panelBottom.add(panelButtons, BorderLayout.SOUTH);
        panel.add(panelBottom, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // PANEL KELOLA AKUN
    // ==========================================
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Filter Bar Akun
        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilterBar.setBackground(bgColor);
        
        JLabel lblSearch = new JLabel("Cari Username:");
        lblSearch.setForeground(textColor);
        panelFilterBar.add(lblSearch);
        
        txtSearchUser = new JTextField(20);
        styleTextField(txtSearchUser);
        panelFilterBar.add(txtSearchUser);

        JButton btnSearchUser = styleButton("Cari");
        btnSearchUser.addActionListener(e -> loadUserData());
        panelFilterBar.add(btnSearchUser);

        panel.add(panelFilterBar, BorderLayout.NORTH);

        // Tabel Akun
        usersTableModel = new DefaultTableModel(new String[]{"ID", "Username", "Password", "Role", "Tanggal Dibuat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableUsers = new JTable(usersTableModel);
        styleTable(tableUsers);
        tableUsers.setRowHeight(35); // Tinggi baris normal untuk teks
        tableUsers.getColumnModel().getColumn(0).setMaxWidth(50);
        
        JScrollPane scrollUsers = new JScrollPane(tableUsers);
        scrollUsers.getViewport().setBackground(panelColor);
        scrollUsers.setBorder(BorderFactory.createLineBorder(panelColor, 2));
        panel.add(scrollUsers, BorderLayout.CENTER);

        // Tombol Aksi Akun
        JPanel panelActionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelActionButtons.setBackground(bgColor);

        JButton btnChangePassword = styleButton("Ubah Password");
        JButton btnChangeRole = styleButton("Ubah Role");
        JButton btnDeleteUser = styleButton("Hapus Akun");

        btnChangePassword.addActionListener(e -> changeUserPassword());
        btnChangeRole.addActionListener(e -> changeUserRole());
        btnDeleteUser.addActionListener(e -> deleteUser());

        panelActionButtons.add(btnChangePassword);
        panelActionButtons.add(btnChangeRole);
        panelActionButtons.add(btnDeleteUser);

        panel.add(panelActionButtons, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // LOGIC DATABASE KOMIK
    // ==========================================
    private void loadComicData() {
        comicsTableModel.setRowCount(0);
        StringBuilder sql = new StringBuilder("SELECT * FROM comics WHERE 1=1");
        String searchKeyword = txtSearchComic.getText().trim().toLowerCase();
        String selectedType = (String) cbTypeFilter.getSelectedItem();
        
        if (selectedType != null && !selectedType.equals("Semua")) sql.append(" AND type = ?");
        if (!searchKeyword.isEmpty()) sql.append(" AND LOWER(title) LIKE ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {
             
            int paramIndex = 1;
            if (selectedType != null && !selectedType.equals("Semua")) pst.setString(paramIndex++, selectedType);
            if (!searchKeyword.isEmpty()) pst.setString(paramIndex++, "%" + searchKeyword + "%");
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    
                    String finalPath = "image/" + rs.getString("image_path");
                    ImageIcon finalIcon = null;
                    try {
                        Image scaledImg = new ImageIcon(finalPath).getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH);
                        finalIcon = new ImageIcon(scaledImg);
                    } catch (Exception ex) { finalIcon = new ImageIcon(); }
                    
                    row.add(finalIcon);
                    row.add(rs.getString("title"));
                    row.add(rs.getString("type"));
                    row.add(rs.getString("genre"));
                    row.add(rs.getInt("chapters"));
                    row.add(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at") : "-");
                    comicsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void addComic() {
        String sql = "INSERT INTO comics (title, type, genre, image_path, chapters) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, txtTitle.getText().trim());
            pst.setString(2, txtType.getText().trim());
            pst.setString(3, txtGenre.getText().trim());
            pst.setString(4, txtImagePath.getText().trim());
            pst.setInt(5, Integer.parseInt(txtChapters.getText().trim()));
            pst.executeUpdate();
            
            txtTitle.setText(""); txtType.setText(""); txtGenre.setText("");
            txtImagePath.setText(""); txtChapters.setText("");
            
            loadComicData();
            JOptionPane.showMessageDialog(this, "Komik berhasil ditambahkan!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Pastikan input valid dan chapter berisi angka.");
        }
    }

    private void updateChapter() {
        int selectedRow = tableComics.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih komik dari tabel terlebih dahulu!");
            return;
        }
        int comicId = (int) comicsTableModel.getValueAt(selectedRow, 0);
        String newChapter = JOptionPane.showInputDialog(this, "Masukkan jumlah chapter baru:");
        if (newChapter != null && !newChapter.isEmpty()) {
            String sql = "UPDATE comics SET chapters = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(newChapter.trim()));
                pst.setInt(2, comicId);
                pst.executeUpdate();
                loadComicData();
                JOptionPane.showMessageDialog(this, "Chapter berhasil diupdate!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!");
            }
        }
    }

    private void deleteComic() {
        int selectedRow = tableComics.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih komik dari tabel terlebih dahulu!");
            return;
        }
        int comicId = (int) comicsTableModel.getValueAt(selectedRow, 0);
        if (JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus komik ini?", "Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM comics WHERE id = ?")) {
                pst.setInt(1, comicId);
                pst.executeUpdate();
                loadComicData();
                JOptionPane.showMessageDialog(this, "Komik dihapus!");
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ==========================================
    // LOGIC DATABASE AKUN (USERS)
    // ==========================================
    private void loadUserData() {
        usersTableModel.setRowCount(0);
        String search = txtSearchUser.getText().trim().toLowerCase();
        String sql = "SELECT id, username, password, role, created_at FROM users WHERE LOWER(username) LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + search + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("username"));
                    row.add(rs.getString("password")); 
                    row.add(rs.getString("role"));
                    // Asumsi kolom created_at ada di DB (bisa disesuaikan jika namanya berbeda)
                    row.add(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at") : "-");
                    usersTableModel.addRow(row);
                }
            }
        } catch (SQLException e) { 
            // Jika table users tidak punya created_at, modifikasi kueri di atas untuk menyesuaikan.
            e.printStackTrace(); 
        }
    }

    private void changeUserPassword() {
        int selectedRow = tableUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun dari tabel terlebih dahulu!");
            return;
        }
        int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
        String username = (String) usersTableModel.getValueAt(selectedRow, 1);
        
        String newPass = JOptionPane.showInputDialog(this, "Masukkan Password baru untuk akun: " + username);
        if (newPass != null && !newPass.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
                pst.setString(1, newPass.trim());
                pst.setInt(2, userId);
                pst.executeUpdate();
                loadUserData();
                JOptionPane.showMessageDialog(this, "Password akun berhasil diubah!");
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void changeUserRole() {
        int selectedRow = tableUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun dari tabel terlebih dahulu!");
            return;
        }
        int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
        String currentRole = (String) usersTableModel.getValueAt(selectedRow, 3);
        String username = (String) usersTableModel.getValueAt(selectedRow, 1);
        
        if (username.equals(adminUsername)) {
            JOptionPane.showMessageDialog(this, "Anda tidak dapat mengubah role Anda sendiri saat sedang login!");
            return;
        }

        String newRole = currentRole.equals("admin") ? "user" : "admin";
        int confirm = JOptionPane.showConfirmDialog(this, "Ubah role akun '" + username + "' menjadi " + newRole.toUpperCase() + "?", "Ubah Role", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement("UPDATE users SET role = ? WHERE id = ?")) {
                pst.setString(1, newRole);
                pst.setInt(2, userId);
                pst.executeUpdate();
                loadUserData();
                JOptionPane.showMessageDialog(this, "Role berhasil diubah!");
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void deleteUser() {
        int selectedRow = tableUsers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun dari tabel terlebih dahulu!");
            return;
        }
        int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
        String username = (String) usersTableModel.getValueAt(selectedRow, 1);

        if (username.equals(adminUsername)) {
            JOptionPane.showMessageDialog(this, "Anda tidak dapat menghapus akun Anda sendiri!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus akun '" + username + "' secara permanen?", "Hapus Akun", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                pst.setInt(1, userId);
                pst.executeUpdate();
                loadUserData();
                JOptionPane.showMessageDialog(this, "Akun dihapus!");
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ==========================================
    // UTILITAS & STYLING UI
    // ==========================================
    private void logout() {
        Login loginWindow = new Login();
        loginWindow.setExtendedState(this.getExtendedState());
        if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
            loginWindow.setBounds(this.getBounds());
        }
        loginWindow.setVisible(true);
        dispose();
    }

    private void addFormRow(JPanel panel, String labelText, JTextField textField, GridBagConstraints gbc, int yPos) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(textColor);
        gbc.gridx = 0; gbc.gridy = yPos; gbc.weightx = 0.1;
        panel.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.gridy = yPos; gbc.weightx = 0.9;
        panel.add(textField, gbc);
    }

    private JButton styleButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(accentColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
}