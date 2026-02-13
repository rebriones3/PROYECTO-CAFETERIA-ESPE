
package test.login;

import controlador.ControladorPrincipal;
import controlador.ControladorUsuarios;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class TestHistorias9 {

    @Test
    void testLoginPersonalExitoso() {

        try (MockedStatic<ControladorUsuarios> mocked
                = Mockito.mockStatic(ControladorUsuarios.class)) {

            // Simular credenciales válidas
            mocked.when(()
                    -> ControladorUsuarios.verificarCredenciales(
                            "personal@espe.edu.ec", "1234"))
                    .thenReturn(true);

            mocked.when(()
                    -> ControladorUsuarios.obtenerRolPorCorreo(
                            "personal@espe.edu.ec"))
                    .thenReturn("Personal");

            boolean resultado = ControladorPrincipal.validarLogin(
                    "Personal",
                    "personal@espe.edu.ec",
                    "1234"
            );

            assertTrue(resultado);
            assertEquals("Personal", ControladorPrincipal.getRolActual());
            assertEquals("personal@espe.edu.ec", ControladorPrincipal.getUsuarioActual());
        }
    }

    @Test
    void testLoginCredencialesIncorrectas() {

        try (MockedStatic<ControladorUsuarios> mocked
                = Mockito.mockStatic(ControladorUsuarios.class)) {

            mocked.when(()
                    -> ControladorUsuarios.verificarCredenciales(
                            "personal@espe.edu.ec", "mal"))
                    .thenReturn(false);

            boolean resultado = ControladorPrincipal.validarLogin(
                    "Personal",
                    "personal@espe.edu.ec",
                    "mal"
            );

            assertFalse(resultado);
        }
    }

    @Test
    void testLoginRolIncorrecto() {

        try (MockedStatic<ControladorUsuarios> mocked
                = Mockito.mockStatic(ControladorUsuarios.class)) {

            mocked.when(()
                    -> ControladorUsuarios.verificarCredenciales(
                            "usuario@espe.edu.ec", "1234"))
                    .thenReturn(true);

            mocked.when(()
                    -> ControladorUsuarios.obtenerRolPorCorreo(
                            "usuario@espe.edu.ec"))
                    .thenReturn("Estudiante");

            boolean resultado = ControladorPrincipal.validarLogin(
                    "Personal",
                    "usuario@espe.edu.ec",
                    "1234"
            );

            assertFalse(resultado);
        }
    }

    @Test
    void testLoginCamposVacios() {

        boolean resultado = ControladorPrincipal.validarLogin(
                "Personal",
                "",
                ""
        );

        assertFalse(resultado);
    }

    @Test
    void testTiempoAutenticacion() {

        long inicio = System.currentTimeMillis();

        try (MockedStatic<ControladorUsuarios> mocked
                = Mockito.mockStatic(ControladorUsuarios.class)) {

            mocked.when(()
                    -> ControladorUsuarios.verificarCredenciales(
                            "personal@espe.edu.ec", "1234"))
                    .thenReturn(true);

            mocked.when(()
                    -> ControladorUsuarios.obtenerRolPorCorreo(
                            "personal@espe.edu.ec"))
                    .thenReturn("Personal");

            ControladorPrincipal.validarLogin(
                    "Personal",
                    "personal@espe.edu.ec",
                    "1234"
            );
        }

        long fin = System.currentTimeMillis();
        assertTrue((fin - inicio) < 3000);
    }

}
