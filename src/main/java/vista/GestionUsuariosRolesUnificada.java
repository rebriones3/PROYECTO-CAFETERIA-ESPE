package vista;

import controlador.ControladorUsuarios;
import controlador.ControladorRolesAvanzados;
import modelo.Rol;
import modelo.ConexionMongoDB;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class GestionUsuariosRolesUnificada extends JFrame {
    
    private DefaultTableModel modeloTablaUsuarios;
    private DefaultTableModel modeloTablaRoles;
    private JTable tablaUsuarios;
    private JTable tablaRoles;
    private JTabbedPane tabbedPane;
    
    public GestionUsuariosRolesUnificada() {
        initComponents();
        cargarUsuarios();
        cargarRoles();
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Gestión de Usuarios y Roles - Sistema Cafetería ESPE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con título
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con pestañas
        tabbedPane = new JTabbedPane();
        
        // Pestaña 1: Gestión de Usuarios
        JPanel panelUsuarios = crearPanelUsuarios();
        tabbedPane.addTab("Gestión de Usuarios", new ImageIcon(), panelUsuarios, "Crear y administrar usuarios del sistema");
        
        // Pestaña 2: Gestión de Roles (EXACTA como GestionarRolesInterfaz)
        JPanel panelRoles = crearPanelRolesCompleto();
        tabbedPane.addTab(" Gestión de Roles", new ImageIcon(), panelRoles, "Crear y administrar roles y permisos");
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botón volver
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("ADMINISTRACIÓN DE USUARIOS Y ROLES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Gestione usuarios del sistema y sus permisos de acceso");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(240, 240, 240));
        
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);
        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createVerticalStrut(5));
        panelTexto.add(lblSubtitulo);
        
        panel.add(panelTexto, BorderLayout.WEST);
        
        return panel;
    }
    
    // ========== PANEL USUARIOS (igual que antes) ==========
    
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Panel de búsqueda
        JPanel panelBusqueda = crearPanelBusquedaUsuarios();
        panel.add(panelBusqueda, BorderLayout.NORTH);
        
        // Tabla de usuarios
        JPanel panelTabla = crearPanelTablaUsuarios();
        panel.add(panelTabla, BorderLayout.CENTER);
        
        // Panel de botones para usuarios
        JPanel panelBotonesUsuarios = crearPanelBotonesUsuarios();
        panel.add(panelBotonesUsuarios, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelBusquedaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel lblBuscar = new JLabel("Buscar usuario:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JTextField txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JButton btnBuscar = crearBoton("Buscar", new Color(70, 130, 180));
        JButton btnLimpiar = crearBoton("Limpiar", new Color(169, 169, 169));
        
        btnBuscar.addActionListener(e -> buscarUsuario(txtBuscar.getText().trim()));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarUsuarios();
        });
        
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelInput.add(lblBuscar);
        panelInput.add(txtBuscar);
        panelInput.add(btnBuscar);
        panelInput.add(btnLimpiar);
        
        panel.add(panelInput, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelTablaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        
        modeloTablaUsuarios = new DefaultTableModel(
            new Object[]{"Rol", "Correo Electrónico", "Contraseña", "Estado Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloTablaUsuarios);
        tablaUsuarios.setRowHeight(35);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(new Color(220, 230, 240));
        
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        tablaUsuarios.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Boolean) {
                    boolean estado = (Boolean) value;
                    if (estado) {
                        c.setBackground(new Color(220, 255, 220));
                        c.setForeground(new Color(0, 128, 0));
                        ((JLabel) c).setText("ACTIVO");
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        c.setBackground(new Color(255, 220, 220));
                        c.setForeground(new Color(178, 34, 34));
                        ((JLabel) c).setText("INACTIVO");
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Usuarios Registrados en el Sistema",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesUsuarios() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnAgregar = crearBoton("Nuevo Usuario", new Color(34, 139, 34));
        JButton btnEditar = crearBoton("Editar Usuario", new Color(255, 140, 0));
        JButton btnAsignarRol = crearBoton(" Asignar Rol", new Color(70, 130, 180));
        JButton btnCambiarEstado = crearBoton("?Estado Rol", new Color(138, 43, 226));
        JButton btnEliminar = crearBoton(" Eliminar", new Color(220, 20, 60));
        JButton btnVerPermisos = crearBoton(" Ver Permisos", new Color(153, 50, 204));
        
        btnAgregar.addActionListener(e -> mostrarDialogoAgregarUsuario());
        btnEditar.addActionListener(e -> mostrarDialogoEditarUsuario());
        btnAsignarRol.addActionListener(e -> mostrarDialogoAsignarRol());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoRolUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnVerPermisos.addActionListener(e -> mostrarPermisosUsuario());
        
        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnAsignarRol);
        panel.add(btnCambiarEstado);
        panel.add(btnEliminar);
        panel.add(btnVerPermisos);
        
        return panel;
    }
    
    // ========== PANEL ROLES COMPLETO (EXACTO como GestionarRolesInterfaz) ==========
    
    private JPanel crearPanelRolesCompleto() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Tabla de roles (igual que en GestionarRolesInterfaz)
        JPanel panelTablaRoles = crearPanelTablaRolesCompleto();
        panel.add(panelTablaRoles, BorderLayout.CENTER);
        
        // Botones para roles (igual que en GestionarRolesInterfaz)
        JPanel panelBotonesRoles = crearPanelBotonesRolesCompleto();
        panel.add(panelBotonesRoles, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelTablaRolesCompleto() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // EXACTO como en GestionarRolesInterfaz
        modeloTablaRoles = new DefaultTableModel(
            new Object[]{"Nombre del Rol", "Descripción", "Cantidad Permisos", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRoles = new JTable(modeloTablaRoles);
        tablaRoles.setRowHeight(30);
        tablaRoles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaRoles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaRoles.getTableHeader().setBackground(new Color(230, 230, 250));
        
        // Configurar ancho de columnas (igual que antes)
        tablaRoles.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaRoles.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaRoles.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaRoles.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaRoles.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        // Renderizador para columna de estado (igual que antes)
        tablaRoles.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof String) {
                    String estado = (String) value;
                    if ("ACTIVO".equals(estado)) {
                        c.setBackground(new Color(220, 255, 220));
                        c.setForeground(new Color(0, 100, 0));
                    } else {
                        c.setBackground(new Color(255, 220, 220));
                        c.setForeground(new Color(139, 0, 0));
                    }
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaRoles);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(153, 0, 153), 2),
            "Roles Registrados en el Sistema",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(153, 0, 153)
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesRolesCompleto() {
        // EXACTO como en GestionarRolesInterfaz.crearPanelBotones()
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnCrear = crearBotonRoles("Crear Rol Nuevo", new Color(0, 153, 51));
        JButton btnEditar = crearBotonRoles(" Editar Rol", new Color(255, 153, 0));
        JButton btnEliminar = crearBotonRoles(" Eliminar Rol", new Color(204, 0, 0));
        JButton btnVerPermisos = crearBotonRoles(" Ver Permisos", new Color(51, 102, 204));
        JButton btnActivarDesactivar = crearBotonRoles("Activar/Desactivar", new Color(102, 102, 102));
        
        // Usar los mismos métodos de GestionarRolesInterfaz
        btnCrear.addActionListener(e -> mostrarDialogoCrearRolCompleto());
        btnEditar.addActionListener(e -> mostrarDialogoEditarRolCompleto());
        btnEliminar.addActionListener(e -> eliminarRolCompleto());
        btnVerPermisos.addActionListener(e -> mostrarPermisosRolCompleto());
        btnActivarDesactivar.addActionListener(e -> cambiarEstadoRolCompleto());
        
        panel.add(btnCrear);
        panel.add(btnEditar);
        panel.add(btnVerPermisos);
        panel.add(btnActivarDesactivar);
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private JButton crearBotonRoles(String texto, Color color) {
        // EXACTO como en GestionarRolesInterfaz.crearBoton()
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnVolver = crearBoton("← Volver al Menú Principal", new Color(169, 169, 169));
        btnVolver.addActionListener(e -> {
            dispose();
            // new AdministradorInterfaz().setVisible(true);
        });
        
        panel.add(btnVolver);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    // ========== MÉTODOS PARA USUARIOS ==========
    
    private void cargarUsuarios() {
        ControladorUsuarios.cargarUsuarios(modeloTablaUsuarios);
        
        for (int i = 0; i < modeloTablaUsuarios.getRowCount(); i++) {
            String rol = (String) modeloTablaUsuarios.getValueAt(i, 0);
            boolean estadoRol = verificarEstadoRol(rol);
            modeloTablaUsuarios.setValueAt(estadoRol, i, 3);
        }
    }
    
    private void buscarUsuario(String busqueda) {
        if (busqueda.isEmpty()) {
            cargarUsuarios();
            return;
        }
        
        modeloTablaUsuarios.setRowCount(0);
        boolean encontrado = false;
        
        for (var usuario : ControladorUsuarios.getUsuarios()) {
            if (usuario.getRol().toLowerCase().contains(busqueda.toLowerCase()) ||
                usuario.getCorreo().toLowerCase().contains(busqueda.toLowerCase())) {
                
                boolean estadoRol = verificarEstadoRol(usuario.getRol());
                
                modeloTablaUsuarios.addRow(new Object[]{
                    usuario.getRol(),
                    usuario.getCorreo(),
                    "********",
                    estadoRol
                });
                encontrado = true;
            }
        }
        
        if (!encontrado) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron usuarios que coincidan con: " + busqueda,
                "Sin resultados",
                JOptionPane.INFORMATION_MESSAGE);
            cargarUsuarios();
        }
    }
    
    private void mostrarDialogoAgregarUsuario() {
        // Implementación simplificada
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        JLabel lblCorreo = new JLabel("Correo electrónico:");
        JTextField txtCorreo = new JTextField(20);
        
        JLabel lblContraseña = new JLabel("Contraseña:");
        JPasswordField txtContraseña = new JPasswordField(20);
        
        JLabel lblConfirmar = new JLabel("Confirmar contraseña:");
        JPasswordField txtConfirmar = new JPasswordField(20);
        
        JLabel lblRol = new JLabel("Rol inicial:");
        List<String> rolesDisponibles = new ArrayList<>();
        rolesDisponibles.add("Estudiante");
        rolesDisponibles.add("Personal");
        rolesDisponibles.add("Administrador");
        
        // Agregar roles personalizados
        for (int i = 0; i < modeloTablaRoles.getRowCount(); i++) {
            String tipo = (String) modeloTablaRoles.getValueAt(i, 3);
            if ("Personalizado".equals(tipo)) {
                String nombreRol = (String) modeloTablaRoles.getValueAt(i, 0);
                rolesDisponibles.add(nombreRol);
            }
        }
        
        JComboBox<String> cmbRol = new JComboBox<>(rolesDisponibles.toArray(new String[0]));
        
        panel.add(lblCorreo);
        panel.add(txtCorreo);
        panel.add(lblContraseña);
        panel.add(txtContraseña);
        panel.add(lblConfirmar);
        panel.add(txtConfirmar);
        panel.add(lblRol);
        panel.add(cmbRol);
        
        int opcion = JOptionPane.showConfirmDialog(this, panel, 
            "Agregar Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String correo = txtCorreo.getText().trim();
            String contraseña = new String(txtContraseña.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            String rol = (String) cmbRol.getSelectedItem();
            
            if (correo.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor, complete todos los campos.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un correo válido.",
                    "Correo inválido",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!contraseña.equals(confirmar)) {
                JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden.",
                    "Error de confirmación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (ControladorUsuarios.agregarUsuario(rol, correo, contraseña)) {
                cargarUsuarios();
            }
        }
    }
    
    private void mostrarDialogoEditarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Implementación simplificada
        String correo = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        String rolActual = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 0);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel lblCorreo = new JLabel("Correo:");
        JLabel lblCorreoValor = new JLabel(correo);
        lblCorreoValor.setFont(lblCorreoValor.getFont().deriveFont(Font.BOLD));
        
        JLabel lblRol = new JLabel("Rol:");
        List<String> rolesDisponibles = new ArrayList<>();
        rolesDisponibles.add("Estudiante");
        rolesDisponibles.add("Personal");
        rolesDisponibles.add("Administrador");
        
        // Agregar roles personalizados
        for (int i = 0; i < modeloTablaRoles.getRowCount(); i++) {
            String tipo = (String) modeloTablaRoles.getValueAt(i, 3);
            if ("Personalizado".equals(tipo)) {
                String nombreRol = (String) modeloTablaRoles.getValueAt(i, 0);
                rolesDisponibles.add(nombreRol);
            }
        }
        
        JComboBox<String> cmbRol = new JComboBox<>(rolesDisponibles.toArray(new String[0]));
        cmbRol.setSelectedItem(rolActual);
        
        JLabel lblContraseña = new JLabel("Nueva contraseña:");
        JPasswordField txtContraseña = new JPasswordField(20);
        
        panel.add(lblCorreo);
        panel.add(lblCorreoValor);
        panel.add(lblRol);
        panel.add(cmbRol);
        panel.add(lblContraseña);
        panel.add(txtContraseña);
        
        int opcion = JOptionPane.showConfirmDialog(this, panel, 
            "Editar Usuario", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String nuevoRol = (String) cmbRol.getSelectedItem();
            String nuevaContraseña = new String(txtContraseña.getPassword()).trim();
            
            if (nuevaContraseña.isEmpty()) {
                if (ControladorUsuarios.asignarRolUsuario(filaSeleccionada, nuevoRol)) {
                    cargarUsuarios();
                }
            } else {
                if (ControladorUsuarios.editarUsuario(filaSeleccionada, nuevoRol, nuevaContraseña)) {
                    cargarUsuarios();
                }
            }
        }
    }
    
    private void mostrarDialogoAsignarRol() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        var usuario = ControladorUsuarios.getUsuario(filaSeleccionada);
        
        JLabel lblCorreo = new JLabel(usuario.getCorreo());
        lblCorreo.setFont(lblCorreo.getFont().deriveFont(Font.BOLD));
        
        JLabel lblRolActual = new JLabel(usuario.getRol());
        lblRolActual.setFont(lblRolActual.getFont().deriveFont(Font.BOLD));
        
        List<String> rolesDisponibles = new ArrayList<>();
        rolesDisponibles.add("Estudiante");
        rolesDisponibles.add("Personal");
        rolesDisponibles.add("Administrador");
        
        // Agregar roles personalizados
        for (int i = 0; i < modeloTablaRoles.getRowCount(); i++) {
            String tipo = (String) modeloTablaRoles.getValueAt(i, 3);
            if ("Personalizado".equals(tipo)) {
                String nombreRol = (String) modeloTablaRoles.getValueAt(i, 0);
                rolesDisponibles.add(nombreRol);
            }
        }
        
        JComboBox<String> cmbRol = new JComboBox<>(rolesDisponibles.toArray(new String[0]));
        cmbRol.setSelectedItem(usuario.getRol());
        
        Object[] mensaje = {
            "Usuario seleccionado:", lblCorreo,
            "Rol actual:", lblRolActual,
            "Nuevo rol:", cmbRol
        };
        
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, 
            "Asignar Rol al Usuario", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String nuevoRol = (String) cmbRol.getSelectedItem();
            
            if (ControladorUsuarios.asignarRolUsuario(filaSeleccionada, nuevoRol)) {
                cargarUsuarios();
            }
        }
    }
    
    private void eliminarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String correo = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        String rol = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 0);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el usuario?\n\n" +
            "Rol: " + rol + "\n" +
            "Correo: " + correo + "\n\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (ControladorUsuarios.eliminarUsuario(filaSeleccionada)) {
                cargarUsuarios();
            }
        }
    }
    
    private void cambiarEstadoRolUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String rol = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 0);
        String correo = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        boolean estadoActual = (Boolean) modeloTablaUsuarios.getValueAt(filaSeleccionada, 3);
        
        if (rol.equals("Estudiante") || rol.equals("Personal") || rol.equals("Administrador")) {
            JOptionPane.showMessageDialog(this,
                "No se puede cambiar el estado de un rol predeterminado del sistema.",
                "Operación no permitida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean nuevoEstado = !estadoActual;
        String mensaje = nuevoEstado ?
            "¿Desea ACTIVAR el rol '" + rol + "'?" :
            "¿Desea DESACTIVAR el rol '" + rol + "'?\n\n" +
            "El usuario " + correo + " NO podrá acceder al sistema con este rol.";
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            mensaje,
            "Confirmar cambio de estado",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
                
                collection.updateOne(
                    eq("nombre", rol),
                    new Document("$set", new Document("activo", nuevoEstado))
                );
                
                cargarUsuarios();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cambiar el estado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarPermisosUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String rolUsuario = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 0);
        String correo = (String) modeloTablaUsuarios.getValueAt(filaSeleccionada, 1);
        
        // Mostrar permisos del rol
        String[] permisos = obtenerPermisosPorRol(rolUsuario);
        
        JDialog dialogo = new JDialog(this, "Permisos del Usuario: " + correo, true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(500, 400);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblUsuario = new JLabel(" Usuario: " + correo);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel lblRol = new JLabel(" Rol: " + rolUsuario);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRol.setForeground(new Color(70, 130, 180));
        
        panelContenido.add(lblUsuario);
        panelContenido.add(Box.createVerticalStrut(10));
        panelContenido.add(lblRol);
        panelContenido.add(Box.createVerticalStrut(20));
        
        JLabel lblTituloPermisos = new JLabel("Permisos disponibles (" + permisos.length + "):");
        lblTituloPermisos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelContenido.add(lblTituloPermisos);
        panelContenido.add(Box.createVerticalStrut(10));
        
        JPanel panelPermisos = new JPanel();
        panelPermisos.setLayout(new BoxLayout(panelPermisos, BoxLayout.Y_AXIS));
        
        for (String permiso : permisos) {
            JLabel lblPermiso = new JLabel("  • " + Rol.Permisos.obtenerDescripcion(permiso));
            lblPermiso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panelPermisos.add(lblPermiso);
        }
        
        JScrollPane scrollPane = new JScrollPane(panelPermisos);
        panelContenido.add(scrollPane);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogo.dispose());
        
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        
        dialogo.add(panelContenido, BorderLayout.CENTER);
        dialogo.add(panelBoton, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    // ========== MÉTODOS PARA ROLES (EXACTOS como en GestionarRolesInterfaz) ==========
    
    private void cargarRoles() {
        modeloTablaRoles.setRowCount(0);
        
        // Cargar roles predeterminados (EXACTO como antes)
        agregarRolPredeterminado("Estudiante", "Usuario estudiante con acceso al menú y pedidos", 
            Rol.Permisos.obtenerPermisosEstudiante().length);
        agregarRolPredeterminado("Personal", "Personal de cocina para gestión de pedidos", 
            Rol.Permisos.obtenerPermisosPersonal().length);
        agregarRolPredeterminado("Administrador", "Administrador con acceso completo al sistema", 
            Rol.Permisos.obtenerPermisosAdministrador().length);
        
        // Cargar roles personalizados desde MongoDB (EXACTO como antes)
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            
            for (Document doc : collection.find()) {
                String nombre = doc.getString("nombre");
                String descripcion = doc.getString("descripcion");
                List<String> permisos = (List<String>) doc.get("permisos");
                boolean activo = doc.getBoolean("activo", true);
                
                modeloTablaRoles.addRow(new Object[]{
                    nombre,
                    descripcion,
                    permisos != null ? permisos.size() : 0,
                    "Personalizado",
                    activo ? "ACTIVO" : "INACTIVO"
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar roles personalizados: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarRolPredeterminado(String nombre, String descripcion, int cantidadPermisos) {
        modeloTablaRoles.addRow(new Object[]{
            nombre,
            descripcion,
            cantidadPermisos,
            "Predeterminado",
            "ACTIVO"
        });
    }
    
    // ========== MÉTODOS DE DIÁLOGOS PARA ROLES (EXACTOS como en GestionarRolesInterfaz) ==========
    
    private void mostrarDialogoCrearRolCompleto() {
        // EXACTO como mostrarDialogoCrearRol() en GestionarRolesInterfaz
        JDialog dialogo = new JDialog(this, "Crear Nuevo Rol Personalizado", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(900, 700);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Información básica (igual que antes)
        JPanel panelInfo = crearPanelInformacionRol();
        JTextField txtNombre = (JTextField) panelInfo.getComponent(1);
        JTextField txtDescripcion = (JTextField) panelInfo.getComponent(3);
        
        panelPrincipal.add(panelInfo);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // Panel de permisos con checkboxes (igual que antes)
        Map<String, JCheckBox> checkboxesPermisos = new HashMap<>();
        JPanel panelPermisos = crearPanelPermisosCompleto(checkboxesPermisos);
        
        panelPrincipal.add(panelPermisos);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // Botones rápidos (igual que antes)
        JPanel panelBotonesRapidos = crearPanelBotonesRapidos(checkboxesPermisos);
        panelPrincipal.add(panelBotonesRapidos);
        
        // Preview de permisos seleccionados (igual que antes)
        JLabel lblPreview = new JLabel(" Vista Previa: 0 permisos seleccionados");
        lblPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPreview.setForeground(new Color(102, 102, 102));
        lblPreview.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        for (JCheckBox cb : checkboxesPermisos.values()) {
            cb.addActionListener(e -> {
                long sel = checkboxesPermisos.values().stream().filter(JCheckBox::isSelected).count();
                lblPreview.setText("Vista Previa: " + sel + " permisos seleccionados");
            });
        }
        
        panelPrincipal.add(lblPreview);
        
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Botones finales (igual que antes)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        JButton btnGuardar = new JButton("✓ Crear Rol");
        btnGuardar.setBackground(new Color(0, 153, 51));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        
        JButton btnCancelar = new JButton("✗ Cancelar");
        btnCancelar.setBackground(new Color(153, 153, 153));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,
                    "Ingrese un nombre para el rol.",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (descripcion.isEmpty()) {
                descripcion = "Rol personalizado: " + nombre;
            }
            
            Set<String> permisosSeleccionados = new HashSet<>();
            for (Map.Entry<String, JCheckBox> entry : checkboxesPermisos.entrySet()) {
                if (entry.getValue().isSelected()) {
                    permisosSeleccionados.add(entry.getKey());
                }
            }
            
            if (permisosSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,
                    "Debe seleccionar al menos un permiso.",
                    "Sin permisos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Usar ControladorRolesAvanzados si existe, sino crear directamente
            try {
                MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
                
                // Verificar si ya existe
                if (collection.find(eq("nombre", nombre)).first() != null) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Ya existe un rol con ese nombre.",
                        "Rol duplicado",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Crear documento del rol
                Document rolDoc = new Document()
                    .append("nombre", nombre)
                    .append("descripcion", descripcion)
                    .append("permisos", new ArrayList<>(permisosSeleccionados))
                    .append("activo", true)
                    .append("fechaCreacion", new java.util.Date());
                
                collection.insertOne(rolDoc);
                
                JOptionPane.showMessageDialog(dialogo,
                    "¡Rol creado exitosamente!\n\n" +
                    "Nombre: " + nombre + "\n" +
                    "Permisos: " + permisosSeleccionados.size() + "\n\n" +
                    "Ahora puede asignar este rol a usuarios.",
                    "Rol Creado",
                    JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
                cargarRoles();
                cargarUsuarios(); // Para actualizar combobox de usuarios
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo,
                    "Error al crear rol: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(scrollPane, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private JPanel crearPanelInformacionRol() {
        // EXACTO como en GestionarRolesInterfaz
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(153, 0, 153), 2),
            "Información del Rol",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(153, 0, 153)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre del rol:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(25);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblDescripcion, gbc);
        
        gbc.gridx = 1;
        JTextField txtDescripcion = new JTextField(25);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtDescripcion, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelPermisosCompleto(Map<String, JCheckBox> checkboxesPermisos) {
        // EXACTO como en GestionarRolesInterfaz
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(153, 0, 153), 2),
            "Seleccione los Permisos del Rol",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(153, 0, 153)
        ));
        
        // Permisos de Estudiante
        JPanel panelEstudiante = crearPanelCategoria(
            "FUNCIONES DE ESTUDIANTE",
            new Color(102, 153, 255),
            Rol.Permisos.obtenerPermisosEstudiante(),
            checkboxesPermisos
        );
        panelPrincipal.add(panelEstudiante);
        panelPrincipal.add(Box.createVerticalStrut(10));
        
        // Permisos de Personal
        JPanel panelPersonal = crearPanelCategoria(
            " FUNCIONES DE PERSONAL/COCINA",
            new Color(255, 153, 0),
            Rol.Permisos.obtenerPermisosPersonal(),
            checkboxesPermisos
        );
        panelPrincipal.add(panelPersonal);
        panelPrincipal.add(Box.createVerticalStrut(10));
        
        // Permisos de Administrador
        JPanel panelAdministrador = crearPanelCategoria(
            "️ FUNCIONES DE ADMINISTRADOR",
            new Color(204, 0, 102),
            Rol.Permisos.obtenerPermisosAdministrador(),
            checkboxesPermisos
        );
        panelPrincipal.add(panelAdministrador);
        
        return panelPrincipal;
    }
    
    private JPanel crearPanelCategoria(String titulo, Color color, String[] permisos, 
                                       Map<String, JCheckBox> map) {
        // EXACTO como en GestionarRolesInterfaz
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(250, 250, 250));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(color);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(8));
        
        for (String permiso : permisos) {
            JCheckBox checkbox = new JCheckBox("  " + Rol.Permisos.obtenerDescripcion(permiso));
            checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checkbox.setName(permiso);
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkbox.setBackground(new Color(250, 250, 250));
            map.put(permiso, checkbox);
            panel.add(checkbox);
            panel.add(Box.createVerticalStrut(3));
        }
        
        return panel;
    }
    
    private JPanel crearPanelBotonesRapidos(Map<String, JCheckBox> checkboxesPermisos) {
        // EXACTO como en GestionarRolesInterfaz
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnTodoEstudiante = crearBotonRapido("Todos: Estudiante", new Color(102, 153, 255));
        JButton btnTodoPersonal = crearBotonRapido("Todos: Personal", new Color(255, 153, 0));
        JButton btnTodoAdmin = crearBotonRapido("Todos: Admin", new Color(204, 0, 102));
        JButton btnTodoCompleto = crearBotonRapido("Super Usuario (Todos)", new Color(153, 0, 153));
        JButton btnSeleccionarTodo = crearBotonRapido("Seleccionar Todo", new Color(0, 153, 51));
        JButton btnLimpiar = crearBotonRapido("Limpiar", new Color(153, 153, 153));
        
        btnTodoEstudiante.addActionListener(e -> {
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(false));
            for (String p : Rol.Permisos.obtenerPermisosEstudiante()) {
                checkboxesPermisos.get(p).setSelected(true);
            }
        });
        
        btnTodoPersonal.addActionListener(e -> {
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(false));
            for (String p : Rol.Permisos.obtenerPermisosPersonal()) {
                checkboxesPermisos.get(p).setSelected(true);
            }
        });
        
        btnTodoAdmin.addActionListener(e -> {
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(false));
            for (String p : Rol.Permisos.obtenerPermisosAdministrador()) {
                checkboxesPermisos.get(p).setSelected(true);
            }
        });
        
        btnTodoCompleto.addActionListener(e -> {
            // Super Usuario: todos los permisos
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(true));
        });
        
        btnSeleccionarTodo.addActionListener(e -> 
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(true))
        );
        
        btnLimpiar.addActionListener(e -> 
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(false))
        );
        
        panel.add(btnTodoEstudiante);
        panel.add(btnTodoPersonal);
        panel.add(btnTodoAdmin);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnTodoCompleto);
        panel.add(btnSeleccionarTodo);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JButton crearBotonRapido(String texto, Color color) {
        // EXACTO como en GestionarRolesInterfaz
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }
    
    private void mostrarDialogoEditarRolCompleto() {
        // EXACTO como mostrarDialogoEditarRol() en GestionarRolesInterfaz
        int filaSeleccionada = tablaRoles.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un rol de la tabla.",
                "Ningún rol seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombreRol = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 0);
        String tipo = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 3);
        
        if ("Predeterminado".equals(tipo)) {
            JOptionPane.showMessageDialog(this,
                "No se pueden editar los roles predeterminados del sistema.\n" +
                "Solo puede editar roles personalizados.",
                "Operación no permitida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cargar datos del rol desde MongoDB
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            Document rolDoc = collection.find(eq("nombre", nombreRol)).first();
            
            if (rolDoc == null) {
                JOptionPane.showMessageDialog(this,
                    "No se encontró el rol en la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String descripcionActual = rolDoc.getString("descripcion");
            List<String> permisosActuales = (List<String>) rolDoc.get("permisos");
            
            // Crear diálogo de edición (similar al de creación)
            JDialog dialogo = new JDialog(this, "Editar Rol: " + nombreRol, true);
            dialogo.setLayout(new BorderLayout(10, 10));
            dialogo.setSize(900, 700);
            dialogo.setLocationRelativeTo(this);
            
            JPanel panelPrincipal = new JPanel();
            panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
            panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Información básica (nombre no editable)
            JPanel panelInfo = crearPanelInformacionRol();
            JTextField txtNombre = (JTextField) panelInfo.getComponent(1);
            JTextField txtDescripcion = (JTextField) panelInfo.getComponent(3);
            
            txtNombre.setText(nombreRol);
            txtNombre.setEditable(false);
            txtNombre.setBackground(new Color(240, 240, 240));
            txtDescripcion.setText(descripcionActual);
            
            panelPrincipal.add(panelInfo);
            panelPrincipal.add(Box.createVerticalStrut(15));
            
            // Panel de permisos
            Map<String, JCheckBox> checkboxesPermisos = new HashMap<>();
            JPanel panelPermisos = crearPanelPermisosCompleto(checkboxesPermisos);
            
            // Marcar permisos actuales
            if (permisosActuales != null) {
                for (String permiso : permisosActuales) {
                    JCheckBox cb = checkboxesPermisos.get(permiso);
                    if (cb != null) {
                        cb.setSelected(true);
                    }
                }
            }
            
            panelPrincipal.add(panelPermisos);
            panelPrincipal.add(Box.createVerticalStrut(15));
            
            // Botones rápidos
            JPanel panelBotonesRapidos = crearPanelBotonesRapidos(checkboxesPermisos);
            panelPrincipal.add(panelBotonesRapidos);
            
            // Preview
            JLabel lblPreview = new JLabel("Vista Previa: " + (permisosActuales != null ? permisosActuales.size() : 0) + " permisos seleccionados");
            lblPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblPreview.setForeground(new Color(102, 102, 102));
            lblPreview.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            
            for (JCheckBox cb : checkboxesPermisos.values()) {
                cb.addActionListener(e -> {
                    long sel = checkboxesPermisos.values().stream().filter(JCheckBox::isSelected).count();
                    lblPreview.setText("Vista Previa: " + sel + " permisos seleccionados");
                });
            }
            
            panelPrincipal.add(lblPreview);
            
            JScrollPane scrollPane = new JScrollPane(panelPrincipal);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            
            // Botones
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            
            JButton btnGuardar = new JButton(" Guardar Cambios");
            btnGuardar.setBackground(new Color(0, 153, 51));
            btnGuardar.setForeground(Color.WHITE);
            btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnGuardar.setFocusPainted(false);
            btnGuardar.setPreferredSize(new Dimension(180, 40));
            
            JButton btnCancelar = new JButton(" Cancelar");
            btnCancelar.setBackground(new Color(153, 153, 153));
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnCancelar.setFocusPainted(false);
            btnCancelar.setPreferredSize(new Dimension(150, 40));
            
            btnGuardar.addActionListener(e -> {
                String descripcion = txtDescripcion.getText().trim();
                
                Set<String> permisosSeleccionados = new HashSet<>();
                for (Map.Entry<String, JCheckBox> entry : checkboxesPermisos.entrySet()) {
                    if (entry.getValue().isSelected()) {
                        permisosSeleccionados.add(entry.getKey());
                    }
                }
                
                if (permisosSeleccionados.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Debe seleccionar al menos un permiso.",
                        "Sin permisos",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Actualizar en MongoDB
                try {
                    Document updateDoc = new Document("$set", 
                        new Document("descripcion", descripcion)
                            .append("permisos", new ArrayList<>(permisosSeleccionados))
                            .append("fechaModificacion", new java.util.Date()));
                    
                    collection.updateOne(eq("nombre", nombreRol), updateDoc);
                    
                    JOptionPane.showMessageDialog(dialogo,
                        "¡Rol actualizado exitosamente!",
                        "Rol Actualizado",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                    cargarRoles();
                    cargarUsuarios(); // Para actualizar combobox de usuarios
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Error al actualizar el rol: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialogo.dispose());
            
            panelBotones.add(btnGuardar);
            panelBotones.add(btnCancelar);
            
            dialogo.add(scrollPane, BorderLayout.CENTER);
            dialogo.add(panelBotones, BorderLayout.SOUTH);
            dialogo.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar el rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarRolCompleto() {
        // EXACTO como eliminarRol() en GestionarRolesInterfaz
        int filaSeleccionada = tablaRoles.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un rol de la tabla.",
                "Ningún rol seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombreRol = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 0);
        String tipo = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 3);
        
        if ("Predeterminado".equals(tipo)) {
            JOptionPane.showMessageDialog(this,
                "No se pueden eliminar los roles predeterminados del sistema.",
                "Operación no permitida",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si hay usuarios con este rol
        int usuariosConRol = 0;
        for (int i = 0; i < modeloTablaUsuarios.getRowCount(); i++) {
            if (nombreRol.equals(modeloTablaUsuarios.getValueAt(i, 0))) {
                usuariosConRol++;
            }
        }
        
        if (usuariosConRol > 0) {
            JOptionPane.showMessageDialog(this,
                "No se puede eliminar el rol '" + nombreRol + "' porque tiene " + 
                usuariosConRol + " usuario(s) asignado(s).\n\n" +
                "Reasigne los usuarios a otro rol antes de eliminar este.",
                "Rol en uso",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el rol '" + nombreRol + "'?\n\n" +
            "Esta acción eliminará el rol del sistema.\n" +
            "Los usuarios con este rol perderán acceso al sistema.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
                collection.deleteOne(eq("nombre", nombreRol));
                
                JOptionPane.showMessageDialog(this,
                    "Rol eliminado exitosamente.",
                    "Rol Eliminado",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarRoles();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el rol: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarPermisosRolCompleto() {
        // EXACTO como mostrarPermisosRol() en GestionarRolesInterfaz
        int filaSeleccionada = tablaRoles.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un rol de la tabla.",
                "Ningún rol seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombreRol = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 0);
        
        // Obtener permisos según el rol
        String[] permisos = obtenerPermisosPorRol(nombreRol);
        
        // Crear diálogo para mostrar permisos
        JDialog dialogo = new JDialog(this, "Permisos del Rol: " + nombreRol, true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(600, 500);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Información del rol
        JLabel lblNombre = new JLabel(" Nombre: " + nombreRol);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        String descripcion = obtenerDescripcionRol(nombreRol);
        JLabel lblDescripcion = new JLabel(" Descripción: " + descripcion);
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        panelContenido.add(lblNombre);
        panelContenido.add(lblDescripcion);
        
        // Lista de permisos
        JLabel lblTituloPermisos = new JLabel(" Permisos asignados (" + permisos.length + "):");
        lblTituloPermisos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloPermisos.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelContenido.add(lblTituloPermisos);
        
        JPanel panelPermisos = new JPanel();
        panelPermisos.setLayout(new BoxLayout(panelPermisos, BoxLayout.Y_AXIS));
        panelPermisos.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        List<String> permisosOrdenados = new ArrayList<>(Arrays.asList(permisos));
        Collections.sort(permisosOrdenados);
        
        for (String permiso : permisosOrdenados) {
            JLabel lblPermiso = new JLabel("  • " + Rol.Permisos.obtenerDescripcion(permiso));
            lblPermiso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblPermiso.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            panelPermisos.add(lblPermiso);
        }
        
        JScrollPane scrollPane = new JScrollPane(panelPermisos);
        scrollPane.setPreferredSize(new Dimension(550, 300));
        panelContenido.add(scrollPane);
        
        // Botón cerrar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(120, 35));
        btnCerrar.addActionListener(e -> dialogo.dispose());
        panelBoton.add(btnCerrar);
        
        dialogo.add(panelContenido, BorderLayout.CENTER);
        dialogo.add(panelBoton, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private String obtenerDescripcionRol(String nombreRol) {
        for (int i = 0; i < modeloTablaRoles.getRowCount(); i++) {
            if (nombreRol.equals(modeloTablaRoles.getValueAt(i, 0))) {
                return (String) modeloTablaRoles.getValueAt(i, 1);
            }
        }
        return "Sin descripción";
    }
    
    private void cambiarEstadoRolCompleto() {
        // EXACTO como cambiarEstadoRol() en GestionarRolesInterfaz
        int filaSeleccionada = tablaRoles.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un rol de la tabla.",
                "Ningún rol seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombreRol = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 0);
        String tipo = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 3);
        String estadoActual = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 4);
        
        if ("Predeterminado".equals(tipo)) {
            JOptionPane.showMessageDialog(this,
                "No se puede desactivar un rol predeterminado del sistema.",
                "Operación no permitida",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean activo = "ACTIVO".equals(estadoActual);
        boolean nuevoEstado = !activo;
        
        String mensaje = nuevoEstado ? 
            "¿Desea ACTIVAR el rol '" + nombreRol + "'?" :
            "¿Desea DESACTIVAR el rol '" + nombreRol + "'?\n\n" +
            "Los usuarios con este rol no podrán acceder al sistema.";
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            mensaje,
            "Confirmar cambio de estado",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
                
                collection.updateOne(
                    eq("nombre", nombreRol),
                    new Document("$set", new Document("activo", nuevoEstado))
                );
                
                JOptionPane.showMessageDialog(this,
                    "Estado del rol actualizado exitosamente.",
                    "Estado Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarRoles();
                cargarUsuarios();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cambiar el estado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private boolean verificarEstadoRol(String nombreRol) {
        // Roles predeterminados siempre activos
        if (nombreRol.equals("Estudiante") || nombreRol.equals("Personal") || 
            nombreRol.equals("Administrador")) {
            return true;
        }
        
        // Verificar estado de roles personalizados
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            Document rolDoc = collection.find(eq("nombre", nombreRol)).first();
            
            if (rolDoc != null) {
                return rolDoc.getBoolean("activo", true);
            }
        } catch (Exception e) {
            System.err.println("Error al verificar estado del rol: " + e.getMessage());
        }
        
        return true;
    }
    
    private String[] obtenerPermisosPorRol(String rol) {
        // Roles predeterminados
        switch (rol) {
            case "Estudiante":
                return Rol.Permisos.obtenerPermisosEstudiante();
            case "Personal":
                return Rol.Permisos.obtenerPermisosPersonal();
            case "Administrador":
                return Rol.Permisos.obtenerPermisosAdministrador();
            default:
                // Para roles personalizados, obtener de la base de datos
                try {
                    MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
                    Document rolDoc = collection.find(eq("nombre", rol)).first();
                    
                    if (rolDoc != null && rolDoc.containsKey("permisos")) {
                        List<String> permisosList = (List<String>) rolDoc.get("permisos");
                        return permisosList.toArray(new String[0]);
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener permisos del rol: " + e.getMessage());
                }
                return new String[0];
        }
    }
    
    public static void main(String[] args) {
        ConexionMongoDB.conectar();
        
        SwingUtilities.invokeLater(() -> {
            new GestionUsuariosRolesUnificada();
        });
    }
}