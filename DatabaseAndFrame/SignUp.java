import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SignUp extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;

    public SignUp() {
        setTitle("ComicZone - Sign Up");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel formPanel = new JPanel();
        formPanel.setPreferredSize(new Dimension(400, 380));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        formPanel.setLayout(null);

        JLabel lblTitle = new JLabel("SIGN UP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(100, 30, 210, 30);
        formPanel.add(lblTitle);

        JLabel lblAskLogin = new JLabel("<html><font color='black'>Sudah punya account? </font></html>");
        lblAskLogin.setBounds(121, 60, 200, 25);
        formPanel.add(lblAskLogin);
        
        JLabel lblLogin = new JLabel("<html><font color='blue'><u>Login</u></font></html>");
        lblLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLogin.setBounds(255, 60, 200, 25);
        formPanel.add(lblLogin);
        
        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new Login().setVisible(true);
                dispose();
            }
        });

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 105, 100, 25);
        formPanel.add(lblUser);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(50, 130, 300, 30);
        formPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 170, 100, 25);
        formPanel.add(lblPass);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 195, 300, 30);
        formPanel.add(txtPassword);

        chkShowPassword = new JCheckBox("Tampilkan Password");
        chkShowPassword.setBounds(50, 230, 200, 20);
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('*');
            }
        });
        formPanel.add(chkShowPassword);

        JButton btnRegister = new JButton("Daftar Akun");
        btnRegister.setBounds(50, 290, 300, 35);
        formPanel.add(btnRegister);

        add(formPanel);

        btnRegister.addActionListener(e -> registerProcess());
    }

    private void registerProcess() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Field tidak boleh kosong!");
            return;
        }

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan Login.");
            new Login().setVisible(true);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Username sudah terdaftar!");
        }
    }
}