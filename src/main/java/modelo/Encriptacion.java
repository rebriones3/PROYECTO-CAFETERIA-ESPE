package modelo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encriptacion {

    public static String encriptarContraseña(String contraseña) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(contraseña.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    
    public static boolean verificarContraseña(String contraseña, String hashAlmacenado) {
        String hashVerificado = encriptarContraseña(contraseña);
        return hashVerificado.equals(hashAlmacenado);
    }
}