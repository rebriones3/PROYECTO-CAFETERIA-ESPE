package controlador;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;
import modelo.*;

public class ControladorPedido {

    private static List<Pedido> pedidos = new ArrayList<>();

    static {
        ConexionMongoDB.conectar();
    }

    public static void cargarPedidos(DefaultTableModel modelo) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("pedidos");
            pedidos.clear();
            modelo.setRowCount(0);

            // Solo cargar pedidos NO entregados
            for (Document doc : collection.find(ne("estado", "Entregado"))) {
                String estudiante = doc.getString("estudiante");
                String productos = doc.getString("productos");
                double total = doc.getDouble("total");
                String estado = doc.getString("estado");
                String codigoTransaccion = doc.getString("codigoTransaccion");

                Pedido pedido = new Pedido(estudiante, productos, total, estado, codigoTransaccion);
                pedidos.add(pedido);
                modelo.addRow(new Object[]{estudiante, productos, total, estado});
            }
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, "Error de base de datos al cargar pedidos: " + e.getMessage());
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Error: datos nulos al cargar pedidos: " + e.getMessage());
        }
    }

    public static boolean eliminarPedido(int filaSeleccionada) {
        if (filaSeleccionada < 0 || filaSeleccionada >= pedidos.size()) {
            JOptionPane.showMessageDialog(null, "Seleccione un pedido de la tabla.");
            return false;
        }

        try {
            Pedido pedido = pedidos.get(filaSeleccionada);
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("pedidos");
            collection.updateOne(
                    and(
                            eq("estudiante", pedido.getEstudiante()),
                            eq("productos", pedido.getProductos()),
                            eq("codigoTransaccion", pedido.getCodigoTransaccion())
                    ),
                    new Document("$set", new Document("estado", "Entregado")
                            .append("fechaEntrega", new java.util.Date()))
            );
            pedidos.remove(filaSeleccionada);
            return true;

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null, "Error de base de datos al actualizar pedido: " + e.getMessage());
            return false;
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Error: índice de pedido inválido: " + e.getMessage());
            return false;
        }
    }

    public static List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public static Pedido getPedido(int indice) {
        if (indice >= 0 && indice < pedidos.size()) {
            return pedidos.get(indice);
        }
        return null;
    }

    public static boolean cambiarEstadoPedido(int filaSeleccionada, String nuevoEstado) {
        if (filaSeleccionada < 0 || filaSeleccionada >= pedidos.size()) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido de la tabla.");
            return false;
        }

        try {
            Pedido pedido = pedidos.get(filaSeleccionada);
            pedido.setEstado(nuevoEstado);

            MongoCollection<Document> collection = ConexionMongoDB.getCollection("pedidos");

            collection.updateOne(
                    and(
                            eq("estudiante", pedido.getEstudiante()),
                            eq("productos", pedido.getProductos()),
                            eq("codigoTransaccion", pedido.getCodigoTransaccion())
                    ),
                    new Document("$set", new Document("estado", nuevoEstado))
            );

            // ⭐ NUEVA FUNCIONALIDAD: Si el estado cambia a "Listo", notificar al estudiante
            if ("Listo".equalsIgnoreCase(nuevoEstado)) {
                notificarEstudiantePedidoListo(pedido.getEstudiante(), pedido.getProductos());
                System.out.println("Notificación enviada a " + pedido.getEstudiante());
            }

            return true;

        } catch (MongoException e) {
            JOptionPane.showMessageDialog(null,
                    "Error de base de datos al actualizar estado: " + e.getMessage());
            return false;
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: índice de pedido inválido: " + e.getMessage());
            return false;
        }
    }

    // Agregar este método nuevo
    public static void notificarEstudiantePedidoListo(String estudiante, String productos) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("notificaciones");

            Document notificacion = new Document()
                    .append("estudiante", estudiante)
                    .append("mensaje", "¡Tu pedido está listo para recoger!")
                    .append("productos", productos)
                    .append("fecha", new java.util.Date())
                    .append("leida", false)
                    .append("tipo", "pedido_listo");

            collection.insertOne(notificacion);

            System.out.println("✅ Notificación creada para: " + estudiante);

        } catch (MongoException e) {
            System.err.println("Error de base de datos al crear notificación: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Datos inválidos al crear notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
