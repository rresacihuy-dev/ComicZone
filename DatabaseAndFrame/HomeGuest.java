import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class HomeGuest extends JFrame {
    private JTable tableComics;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbType;

    // Warna tema gelap mirip halaman login
    private final Color bgColor = new Color(43, 45, 58);
    private final Color panelColor = new Color(55, 57, 73);
    private final Color accentColor = new Color(138, 114, 255); // Ungu
    private final Color textColor = Color.WHITE;

    public HomeGuest() {
        setTitle("ComicZone - Home Page (Guest)");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // --- TOP BAR (LOGO SEBAGAI TOMBOL HOME) ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelTop.setBackground(bgColor);
        
        ImageIcon originalLogo = new ImageIcon("assets/logo.png");
        Image scaledLogo = originalLogo.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(scaledLogo));
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Refresh halaman HomeGuest dan pertahankan fullscreen
                HomeGuest home = new HomeGuest();
                home.setExtendedState(getExtendedState());
                home.setVisible(true);
                dispose();
            }
        });
        panelTop.add(lblLogo);
        add(panelTop, BorderLayout.NORTH);

        // --- SIDEBAR ---
        JPanel panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(panelColor);
        panelSidebar.setPreferredSize(new Dimension(150, 0));
        panelSidebar.setBorder(new EmptyBorder(20, 10, 0, 10));

        JButton btnLogin = styleButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(120, 40));
        panelSidebar.add(btnLogin);
        add(panelSidebar, BorderLayout.WEST);

        // --- CONTENT AREA ---
        JPanel panelContent = new JPanel(new BorderLayout());
        panelContent.setBackground(bgColor);
        panelContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Container Atas (Banner + Filter)
        JPanel panelTopContent = new JPanel();
        panelTopContent.setLayout(new BoxLayout(panelTopContent, BoxLayout.Y_AXIS));
        panelTopContent.setBackground(bgColor);

        // 1. Banner Custom
        JPanel panelBanner = new JPanel() {
            private Image bannerImg = new ImageIcon("assets/banner.jpeg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bannerImg != null) {
                    g.drawImage(bannerImg, 0, 0, getWidth(), getHeight(), this);
                    // Overlay gelap transparan agar teks terbaca
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelBanner.setPreferredSize(new Dimension(800, 150));
        panelBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panelBanner.setLayout(new GridBagLayout()); // Untuk menengahkan konten
        
        JLabel lblBannerTitle = new JLabel("Why Sign Up?");
        lblBannerTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblBannerTitle.setForeground(Color.WHITE);
        
        JLabel lblBannerDesc = new JLabel("Bookmark manga kesukaanmu dan pantau semua chapter yang telah kamu baca!");
        lblBannerDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBannerDesc.setForeground(Color.LIGHT_GRAY);
        
        JPanel bannerTextPanel = new JPanel();
        bannerTextPanel.setLayout(new BoxLayout(bannerTextPanel, BoxLayout.Y_AXIS));
        bannerTextPanel.setOpaque(false);
        lblBannerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBannerDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        bannerTextPanel.add(lblBannerTitle);
        bannerTextPanel.add(Box.createVerticalStrut(10));
        bannerTextPanel.add(lblBannerDesc);
        
        panelBanner.add(bannerTextPanel);
        panelTopContent.add(panelBanner);
        panelTopContent.add(Box.createVerticalStrut(15));

        // 2. Filter Bar
        JPanel panelFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilterBar.setBackground(bgColor);
        
        JLabel lblSearch = new JLabel("Cari Judul:");
        lblSearch.setForeground(textColor);
        panelFilterBar.add(lblSearch);
        
        txtSearch = new JTextField(20);
        panelFilterBar.add(txtSearch);

        JLabel lblType = new JLabel("Tipe:");
        lblType.setForeground(textColor);
        panelFilterBar.add(lblType);
        
        cbType = new JComboBox<>(new String[]{"Semua", "Manga", "Manhwa", "Manhua"});
        panelFilterBar.add(cbType);

        JButton btnFilter = styleButton("Cari");
        panelFilterBar.add(btnFilter);
        
        panelTopContent.add(panelFilterBar);
        panelContent.add(panelTopContent, BorderLayout.NORTH);

        // --- TABLE ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Gambar", "Judul", "Tipe", "Genre", "Chapter", "Update Terakhir"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableComics = new JTable(tableModel);
        styleTable(tableComics);
        
        tableComics.getColumnModel().getColumn(0).setMinWidth(0);
        tableComics.getColumnModel().getColumn(0).setMaxWidth(0);
        tableComics.getColumnModel().getColumn(1).setMinWidth(90);
        tableComics.getColumnModel().getColumn(1).setMaxWidth(90);
        
        JScrollPane scrollPane = new JScrollPane(tableComics);
        scrollPane.getViewport().setBackground(panelColor);
        scrollPane.setBorder(BorderFactory.createLineBorder(panelColor, 2));
        panelContent.add(scrollPane, BorderLayout.CENTER);
        
        add(panelContent, BorderLayout.CENTER);
        loadComics();

        // --- LISTENERS ---
        btnFilter.addActionListener(e -> loadComics());
        cbType.addActionListener(e -> loadComics());

        btnLogin.addActionListener(e -> {
            Login loginWindow = new Login();
            loginWindow.setExtendedState(this.getExtendedState()); // Tahan Fullscreen
            loginWindow.setVisible(true);
            dispose();
        });
    }

    // Fungsi Styling Button
    private JButton styleButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(accentColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    // Fungsi Styling Tabel
    private void styleTable(JTable table) {
        table.setRowHeight(115); 
        table.setBackground(panelColor);
        table.setForeground(textColor);
        table.setGridColor(bgColor);
        table.setSelectionBackground(accentColor);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 32, 44));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void loadComics() {
        tableModel.setRowCount(0);
        StringBuilder sql = new StringBuilder("SELECT id, image_path, title, type, genre, chapters, last_update FROM comics WHERE 1=1");
        String searchKeyword = txtSearch.getText().trim().toLowerCase();
        String selectedType = (String) cbType.getSelectedItem();
        
        if (selectedType != null && !selectedType.equals("Semua")) sql.append(" AND type = ?");
        if (!searchKeyword.isEmpty()) sql.append(" AND LOWER(title) LIKE ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {
             
            int paramIndex = 1;
            if (selectedType != null && !selectedType.equals("Semua")) pst.setString(paramIndex++, selectedType);
            if (!searchKeyword.isEmpty()) pst.setString(paramIndex++, "%" + searchKeyword + "%");
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    
                    String finalPath = "image/" + rs.getString("image_path");
                    ImageIcon finalIcon = null;
                    try {
                        Image scaledImg = new ImageIcon(finalPath).getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH);
                        finalIcon = new ImageIcon(scaledImg);
                    } catch (Exception ex) { finalIcon = new ImageIcon(); }
                    
                    row.add(finalIcon);
                    row.add(rs.getString("title"));
                    row.add(rs.getString("type"));
                    row.add(rs.getString("genre"));
                    row.add(rs.getInt("chapters"));
                    row.add(rs.getTimestamp("last_update"));
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeGuest().setVisible(true));
    }
}