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
            
            for (Document doc : collection.find()) {
                if (doc.containsKey("contraseña")) {
                    String contraseñaTextoPlano = doc.getString("contraseña");
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
                            
                            System.out.println("Usuario migrado: " + doc.getString("correo"));
                            contador++;
                            
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            System.err.println("Error al encriptar contraseña para usuario " + 
                                doc.getString("correo") + ": " + e.getMessage());
                        }
                    }
                }
            }
            
            System.out.println("Migración completada. " + contador + " usuarios actualizados.");
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos en migración: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Error: datos nulos en migración: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método main para ejecutar la migración
    public static void main(String[] args) {
        migrarContraseñas();
    }
}
