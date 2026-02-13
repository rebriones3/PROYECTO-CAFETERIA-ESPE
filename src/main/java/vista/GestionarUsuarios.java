package vista;

import controlador.*;
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
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class GestionarUsuarios extends javax.swing.JFrame {

    private DefaultTableModel modeloTabla;

    public GestionarUsuarios() {
        initComponents();
        configurarTabla();
        configurarEventos();
        cargarUsuarios();
        actualizarVisualizacionRoles();
    }
    
    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{"Rol", "Correo", "Contraseña", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modeloTabla);
        
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (value instanceof Boolean) {
                    Boolean activo = (Boolean) value;
                    if (activo) {
                        c.setBackground(new Color(220, 255, 220));
                        c.setForeground(new Color(0, 100, 0));
                        ((JLabel) c).setText("ACTIVO");
                    } else {
                        c.setBackground(new Color(255, 220, 220));
                        c.setForeground(new Color(139, 0, 0));
                        ((JLabel) c).setText("INACTIVO");
                    }
                }
                
                return c;
            }
        });
    }
    
    private void configurarEventos() {
        jButtonAgregar.addActionListener(evt -> mostrarDialogoAgregarUsuario());
        jButtonAsignarRol.addActionListener(evt -> mostrarDialogoAsignarRol());
        jButtonEditar.addActionListener(evt -> mostrarDialogoEditar());
        jButtonEliminar.addActionListener(evt -> eliminarUsuario());
        jButtonBuscar.addActionListener(evt -> buscarUsuario());
        jTextFieldBuscar.addActionListener(evt -> buscarUsuario());
        jButtonVolver.addActionListener(evt -> volver());
        jButtonGestionarEstado.addActionListener(evt -> mostrarDialogoGestionarEstadoRol());
        jButtonCrearRol.addActionListener(evt -> mostrarDialogoCrearRolPersonalizado());
    }
    
    private void cargarUsuarios() {
        ControladorUsuarios.cargarUsuarios(modeloTabla);
        
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String rol = (String) modeloTabla.getValueAt(i, 0);
            boolean estadoRol = verificarEstadoRol(rol);
            modeloTabla.setValueAt(estadoRol, i, 3);
        }
    }
    
    private void buscarUsuario() {
        String busqueda = jTextFieldBuscar.getText().trim().toLowerCase();
        
        if (busqueda.isEmpty()) {
            cargarUsuarios();
            return;
        }
        
        modeloTabla.setRowCount(0);
        boolean encontrado = false;
        
        for (var usuario : ControladorUsuarios.getUsuarios()) {
            if (usuario.getRol().toLowerCase().contains(busqueda) ||
                usuario.getCorreo().toLowerCase().contains(busqueda)) {
                
                boolean estadoRol = verificarEstadoRol(usuario.getRol());
                
                modeloTabla.addRow(new Object[]{
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
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        JLabel lblCorreo = new JLabel("Correo electrónico:");
        JTextField txtCorreo = new JTextField(20);
        
        JLabel lblContraseña = new JLabel("Contraseña:");
        JPasswordField txtContraseña = new JPasswordField(20);
        
        JLabel lblConfirmar = new JLabel("Confirmar contraseña:");
        JPasswordField txtConfirmar = new JPasswordField(20);
        
        JLabel lblRol = new JLabel("Rol inicial:");
        java.util.List<String> rolesDisponibles = ControladorRolesAvanzados.obtenerTodosLosRoles();
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
            String contraseña = new String(txtContraseña.getPassword()).trim();
            String confirmar = new String(txtConfirmar.getPassword()).trim();
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
            
            if (contraseña.length() < 4) {
                JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 4 caracteres.",
                    "Contraseña muy corta",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (ControladorUsuarios.agregarUsuario(rol, correo, contraseña)) {
                cargarUsuarios();
            }
        }
    }
    
    private void mostrarDialogoCrearRolPersonalizado() {
        JDialog dialogo = new JDialog(this, "Crear Nuevo Rol Personalizado", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(800, 650);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // INFORMACIÓN BÁSICA
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(153, 0, 153), 2),
            "Información del Rol",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(153, 0, 153)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre del rol:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelInfo.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(25);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelInfo.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelInfo.add(lblDescripcion, gbc);
        
        gbc.gridx = 1;
        JTextField txtDescripcion = new JTextField(25);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelInfo.add(txtDescripcion, gbc);
        
        panelPrincipal.add(panelInfo);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // PERMISOS
        JPanel panelPermisos = new JPanel();
        panelPermisos.setLayout(new BoxLayout(panelPermisos, BoxLayout.Y_AXIS));
        panelPermisos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(153, 0, 153), 2),
            "Seleccione los Permisos del Rol",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(153, 0, 153)
        ));
        
        Map<String, JCheckBox> checkboxesPermisos = new HashMap<>();
        
        JPanel panelEstudiante = crearPanelCategoria(
            "🛒 FUNCIONES DE ESTUDIANTE",
            new Color(102, 153, 255),
            Rol.Permisos.obtenerPermisosEstudiante(),
            checkboxesPermisos
        );
        panelPermisos.add(panelEstudiante);
        panelPermisos.add(Box.createVerticalStrut(10));
        
        JPanel panelPersonal = crearPanelCategoria(
            "👨‍🍳 FUNCIONES DE PERSONAL/COCINA",
            new Color(255, 153, 0),
            Rol.Permisos.obtenerPermisosPersonal(),
            checkboxesPermisos
        );
        panelPermisos.add(panelPersonal);
        panelPermisos.add(Box.createVerticalStrut(10));
        
        JPanel panelAdministrador = crearPanelCategoria(
            "⚙️ FUNCIONES DE ADMINISTRADOR",
            new Color(204, 0, 102),
            Rol.Permisos.obtenerPermisosAdministrador(),
            checkboxesPermisos
        );
        panelPermisos.add(panelAdministrador);
        
        panelPrincipal.add(panelPermisos);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // BOTONES RÁPIDOS
        JPanel panelBotonesRapidos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnTodoEstudiante = crearBotonRapido("Todos: Estudiante", new Color(102, 153, 255));
        JButton btnTodoPersonal = crearBotonRapido("Todos: Personal", new Color(255, 153, 0));
        JButton btnTodoAdmin = crearBotonRapido("Todos: Admin", new Color(204, 0, 102));
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
        
        btnSeleccionarTodo.addActionListener(e -> 
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(true))
        );
        
        btnLimpiar.addActionListener(e -> 
            checkboxesPermisos.values().forEach(cb -> cb.setSelected(false))
        );
        
        panelBotonesRapidos.add(btnTodoEstudiante);
        panelBotonesRapidos.add(btnTodoPersonal);
        panelBotonesRapidos.add(btnTodoAdmin);
        panelBotonesRapidos.add(new JSeparator(SwingConstants.VERTICAL));
        panelBotonesRapidos.add(btnSeleccionarTodo);
        panelBotonesRapidos.add(btnLimpiar);
        
        panelPrincipal.add(panelBotonesRapidos);
        
        // PREVIEW
        JLabel lblPreview = new JLabel("📋 Vista Previa: 0 permisos seleccionados");
        lblPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPreview.setForeground(new Color(102, 102, 102));
        lblPreview.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        for (JCheckBox cb : checkboxesPermisos.values()) {
            cb.addActionListener(e -> {
                long sel = checkboxesPermisos.values().stream().filter(JCheckBox::isSelected).count();
                lblPreview.setText("📋 Vista Previa: " + sel + " permisos seleccionados");
            });
        }
        
        panelPrincipal.add(lblPreview);
        
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // BOTONES FINALES
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
            
            if (ControladorRolesAvanzados.crearRolPersonalizado(nombre, descripcion, permisosSeleccionados)) {
                JOptionPane.showMessageDialog(dialogo,
                    "¡Rol creado exitosamente!\n\n" +
                    "Nombre: " + nombre + "\n" +
                    "Permisos: " + permisosSeleccionados.size() + "\n\n" +
                    "Ahora puede asignar este rol a usuarios.",
                    "Rol Creado",
                    JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(scrollPane, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private JPanel crearPanelCategoria(String titulo, Color color, String[] permisos, Map<String, JCheckBox> map) {
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
    
    private JButton crearBotonRapido(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }
    
    private void mostrarDialogoAsignarRol() {
        int filaSeleccionada = jTable1.getSelectedRow();
        
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
        
        java.util.List<String> rolesDisponibles = ControladorRolesAvanzados.obtenerTodosLosRoles();
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
                JOptionPane.showMessageDialog(this,
                    "Rol asignado exitosamente.\n\n" +
                    "Usuario: " + usuario.getCorreo() + "\n" +
                    "Nuevo rol: " + nuevoRol,
                    "Rol Asignado",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void mostrarDialogoEditar() {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        var usuario = ControladorUsuarios.getUsuario(filaSeleccionada);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel lblCorreo = new JLabel("Correo:");
        JLabel lblCorreoValor = new JLabel(usuario.getCorreo());
        lblCorreoValor.setFont(lblCorreoValor.getFont().deriveFont(Font.BOLD));
        
        JLabel lblRol = new JLabel("Rol:");
        java.util.List<String> rolesDisponibles = ControladorRolesAvanzados.obtenerTodosLosRoles();
        JComboBox<String> cmbRol = new JComboBox<>(rolesDisponibles.toArray(new String[0]));
        cmbRol.setSelectedItem(usuario.getRol());
        
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
            
            if (nuevaContraseña.length() < 4 && !nuevaContraseña.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 4 caracteres.",
                    "Contraseña muy corta",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (nuevaContraseña.isEmpty()) {
                if (ControladorUsuarios.asignarRolUsuario(filaSeleccionada, nuevoRol)) {
                    JOptionPane.showMessageDialog(this,
                        "Rol actualizado exitosamente.",
                        "Usuario Actualizado",
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarUsuarios();
                }
            } else {
                if (ControladorUsuarios.editarUsuario(filaSeleccionada, nuevoRol, nuevaContraseña)) {
                    cargarUsuarios();
                }
            }
        }
    }
    
    private void eliminarUsuario() {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String correo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String rol = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        
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
                verificarRolSinUsuarios(rol);
            }
        }
    }
    
    private void mostrarDialogoGestionarEstadoRol() {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la tabla.",
                "Ningún usuario seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String rol = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String correo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        boolean estadoActual = (Boolean) modeloTabla.getValueAt(filaSeleccionada, 3);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel lblRol = new JLabel("<html><b>Rol:</b> " + rol + "</html>");
        JLabel lblUsuario = new JLabel("<html><b>Usuario:</b> " + correo + "</html>");
        panelInfo.add(lblRol);
        panelInfo.add(lblUsuario);
        
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lblEstado = new JLabel("Estado actual:");
        JLabel lblEstadoValor = new JLabel(estadoActual ? "ACTIVO" : "INACTIVO");
        lblEstadoValor.setFont(lblEstadoValor.getFont().deriveFont(Font.BOLD, 14));
        lblEstadoValor.setForeground(estadoActual ? Color.GREEN.darker() : Color.RED);
        
        panelEstado.add(lblEstado);

        panelEstado.add(lblEstadoValor);
        
        panel.add(panelInfo, BorderLayout.NORTH);
        panel.add(panelEstado, BorderLayout.CENTER);
        
        int opcion = JOptionPane.showConfirmDialog(this, panel,
            "Estado del Rol: " + rol,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (opcion == JOptionPane.OK_OPTION) {
            boolean nuevoEstado = !estadoActual;
            
            if (cambiarEstadoRol(rol, nuevoEstado)) {
                JOptionPane.showMessageDialog(this,
                    "Estado del rol '" + rol + "' actualizado a: " +
                    (nuevoEstado ? "ACTIVO" : "INACTIVO"),
                    "Estado Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            }
        }
    }
    
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
    
    private boolean cambiarEstadoRol(String nombreRol, boolean nuevoEstado) {
        // No permitir desactivar roles predeterminados
        if (nombreRol.equals("Estudiante") || nombreRol.equals("Personal") || 
            nombreRol.equals("Administrador")) {
            JOptionPane.showMessageDialog(this,
                "No se puede desactivar un rol predeterminado del sistema.",
                "Operación no permitida",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            
            collection.updateOne(
                eq("nombre", nombreRol),
                new Document("$set", new Document("activo", nuevoEstado)
                    .append("fechaModificacion", new java.util.Date())
                    .append("modificadoPor", ControladorPrincipal.getUsuarioActual()))
            );
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cambiar estado del rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void verificarRolSinUsuarios(String rol) {
        // Verificar si quedan usuarios con este rol
        int usuariosConRol = 0;
        for (var usuario : ControladorUsuarios.getUsuarios()) {
            if (usuario.getRol().equals(rol)) {
                usuariosConRol++;
            }
        }
        
        if (usuariosConRol == 0 && !esRolPredeterminado(rol)) {
            int opcion = JOptionPane.showConfirmDialog(this,
                "No quedan usuarios con el rol '" + rol + "'.\n" +
                "¿Desea eliminar este rol del sistema?",
                "Rol sin usuarios",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (opcion == JOptionPane.YES_OPTION) {
                if (ControladorRolesAvanzados.eliminarRolPersonalizado(rol)) {
                    JOptionPane.showMessageDialog(this,
                        "El rol '" + rol + "' ha sido eliminado del sistema.",
                        "Rol eliminado",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    private boolean esRolPredeterminado(String rol) {
        return rol.equals("Estudiante") || rol.equals("Personal") || rol.equals("Administrador");
    }
    
    private void actualizarVisualizacionRoles() {
        // Este método puede ser llamado para actualizar la visualización
        // después de crear o modificar roles
    }
    
    private void volver() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Desea volver al panel de administración?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            this.dispose();
            new AdministradorInterfaz().setVisible(true);
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButtonAgregar;
    private javax.swing.JButton jButtonAsignarRol;
    private javax.swing.JButton jButtonBuscar;
    private javax.swing.JButton jButtonCrearRol;
    private javax.swing.JButton jButtonEditar;
    private javax.swing.JButton jButtonEliminar;
    private javax.swing.JButton jButtonGestionarEstado;
    private javax.swing.JButton jButtonVolver;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldBuscar;
    // End of variables declaration

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Código generado por el Form Editor
        // Configurar los componentes de la interfaz aquí
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestión de Usuarios y Roles");
        
        // Inicializar botones
        jButtonAgregar = new javax.swing.JButton("Agregar Usuario");
        jButtonAsignarRol = new javax.swing.JButton("Asignar Rol");
        jButtonEditar = new javax.swing.JButton("Editar Usuario");
        jButtonEliminar = new javax.swing.JButton("Eliminar Usuario");
        jButtonBuscar = new javax.swing.JButton("Buscar");
        jButtonVolver = new javax.swing.JButton("Volver");
        jButtonGestionarEstado = new javax.swing.JButton("Gestionar Estado Rol");
        jButtonCrearRol = new javax.swing.JButton("Crear Rol Personalizado");
        
        jTextFieldBuscar = new javax.swing.JTextField();
        jTable1 = new javax.swing.JTable();
        
        // Configurar layout y otros componentes según tu diseño
        pack();
        setLocationRelativeTo(null);
    }
}