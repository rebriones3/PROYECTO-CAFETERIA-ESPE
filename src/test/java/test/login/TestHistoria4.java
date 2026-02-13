package test.login;

import modelo.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHistoria4 {

    @Test
    void testCrearPedido() {
        Pedido pedido = new Pedido(
                "Estefany",
                "Café",
                3.00,
                "Pendiente",
                "TX001"
        );

        assertEquals("Estefany", pedido.getEstudiante());
        assertEquals("Café", pedido.getProductos());
        assertEquals(3.00, pedido.getTotal(), 0.001);
        assertEquals("Pendiente", pedido.getEstado());
        assertEquals("TX001", pedido.getCodigoTransaccion());
    }

    @Test
    void testModificarEstadoPedido() {
        Pedido pedido = new Pedido(
                "Juan",
                "Sandwich",
                3.50,
                "Pendiente",
                "TX002"
        );

        pedido.setEstado("Pagado");

        assertEquals("Pagado", pedido.getEstado());
    }

    @Test
    void testSubtotalProducto() {
        Producto producto = new Producto(
                "Jugo",
                2.00,
                "Bebida",
                "Mediano"
        );

        producto.setCantidad(3);

        assertEquals(3, producto.getCantidad());
        assertEquals(6.00, producto.getSubtotal(), 0.001);
    }
}
