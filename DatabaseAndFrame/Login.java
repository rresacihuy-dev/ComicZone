import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public Login() {
        setTitle("ComicZone - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitle = new JLabel("Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(100, 20, 200, 30);
        add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 80, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 80, 180, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 120, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 120, 180, 25);
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 160, 100, 30);
        add(btnLogin);

        JLabel lblAskSignUp = new JLabel("<html><font color='black'>belum punya account?</font></html>");
        lblAskSignUp.setBounds(115, 45, 200, 25);
        add(lblAskSignUp);
        
        JLabel lblSignUp = new JLabel("<html><font color='blue'><u>sign up</u></font></html>");
        lblSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSignUp.setBounds(245, 45, 200, 25);
        add(lblSignUp);

        // Pindah ke halaman Sign Up
        lblSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new SignUp().setVisible(true);
                dispose();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");

                if (role.equals("admin")) {
                    // new AdminDashboard(username).setVisible(true); // Akan kita buat di tahap selanjutnya
                    JOptionPane.showMessageDialog(this, "Login Admin Berhasil!");
                } else {
                    // new HomeAfterLogin(username, userId).setVisible(true); // Akan kita buat di tahap selanjutnya
                    JOptionPane.showMessageDialog(this, "Login Berhasil!");
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan pada database.");
            ex.printStackTrace();
        }
    }

    // Main method sementara untuk menjalankan aplikasi
    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}