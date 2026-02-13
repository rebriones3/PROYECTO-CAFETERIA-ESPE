package modelo;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.JOptionPane;

public class ConexionMongoDB {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    public static void conectar() {
        try {
            // Cargar configuración desde variables de entorno
            ConfiguracionSegura.cargarConfiguracion();
            
            // Obtener cadena de conexión de forma segura
            String connectionString = ConfiguracionSegura.getMongoDBConnectionString();
            String databaseName = ConfiguracionSegura.getMongoDBDatabase();
            
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            
            // Verificar conexión
            database.runCommand(new Document("ping", 1));
            System.out.println("Conexión a MongoDB establecida correctamente");
            
        } catch (MongoException e) {
            System.err.println("Error de MongoDB al conectar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error de conexión a la base de datos MongoDB: " + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: cadena de conexión inválida: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error: configuración de conexión inválida: " + e.getMessage(),
                "Error de Configuración",
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
