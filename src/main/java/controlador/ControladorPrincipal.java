package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.*;
import vista.*;

public class ControladorPrincipal {

    private static final Logger logger = Logger.getLogger(ControladorPrincipal.class.getName());
    private static String usuarioActual;
    private static String rolActual;

    static {
        ConexionMongoDB.conectar();
    }

    public static boolean validarLoginAutomatico(String correo, String contraseña) {
        try {
            if (correo.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(null,"Ingrese correo y contraseña.");
                return false;
            }

            if (ControladorUsuarios.verificarCredenciales(correo, contraseña)) {
                String rolBD = ControladorUsuarios.obtenerRolPorCorreo(correo);

                if (rolBD != null) {
                    Rol rol = ControladorRolesAvanzados.obtenerRol(rolBD);

                    if (rol == null || !rol.isActivo()) {
                        JOptionPane.showMessageDialog(null,
                                "Su rol está inactivo.",
                                "Rol inactivo",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    usuarioActual = correo;
                    rolActual = rolBD;
                    return true;
                }
            }

            JOptionPane.showMessageDialog(null,"Correo o contraseña incorrectos.");
            return false;

        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error en login automático",e);
            return false;
        }
    }
}

