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
            if (correo == null || correo.trim().isEmpty() || 
                contraseña == null || contraseña.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese correo y contraseña.");
                return false;
            }

            String correoSanitizado;
            try {
                correoSanitizado = SanitizadorEntradas.sanitizarCorreo(correo);
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Intento de login con correo inválido: {0}", e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Formato de correo inválido.", 
                    "Error de validación", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (ControladorUsuarios.verificarCredenciales(correoSanitizado, contraseña)) {
                String rolBD = ControladorUsuarios.obtenerRolPorCorreo(correoSanitizado);

                if (rolBD != null) {
                    Rol rol = ControladorRolesAvanzados.obtenerRol(rolBD);

                    if (rol == null || !rol.isActivo()) {
                        logger.log(Level.WARNING, "Intento de login con rol inactivo: {0}", rolBD);
                        JOptionPane.showMessageDialog(null,
                                "Su rol está inactivo.",
                                "Rol inactivo",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    usuarioActual = correoSanitizado;
                    rolActual = rolBD;
                    logger.log(Level.INFO, "Login exitoso para usuario con rol: {0}", rolBD);
                    return true;
                }
            }

            logger.log(Level.WARNING, "Intento de login fallido");
            JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
            return false;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en login automático", e);
            JOptionPane.showMessageDialog(null, 
                "Error del sistema. Intente nuevamente.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public static String getUsuarioActual() {
        return usuarioActual;
    }
    
    public static String getRolActual() {
        return rolActual;
    }
    
    public static void cerrarSesion() {
        logger.log(Level.INFO, "Sesión cerrada para usuario: {0}", usuarioActual);
        usuarioActual = null;
        rolActual = null;
    }
}
