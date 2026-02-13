package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import modelo.ConexionMongoDB;
import java.util.*;
import static com.mongodb.client.model.Filters.*;

public class ControladorNotificaciones {
    
    public static int contarNotificacionesNoLeidas(String estudiante) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("notificaciones");
            
            long count = collection.countDocuments(
                and(
                    eq("estudiante", estudiante),
                    eq("leida", false)
                )
            );
            
            return (int) count;
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al contar notificaciones: " + e.getMessage());
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Argumento inválido al contar notificaciones: " + e.getMessage());
            return 0;
        }
    }
    
    public static List<Document> obtenerNotificacionesNoLeidas(String estudiante) {
        List<Document> notificaciones = new ArrayList<>();
        
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("notificaciones");
            
            for (Document doc : collection.find(
                and(
                    eq("estudiante", estudiante),
                    eq("leida", false)
                )
            ).sort(new Document("fecha", -1))) {
                notificaciones.add(doc);
            }
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al obtener notificaciones: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Argumento inválido al obtener notificaciones: " + e.getMessage());
        }
        
        return notificaciones;
    }
    
    public static void marcarComoLeida(Document notificacion) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("notificaciones");
            
            collection.updateOne(
                eq("_id", notificacion.getObjectId("_id")),
                new Document("$set", new Document("leida", true))
            );
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al marcar notificación como leída: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Notificación nula al marcar como leída: " + e.getMessage());
        }
    }
    
    public static void marcarTodasComoLeidas(String estudiante) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("notificaciones");
            
            collection.updateMany(
                and(
                    eq("estudiante", estudiante),
                    eq("leida", false)
                ),
                new Document("$set", new Document("leida", true))
            );
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al marcar todas como leídas: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Argumento inválido al marcar todas como leídas: " + e.getMessage());
        }
    }
}
