package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.*;
import modelo.*;

public class ControladorRolesAvanzados {

    private static final Logger logger = Logger.getLogger(ControladorRolesAvanzados.class.getName());

    public static boolean crearRolPersonalizado(String nombre, String descripcion, Set<String> permisos) {
        try {
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

            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");

            if (collection.find(eq("nombre", nombre)).first() != null) {
                JOptionPane.showMessageDialog(null,
                        "Ya existe un rol con este nombre.",
                        "Rol duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (nombre.equals("Estudiante") || nombre.equals("Personal") || nombre.equals("Administrador")) {
                JOptionPane.showMessageDialog(null,
                        "No puede usar nombres de roles predeterminados.",
                        "Nombre reservado",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Document rolDoc = new Document()
                    .append("nombre", nombre)
                    .append("descripcion", descripcion)
                    .append("permisos", new ArrayList<>(permisos))
                    .append("activo", true)
                    .append("fechaCreacion", new Date())
                    .append("creadoPor", ControladorPrincipal.getUsuarioActual());

            collection.insertOne(rolDoc);

            logger.log(Level.INFO, "Rol personalizado creado: {0} con {1} permisos", 
                new Object[]{nombre, permisos.size()});

            JOptionPane.showMessageDialog(null,
                    "Rol '" + nombre + "' creado exitosamente con " + permisos.size() + " permisos.",
                    "Rol Creado",
                    JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (MongoException e) {
            logger.log(Level.SEVERE, "Error de MongoDB al crear rol personalizado", e);
            JOptionPane.showMessageDialog(null,
                    "Error de base de datos al crear rol.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Datos inválidos al crear rol: {0}", e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error: datos inválidos al crear rol.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public static Rol obtenerRol(String nombreRol) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("roles_personalizados");
            Document rolDoc = collection.find(eq("nombre", nombreRol)).first();
            
            if (rolDoc == null) {
                // Rol predeterminado
                return new Rol(nombreRol, true);
            }
            
            boolean activo = rolDoc.getBoolean("activo", true);
            return new Rol(nombreRol, activo);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener rol: " + nombreRol, e);
            return null;
        }
    }
}
