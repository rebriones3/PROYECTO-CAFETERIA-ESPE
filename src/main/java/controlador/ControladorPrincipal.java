package controlador;

import com.mongodb.client.*;
import org.bson.Document;
import javax.swing.JOptionPane;
import java.util.*;
import javax.swing.JFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.*;
import vista.*;

public class ControladorPrincipal {

    private static final Logger logger = Logger.getLogger(ControladorPrincipal.class.getName());

    private static List<Usuario> usuarios = new ArrayList<>();
    private static String usuarioActual;
    private static String rolActual;

    static {
        ConexionMongoDB.conectar();
        cargarUsuarios();
    }

    private static void cargarUsuarios() {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("usuarios");
            usuarios.clear();

            for (Document doc : collection.find()) {

                logger.info("Documento completo: " + doc.toJson());

                String rol = doc.getString("rol");
                String correo = doc.getString("correo");

                String contraseña = null;
                if (doc.containsKey("contraseña")) {
                    contraseña = doc.getString("contraseña");
                } else if (doc.containsKey("contrasena")) {
                    contraseña = doc.getString("contrasena");
                } else if (doc.containsKey("password")) {
                    contraseña = doc.getString("password");
                } else if (doc.containsKey("clave")) {
                    contraseña = doc.getString("clave");
                }

                logger.info("Rol: " + rol + ", Correo: " + correo + ", Contraseña: " + contraseña);

                if (contraseña != null) {
                    usuarios.add(new Usuario(rol, correo, contraseña));
                } else {
                    logger.severe("Error: Contraseña es null para usuario: " + correo);
                }
            }

            logger.info("Usuarios cargados: " + usuarios.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar usuarios desde MongoDB: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar usuarios desde MongoDB", e);
        }
    }

    public static void guardarPedido(String productos, double total, String codigoTransaccion) {
        try {
            MongoCollection<Document> collection = ConexionMongoDB.getCollection("pedidos");

            Document pedido = new Document()
                    .append("estudiante", usuarioActual)
                    .append("productos", productos)
                    .append("total", total)
                    .append("estado", "Pendiente")
                    .append("codigoTransaccion", codigoTransaccion)
                    .append("fecha", new java.util.Date());

            collection.insertOne(ped
