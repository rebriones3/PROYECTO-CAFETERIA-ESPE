package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;
import modelo.*;

public class ControladorProductos {
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
                    System.err.println("Tipo de precio no válido para: " + nombre);
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

            System.out.println("Productos cargados: " + productos.size());

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al cargar productos: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: datos nulos al cargar productos: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: tipo de dato incorrecto en productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static boolean agregarProducto(String nombre, double precio, String categoria, String disponible) {
        if (nombre.isEmpty() || precio <= 0 || categoria.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete todos los campos correctamente.");
            return false;
        }
        
        // Verificar si el producto ya existe
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(null, "Este producto ya existe.");
                return false;
            }
        }
        
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            
            Document producto = new Document()
                .append("nombre", nombre)
                .append("precio", precio)
                .append("categoria", categoria)
                .append("disponible", disponible)
                .append("tamaño", "Mediano");
            
            collection.insertOne(producto);
            
            // Actualizar lista local
            Producto nuevoProducto = new Producto(nombre, precio, categoria, "Mediano");
            nuevoProducto.setDisponible(disponible);
            productos.add(nuevoProducto);
            
            return true;
            
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al agregar producto: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: datos inválidos al agregar producto: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean editarProducto(int indice, String nombre, double precio, String categoria, String disponible) {
        if (indice < 0 || indice >= productos.size()) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto válido.");
            return false;
        }

        if (nombre.isEmpty() || precio <= 0 || categoria.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete todos los campos correctamente.");
            return false;
        }

        try {
            Producto productoViejo = productos.get(indice);
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            
            // Actualizar en MongoDB
            collection.updateOne(
                eq("nombre", productoViejo.getNombre()),
                new Document("$set", 
                    new Document()
                        .append("nombre", nombre)
                        .append("precio", precio)
                        .append("categoria", categoria)
                        .append("disponible", disponible)
                )
            );

            // Actualizar lista local
            Producto producto = productos.get(indice);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setDisponible(disponible);

            return true;

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al editar producto: " + e.getMessage());
            return false;
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: índice de producto inválido: " + e.getMessage());
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
            
            // Eliminar de MongoDB
            collection.deleteOne(eq("nombre", nombreProducto));
            
            // Eliminar de lista local
            productos.remove(indice);
            
            return true;

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al eliminar producto: " + e.getMessage());
            return false;
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, 
                "Error: índice de producto inválido: " + e.getMessage());
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
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }
    
    public static List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }
}
