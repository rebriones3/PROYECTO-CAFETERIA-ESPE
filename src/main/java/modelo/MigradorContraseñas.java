package modelo;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;

public class MigradorContraseñas {
    
    public static void migrarContraseñas() {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            int contador = 0;
            int errores = 0;
            
            for (Document doc : collection.find()) {
                if (doc.containsKey("contraseña")) {
                    String contraseñaTextoPlano = doc.getString("contraseña");
                    String correo = doc.getString("correo");
                    
                    // Validar que el correo sea válido antes de procesar
                    if (correo == null || correo.trim().isEmpty()) {
                        System.err.println("Usuario sin correo válido, saltando...");
                        errores++;
                        continue;
                    }
                    
                    // Verificar si ya está encriptada (los hashes SHA-256 tienen longitud específica)
                    if (contraseñaTextoPlano != null && 
                        !contraseñaTextoPlano.isEmpty() && 
                        !contraseñaTextoPlano.startsWith("********") &&
                        contraseñaTextoPlano.length() < 50) { 
                        
                        try {
                            // Encriptar la contraseña
                            String contraseñaEncriptada = Encriptacion.encriptarContraseña(contraseñaTextoPlano);
                            
                            // Actualizar el documento
                            collection.updateOne(
                                eq("_id", doc.getObjectId("_id")),
                                new Document("$set", 
                                    new Document("contraseña", contraseñaEncriptada)
                                )
                            );
                            
                            // Sanitizar el correo para el log (sin mostrarlo completo por seguridad)
                            String correoOfuscado = ofuscarCorreo(correo);
                            System.out.println("Usuario migrado: " + correoOfuscado);
                            contador++;
                            
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            String correoOfuscado = ofuscarCorreo(correo);
                            System.err.println("Error al encriptar contraseña para usuario " + 
                                correoOfuscado + ": " + e.getMessage());
                            errores++;
                        }
                    }
                }
            }
            
            System.out.println("Migración completada.");
            System.out.println("Usuarios actualizados: " + contador);
            if (errores > 0) {
                System.out.println("Errores encontrados: " + errores);
            }
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos en migración: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error en migración: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ofusca un correo electrónico para logs (muestra solo primeros 3 caracteres)
     * Ejemplo: usuario@ejemplo.com -> usu***@***
     */
    private static String ofuscarCorreo(String correo) {
        if (correo == null || correo.length() < 5) {
            return "***";
        }
        
        int posArroba = correo.indexOf('@');
        if (posArroba > 0) {
            String inicio = correo.substring(0, Math.min(3, posArroba));
            return inicio + "***@***";
        }
        
        return correo.substring(0, 3) + "***";
    }
    
    // Método main para ejecutar la migración
    public static void main(String[] args) {
        System.out.println("=== INICIANDO MIGRACIÓN DE CONTRASEÑAS ===");
        System.out.println("Este proceso encriptará todas las contraseñas en texto plano.");
        System.out.println();
        
        migrarContraseñas();
        
        System.out.println();
        System.out.println("=== MIGRACIÓN FINALIZADA ===");
    }
}
