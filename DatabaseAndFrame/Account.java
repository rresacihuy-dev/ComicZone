import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Account extends JFrame {
    private String username;
    private int userId;
    private JLabel lblCreatedAt;
    private JLabel lblTotalBookmarks;
    private JLabel lblTotalChapters;
    private JTextField txtNewUsername;
    private JPasswordField txtNewPassword;
    private JCheckBox chkShowPassword;

    private final Color bgColor = new Color(43, 45, 58);
    private final Color panelColor = new Color(55, 57, 73);
    private final Color accentColor = new Color(138, 114, 255);
    private final Color textColor = Color.WHITE;

    public Account(String username, int userId) {
        this.username = username;
        this.userId = userId;

        setTitle("ComicZone - Account Page");
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

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(bgColor);

        JPanel cardPanel = new JPanel(new BorderLayout(10, 20));
        cardPanel.setPreferredSize(new Dimension(740, 480));
        cardPanel.setBackground(panelColor);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblHeader = new JLabel("PENGATURAN AKUN", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(textColor);
        cardPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCenter.setBackground(panelColor);

        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setBackground(panelColor);
        panelLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 110)), 
                " Informasi ", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14), textColor
        ));

        JLabel lblName = new JLabel("Nama: " + this.username);
        JLabel lblId = new JLabel("ID User: #" + this.userId);
        lblCreatedAt = new JLabel("Terdaftar Sejak: Memuat...");
        lblTotalBookmarks = new JLabel("• Total Komik Tersimpan: Memuat...");
        lblTotalChapters = new JLabel("• Total Chapter Dibaca: Memuat...");
        
        Font fontFields = new Font("Segoe UI", Font.BOLD, 14);
        JLabel[] leftLabels = {lblName, lblId, lblCreatedAt, lblTotalBookmarks, lblTotalChapters};
        for (JLabel label : leftLabels) {
            label.setFont(fontFields);
            label.setForeground(textColor);
        }

        panelLeft.add(Box.createVerticalStrut(15));
        panelLeft.add(lblName);
        panelLeft.add(Box.createVerticalStrut(8));
        panelLeft.add(lblId);
        panelLeft.add(Box.createVerticalStrut(8));
        panelLeft.add(lblCreatedAt);
        panelLeft.add(Box.createVerticalStrut(25));
        
        JLabel lblStatTitle = new JLabel("Statistik Membaca:");
        lblStatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatTitle.setForeground(accentColor);
        panelLeft.add(lblStatTitle);
        panelLeft.add(Box.createVerticalStrut(10));
        panelLeft.add(lblTotalBookmarks);
        panelLeft.add(Box.createVerticalStrut(8));
        panelLeft.add(lblTotalChapters);

        JPanel panelRight = new JPanel(new GridBagLayout());
        panelRight.setBackground(panelColor);
        panelRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 110)), 
                " Manajemen Data ", 
                TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14), textColor
        ));
        
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(6, 10, 6, 10);
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1.0;

        grid.gridx = 0; grid.gridy = 0;
        JLabel lblNewUser = new JLabel("Username Baru:");
        lblNewUser.setForeground(textColor);
        panelRight.add(lblNewUser, grid);
        
        grid.gridx = 0; grid.gridy = 1;
        txtNewUsername = new JTextField();
        txtNewUsername.setFont(fontFields);
        txtNewUsername.setText(this.username);
        styleTextField(txtNewUsername);
        panelRight.add(txtNewUsername, grid);

        grid.gridx = 0; grid.gridy = 2;
        JLabel lblNewPass = new JLabel("Password Baru (Opsional):");
        lblNewPass.setForeground(textColor);
        panelRight.add(lblNewPass, grid);

        grid.gridx = 0; grid.gridy = 3;
        txtNewPassword = new JPasswordField();
        txtNewPassword.setFont(fontFields);
        styleTextField(txtNewPassword);
        panelRight.add(txtNewPassword, grid);

        grid.gridx = 0; grid.gridy = 4;
        grid.insets = new Insets(0, 10, 10, 10);
        chkShowPassword = new JCheckBox("Tampilkan Password");
        chkShowPassword.setBackground(panelColor);
        chkShowPassword.setForeground(textColor);
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtNewPassword.setEchoChar((char) 0);
            } else {
                txtNewPassword.setEchoChar('•');
            }
        });
        panelRight.add(chkShowPassword, grid);

        grid.gridx = 0; grid.gridy = 5;
        grid.insets = new Insets(15, 10, 5, 10);
        JButton btnSave = new JButton("Simpan Perubahan");
        btnSave.setBackground(accentColor);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelRight.add(btnSave, grid);

        grid.gridx = 0; grid.gridy = 6;
        grid.insets = new Insets(20, 10, 0, 10);
        JButton btnDeleteAccount = new JButton("Hapus Akun Permanen");
        btnDeleteAccount.setBackground(new Color(220, 53, 69));
        btnDeleteAccount.setForeground(Color.WHITE);
        btnDeleteAccount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeleteAccount.setFocusPainted(false);
        btnDeleteAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelRight.add(btnDeleteAccount, grid);

        panelCenter.add(panelLeft);
        panelCenter.add(panelRight);
        cardPanel.add(panelCenter, BorderLayout.CENTER);

        wrapperPanel.add(cardPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        loadUserStats();

        btnSave.addActionListener(e -> updateAccount());
        btnDeleteAccount.addActionListener(e -> deleteAccount());
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

    private void backToHome() {
        HomeUser home = new HomeUser(username, userId);
        home.setExtendedState(this.getExtendedState());
        if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
            home.setBounds(this.getBounds());
        }
        home.setVisible(true);
        dispose();
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
                lblCreatedAt.setText("Terdaftar Sejak: " + (timestamp != null ? timestamp.toString().substring(0, 10) : "-"));
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
        } catch (SQLException ex) { ex.printStackTrace(); }
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
            
            Account account = new Account(this.username, this.userId);
            account.setExtendedState(this.getExtendedState());
            if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                account.setBounds(this.getBounds());
            }
            account.setVisible(true);
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
                Login login = new Login();
                login.setExtendedState(this.getExtendedState());
                if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    login.setBounds(this.getBounds());
                }
                login.setVisible(true);
                dispose();
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}