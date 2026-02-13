package modelo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.JOptionPane;

public class ConexionMongoDB {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    // Configuración de conexión
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "cafeteria_espe";
    
    public static void conectar() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);
            
            // Verificar conexión
            database.runCommand(new Document("ping", 1));
            System.out.println("Conexión a MongoDB establecida correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error de conexión a la base de datos: " + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static MongoDatabase getDatabase() {
        if (database == null) {
            conectar();
        }
        return database;
    }
    
    public static MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }
    
    public static void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión a MongoDB cerrada");
        }
    }
}