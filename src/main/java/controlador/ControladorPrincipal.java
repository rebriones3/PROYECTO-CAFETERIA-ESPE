package controlador;

import com.mongodb.client.*;
import org.bson.Document;
import javax.swing.JOptionPane;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import modelo.*;
import vista.*;

public class ControladorPrincipal {
    private static final Logger logger = Logger.getLogger(ControladorPrincipal.class.getName());
    private static List<Usuario> usuarios = new ArrayList<>();
    private static String usuarioActual;
    private static String rolActual;
    
    static {
        // Inicializar conexión MongoDB
        ConexionMongoDB.conectar();
        cargarUsuarios();
    }
    
    private static void cargarUsuarios() {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            usuarios.clear();
            
            for (Document doc : collection.find()) {
                // DEBUG: Mostrar todos los campos del documento
                System.out.println("Documento completo: " + doc.toJson());
                
                String rol = doc.getString("rol");
                String correo = doc.getString("correo");
                
                // Intentar diferentes nombres de campo para la contraseña
                String contraseña = null;
                if (doc.containsKey("contraseña")) {
                    contraseña = doc.getString("contraseña");
                } else if (doc.containsKey("contrasena")) {
                    contraseña = doc.getString("contrasena");
                } else if (doc.containsKey("password")) {
                    contraseña = doc.getString("password");
                } else if (doc.containsKey("clave")) {
                    contraseña = doc.getString("clave");
                }
                
                // DEBUG: Verificar valores
                System.out.println("Rol: " + rol + ", Correo: " + correo + ", Contraseña: " + contraseña);
                
                if (contraseña != null) {
                    usuarios.add(new Usuario(rol, correo, contraseña));
                } else {
                    System.err.println("Error: Contraseña es null para usuario: " + correo);
                }
            }
            
            System.out.println("Usuarios cargados: " + usuarios.size());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al cargar usuarios desde MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar usuarios desde MongoDB", e);
        }
    }
    
    public static boolean validarLogin(String rol, String correo, String contraseña) {
        if (correo.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese su correo y contraseña.");
            return false;
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un correo válido.");
            return false;
        }

        // Usar el nuevo método de verificación con encriptación
        if (ControladorUsuarios.verificarCredenciales(correo, contraseña)) {
            // Obtener el rol del usuario desde la base de datos
            String rolBD = ControladorUsuarios.obtenerRolPorCorreo(correo);

            if (rolBD != null && rolBD.equalsIgnoreCase(rol)) {
                usuarioActual = correo;
                rolActual = rol;
                return true;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "El rol seleccionado no coincide con el usuario.");
                return false;
            }
        }

        JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
        return false;
    }
    
    public static void guardarPedido(String productos, double total, String codigoTransaccion) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("pedidos");

            Document pedido = new Document()
                .append("estudiante", usuarioActual)
                .append("productos", productos)
                .append("total", total)
                .append("estado", "Pendiente")
                .append("codigoTransaccion", codigoTransaccion)
                .append("fecha", new java.util.Date());

            collection.insertOne(pedido);

            JOptionPane.showMessageDialog(null, "Pedido guardado exitosamente!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al guardar el pedido en MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al guardar el pedido en MongoDB", e);
        }
    }
    
    public static void navegarSegunRol() {
        if ("Estudiante".equalsIgnoreCase(rolActual)) {
            ESPECAFE ventanaEstudiante = new ESPECAFE();
            ventanaEstudiante.setVisible(true);
        } else if ("Personal".equalsIgnoreCase(rolActual)) {
            InterfazPersonal ventanaPersonal = new InterfazPersonal();
            ventanaPersonal.setVisible(true);
        } else if ("Administrador".equalsIgnoreCase(rolActual)) {
            AdministradorInterfaz ventanaAdministrador = new AdministradorInterfaz();
            ventanaAdministrador.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Rol no reconocido: " + rolActual);
        }
    }
    
    public static String getUsuarioActual() {
        return usuarioActual;
    }
    
    public static String getRolActual() {
        return rolActual;
    }
    
    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
        abrirLogin();
    }
    // AGREGAR ESTOS MÉTODOS A ControladorPrincipal.java

/**
 * Validar login automático (sin seleccionar rol manualmente)
     * @param correo
     * @param contraseña
 */
public static boolean validarLoginAutomatico(String correo, String contraseña) {
    if (correo.isEmpty() || contraseña.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Por favor, ingrese su correo y contraseña.");
        return false;
    }

    if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        JOptionPane.showMessageDialog(null, "Por favor, ingrese un correo válido.");
        return false;
    }

    // Verificar credenciales
    if (ControladorUsuarios.verificarCredenciales(correo, contraseña)) {
        // Obtener el rol automáticamente
        String rolBD = ControladorUsuarios.obtenerRolPorCorreo(correo);

        if (rolBD != null) {
            // Verificar que el rol esté activo
            modelo.Rol rol = ControladorRolesAvanzados.obtenerRol(rolBD);
            if (rol == null || !rol.isActivo()) {
                JOptionPane.showMessageDialog(null,
                    "Su rol '" + rolBD + "' está inactivo.\n" +
                    "Contacte al administrador del sistema.",
                    "Rol inactivo",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            usuarioActual = correo;
            rolActual = rolBD;
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo identificar el rol del usuario.");
            return false;
        }
    }

    JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
    return false;
}

public static void navegarInterfazUnificada() {
    try {
        // Obtener permisos del usuario actual
        modelo.Rol rol = ControladorRolesAvanzados.obtenerRol(rolActual);
        
        if (rol == null) {
            JOptionPane.showMessageDialog(null, "Error: No se encontró el rol asignado.");
            return;
        }
        
        // Abrir la interfaz unificada con los permisos del rol
        InterfazUnificada interfazUnificada = new InterfazUnificada(usuarioActual, rol);
        interfazUnificada.setVisible(true);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, 
            "Error al abrir la interfaz: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        logger.log(Level.SEVERE, "Error al abrir la interfaz unificada", e);
    }
}
    public static void abrirLogin() {
        JFrame frameLogin = new JFrame("Cafetería ESPE - Login");
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        InterfazLoginMejorada panelLogin = new InterfazLoginMejorada();
        frameLogin.add(panelLogin);
        frameLogin.pack();
        frameLogin.setLocationRelativeTo(null);
        frameLogin.setVisible(true);
    }
}