package vista;

import controlador.ControladorRolesAvanzados;
import modelo.ConexionMongoDB;
import modelo.Rol;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GestionarRolesInterfaz extends JFrame {
    
    private DefaultTableModel modeloTablaRoles;
    private JTable tablaRoles;
    
    public GestionarRolesInterfaz() {
        initComponents();
        configurarTabla();
        cargarRoles();
    }
    
    private void initComponents() {
        setTitle("Gestión de Roles - Sistema Cafetería ESPE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior (encabezado)
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central (tabla)
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior (botones)
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(153, 0, 153));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("⚙️ GESTIÓN DE ROLES DEL SISTEMA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Cree y gestione roles con permisos personalizados");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(230, 230, 230));
        
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);
        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createVerticalStrut(5));
        panelTexto.add(lblSubtitulo);
        
        panel.add(panelTexto, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        tablaRoles = new JTable();
        tablaRoles.setRowHeight(30);
        tablaRoles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaRoles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaRoles.getTableHeader().setBackground(new Color(230, 230, 250));
        
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
    
private JPanel crearPanelBotones() {
    // Usar GridLayout con 2 filas en lugar de FlowLayout
    JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 filas, 3 columnas
    panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
    
    JButton btnCrear = crearBoton("➕ Crear Rol Nuevo", new Color(0, 153, 51));
    JButton btnEditar = crearBoton("✏️ Editar Rol", new Color(255, 153, 0));
    JButton btnEliminar = crearBoton("🗑️ Eliminar Rol", new Color(204, 0, 0));
    JButton btnVerPermisos = crearBoton("👁️ Ver Permisos", new Color(51, 102, 204));
    JButton btnActivarDesactivar = crearBoton("🔄 Activar/Desactivar", new Color(102, 102, 102));
    JButton btnVolver = crearBoton("← Volver", new Color(153, 153, 153));
    
    btnCrear.addActionListener(e -> mostrarDialogoCrearRol());
    btnEditar.addActionListener(e -> mostrarDialogoEditarRol());
    btnEliminar.addActionListener(e -> eliminarRol());
    btnVerPermisos.addActionListener(e -> mostrarPermisosRol());
    btnActivarDesactivar.addActionListener(e -> cambiarEstadoRol());
    btnVolver.addActionListener(e -> dispose());
    
    // Agregar en el orden que prefieras
    panel.add(btnCrear);
    panel.add(btnEditar);
    panel.add(btnEliminar);
    panel.add(btnVerPermisos);
    panel.add(btnActivarDesactivar);
    panel.add(btnVolver);
    
    return panel;
}
    
    private JButton crearBoton(String texto, Color color) {
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
    
    private void configurarTabla() {
        modeloTablaRoles = new DefaultTableModel(
            new Object[]{"Nombre del Rol", "Descripción", "Cantidad Permisos", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRoles.setModel(modeloTablaRoles);
        
        // Configurar ancho de columnas
        tablaRoles.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaRoles.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaRoles.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaRoles.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaRoles.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        // Renderizador para columna de estado
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
    }
    
    private void cargarRoles() {
        modeloTablaRoles.setRowCount(0);
        
        // Cargar roles predeterminados
        agregarRolPredeterminado("Estudiante", "Usuario estudiante con acceso al menú y pedidos", 
            Rol.Permisos.obtenerPermisosEstudiante().length);
        agregarRolPredeterminado("Personal", "Personal de cocina para gestión de pedidos", 
            Rol.Permisos.obtenerPermisosPersonal().length);
        agregarRolPredeterminado("Administrador", "Administrador con acceso completo al sistema", 
            Rol.Permisos.obtenerPermisosAdministrador().length);
        
        // Cargar roles personalizados desde MongoDB
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
    
    void mostrarDialogoCrearRol() {
        JDialog dialogo = new JDialog(this, "Crear Nuevo Rol Personalizado", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(900, 700);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Información básica
        JPanel panelInfo = crearPanelInformacionRol();
        JTextField txtNombre = (JTextField) panelInfo.getComponent(1);
        JTextField txtDescripcion = (JTextField) panelInfo.getComponent(3);
        
        panelPrincipal.add(panelInfo);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // Panel de permisos con checkboxes
        Map<String, JCheckBox> checkboxesPermisos = new HashMap<>();
        JPanel panelPermisos = crearPanelPermisosCompleto(checkboxesPermisos);
        
        panelPrincipal.add(panelPermisos);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // Botones rápidos
        JPanel panelBotonesRapidos = crearPanelBotonesRapidos(checkboxesPermisos);
        panelPrincipal.add(panelBotonesRapidos);
        
        // Preview de permisos seleccionados
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
        
        // Botones finales
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
                cargarRoles();
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
            "🛒 FUNCIONES DE ESTUDIANTE",
            new Color(102, 153, 255),
            Rol.Permisos.obtenerPermisosEstudiante(),
            checkboxesPermisos
        );
        panelPrincipal.add(panelEstudiante);
        panelPrincipal.add(Box.createVerticalStrut(10));
        
        // Permisos de Personal
        JPanel panelPersonal = crearPanelCategoria(
            "👨‍🍳 FUNCIONES DE PERSONAL/COCINA",
            new Color(255, 153, 0),
            Rol.Permisos.obtenerPermisosPersonal(),
            checkboxesPermisos
        );
        panelPrincipal.add(panelPersonal);
        panelPrincipal.add(Box.createVerticalStrut(10));
        
        // Permisos de Administrador
        JPanel panelAdministrador = crearPanelCategoria(
            "⚙️ FUNCIONES DE ADMINISTRADOR",
            new Color(204, 0, 102),
            Rol.Permisos.obtenerPermisosAdministrador(),
            checkboxesPermisos
        );
        panelPrincipal.add(panelAdministrador);
        
        return panelPrincipal;
    }
    
    private JPanel crearPanelCategoria(String titulo, Color color, String[] permisos, 
                                       Map<String, JCheckBox> map) {
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
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }
    
    private void mostrarDialogoEditarRol() {
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
        JLabel lblPreview = new JLabel("📋 Vista Previa: " + permisosActuales.size() + " permisos seleccionados");
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
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        JButton btnGuardar = new JButton("✓ Guardar Cambios");
        btnGuardar.setBackground(new Color(0, 153, 51));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        
        JButton btnCancelar = new JButton("✗ Cancelar");
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
            
            if (ControladorRolesAvanzados.editarRolPersonalizado(nombreRol, nombreRol, 
                descripcion, permisosSeleccionados)) {
                JOptionPane.showMessageDialog(dialogo,
                    "¡Rol actualizado exitosamente!",
                    "Rol Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
                cargarRoles();
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
private void eliminarRol() {
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
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro que desea eliminar el rol '" + nombreRol + "'?\n\n" +
        "Esta acción eliminará el rol del sistema.\n" +
        "Los usuarios con este rol perderán acceso al sistema.",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        if (ControladorRolesAvanzados.eliminarRolPersonalizado(nombreRol)) {
            JOptionPane.showMessageDialog(this,
                "Rol eliminado exitosamente.",
                "Rol Eliminado",
                JOptionPane.INFORMATION_MESSAGE);
            cargarRoles();
        }
    }
}

private void mostrarPermisosRol() {
    int filaSeleccionada = tablaRoles.getSelectedRow();
    
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this,
            "Por favor, seleccione un rol de la tabla.",
            "Ningún rol seleccionado",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String nombreRol = (String) modeloTablaRoles.getValueAt(filaSeleccionada, 0);
    
    Rol rol = ControladorRolesAvanzados.obtenerRol(nombreRol);
    
    if (rol == null) {
        JOptionPane.showMessageDialog(this,
            "No se pudo cargar la información del rol.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Crear diálogo para mostrar permisos
    JDialog dialogo = new JDialog(this, "Permisos del Rol: " + nombreRol, true);
    dialogo.setLayout(new BorderLayout(10, 10));
    dialogo.setSize(600, 500);
    dialogo.setLocationRelativeTo(this);
    
    JPanel panelContenido = new JPanel();
    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
    panelContenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Información del rol
    JLabel lblNombre = new JLabel("📌 Nombre: " + rol.getNombre());
    lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblNombre.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    
    JLabel lblDescripcion = new JLabel("📄 Descripción: " + rol.getDescripcion());
    lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblDescripcion.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    
    panelContenido.add(lblNombre);
    panelContenido.add(lblDescripcion);
    
    // Lista de permisos
    JLabel lblTituloPermisos = new JLabel("✅ Permisos asignados (" + rol.getPermisos().size() + "):");
    lblTituloPermisos.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblTituloPermisos.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    panelContenido.add(lblTituloPermisos);
    
    JPanel panelPermisos = new JPanel();
    panelPermisos.setLayout(new BoxLayout(panelPermisos, BoxLayout.Y_AXIS));
    panelPermisos.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    
    List<String> permisosOrdenados = new ArrayList<>(rol.getPermisos());
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

private void cambiarEstadoRol() {
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
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cambiar el estado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}