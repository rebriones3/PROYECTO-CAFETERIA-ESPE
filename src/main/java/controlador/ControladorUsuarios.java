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
                String rol = doc.getString("rol");
                String correo = doc.getString("correo");
                String contraseña = doc.getString("contraseña");

                if (contraseña != null) {
                    Usuario usuario = new Usuario(rol, correo, contraseña);
                    usuarios.add(usuario);
                    modelo.addRow(new Object[]{rol, correo, "********"});
                }
            }

            logger.info("Usuarios cargados: " + usuarios.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error al cargar usuarios: " + e.getMessage());
            logger.log(Level.SEVERE,"Error al cargar usuarios",e);
        }
    }
}

