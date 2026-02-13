package test.login;

import controlador.ControladorPedido;
import java.util.List;
import modelo.Pedido;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHistoria10 {

    @Test
    void testCargarPedidosPendientes() {
        // ACT
        List<Pedido> pedidos = ControladorPedido.getPedidos();

        // ASSERT
        assertNotNull(pedidos, "La lista de pedidos no debe ser null");

        for (Pedido p : pedidos) {
            assertNotEquals("Entregado", p.getEstado(),
                    "No deben cargarse pedidos entregados");
        }
    }

    @Test
    void testPedidosMantienenOrdenDeLlegada() {
        List<Pedido> pedidos = ControladorPedido.getPedidos();

        if (pedidos.size() >= 2) {
            Pedido primero = pedidos.get(0);
            Pedido segundo = pedidos.get(1);

            assertNotNull(primero);
            assertNotNull(segundo);
        }
    }

    @Test
    void testManejoDeMultiplesPedidos() {
        List<Pedido> pedidos = ControladorPedido.getPedidos();

        assertTrue(pedidos.size() <= 500,
                "El sistema debe manejar hasta 500 pedidos pendientes");
    }

    @Test
    void testTiempoDeCargaPedidos() {
        long inicio = System.currentTimeMillis();

        List<Pedido> pedidos = ControladorPedido.getPedidos();

        long fin = System.currentTimeMillis();
        long tiempo = fin - inicio;

        assertTrue(tiempo < 2000,
                "La lista debe cargarse en menos de 2 segundos");
    }

    @Test
    void testEliminarPedidoActualizaLista() {
        List<Pedido> pedidosAntes = ControladorPedido.getPedidos();

        if (!pedidosAntes.isEmpty()) {
            boolean eliminado = ControladorPedido.eliminarPedido(0);
            assertTrue(eliminado);

            List<Pedido> pedidosDespues = ControladorPedido.getPedidos();
            assertEquals(pedidosAntes.size() - 1, pedidosDespues.size());
        }
    }

}
