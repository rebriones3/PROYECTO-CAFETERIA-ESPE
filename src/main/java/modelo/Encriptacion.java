package modelo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encriptacion {

    public static String encriptarContraseña(String contraseña) {
        if (contraseña == null || contraseña.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(contraseña.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: algoritmo SHA-256 no disponible: " + e.getMessage());
            throw new IllegalStateException("Error al encriptar contraseña: algoritmo no disponible", e);
        }
    }
    
    public static boolean verificarContraseña(String contraseña, String hashAlmacenado) {
        if (contraseña == null || hashAlmacenado == null) {
            return false;
        }
        
        try {
            String hashVerificado = encriptarContraseña(contraseña);
            return hashVerificado.equals(hashAlmacenado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
}
