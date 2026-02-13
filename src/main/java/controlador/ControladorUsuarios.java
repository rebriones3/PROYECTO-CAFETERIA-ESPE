package controlador;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.*;
import modelo.*;
import modelo.Encriptacion;

public class ControladorUsuarios {
    private static final Logger logger = Logger.getLogger(ControladorUsuarios.class.getName());
    private static List<Usuario> usuarios = new ArrayList<>();
    
    static {
        ConexionMongoDB.conectar();
    }
    
    /**
     * Cargar usuarios desde MongoDB a la tabla
     */
    public static void cargarUsuarios(DefaultTableModel modelo) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            usuarios.clear();
            modelo.setRowCount(0);

            for (Document doc : collection.find()) {
                String rol = doc.getString("rol");
                String correo = doc.getString("correo");
                
                // Obtener contraseña encriptada
                String contraseñaEncriptada = null;
                if (doc.containsKey("contraseña")) {
                    contraseñaEncriptada = doc.getString("contraseña");
                } else if (doc.containsKey("contrasena")) {
                    contraseñaEncriptada = doc.getString("contrasena");
                } else if (doc.containsKey("password")) {
                    contraseñaEncriptada = doc.getString("password");
                } else if (doc.containsKey("clave")) {
                    contraseñaEncriptada = doc.getString("clave");
                }
                
                if (contraseñaEncriptada != null) {
                    Usuario usuario = new Usuario(rol, correo, contraseñaEncriptada);
                    usuarios.add(usuario);
                    
                    // Agregar a la tabla (mostrar como encriptada)
                    modelo.addRow(new Object[]{
                        rol,
                        correo,
                        "********" 
                    });
                }
            }
            
            System.out.println("Usuarios cargados: " + usuarios.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al cargar usuarios desde MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar usuarios desde MongoDB", e);
        }
    }
    
    public static boolean asignarRolUsuario(int indice, String nuevoRol) {
        if (indice < 0 || indice >= usuarios.size()) {
            JOptionPane.showMessageDialog(null, 
                "Seleccione un usuario válido.",
                "Usuario no seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (nuevoRol.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Seleccione un rol válido.",
                "Rol inválido",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Usuario usuario = usuarios.get(indice);
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            
            // Actualizar solo el rol en MongoDB
            collection.updateOne(
                eq("correo", usuario.getCorreo()),
                new Document("$set", 
                    new Document()
                        .append("rol", nuevoRol)
                        .append("fechaAsignacionRol", new java.util.Date())
                )
            );

            // Actualizar lista local
            usuario.setRol(nuevoRol);

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al asignar rol en MongoDB: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error al asignar rol en MongoDB", e);
            return false;
        }
    }
    
    /**
     * Agregar nuevo usuario con contraseña encriptada
     */
    public static boolean agregarUsuario(String rol, String correo, String contraseña) {
        // Validaciones
        if (rol.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Complete todos los campos correctamente.",
                "Campos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validar formato de correo
        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(null, 
                "Por favor, ingrese un correo válido.",
                "Correo inválido",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar longitud de contraseña
        if (contraseña.length() < 4) {
            JOptionPane.showMessageDialog(null, 
                "La contraseña debe tener al menos 4 caracteres.",
                "Contraseña muy corta",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Verificar si el usuario ya existe
        for (Usuario u : usuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo)) {
                JOptionPane.showMessageDialog(null, 
                    "Ya existe un usuario con este correo electrónico.",
                    "Usuario duplicado",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            
            // ENCRIPTAR CONTRASEÑA
            String contraseñaEncriptada = Encriptacion.encriptarContraseña(contraseña);
            
            // Crear documento del usuario
            Document usuarioDoc = new Document()
                .append("rol", rol)
                .append("correo", correo)
                .append("contraseña", contraseñaEncriptada)  // Guardar encriptada
                .append("fechaCreacion", new java.util.Date());
            
            collection.insertOne(usuarioDoc);
            
            // Actualizar lista local
            Usuario nuevoUsuario = new Usuario(rol, correo, contraseñaEncriptada);
            usuarios.add(nuevoUsuario);
            
            JOptionPane.showMessageDialog(null, 
                "Usuario creado exitosamente.\n\n" +
                "Rol: " + rol + "\n" +
                "Correo: " + correo + "\n" +
                "Contraseña: Encriptada y almacenada de forma segura",
                "Usuario Creado",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al agregar usuario en MongoDB: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error al agregar usuario en MongoDB", e);
            return false;
        }
    }
    
    /**
     * Eliminar usuario
     */
    public static boolean eliminarUsuario(int indice) {
        if (indice < 0 || indice >= usuarios.size()) {
            JOptionPane.showMessageDialog(null, 
                "Seleccione un usuario de la tabla.",
                "Usuario no seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Usuario usuario = usuarios.get(indice);
            String correoEliminar = usuario.getCorreo();
            
            // No permitir eliminar al administrador principal
            if (correoEliminar.equalsIgnoreCase("admin@espe.edu.ec")) {
                JOptionPane.showMessageDialog(null, 
                    "No se puede eliminar al administrador principal del sistema.",
                    "Operación no permitida",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            
            // Eliminar de MongoDB
            collection.deleteOne(eq("correo", correoEliminar));
            
            // Eliminar de lista local
            usuarios.remove(indice);
            
            JOptionPane.showMessageDialog(null, 
                "Usuario eliminado exitosamente.",
                "Usuario Eliminado",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar usuario de MongoDB: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error al eliminar usuario de MongoDB", e);
            return false;
        }
    }
    
    /**
     * Editar usuario con contraseña encriptada
     */
    public static boolean editarUsuario(int indice, String nuevoRol, String nuevaContraseña) {
        if (indice < 0 || indice >= usuarios.size()) {
            JOptionPane.showMessageDialog(null, 
                "Seleccione un usuario válido.",
                "Usuario no seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (nuevoRol.isEmpty() || nuevaContraseña.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Complete todos los campos correctamente.",
                "Campos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validar longitud de contraseña
        if (nuevaContraseña.length() < 4) {
            JOptionPane.showMessageDialog(null, 
                "La contraseña debe tener al menos 4 caracteres.",
                "Contraseña muy corta",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Usuario usuario = usuarios.get(indice);
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            
            // ENCRIPTAR NUEVA CONTRASEÑA
            String nuevaContraseñaEncriptada = Encriptacion.encriptarContraseña(nuevaContraseña);
            
            // Actualizar en MongoDB
            collection.updateOne(
                eq("correo", usuario.getCorreo()),
                new Document("$set", 
                    new Document()
                        .append("rol", nuevoRol)
                        .append("contraseña", nuevaContraseñaEncriptada)  // Actualizar encriptada
                        .append("fechaModificacion", new java.util.Date())
                )
            );

            // Actualizar lista local
            usuario.setRol(nuevoRol);
            usuario.setContraseña(nuevaContraseñaEncriptada);

            JOptionPane.showMessageDialog(null, 
                "Usuario actualizado exitosamente.\n" +
                "La nueva contraseña ha sido encriptada.",
                "Usuario Actualizado",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al editar usuario en MongoDB: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error al editar usuario en MongoDB", e);
            return false;
        }
    }
    
    /**
     * Obtener usuario por índice
     */
    public static Usuario getUsuario(int indice) {
        if (indice >= 0 && indice < usuarios.size()) {
            return usuarios.get(indice);
        }
        return null;
    }
    
    /**
     * Obtener lista de usuarios
     */
    public static List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }
    
    /**
     * Verificar credenciales para login
     */
    public static boolean verificarCredenciales(String correo, String contraseñaIngresada) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            Document usuarioDoc = collection.find(eq("correo", correo)).first();
            
            if (usuarioDoc != null) {
                String contraseñaEncriptadaAlmacenada = usuarioDoc.getString("contraseña");
                if (contraseñaEncriptadaAlmacenada != null) {
                    return Encriptacion.verificarContraseña(contraseñaIngresada, contraseñaEncriptadaAlmacenada);
                }
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("Error al verificar credenciales: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener rol de usuario por correo
     */
    public static String obtenerRolPorCorreo(String correo) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            Document usuarioDoc = collection.find(eq("correo", correo)).first();
            
            if (usuarioDoc != null) {
                return usuarioDoc.getString("rol");
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
            return null;
        }
    }
}