/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import java.util.Date;
import modelo.ConexionMongoDB;
import org.bson.Document;

/**
 *
 * @author PERSONAL
 */
public class ControladorCalificaciones {
    /**
     * Guardar calificación del pedido
     */
    public static boolean guardarCalificacion(String codigoTransaccion, String estudiante, 
                                             int calificacion, String comentario) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("calificaciones");
            
            Document calificacionDoc = new Document()
                .append("codigoTransaccion", codigoTransaccion)
                .append("estudiante", estudiante)
                .append("calificacion", calificacion)
                .append("comentario", comentario)
                .append("fecha", new Date());
            
            collection.insertOne(calificacionDoc);
            
            System.out.println("✅ Calificación guardada: " + calificacion + " estrellas");
            return true;
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al guardar calificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Datos inválidos al guardar calificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtener promedio de calificaciones
     */
    public static double obtenerPromedioCalificaciones() {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("calificaciones");
            
            double sumaCalificaciones = 0;
            int totalCalificaciones = 0;
            
            for (Document doc : collection.find()) {
                sumaCalificaciones += doc.getInteger("calificacion");
                totalCalificaciones++;
            }
            
            if (totalCalificaciones == 0) {
                return 0;
            }
            
            return sumaCalificaciones / totalCalificaciones;
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al obtener promedio: " + e.getMessage());
            return 0;
        } catch (NullPointerException e) {
            System.err.println("Datos nulos al obtener promedio: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Contar calificaciones por estrellas
     */
    public static int[] contarCalificacionesPorEstrellas() {
        int[] conteo = new int[5]; // Índices 0-4 para 1-5 estrellas
        
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("calificaciones");
            
            for (Document doc : collection.find()) {
                int calificacion = doc.getInteger("calificacion");
                if (calificacion >= 1 && calificacion <= 5) {
                    conteo[calificacion - 1]++;
                }
            }
            
        } catch (MongoException e) {
            System.err.println("Error de base de datos al contar calificaciones: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Datos nulos al contar calificaciones: " + e.getMessage());
        }
        
        return conteo;
    }
}
