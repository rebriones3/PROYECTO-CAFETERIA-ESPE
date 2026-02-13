package test.login;

import controlador.ControladorPedido;
import modelo.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestHistoria6 {

    @BeforeEach
    void setUp() {
        // Limpiar pedidos antes de cada test
        List<Pedido> pedidos = ControladorPedido.getPedidos();
        pedidos.clear();
    }

    // 🔴 Escenario: Pago inválido (pedido inexistente)
    @Test
    void testPagoInvalidoPedidoNoExiste() {
        boolean resultado =
                ControladorPedido.cambiarEstadoPedido(5, "Pagado");

        assertFalse(resultado);
    }
}
