import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    
    public Login() {
        setTitle("ComicZone - Login");
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
        
        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 30, 380, 40);
        formPanel.add(lblTitle);
        
        JLabel lblAskSignUp = new JLabel("Don't have an account?", SwingConstants.RIGHT);
        lblAskSignUp.setForeground(new Color(200, 200, 200));
        lblAskSignUp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAskSignUp.setBounds(105, 75, 123, 25);
        formPanel.add(lblAskSignUp);
        
        JLabel lblSignUp = new JLabel("<html><b>Sign Up</b></html>");
        lblSignUp.setForeground(new Color(160, 130, 255));
        lblSignUp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSignUp.setBounds(233, 75, 43, 25);
        formPanel.add(lblSignUp);
        
        lblSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SignUp signUpFrame = new SignUp();
                
                // --- PERTAHANKAN FULLSCREEN/UKURAN LAYAR ---
                signUpFrame.setExtendedState(Login.this.getExtendedState());
                if (Login.this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    signUpFrame.setBounds(Login.this.getBounds());
                }
                
                signUpFrame.setVisible(true);
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

        JButton btnLogin = new JButton("ENTER");
        btnLogin.setBounds(40, 330, 300, 45);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(143, 115, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        formPanel.add(btnLogin);

        backgroundPanel.add(formPanel);
        btnLogin.addActionListener(e -> loginProcess());
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
                
                JOptionPane.showMessageDialog(this, "Login Berhasil!");
                
                if (role.equals("admin")) {
                    AdminDashboard adminWindow = new AdminDashboard(username);
                    
                    // --- PERTAHANKAN FULLSCREEN/UKURAN LAYAR ---
                    adminWindow.setExtendedState(this.getExtendedState());
                    if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                        adminWindow.setBounds(this.getBounds());
                    }
                    adminWindow.setVisible(true);
                } else {
                    HomeUser homeWindow = new HomeUser(username, userId);
                    
                    // --- PERTAHANKAN FULLSCREEN/UKURAN LAYAR ---
                    homeWindow.setExtendedState(this.getExtendedState());
                    if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                        homeWindow.setBounds(this.getBounds());
                    }
                    homeWindow.setVisible(true);
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