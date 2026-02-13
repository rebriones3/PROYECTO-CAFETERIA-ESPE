package vista;

import controlador.*;
import modelo.Rol;
import javax.swing.*;
import java.awt.*;

public class InterfazUnificada extends JFrame {
    private String usuarioActual;
    private Rol rolActual;
    private JPanel panelContenido;
    private JPanel panelMenu;
    
    public InterfazUnificada(String usuario, Rol rol) {
        this.usuarioActual = usuario;
        this.rolActual = rol;
        
        initComponents();
        construirMenuSegunPermisos();
        mostrarPanelBienvenida();
    }
    
    private void initComponents() {
        setTitle("Sistema Cafetería ESPE - " + rolActual.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        
        // Panel superior (header)
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel izquierdo (menú)
        panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(new Color(51, 51, 51));
        panelMenu.setPreferredSize(new Dimension(250, 0));
        
        JScrollPane scrollMenu = new JScrollPane(panelMenu);
        scrollMenu.setBorder(BorderFactory.createEmptyBorder());
        add(scrollMenu, BorderLayout.WEST);
        
        // Panel central (contenido)
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);
        add(panelContenido, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 153, 51));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("SISTEMA CAFETERÍA ESPE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        
        // Info usuario
        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelUsuario.setOpaque(false);
        
        JLabel lblUsuario = new JLabel(" " + usuarioActual);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(Color.WHITE);
        
        JLabel lblRol = new JLabel("| Rol: " + rolActual.getNombre());
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRol.setForeground(Color.WHITE);
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(204, 51, 0));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        panelUsuario.add(lblUsuario);
        panelUsuario.add(lblRol);
        panelUsuario.add(Box.createHorizontalStrut(20));
        panelUsuario.add(btnCerrarSesion);
        
        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(panelUsuario, BorderLayout.EAST);
        
        return panel;
    }
    
    private void construirMenuSegunPermisos() {
        panelMenu.removeAll();
        
        // Título del menú
        JLabel lblTituloMenu = new JLabel("  MENÚ DE OPCIONES");
        lblTituloMenu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloMenu.setForeground(Color.WHITE);
        lblTituloMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        lblTituloMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelMenu.add(lblTituloMenu);
        
        // SECCIÓN ESTUDIANTE
        if (tieneAlgunPermiso(Rol.Permisos.obtenerPermisosEstudiante())) {
            agregarSeccionMenu(" PEDIDOS", new Color(102, 153, 255));
            
            if (rolActual.tienePermiso(Rol.Permisos.VER_MENU)) {
                agregarBotonMenu("Ver Menú", e -> mostrarMenu());
            }
            if (rolActual.tienePermiso(Rol.Permisos.HACER_PEDIDO)) {
                agregarBotonMenu("Realizar Pedido", e -> realizarPedido());
            }
            if (rolActual.tienePermiso(Rol.Permisos.REALIZAR_PAGO)) {
                agregarBotonMenu("Procesar Pago", e -> procesarPago());
            }
        }
        
        // SECCIÓN PERSONAL
        if (tieneAlgunPermiso(Rol.Permisos.obtenerPermisosPersonal())) {
            agregarSeccionMenu(" COCINA", new Color(255, 153, 0));
            
            if (rolActual.tienePermiso(Rol.Permisos.VER_PEDIDOS)) {
                agregarBotonMenu("Ver Pedidos", e -> verPedidos());
            }
            if (rolActual.tienePermiso(Rol.Permisos.CAMBIAR_ESTADO_PEDIDO)) {
                agregarBotonMenu("Cambiar Estado", e -> cambiarEstadoPedido());
            }
            if (rolActual.tienePermiso(Rol.Permisos.MARCAR_ENTREGADO)) {
                agregarBotonMenu("Marcar Entregado", e -> marcarEntregado());
            }
        }
        
        // SECCIÓN ADMINISTRADOR
        if (tieneAlgunPermiso(Rol.Permisos.obtenerPermisosAdministrador())) {
            agregarSeccionMenu("️ ADMINISTRACIÓN", new Color(204, 0, 102));
            
            if (rolActual.tienePermiso(Rol.Permisos.VER_PRODUCTOS)) {
                agregarBotonMenu("Ver Productos", e -> verProductos());
            }
            if (rolActual.tienePermiso(Rol.Permisos.AGREGAR_PRODUCTO)) {
                agregarBotonMenu("Agregar Producto", e -> agregarProducto());
            }
            if (rolActual.tienePermiso(Rol.Permisos.EDITAR_PRODUCTO)) {
                agregarBotonMenu("Editar Producto", e -> editarProducto());
            }
            if (rolActual.tienePermiso(Rol.Permisos.ELIMINAR_PRODUCTO)) {
                agregarBotonMenu("Eliminar Producto", e -> eliminarProducto());
            }
            if (rolActual.tienePermiso(Rol.Permisos.GESTIONAR_USUARIOS)) {
                agregarBotonMenu("Gestionar Usuarios", e -> gestionarUsuarios());
            }
            if (rolActual.tienePermiso(Rol.Permisos.GENERAR_REPORTES)) {
                agregarBotonMenu("Generar Reportes", e -> generarReportes());
            }
            if (rolActual.tienePermiso(Rol.Permisos.GESTIONAR_ROLES)) {
                agregarBotonMenu("Gestionar Roles", e -> gestionarRoles());
            }
        }
        
        panelMenu.revalidate();
        panelMenu.repaint();
    }
    
    private boolean tieneAlgunPermiso(String[] permisos) {
        for (String permiso : permisos) {
            if (rolActual.tienePermiso(permiso)) {
                return true;
            }
        }
        return false;
    }
    
    private void agregarSeccionMenu(String titulo, Color color) {
        JLabel lblSeccion = new JLabel("  " + titulo);
        lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccion.setForeground(color);
        lblSeccion.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelMenu.add(lblSeccion);
    }
    
    private void agregarBotonMenu(String texto, java.awt.event.ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 40));
        btn.setBackground(new Color(68, 68, 68));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(85, 85, 85));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(68, 68, 68));
            }
        });
        
        btn.addActionListener(accion);
        panelMenu.add(btn);
    }
    
    private void mostrarPanelBienvenida() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel lblBienvenida = new JLabel("¡Bienvenido/a al Sistema!");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblBienvenida.setForeground(new Color(255, 153, 51));
        panel.add(lblBienvenida, gbc);
        
        gbc.gridy = 1;
        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(lblUsuario, gbc);
        
        gbc.gridy = 2;
        JLabel lblRol = new JLabel("Rol: " + rolActual.getNombre());
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRol.setForeground(new Color(51, 102, 204));
        panel.add(lblRol, gbc);
        
        gbc.gridy = 3;
        JLabel lblPermisos = new JLabel("Permisos asignados: " + rolActual.getPermisos().size());
        lblPermisos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPermisos.setForeground(new Color(102, 102, 102));
        panel.add(lblPermisos, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 10, 10, 10);
        JLabel lblInstruccion = new JLabel("<html><center>Utilice el menú lateral para acceder<br>a las funciones disponibles</center></html>");
        lblInstruccion.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInstruccion.setForeground(new Color(153, 153, 153));
        lblInstruccion.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblInstruccion, gbc);
        
        mostrarEnPanelContenido(panel);
    }
    
    private void mostrarEnPanelContenido(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
    
    // IMPLEMENTACIÓN DE FUNCIONES
    
    private void mostrarMenu() {
        // Redirigir a la interfaz de estudiante (menú)
        this.dispose();
        new ESPECAFE().setVisible(true);
    }
    
    private void realizarPedido() {
        JOptionPane.showMessageDialog(this,
            "Función: Realizar Pedido\n(Redirecciona al menú de productos)",
            "Realizar Pedido",
            JOptionPane.INFORMATION_MESSAGE);
        mostrarMenu();
    }
    
    private void procesarPago() {
        JOptionPane.showMessageDialog(this,
            "Función: Procesar Pago\n(Disponible en la interfaz de pedidos)",
            "Procesar Pago",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void verPedidos() {
        // Redirigir a interfaz de personal
        new InterfazPersonal().setVisible(true);
    }
    
    private void cambiarEstadoPedido() {
        JOptionPane.showMessageDialog(this,
            "Función: Cambiar Estado de Pedido\n(Disponible en ver pedidos)",
            "Cambiar Estado",
            JOptionPane.INFORMATION_MESSAGE);
        verPedidos();
    }
    
    private void marcarEntregado() {
        JOptionPane.showMessageDialog(this,
            "Función: Marcar Pedido como Entregado\n(Disponible en ver pedidos)",
            "Marcar Entregado",
            JOptionPane.INFORMATION_MESSAGE);
        verPedidos();
    }
    
    private void verProductos() {
        // Redirigir a interfaz de administrador
        new AdministradorInterfaz().setVisible(true);
    }
    
    private void agregarProducto() {
        JOptionPane.showMessageDialog(this,
            "Función: Agregar Producto\n(Disponible en la interfaz de administración)",
            "Agregar Producto",
            JOptionPane.INFORMATION_MESSAGE);
        verProductos();
    }
    
    private void editarProducto() {
        JOptionPane.showMessageDialog(this,
            "Función: Editar Producto\n(Disponible en la interfaz de administración)",
            "Editar Producto",
            JOptionPane.INFORMATION_MESSAGE);
        verProductos();
    }
    
    private void eliminarProducto() {
        JOptionPane.showMessageDialog(this,
            "Función: Eliminar Producto\n(Disponible en la interfaz de administración)",
            "Eliminar Producto",
            JOptionPane.INFORMATION_MESSAGE);
        verProductos();
    }
    
    private void gestionarUsuarios() {
        new GestionarUsuarios().setVisible(true);
    }
    
    private void generarReportes() {
        JOptionPane.showMessageDialog(this,
            "Función: Generar Reportes\n(Disponible en la interfaz de administración)",
            "Generar Reportes",
            JOptionPane.INFORMATION_MESSAGE);
        verProductos();
    }
    
    private void gestionarRoles() {
        new ControladorRolesAvanzados().setVisible(true);
    }
    
    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            this.dispose();
            ControladorPrincipal.cerrarSesion();
        }
    }
}