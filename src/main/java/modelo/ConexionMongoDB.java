package modelo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionMongoDB {

    private static final Logger logger = Logger.getLogger(ConexionMongoDB.class.getName());

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "cafeteria_espe";

    public static void conectar() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);

            database.runCommand(new Document("ping", 1));
            logger.info("Conexión a MongoDB establecida correctamente");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al conectar con MongoDB", e);
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
            logger.info("Conexión a MongoDB cerrada");
        }
    }
}
