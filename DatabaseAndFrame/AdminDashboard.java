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
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelHeader = new JPanel();
        JLabel lblHeader = new JLabel("Hi, " + adminUsername + " (Admin Panel)", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        panelHeader.add(lblHeader);
        add(panelHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Judul", "Tipe", "Genre", "Chapter", "Tanggal Masuk"}, 0) {
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
        
        tableComics.setRowHeight(120); 
        tableComics.getColumnModel().getColumn(1).setPreferredWidth(90);
        tableComics.getColumnModel().getColumn(0).setPreferredWidth(40);
        
        add(new JScrollPane(tableComics), BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new GridLayout(3, 1));
        
        JPanel panelInput = new JPanel(new FlowLayout());
        txtTitle = new JTextField(10);
        txtType = new JTextField(8);
        txtGenre = new JTextField(8);
        txtImagePath = new JTextField(12);
        txtChapters = new JTextField(5);
        
        panelInput.add(new JLabel("Judul:")); panelInput.add(txtTitle);
        panelInput.add(new JLabel("Tipe:")); panelInput.add(txtType);
        panelInput.add(new JLabel("Genre:")); panelInput.add(txtGenre);
        panelInput.add(new JLabel("Image Path (image/'file name'):")); panelInput.add(txtImagePath);
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
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM comics")) {
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
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, txtTitle.getText());
            pst.setString(2, txtType.getText());
            pst.setString(3, txtGenre.getText());
            pst.setString(4, txtImagePath.getText());
            pst.setInt(5, Integer.parseInt(txtChapters.getText()));
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
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(newChapter));
                pstmt.setInt(2, comicId);
                pstmt.executeUpdate();
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