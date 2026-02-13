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
            logger.log(Level.SEVERE, "Error de base de datos al crear rol", e);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: datos inválidos al crear rol: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Datos inválidos al crear rol", e);
            return false;
        }
    }
}

