package modelo;

import com.mongodb.MongoException;
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

    public static void conectar() {
        try {
            ConfiguracionSegura.cargarConfiguracion();

            String connectionString = ConfiguracionSegura.getMongoDBConnectionString();
            String databaseName = ConfiguracionSegura.getMongoDBDatabase();

            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);

            database.runCommand(new Document("ping", 1));

            logger.info("Conexión a MongoDB establecida correctamente");

        } catch (MongoException e) {
            logger.log(Level.SEVERE,"Error Mongo al conectar",e);
            JOptionPane.showMessageDialog(null,
                    "Error de conexión a MongoDB: " + e.getMessage(),
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public static void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("Conexión a MongoDB cerrada");
        }
    }
}

