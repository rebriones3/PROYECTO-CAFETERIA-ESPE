package modelo;

import java.util.regex.Pattern;

/**
 * Clase para sanitizar y validar entradas de usuario
 * Previene inyecciones NoSQL y asegura integridad de datos
 */
public class SanitizadorEntradas {
    
    // Patrones de validación
    private static final Pattern PATRON_EMAIL = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PATRON_NOMBRE_PRODUCTO = Pattern.compile(
        "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s.-]{1,100}$"
    );
    
    private static final Pattern PATRON_CATEGORIA = Pattern.compile(
        "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]{1,50}$"
    );
    
    // Caracteres peligrosos para MongoDB
    private static final String[] CARACTERES_PELIGROSOS = {
        "$", "{", "}", "[", "]", "(", ")", "<", ">", 
        "\"", "'", "\\", "|", "&", ";", "`"
    };
    
    /**
     * Sanitiza un String removiendo caracteres potencialmente peligrosos
     */
    public static String sanitizarTexto(String input) {
        if (input == null) {
            return "";
        }
        
        // Eliminar espacios al inicio y final
        String sanitizado = input.trim();
        
        // Limitar longitud máxima
        if (sanitizado.length() > 500) {
            sanitizado = sanitizado.substring(0, 500);
        }
        
        // Remover caracteres peligrosos para NoSQL
        for (String caracter : CARACTERES_PELIGROSOS) {
            sanitizado = sanitizado.replace(caracter, "");
        }
        
        return sanitizado;
    }
    
    /**
     * Valida y sanitiza un correo electrónico
     */
    public static String sanitizarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo no puede estar vacío");
        }
        
        String correoLimpio = correo.trim().toLowerCase();
        
        if (!PATRON_EMAIL.matcher(correoLimpio).matches()) {
            throw new IllegalArgumentException("Formato de correo inválido");
        }
        
        if (correoLimpio.length() > 100) {
            throw new IllegalArgumentException("El correo es demasiado largo");
        }
        
        return correoLimpio;
    }
    
    /**
     * Valida y sanitiza el nombre de un producto
     */
    public static String sanitizarNombreProducto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        
        String nombreLimpio = nombre.trim();
        
        if (!PATRON_NOMBRE_PRODUCTO.matcher(nombreLimpio).matches()) {
            throw new IllegalArgumentException("El nombre del producto contiene caracteres inválidos");
        }
        
        if (nombreLimpio.length() < 2) {
            throw new IllegalArgumentException("El nombre del producto es demasiado corto");
        }
        
        return nombreLimpio;
    }
    
    /**
     * Valida y sanitiza una categoría
     */
    public static String sanitizarCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía");
        }
        
        String categoriaLimpia = categoria.trim();
        
        if (!PATRON_CATEGORIA.matcher(categoriaLimpia).matches()) {
            throw new IllegalArgumentException("La categoría contiene caracteres inválidos");
        }
        
        return categoriaLimpia;
    }
    
    /**
     * Valida un precio
     */
    public static double validarPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        
        if (precio > 999999.99) {
            throw new IllegalArgumentException("El precio es demasiado alto");
        }
        
        // Redondear a 2 decimales
        return Math.round(precio * 100.0) / 100.0;
    }
    
    /**
     * Valida una disponibilidad
     */
    public static String validarDisponibilidad(String disponible) {
        if (disponible == null || disponible.trim().isEmpty()) {
            return "Si"; // Valor por defecto
        }
        
        String disponibleLimpio = disponible.trim();
        
        if (!disponibleLimpio.equalsIgnoreCase("Si") && 
            !disponibleLimpio.equalsIgnoreCase("No")) {
            return "Si"; // Valor por defecto si es inválido
        }
        
        return disponibleLimpio;
    }
    
    /**
     * Escapa caracteres especiales para mensajes de usuario
     * (útil para prevenir confusión en mensajes de error)
     */
    public static String escaparParaMensaje(String mensaje) {
        if (mensaje == null) {
            return "";
        }
        
        return mensaje
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
}
