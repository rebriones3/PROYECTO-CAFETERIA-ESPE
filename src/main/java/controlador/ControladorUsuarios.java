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

    public static void cargarUsuarios(DefaultTableModel modelo) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            usuarios.clear();
            modelo.setRowCount(0);

            for (Document doc : collection.find()) {
                String rol = doc.getString("rol");
                String correo = doc.getString("correo");

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

                    modelo.addRow(new Object[]{
                        rol,
                        correo,
                        "********"
                    });
                }
            }

            logger.info("Usuarios cargados: " + usuarios.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar usuarios desde MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar usuarios", e);
        }
    }

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
            logger.log(Level.SEVERE, "Error al verificar credenciales", e);
            return false;
        }
    }

    public static String obtenerRolPorCorreo(String correo) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            Document usuarioDoc = collection.find(eq("correo", correo)).first();

            if (usuarioDoc != null) {
                return usuarioDoc.getString("rol");
            }
            return null;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener rol", e);
            return null;
        }
    }
}
