package test.login;

import controlador.ControladorProductos;
import modelo.Producto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class TestHistoria3 {

    @Test
    void testCargarProductos_MenuNoVacio() {
        // DADO
        DefaultTableModel modelo = new DefaultTableModel();

        // CUANDO
        ControladorProductos.cargarProductos(modelo);
        List<Producto> productos = ControladorProductos.getProductos();

        // ENTONCES
        assertNotNull(productos);
        assertFalse(productos.isEmpty(), "El menú no debería estar vacío");
    }

    @Test
    void testProductoTieneNombreYPrecioActualizado() {
        // DADO
        ControladorProductos.cargarProductos(new DefaultTableModel());

        // CUANDO
        Producto producto = ControladorProductos.getProductos().get(0);

        // ENTONCES
        assertNotNull(producto, "El producto no debe ser null");
        assertNotNull(producto.getNombre(), "El nombre del producto no debe ser null");
        assertTrue(producto.getPrecio() > 0, "El precio debe ser mayor a 0");
    }

    @Test
    void testSinProductos_MenuVacio() {
        // DADO
        ControladorProductos.getProductos().clear();

        // CUANDO
        List<Producto> productos = ControladorProductos.getProductos();

        // ENTONCES
        assertTrue(productos.isEmpty(),
                "Debe mostrarse menú vacío cuando no hay productos");
    }
}
