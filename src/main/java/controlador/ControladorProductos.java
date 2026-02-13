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
                String nombre = SanitizadorEntradas.sanitizarTexto(doc.getString("nombre"));

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

            System.out.println("Productos cargados: " + productos.size());

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al cargar productos.");
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al cargar productos.");
            e.printStackTrace();
        }
    }
    
    public static boolean agregarProducto(String nombre, double precio, String categoria, String disponible) {
        try {
            // Validar y sanitizar todas las entradas
            String nombreSanitizado = SanitizadorEntradas.sanitizarNombreProducto(nombre);
            String categoriaSanitizada = SanitizadorEntradas.sanitizarCategoria(categoria);
            double precioValidado = SanitizadorEntradas.validarPrecio(precio);
            String disponibleValidado = SanitizadorEntradas.validarDisponibilidad(disponible);
            
            // Verificar si el producto ya existe
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
            
            // Actualizar lista local
            Producto nuevoProducto = new Producto(nombreSanitizado, precioValidado, categoriaSanitizada, "Mediano");
            nuevoProducto.setDisponible(disponibleValidado);
            productos.add(nuevoProducto);
            
            return true;
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de validación: " + e.getMessage(),
                "Error de validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al agregar producto.");
            return false;
        } catch (Exception e) {
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
            // Validar y sanitizar todas las entradas
            String nombreSanitizado = SanitizadorEntradas.sanitizarNombreProducto(nombre);
            String categoriaSanitizada = SanitizadorEntradas.sanitizarCategoria(categoria);
            double precioValidado = SanitizadorEntradas.validarPrecio(precio);
            String disponibleValidado = SanitizadorEntradas.validarDisponibilidad(disponible);
            
            Producto productoViejo = productos.get(indice);
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("productos");
            
            // Actualizar en MongoDB
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

            // Actualizar lista local
            Producto producto = productos.get(indice);
            producto.setNombre(nombreSanitizado);
            producto.setPrecio(precioValidado);
            producto.setCategoria(categoriaSanitizada);
            producto.setDisponible(disponibleValidado);

            return true;

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de validación: " + e.getMessage(),
                "Error de validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al editar producto.");
            return false;
        } catch (Exception e) {
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
            
            // Eliminar de MongoDB
            collection.deleteOne(eq("nombre", nombreProducto));
            
            // Eliminar de lista local
            productos.remove(indice);
            
            return true;

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de base de datos al eliminar producto.");
            return false;
        } catch (Exception e) {
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
