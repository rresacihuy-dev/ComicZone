import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    
    public Login() {
        setTitle("ComicZone - Login");
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
        
        JLabel lblTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(100, 30, 200, 30);
        formPanel.add(lblTitle);
        
        JLabel lblAskSignUp = new JLabel("<html><font color='black'>Belum punya account? </font></html>");
        lblAskSignUp.setBounds(110, 60, 200, 25);
        formPanel.add(lblAskSignUp);
        
        JLabel lblSignUp = new JLabel("<html><font color='blue'><u>Sign Up</u></font></html>");
        lblSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSignUp.setBounds(244, 60, 200, 25);
        formPanel.add(lblSignUp);
        
        lblSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new SignUp().setVisible(true);
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

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(50, 290, 300, 35);
        formPanel.add(btnLogin);

        add(formPanel);

        btnLogin.addActionListener(e -> loginProcess());
    }

    private void loginProcess() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        String sql = "SELECT id, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String role = rs.getString("role");
                
                if (role.equals("admin")) {
                    new AdminDashboard(username).setVisible(true);
                } else {
                    new HomeUser(username, userId).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}