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

    public AdminDashboard(String username) {
        this.adminUsername = username;
        setTitle("ComicZone - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel panelHeader = new JPanel();
        panelHeader.add(new JLabel("Hi, " + adminUsername, SwingConstants.CENTER));
        add(panelHeader, BorderLayout.NORTH);

        // Tabel Komik
        tableModel = new DefaultTableModel(new String[]{"ID", "Judul", "Tipe", "Genre", "Path Gambar", "Chapter", "Tanggal Masuk"}, 0);
        tableComics = new JTable(tableModel);
        add(new JScrollPane(tableComics), BorderLayout.CENTER);

        // Panel Input & Tombol
        JPanel panelBottom = new JPanel(new GridLayout(3, 1));
        
        JPanel panelInput = new JPanel(new FlowLayout());
        txtTitle = new JTextField(10);
        txtType = new JTextField(8);
        txtGenre = new JTextField(8);
        txtImagePath = new JTextField(10);
        txtChapters = new JTextField(5);
        
        panelInput.add(new JLabel("Judul:")); panelInput.add(txtTitle);
        panelInput.add(new JLabel("Tipe:")); panelInput.add(txtType);
        panelInput.add(new JLabel("Genre:")); panelInput.add(txtGenre);
        panelInput.add(new JLabel("Image Path:")); panelInput.add(txtImagePath);
        panelInput.add(new JLabel("Chapter:")); panelInput.add(txtChapters);
        
        JPanel panelButtons = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Tambah Komik");
        JButton btnUpdate = new JButton("Update Chapter");
        JButton btnDelete = new JButton("Hapus Komik");
        JButton btnLogout = new JButton("Logout");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnLogout);

        panelBottom.add(panelInput);
        panelBottom.add(panelButtons);
        add(panelBottom, BorderLayout.SOUTH);

        loadComicsData();

        // Action Listeners
        btnAdd.addActionListener(e -> addComic());
        btnUpdate.addActionListener(e -> updateChapter());
        btnDelete.addActionListener(e -> deleteComic());
        btnLogout.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadComicsData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM comics")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("type"));
                row.add(rs.getString("genre"));
                row.add(rs.getString("image_path"));
                row.add(rs.getInt("chapters"));
                row.add(rs.getTimestamp("created_at"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addComic() {
        String sql = "INSERT INTO comics (title, type, genre, image_path, chapters) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtTitle.getText());
            pstmt.setString(2, txtType.getText());
            pstmt.setString(3, txtGenre.getText());
            pstmt.setString(4, txtImagePath.getText());
            pstmt.setInt(5, Integer.parseInt(txtChapters.getText()));
            pstmt.executeUpdate();
            loadComicsData();
            JOptionPane.showMessageDialog(this, "Komik berhasil ditambahkan!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Pastikan data valid.");
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
        if (newChapter != null) {
            String sql = "UPDATE comics SET chapters = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(newChapter));
                pstmt.setInt(2, comicId);
                pstmt.executeUpdate();
                loadComicsData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteComic() {
        int selectedRow = tableComics.getSelectedRow();
        if (selectedRow == -1) return;
        int comicId = (int) tableModel.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM comics WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, comicId);
            pstmt.executeUpdate();
            loadComicsData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}