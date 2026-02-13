
package test.login;

import controlador.ControladorPrincipal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
public class TestHistoria1 {
      @Test
    public void testLoginExitoso() {
        // GIVEN: credenciales válidas
        String correo = "rebriones3@espe.edu.ec";
        String contraseña = "1234";

        // WHEN: intento iniciar sesión
        boolean resultado = ControladorPrincipal.validarLoginAutomatico(correo, contraseña);

        // THEN: el sistema permite el acceso
        assertTrue(resultado);
        assertNotNull(ControladorPrincipal.getUsuarioActual());
        assertNotNull(ControladorPrincipal.getRolActual());
    }

    @Test
    public void testLoginFallidoContrasenaIncorrecta() {
        // GIVEN: correo válido y contraseña incorrecta
        String correo = "rebriones3@espe.edu.ec";
        String contraseña = "incorrecta";

        // WHEN: intento iniciar sesión
        boolean resultado = ControladorPrincipal.validarLoginAutomatico(correo, contraseña);

        // THEN: el sistema rechaza el acceso
        assertFalse(resultado);
    }
}
