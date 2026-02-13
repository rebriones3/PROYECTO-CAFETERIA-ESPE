package modelo;

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
                    }
                }
            }
            System.out.println("Migración completada. " + contador + " usuarios actualizados.");
            
        } catch (Exception e) {
            System.err.println("Error en migración: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método main para ejecutar la migración
    public static void main(String[] args) {
        migrarContraseñas();
    }
}