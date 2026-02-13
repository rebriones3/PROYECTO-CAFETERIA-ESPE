package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import java.util.*;
import static com.mongodb.client.model.Filters.*;
import modelo.*;

public class ControladorRolesAvanzados {
    
    /**
     * Crear rol personalizado con permisos específicos
     */
    public static boolean crearRolPersonalizado(String nombre, String descripcion, Set<String> permisos) {
        try {
            // Validaciones
            if (nombre.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "Complete todos los campos obligatorios.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (permisos.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "Debe asignar al menos un permiso al rol.",
                    "Sin permisos",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            // Verificar si el rol ya existe
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            if (collection.find(eq("nombre", nombre)).first() != null) {
                JOptionPane.showMessageDialog(null, 
                    "Ya existe un rol con este nombre.",
                    "Rol duplicado",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Verificar si es un rol predeterminado
            if (nombre.equals("Estudiante") || nombre.equals("Personal") || nombre.equals("Administrador")) {
                JOptionPane.showMessageDialog(null, 
                    "No puede usar nombres de roles predeterminados.",
                    "Nombre reservado",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Crear documento del rol
            Document rolDoc = new Document()
                .append("nombre", nombre)
                .append("descripcion", descripcion)
                .append("permisos", new ArrayList<>(permisos))
                .append("activo", true)
                .append("fechaCreacion", new Date())
                .append("creadoPor", ControladorPrincipal.getUsuarioActual());
            
            collection.insertOne(rolDoc);
            
            JOptionPane.showMessageDialog(null, 
                "Rol '" + nombre + "' creado exitosamente con " + permisos.size() + " permisos.",
                "Rol Creado",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al crear rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: datos inválidos al crear rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtener rol por nombre (incluye roles predeterminados y personalizados)
     */
    public static Rol obtenerRol(String nombreRol) {
        // Primero verificar roles predeterminados
        Rol rol = obtenerRolPredeterminado(nombreRol);
        if (rol != null) {
            return rol;
        }
        
        // Buscar en roles personalizados
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            Document rolDoc = collection.find(eq("nombre", nombreRol)).first();
            
            if (rolDoc != null && rolDoc.getBoolean("activo", true)) {
                Rol rolPersonalizado = new Rol(
                    rolDoc.getString("nombre"),
                    rolDoc.getString("descripcion")
                );
                
                List<String> permisos = (List<String>) rolDoc.get("permisos");
                if (permisos != null) {
                    for (String permiso : permisos) {
                        rolPersonalizado.agregarPermiso(permiso);
                    }
                }
                
                return rolPersonalizado;
            }
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al obtener rol personalizado: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("Error de tipo de dato al obtener rol personalizado: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtener roles predeterminados del sistema
     */
    private static Rol obtenerRolPredeterminado(String nombreRol) {
        Rol rol = new Rol(nombreRol, "Rol predeterminado del sistema");
        
        switch (nombreRol) {
            case "Estudiante":
                rol.setDescripcion("Usuario estudiante con acceso al menú y pedidos");
                for (String permiso : Rol.Permisos.obtenerPermisosEstudiante()) {
                    rol.agregarPermiso(permiso);
                }
                break;
                
            case "Personal":
                rol.setDescripcion("Personal de cocina para gestión de pedidos");
                for (String permiso : Rol.Permisos.obtenerPermisosPersonal()) {
                    rol.agregarPermiso(permiso);
                }
                break;
                
            case "Administrador":
                rol.setDescripcion("Administrador con acceso completo al sistema");
                for (String permiso : Rol.Permisos.obtenerPermisosAdministrador()) {
                    rol.agregarPermiso(permiso);
                }
                // Agregar también permisos de otros roles
                for (String permiso : Rol.Permisos.obtenerPermisosEstudiante()) {
                    rol.agregarPermiso(permiso);
                }
                for (String permiso : Rol.Permisos.obtenerPermisosPersonal()) {
                    rol.agregarPermiso(permiso);
                }
                break;
                
            default:
                return null;
        }
        
        return rol;
    }
    
    /**
     * Obtener todos los roles (predeterminados + personalizados activos)
     */
    public static List<String> obtenerTodosLosRoles() {
        List<String> roles = new ArrayList<>();
        
        // Agregar roles predeterminados
        roles.add("Estudiante");
        roles.add("Personal");
        roles.add("Administrador");
        
        // Agregar roles personalizados activos
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            for (Document doc : collection.find(eq("activo", true))) {
                roles.add(doc.getString("nombre"));
            }
        } catch (MongoException e) {
            System.err.println("Error de base de datos al cargar roles personalizados: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Error: datos nulos al cargar roles personalizados: " + e.getMessage());
        }
        
        return roles;
    }
    
    /**
     * Verificar si un usuario tiene un permiso específico
     */
    public static boolean tienePermiso(String correoUsuario, String permiso) {
        try {
            // Obtener rol del usuario
            String rolUsuario = ControladorUsuarios.obtenerRolPorCorreo(correoUsuario);
            if (rolUsuario == null) {
                return false;
            }
            
            // Obtener permisos del rol
            Rol rol = obtenerRol(rolUsuario);
            if (rol == null) {
                return false;
            }
            
            return rol.tienePermiso(permiso);
            
        } catch (NullPointerException e) {
            System.err.println("Error: datos nulos al verificar permiso: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Editar rol personalizado
     */
    public static boolean editarRolPersonalizado(String nombreOriginal, String nuevoNombre, 
                                                  String nuevaDescripcion, Set<String> nuevosPermisos) {
        try {
            // Validaciones
            if (nuevaDescripcion.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "Complete todos los campos obligatorios.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (nuevosPermisos.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "Debe asignar al menos un permiso al rol.",
                    "Sin permisos",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            
            Document actualizacion = new Document()
                .append("nombre", nuevoNombre)
                .append("descripcion", nuevaDescripcion)
                .append("permisos", new ArrayList<>(nuevosPermisos))
                .append("fechaModificacion", new Date())
                .append("modificadoPor", ControladorPrincipal.getUsuarioActual());
            
            collection.updateOne(
                eq("nombre", nombreOriginal),
                new Document("$set", actualizacion)
            );
            
            // Si cambió el nombre, actualizar usuarios
            if (!nombreOriginal.equals(nuevoNombre)) {
                MongoCollection<Document> usuariosCollection = ConexionMongoDB.getCollection("usuarios");
                usuariosCollection.updateMany(
                    eq("rol", nombreOriginal),
                    new Document("$set", new Document("rol", nuevoNombre))
                );
            }
            
            return true;
            
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al editar rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: datos inválidos al editar rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Eliminar rol personalizado
     */
    public static boolean eliminarRolPersonalizado(String nombreRol) {
        try {
            // Verificar que no sea un rol predeterminado
            if (nombreRol.equals("Estudiante") || nombreRol.equals("Personal") || 
                nombreRol.equals("Administrador")) {
                JOptionPane.showMessageDialog(null, 
                    "No se pueden eliminar los roles predeterminados del sistema.",
                    "Operación no permitida",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Verificar si hay usuarios con este rol
            MongoCollection<Document> usuariosCollection = ConexionMongoDB.getCollection("usuarios");
            long usuariosConRol = usuariosCollection.countDocuments(eq("rol", nombreRol));
            
            if (usuariosConRol > 0) {
                int confirmacion = JOptionPane.showConfirmDialog(null,
                    "Hay " + usuariosConRol + " usuario(s) con este rol.\n" +
                    "¿Desea eliminarlo de todas formas?\n" +
                    "Los usuarios perderán acceso al sistema hasta que se les asigne un nuevo rol.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirmacion != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            // Eliminar rol
            MongoCollection<Document> rolesCollection = ConexionMongoDB.getCollection("roles_personalizados");
            rolesCollection.deleteOne(eq("nombre", nombreRol));
            
            return true;
            
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al eliminar rol: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cambiar estado de un rol (activar/desactivar)
     */
    public static boolean cambiarEstadoRol(String nombreRol, boolean nuevoEstado) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            
            collection.updateOne(
                eq("nombre", nombreRol),
                new Document("$set", new Document("activo", nuevoEstado)
                    .append("fechaModificacion", new Date()))
            );
            
            return true;
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al cambiar estado del rol: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verificar si un rol está activo
     */
    public static boolean esRolActivo(String nombreRol) {
        // Roles predeterminados siempre activos
        if (nombreRol.equals("Estudiante") || nombreRol.equals("Personal") || 
            nombreRol.equals("Administrador")) {
            return true;
        }
        
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            Document rolDoc = collection.find(eq("nombre", nombreRol)).first();
            
            if (rolDoc != null) {
                return rolDoc.getBoolean("activo", true);
            }
        } catch (MongoException e) {
            System.err.println("Error de base de datos al verificar estado del rol: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Error: datos nulos al verificar estado del rol: " + e.getMessage());
        }
        
        return false;
    }
    
    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
