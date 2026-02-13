package test.login;

import modelo.Encriptacion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestEncriptacion {

    @Test
    void testEncriptacionSHA256() {

        String contraseña = "1234";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotEquals(contraseña, hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }
}
