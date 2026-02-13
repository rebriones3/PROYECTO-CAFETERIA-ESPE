package test.login;

import modelo.Pedido;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHistoria5 {

    @Test
    void testMostrarTotalCorrecto() {
        // DADO
        String productos = "Café x2, Sandwich x1";
        double totalCalculado = 6.00;
        String codigo = "TX123";

        // CUANDO
        Pedido pedido = new Pedido(
                "estudiante@espe.edu.ec",
                productos,
                totalCalculado,
                "Pendiente",
                codigo
        );

        // ENTONCES
        assertEquals(6.00, pedido.getTotal(), 0.001);
    }

    @Test
    void testActualizarTotalDelPedido() {
        // DADO
        Pedido pedido = new Pedido(
                "estudiante@espe.edu.ec",
                "Jugo x1",
                2.00,
                "Pendiente",
                "TX456"
        );

        // CUANDO
        pedido.setTotal(6.00); // cambio de cantidad reflejado en el total

        // ENTONCES
        assertEquals(6.00, pedido.getTotal(), 0.001);
    }

    @Test
    void testTotalCeroSinProductos() {
        // DADO
        Pedido pedido = new Pedido(
                "estudiante@espe.edu.ec",
                "",
                0.00,
                "Pendiente",
                "TX789"
        );

        // CUANDO
        double total = pedido.getTotal();

        // ENTONCES
        assertEquals(0.00, total, 0.001);
    }
}
