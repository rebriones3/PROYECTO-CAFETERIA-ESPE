package controlador;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.*;
import modelo.*;

public class ControladorProductos {

    private static final Logger logger = Logger.getLogger(ControladorProductos.class.getName());
    private static List<Producto> productos = new ArrayList<>();

    static {
        ConexionMongoDB.conectar();
    }

    public static void cargarProductos(DefaultTableModel modelo) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            productos.clear();
            modelo.setRowCount(0);

            for (Document doc : collection.find()) {
                String nombre = doc.getString("nombre");

                double precio;
                Object precioObj = doc.get("precio");
                if (precioObj instanceof Integer) {
                    precio = ((Integer) precioObj).doubleValue();
                } else if (precioObj instanceof Double) {
                    precio = (Double) precioObj;
                } else {
                    logger.warning("Tipo de precio no válido para: " + nombre);
                    continue;
                }

                String categoria = doc.getString("categoria");
                String disponible = doc.getString("disponible");

                Producto producto = new Producto(nombre, precio, categoria, "Mediano");
                producto.setDisponible(disponible);
                productos.add(producto);

                modelo.addRow(new Object[]{
                        nombre,
                        String.format("$%.2f", precio),
                        categoria,
                        disponible
                });
            }

            logger.info("Productos cargados: " + productos.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar productos desde MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar productos", e);
        }
    }

    public static boolean agregarProducto(String nombre, double precio, String categoria, String disponible) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");

            Document producto = new Document()
                    .append("nombre", nombre)
                    .append("precio", precio)
                    .append("categoria", categoria)
                    .append("disponible", disponible)
                    .append("tamaño", "Mediano");

            collection.insertOne(producto);

            Producto nuevoProducto = new Producto(nombre, precio, categoria, "Mediano");
            nuevoProducto.setDisponible(disponible);
            productos.add(nuevoProducto);

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al agregar producto en MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al agregar producto", e);
            return false;
        }
    }
}
