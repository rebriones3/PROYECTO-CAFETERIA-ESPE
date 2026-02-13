package controlador;

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

    /**
     * Crear rol personalizado con permisos específicos
     */
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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al crear rol: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error al crear rol personalizado", e);
            return false;
        }
    }

    /**
     * Obtener rol por nombre
     */
    public static Rol obtenerRol(String nombreRol) {

        Rol rol = obtenerRolPredeterminado(nombreRol);
        if (rol != null) {
            return rol;
        }

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

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener rol personalizado", e);
        }

        return null;
    }

    /**
     * Obtener roles predeterminados
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
     * Obtener todos los roles
     */
    public static List<String> obtenerTodosLosRoles() {

        List<String> roles = new ArrayList<>
