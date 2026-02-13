package vista;

import controlador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;

public class InterfazLoginMejorada extends JPanel {
    private JTextField txtCorreo;
    private JPasswordField txtContraseña;
    private JButton btnLogin, btnSalir;
    
    public InterfazLoginMejorada() {
        initComponents();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Panel izquierdo (logo y título)
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setBackground(new Color(255, 204, 102));
        panelIzquierdo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelIzquierdo.setPreferredSize(new Dimension(400, 500));
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        
        // Logo
        JLabel lblLogo = new JLabel();
        try {
            lblLogo.setIcon(new ImageIcon(getClass().getResource("/img/logo cafe.jpg")));
        } catch (Exception e) {
            lblLogo.setText("☕");
            lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel("Cafeteria ESPE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelIzquierdo.add(Box.createVerticalGlue());
        panelIzquierdo.add(lblLogo);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(lblTitulo);
        panelIzquierdo.add(Box.createVerticalGlue());
        
        // Panel derecho (formulario)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título del formulario
        JLabel lblTituloForm = new JLabel("INICIAR SESIÓN");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloForm.setForeground(new Color(255, 153, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelDerecho.add(lblTituloForm, gbc);
        
        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(153, 153, 153));
        gbc.gridy = 1;
        panelDerecho.add(lblSubtitulo, gbc);
        
        // Campo correo
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblCorreo = new JLabel("Correo electrónico:");
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelDerecho.add(lblCorreo, gbc);
        
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 153, 0), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtCorreo.setPreferredSize(new Dimension(250, 35));
        panelDerecho.add(txtCorreo, gbc);
        
        // Campo contraseña
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel lblContraseña = new JLabel("Contraseña:");
        lblContraseña.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelDerecho.add(lblContraseña, gbc);
        
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        txtContraseña = new JPasswordField(20);
        txtContraseña.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContraseña.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 153, 0), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtContraseña.setPreferredSize(new Dimension(250, 35));
        panelDerecho.add(txtContraseña, gbc);
        
        // Botón Login
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 20, 10, 20);
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(255, 153, 51));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(250, 40));
        btnLogin.setBorder(BorderFactory.createRaisedBevelBorder());
        panelDerecho.add(btnLogin, gbc);
        
        // Botón Salir
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 20, 20, 20);
        btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.setBackground(new Color(153, 153, 153));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setPreferredSize(new Dimension(250, 40));
        btnSalir.setBorder(BorderFactory.createRaisedBevelBorder());
        panelDerecho.add(btnSalir, gbc);
        
        // Nota informativa
        gbc.gridy = 8;
        JLabel lblNota = new JLabel("<html><center>El sistema identificará automáticamente<br>tu rol según tus credenciales</center></html>");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(102, 102, 102));
        lblNota.setHorizontalAlignment(SwingConstants.CENTER);
        panelDerecho.add(lblNota, gbc);
        
        // Agregar paneles al layout principal
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);
    }
    
    private void configurarEventos() {
        btnLogin.addActionListener(evt -> iniciarSesion());
        txtContraseña.addActionListener(evt -> iniciarSesion());
        
        btnSalir.addActionListener(evt -> {
            int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }
    
    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String contraseña = new String(txtContraseña.getPassword()).trim();
        
        // Validar campos vacíos
        if (correo.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese su correo y contraseña.",
                "Campos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar login (el controlador identificará el rol automáticamente)
        if (ControladorPrincipal.validarLoginAutomatico(correo, contraseña)) {
            // Obtener el rol identificado
            String rolIdentificado = ControladorPrincipal.getRolActual();
            
            JOptionPane.showMessageDialog(this,
                "¡Bienvenido al sistema!\n" +
                "Rol identificado: " + rolIdentificado,
                "Inicio de sesión exitoso",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Cerrar ventana de login
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            
            // Navegar según el rol (ahora usando la interfaz unificada)
            ControladorPrincipal.navegarInterfazUnificada();
        }
    }
}
