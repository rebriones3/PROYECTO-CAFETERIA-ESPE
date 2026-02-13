package modelo;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.*;

public class MigradorContraseñas {
    
    private static final Logger logger = Logger.getLogger(MigradorContraseñas.class.getName());
    
    public static void migrarContraseñas() {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            int contador = 0;
            int errores = 0;
            
            logger.log(Level.INFO, "Iniciando migración de contraseñas...");
            
            for (Document doc : collection.find()) {
                if (doc.containsKey("contraseña")) {
                    String contraseñaTextoPlano = doc.getString("contraseña");
                    String correo = doc.getString("correo");
                    
                    if (correo == null || correo.trim().isEmpty()) {
                        logger.log(Level.WARNING, "Usuario sin correo válido, saltando...");
                        errores++;
                        continue;
                    }
                    
                    // Verificar si ya está encriptada
                    if (contraseñaTextoPlano != null && 
                        !contraseñaTextoPlano.isEmpty() && 
                        !contraseñaTextoPlano.startsWith("********") &&
                        contraseñaTextoPlano.length() < 50) { 
                        
                        try {
                            String contraseñaEncriptada = Encriptacion.encriptarContraseña(contraseñaTextoPlano);
                            
                            collection.updateOne(
                                eq("_id", doc.getObjectId("_id")),
                                new Document("$set", 
                                    new Document("contraseña", contraseñaEncriptada)
                                )
                            );
                            
                            String correoOfuscado = ofuscarCorreo(correo);
                            logger.log(Level.INFO, "Usuario migrado: {0}", correoOfuscado);
                            contador++;
                            
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            String correoOfuscado = ofuscarCorreo(correo);
                            logger.log(Level.WARNING, "Error al encriptar contraseña para usuario {0}: {1}", 
                                new Object[]{correoOfuscado, e.getMessage()});
                            errores++;
                        }
                    }
                }
            }
            
            logger.log(Level.INFO, "Migración completada. Usuarios actualizados: {0}, Errores: {1}", 
                new Object[]{contador, errores});
            
            System.out.println("\n=== RESUMEN DE MIGRACIÓN ===");
            System.out.println("Usuarios actualizados: " + contador);
            if (errores > 0) {
                System.out.println("Errores encontrados: " + errores);
            }
            System.out.println("===========================\n");
            
        } catch (MongoException e) {
            logger.log(Level.SEVERE, "Error de MongoDB en migración", e);
            System.err.println("Error crítico de base de datos. Ver logs para detalles.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error general en migración", e);
            System.err.println("Error crítico en migración. Ver logs para detalles.");
        }
    }
    
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
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO MIGRACIÓN DE CONTRASEÑAS ===");
        System.out.println("Este proceso encriptará todas las contraseñas en texto plano.");
        System.out.println();
        
        migrarContraseñas();
        
        System.out.println("=== MIGRACIÓN FINALIZADA ===");
    }
}
