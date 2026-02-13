package controlador;

import com.mongodb.MongoException;
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
                String rol = SanitizadorEntradas.sanitizarTexto(doc.getString("rol"));
                String correo = doc.getString("correo");
                String contraseña = doc.getString("contraseña");

                if (contraseña != null && correo != null) {
                    // Sanitizar correo
                    try {
                        correo = SanitizadorEntradas.sanitizarCorreo(correo);
                    } catch (IllegalArgumentException e) {
                        logger.warning("Correo inválido en BD: " + correo);
                        continue;
                    }
                    
                    Usuario usuario = new Usuario(rol, correo, contraseña);
                    usuarios.add(usuario);
                    modelo.addRow(new Object[]{rol, correo, "********"});
                }
            }

            logger.info("Usuarios cargados: " + usuarios.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios.");
            logger.log(Level.SEVERE, "Error al cargar usuarios", e);
        }
    }
    
    /**
     * Verifica las credenciales de un usuario
     * @param correo Correo ya sanitizado
     * @param contraseña Contraseña en texto plano
     * @return true si las credenciales son correctas
     */
    public static boolean verificarCredenciales(String correo, String contraseña) {
        try {
            if (correo == null || contraseña == null) {
                return false;
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            
            // Buscar usuario por correo (ya sanitizado)
            Document usuario = collection.find(eq("correo", correo)).first();
            
            if (usuario == null) {
                return false;
            }
            
            String contraseñaAlmacenada = usuario.getString("contraseña");
            
            // Verificar contraseña encriptada
            return Encriptacion.verificarContraseña(contraseña, contraseñaAlmacenada);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al verificar credenciales", e);
            return false;
        }
    }
    
    /**
     * Obtiene el rol de un usuario por su correo
     * @param correo Correo ya sanitizado
     * @return El rol del usuario o null si no existe
     */
    public static String obtenerRolPorCorreo(String correo) {
        try {
            if (correo == null) {
                return null;
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            
            // Buscar usuario por correo (ya sanitizado)
            Document usuario = collection.find(eq("correo", correo)).first();
            
            if (usuario == null) {
                return null;
            }
            
            return SanitizadorEntradas.sanitizarTexto(usuario.getString("rol"));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener rol", e);
            return null;
        }
    }
}
