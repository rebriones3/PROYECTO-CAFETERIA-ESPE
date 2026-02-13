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
                    try {
                        correo = SanitizadorEntradas.sanitizarCorreo(correo);
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "Correo inválido en BD, saltando usuario");
                        continue;
                    }
                    
                    Usuario usuario = new Usuario(rol, correo, contraseña);
                    usuarios.add(usuario);
                    modelo.addRow(new Object[]{rol, correo, "********"});
                }
            }

            logger.log(Level.INFO, "Usuarios cargados exitosamente: {0}", usuarios.size());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar usuarios", e);
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios.");
        }
    }
    
    public static boolean verificarCredenciales(String correo, String contraseña) {
        try {
            if (correo == null || contraseña == null) {
                return false;
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            Document usuario = collection.find(eq("correo", correo)).first();
            
            if (usuario == null) {
                return false;
            }
            
            String contraseñaAlmacenada = usuario.getString("contraseña");
            return Encriptacion.verificarContraseña(contraseña, contraseñaAlmacenada);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al verificar credenciales", e);
            return false;
        }
    }
    
    public static String obtenerRolPorCorreo(String correo) {
        try {
            if (correo == null) {
                return null;
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
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
