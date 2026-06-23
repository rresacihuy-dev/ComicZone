import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;

public class Account extends JFrame {
    private String username;
    private int userId;
    private JLabel lblCreatedAt;
    private JLabel lblTotalBookmarks;
    private JLabel lblTotalChapters;
    private JTextField txtNewUsername;
    private JPasswordField txtNewPassword;

    public Account(String username, int userId) {
        this.username = username;
        this.userId = userId;

        setTitle("ComicZone - Account Page");
        setSize(850, 600);
        setMinimumSize(new Dimension(780, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(235, 238, 242));

        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setPreferredSize(new Dimension(740, 480));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 219), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblHeader = new JLabel("PENGATURAN AKUN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(43, 45, 66));
        cardPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCenter.setBackground(Color.WHITE);

        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setBackground(Color.WHITE);
        panelLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)), 
                " Informasi ", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14), new Color(100, 110, 120)
        ));

        JLabel lblName = new JLabel("Nama: " + this.username);
        JLabel lblId = new JLabel("ID User: #" + this.userId);
        lblCreatedAt = new JLabel("Terdaftar Sejak: Memuat...");
        lblTotalBookmarks = new JLabel("• Total Komik Tersimpan: Memuat...");
        lblTotalChapters = new JLabel("• Total Chapter Dibaca: Memuat...");
        
        Font fontFields = new Font("Segoe UI", Font.BOLD, 14);
        lblName.setFont(fontFields);
        lblId.setFont(fontFields);
        lblCreatedAt.setFont(fontFields);
        lblTotalBookmarks.setFont(fontFields);
        lblTotalChapters.setFont(fontFields);

        panelLeft.add(Box.createVerticalStrut(15));
        panelLeft.add(lblName);
        panelLeft.add(Box.createVerticalStrut(8));
        panelLeft.add(lblId);
        panelLeft.add(Box.createVerticalStrut(8));
        panelLeft.add(lblCreatedAt);
        panelLeft.add(Box.createVerticalStrut(25));
        
        JLabel lblStatTitle = new JLabel("Statistik Membaca:");
        lblStatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelLeft.add(lblStatTitle);
        panelLeft.add(Box.createVerticalStrut(10));
        panelLeft.add(lblTotalBookmarks);
        panelLeft.add(Box.createVerticalStrut(8));
        panelLeft.add(lblTotalChapters);

        JPanel panelRight = new JPanel(new GridBagLayout());
        panelRight.setBackground(Color.WHITE);
        panelRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)), 
                " Manajemen Data ", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14), new Color(100, 110, 120)
        ));
        
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(6, 10, 6, 10);
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1.0;

        grid.gridx = 0; grid.gridy = 0;
        panelRight.add(new JLabel("Username Baru:"), grid);
        
        grid.gridx = 0; grid.gridy = 1;
        txtNewUsername = new JTextField();
        txtNewUsername.setFont(fontFields);
        txtNewUsername.setText(this.username);
        panelRight.add(txtNewUsername, grid);

        grid.gridx = 0; grid.gridy = 2;
        panelRight.add(new JLabel("Password Baru (Opsional):"), grid);

        grid.gridx = 0; grid.gridy = 3;
        txtNewPassword = new JPasswordField();
        txtNewPassword.setFont(fontFields);
        panelRight.add(txtNewPassword, grid);

        grid.gridx = 0; grid.gridy = 4;
        grid.insets = new Insets(15, 10, 5, 10);
        JButton btnSave = new JButton("Simpan Perubahan");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelRight.add(btnSave, grid);

        grid.gridx = 0; grid.gridy = 5;
        grid.insets = new Insets(20, 10, 0, 10);
        JButton btnDeleteAccount = new JButton("Hapus Akun Permanen");
        btnDeleteAccount.setBackground(new Color(220, 53, 69));
        btnDeleteAccount.setForeground(Color.WHITE);
        btnDeleteAccount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeleteAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelRight.add(btnDeleteAccount, grid);

        panelCenter.add(panelLeft);
        panelCenter.add(panelRight);
        cardPanel.add(panelCenter, BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBottom.setBackground(Color.WHITE);
        JButton btnBack = new JButton("Kembali ke Home");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelBottom.add(btnBack);
        cardPanel.add(panelBottom, BorderLayout.SOUTH);

        add(cardPanel);

        loadUserStats();
        btnSave.addActionListener(e -> updateAccount());
        btnDeleteAccount.addActionListener(e -> deleteAccount());
        btnBack.addActionListener(e -> {
            new HomeUser(this.username, this.userId).setVisible(true);
            dispose();
        });
    }

    private void loadUserStats() {
        String sqlUser = "SELECT created_at FROM users WHERE id = ?";
        String sqlCount = "SELECT COUNT(*) AS total_komik FROM bookmarks WHERE user_id = ?";
        String sqlSum = "SELECT SUM(current_chapter) AS total_chapter FROM bookmarks WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstUser = conn.prepareStatement(sqlUser);
             PreparedStatement pstCount = conn.prepareStatement(sqlCount);
             PreparedStatement pstSum = conn.prepareStatement(sqlSum)) {
             
            pstUser.setInt(1, this.userId);
            ResultSet rsUser = pstUser.executeQuery();
            if (rsUser.next()) {
                Timestamp timestamp = rsUser.getTimestamp("created_at");
                if (timestamp != null) {
                    lblCreatedAt.setText("Terdaftar Sejak: " + timestamp.toString().substring(0, 10));
                } else {
                    lblCreatedAt.setText("Terdaftar Sejak: -");
                }
            }

            pstCount.setInt(1, this.userId);
            ResultSet rsCount = pstCount.executeQuery();
            if (rsCount.next()) {
                lblTotalBookmarks.setText("• Total Komik Tersimpan: " + rsCount.getInt("total_komik"));
            }

            pstSum.setInt(1, this.userId);
            ResultSet rsSum = pstSum.executeQuery();
            if (rsSum.next()) {
                lblTotalChapters.setText("• Total Chapter Dibaca: " + rsSum.getInt("total_chapter"));
            }
             
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateAccount() {
        String newUsername = txtNewUsername.getText().trim();
        String newPassword = new String(txtNewPassword.getPassword()).trim();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            PreparedStatement pstmt;

            if (!newPassword.isEmpty()) {
                sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newUsername);
                pstmt.setString(2, newPassword);
                pstmt.setInt(3, this.userId);
            } else {
                sql = "UPDATE users SET username = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newUsername);
                pstmt.setInt(2, this.userId);
            }

            pstmt.executeUpdate();
            this.username = newUsername;
            JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui!");
            
            new Account(this.username, this.userId).setVisible(true);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Username sudah digunakan!");
            ex.printStackTrace();
        }
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus akun permanen?", "Peringatan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql1 = "DELETE FROM bookmarks WHERE user_id = ?";
                try (PreparedStatement pst1 = conn.prepareStatement(sql1)) { pst1.setInt(1, this.userId); pst1.executeUpdate(); }

                String sql2 = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement pst2 = conn.prepareStatement(sql2)) { pst2.setInt(1, this.userId); pst2.executeUpdate(); }

                JOptionPane.showMessageDialog(this, "Akun berhasil dihapus.");
                new Login().setVisible(true);
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}