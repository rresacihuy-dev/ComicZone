import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class AdminDashboard extends JFrame {
    private String adminUsername;
    private JTable tableComics;
    private DefaultTableModel tableModel;
    private JTextField txtTitle, txtType, txtGenre, txtImagePath, txtChapters;
    
    private JTextField txtSearch;
    private JComboBox<String> cbTypeFilter;

    public AdminDashboard(String username) {
        this.adminUsername = username;
        setTitle("ComicZone - Admin Dashboard");
        setSize(1000, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelTopLayout = new JPanel();
        panelTopLayout.setLayout(new BoxLayout(panelTopLayout, BoxLayout.Y_AXIS));
        panelTopLayout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblHeader = new JLabel("Hi, " + adminUsername + " (Admin Panel)", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTopLayout.add(lblHeader);
        panelTopLayout.add(Box.createVerticalStrut(10));

        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilterBar.add(new JLabel("Cari Judul Komik:"));
        txtSearch = new JTextField(20);
        panelFilterBar.add(txtSearch);

        panelFilterBar.add(new JLabel("Filter Tipe:"));
        cbTypeFilter = new JComboBox<>(new String[]{"Semua", "Manga", "Manhwa", "Manhua"});
        panelFilterBar.add(cbTypeFilter);

        JButton btnSearch = new JButton("Cari");
        panelFilterBar.add(btnSearch);

        panelTopLayout.add(panelFilterBar);
        add(panelTopLayout, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Judul", "Tipe", "Genre", "Chapter", "Tanggal Masuk"}, 0) {
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
        tableComics.setRowHeight(115); 
        
        tableComics.getColumnModel().getColumn(0).setMinWidth(45);
        tableComics.getColumnModel().getColumn(0).setMaxWidth(45);
        tableComics.getColumnModel().getColumn(0).setPreferredWidth(45);
        
        tableComics.getColumnModel().getColumn(1).setMinWidth(90);
        tableComics.getColumnModel().getColumn(1).setMaxWidth(90);
        tableComics.getColumnModel().getColumn(1).setPreferredWidth(90);

        tableComics.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tableComics), BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new BorderLayout(5, 5));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createTitledBorder(" Data Komik "));
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(5, 5, 5, 5);
        grid.fill = GridBagConstraints.HORIZONTAL;

        txtTitle = new JTextField();
        txtType = new JTextField();
        txtGenre = new JTextField();
        txtImagePath = new JTextField();
        txtChapters = new JTextField();
        
        grid.gridx = 0; grid.gridy = 0; grid.weightx = 0.1; panelInput.add(new JLabel("Judul Komik:"), grid);
        grid.gridx = 1; grid.gridy = 0; grid.weightx = 0.9; panelInput.add(txtTitle, grid);
        
        grid.gridx = 0; grid.gridy = 1; grid.weightx = 0.1; panelInput.add(new JLabel("Tipe:"), grid);
        grid.gridx = 1; grid.gridy = 1; grid.weightx = 0.9; panelInput.add(txtType, grid);
        
        grid.gridx = 0; grid.gridy = 2; grid.weightx = 0.1; panelInput.add(new JLabel("Genre:"), grid);
        grid.gridx = 1; grid.gridy = 2; grid.weightx = 0.9; panelInput.add(txtGenre, grid);

        grid.gridx = 0; grid.gridy = 3; grid.weightx = 0.1; panelInput.add(new JLabel("Image Path:"), grid);
        grid.gridx = 1; grid.gridy = 3; grid.weightx = 0.9; panelInput.add(txtImagePath, grid);
        
        grid.gridx = 0; grid.gridy = 4; grid.weightx = 0.1; panelInput.add(new JLabel("Total Chapter:"), grid);
        grid.gridx = 1; grid.gridy = 4; grid.weightx = 0.9; panelInput.add(txtChapters, grid);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton btnAdd = new JButton("Tambah Komik");
        JButton btnUpdate = new JButton("Update Chapter");
        JButton btnDelete = new JButton("Hapus Komik");
        JButton btnLogout = new JButton("Logout");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnLogout);

        panelBottom.add(panelInput, BorderLayout.CENTER);
        panelBottom.add(panelButtons, BorderLayout.SOUTH);
        add(panelBottom, BorderLayout.SOUTH);

        loadComicsData();

        btnSearch.addActionListener(e -> loadComicsData());
        cbTypeFilter.addActionListener(e -> loadComicsData());

        btnAdd.addActionListener(e -> addComic());
        btnUpdate.addActionListener(e -> updateChapter());
        btnDelete.addActionListener(e -> deleteComic());
        btnLogout.addActionListener(e -> {
            new HomeGuest().setVisible(true);
            dispose();
        });
    }

    private void loadComicsData() {
        tableModel.setRowCount(0);
        
        StringBuilder sql = new StringBuilder("SELECT * FROM comics WHERE 1=1");
        String searchKeyword = txtSearch.getText().trim().toLowerCase();
        String selectedType = (String) cbTypeFilter.getSelectedItem();
        
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
                    row.add(rs.getTimestamp("created_at"));
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            
            txtTitle.setText("");
            txtType.setText("");
            txtGenre.setText("");
            txtImagePath.setText("");
            txtChapters.setText("");
            
            loadComicsData();
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
        int comicId = (int) tableModel.getValueAt(selectedRow, 0);
        String newChapter = JOptionPane.showInputDialog(this, "Masukkan jumlah chapter baru:");
        if (newChapter != null && !newChapter.isEmpty()) {
            String sql = "UPDATE comics SET chapters = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(newChapter.trim()));
                pst.setInt(2, comicId);
                pst.executeUpdate();
                loadComicsData();
                JOptionPane.showMessageDialog(this, "Chapter berhasil diupdate!");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
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
        int comicId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus komik ini?", "Hapus Komik", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM comics WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, comicId);
                pst.executeUpdate();
                loadComicsData();
                JOptionPane.showMessageDialog(this, "Komik berhasil dihapus!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}