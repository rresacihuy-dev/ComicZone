import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class SignUp extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;

    public SignUp() {
        setTitle("ComicZone - Sign Up");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            Image bg = new ImageIcon("assets/login-sign up background.gif").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        setContentPane(backgroundPanel);

        JPanel formPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 45, 180)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        formPanel.setPreferredSize(new Dimension(380, 420));
        formPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 30, 380, 40);
        formPanel.add(lblTitle);

        JLabel lblAskLogin = new JLabel("Already have an account? ", SwingConstants.RIGHT);
        lblAskLogin.setForeground(new Color(200, 200, 200));
        lblAskLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAskLogin.setBounds(100, 75, 140, 25);
        formPanel.add(lblAskLogin);
        
        JLabel lblLogin = new JLabel("<html><b>Sign In</b></html>");
        lblLogin.setForeground(new Color(160, 130, 255));
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLogin.setBounds(241, 75, 43, 25);
        formPanel.add(lblLogin);
        
        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Login loginFrame = new Login();
                
                loginFrame.setExtendedState(SignUp.this.getExtendedState());
                if (SignUp.this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    loginFrame.setBounds(SignUp.this.getBounds());
                }
                
                loginFrame.setVisible(true);
                dispose();
            }
        });

        JLabel lblUser = new JLabel("USERNAME");
        lblUser.setForeground(new Color(200, 200, 200));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblUser.setBounds(40, 130, 100, 20);
        formPanel.add(lblUser);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(40, 150, 300, 40);
        styleTextField(txtUsername);
        formPanel.add(txtUsername);

        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setForeground(new Color(200, 200, 200));
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblPass.setBounds(40, 210, 100, 20);
        formPanel.add(lblPass);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 230, 300, 40);
        styleTextField(txtPassword);
        formPanel.add(txtPassword);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setBounds(40, 280, 200, 20);
        chkShowPassword.setForeground(new Color(200, 200, 200));
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        formPanel.add(chkShowPassword);

        JButton btnRegister = new JButton("SIGN UP");
        btnRegister.setBounds(40, 330, 300, 45);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBackground(new Color(143, 115, 255));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createEmptyBorder());
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        formPanel.add(btnRegister);

        backgroundPanel.add(formPanel);
        btnRegister.addActionListener(e -> registerProcess());
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(50, 50, 65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 90), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
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
            
            Login loginWindow = new Login();
            
            loginWindow.setExtendedState(this.getExtendedState());
            if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                loginWindow.setBounds(this.getBounds());
            }
            
            loginWindow.setVisible(true);
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Username sudah terdaftar!");
        }
    }
}