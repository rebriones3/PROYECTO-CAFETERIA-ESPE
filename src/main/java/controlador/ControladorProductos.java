package controlador;

import com.mongodb.MongoException;
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
                String nombre = SanitizadorEntradas.sanitizarTexto(doc.getString("nombre"));

                double precio;
                Object precioObj = doc.get("precio");
                if (precioObj instanceof Integer) {
                    precio = ((Integer) precioObj).doubleValue();
                } else if (precioObj instanceof Double) {
                    precio = (Double) precioObj;
                } else {
                    logger.log(Level.WARNING, "Tipo de precio no válido para producto: {0}", nombre);
                    continue; 
                }
                
                String categoria = SanitizadorEntradas.sanitizarTexto(doc.getString("categoria"));
                String disponible = SanitizadorEntradas.validarDisponibilidad(doc.getString("disponible"));

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

            logger.log(Level.INFO, "Productos cargados exitosamente: {0}", productos.size());

        } catch (MongoException e) {
            logger.log(Level.SEVERE, "Error de MongoDB al cargar productos", e);
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al cargar productos.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error general al cargar productos", e);
            JOptionPane.showMessageDialog(null, 
                "Error al cargar productos.");
        }
    }
    
    public static boolean agregarProducto(String nombre, double precio, String categoria, String disponible) {
        try {
            String nombreSanitizado = SanitizadorEntradas.sanitizarNombreProducto(nombre);
            String categoriaSanitizada = SanitizadorEntradas.sanitizarCategoria(categoria);
            double precioValidado = SanitizadorEntradas.validarPrecio(precio);
            String disponibleValidado = SanitizadorEntradas.validarDisponibilidad(disponible);
            
            for (Producto p : productos) {
                if (p.getNombre().equalsIgnoreCase(nombreSanitizado)) {
                    JOptionPane.showMessageDialog(null, "Este producto ya existe.");
                    return false;
                }
            }
            
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            
            Document producto = new Document()
                .append("nombre", nombreSanitizado)
                .append("precio", precioValidado)
                .append("categoria", categoriaSanitizada)
                .append("disponible", disponibleValidado)
                .append("tamaño", "Mediano");
            
            collection.insertOne(producto);
            
            Producto nuevoProducto = new Producto(nombreSanitizado, precioValidado, categoriaSanitizada, "Mediano");
            nuevoProducto.setDisponible(disponibleValidado);
            productos.add(nuevoProducto);
            
            logger.log(Level.INFO, "Producto agregado: {0}", nombreSanitizado);
            return true;
            
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validación fallida al agregar producto: {0}", e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error de validación: " + e.getMessage(),
                "Error de validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (MongoException e) {
            logger.log(Level.SEVERE, "Error de MongoDB al agregar producto", e);
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al agregar producto.");
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error general al agregar producto", e);
            JOptionPane.showMessageDialog(null, 
                "Error al agregar producto.");
            return false;
        }
    }
    
    public static boolean editarProducto(int indice, String nombre, double precio, String categoria, String disponible) {
        if (indice < 0 || indice >= productos.size()) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto válido.");
            return false;
        }

        try {
            String nombreSanitizado = SanitizadorEntradas.sanitizarNombreProducto(nombre);
            String categoriaSanitizada = SanitizadorEntradas.sanitizarCategoria(categoria);
            double precioValidado = SanitizadorEntradas.validarPrecio(precio);
            String disponibleValidado = SanitizadorEntradas.validarDisponibilidad(disponible);
            
            Producto productoViejo = productos.get(indice);
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            
            collection.updateOne(
                eq("nombre", productoViejo.getNombre()),
                new Document("$set", 
                    new Document()
                        .append("nombre", nombreSanitizado)
                        .append("precio", precioValidado)
                        .append("categoria", categoriaSanitizada)
                        .append("disponible", disponibleValidado)
                )
            );

            Producto producto = productos.get(indice);
            producto.setNombre(nombreSanitizado);
            producto.setPrecio(precioValidado);
            producto.setCategoria(categoriaSanitizada);
            producto.setDisponible(disponibleValidado);

            logger.log(Level.INFO, "Producto editado: {0}", nombreSanitizado);
            return true;

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validación fallida al editar producto: {0}", e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error de validación: " + e.getMessage(),
                "Error de validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (MongoException e) {
            logger.log(Level.SEVERE, "Error de MongoDB al editar producto", e);
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al editar producto.");
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error general al editar producto", e);
            JOptionPane.showMessageDialog(null, 
                "Error al editar producto.");
            return false;
        }
    }

    public static boolean eliminarProducto(int indice) {
        if (indice < 0 || indice >= productos.size()) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto de la tabla.");
            return false;
        }

        try {
            String nombreProducto = productos.get(indice).getNombre();
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            
            collection.deleteOne(eq("nombre", nombreProducto));
            productos.remove(indice);
            
            logger.log(Level.INFO, "Producto eliminado: {0}", nombreProducto);
            return true;

        } catch (MongoException e) {
            logger.log(Level.SEVERE, "Error de MongoDB al eliminar producto", e);
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al eliminar producto.");
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error general al eliminar producto", e);
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar producto.");
            return false;
        }
    }
    
    public static Producto getProducto(int indice) {
        if (indice >= 0 && indice < productos.size()) {
            return productos.get(indice);
        }
        return null;
    }
    
    public static Producto getProductoPorNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        
        String nombreSanitizado = SanitizadorEntradas.sanitizarTexto(nombre);
        
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombreSanitizado)) {
                return p;
            }
        }
        return null;
    }
    
    public static List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }
}
