package test.login;

import modelo.Encriptacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class TestEncriptacion {

    @Test
    @DisplayName("Test encriptación básica SHA-256")
    void testEncriptacionSHA256() {
        String contraseña = "1234";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotEquals(contraseña, hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }

    @Test
    @DisplayName("Test que la misma contraseña genera el mismo hash")
    void testMismaContraseñaMismoHash() {
        String contraseña = "miPasswordSeguro123";
        String hash1 = Encriptacion.encriptarContraseña(contraseña);
        String hash2 = Encriptacion.encriptarContraseña(contraseña);

        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test que contraseñas diferentes generan hashes diferentes")
    void testDiferentesContraseñasDiferentesHashes() {
        String contraseña1 = "password123";
        String contraseña2 = "password124";
        
        String hash1 = Encriptacion.encriptarContraseña(contraseña1);
        String hash2 = Encriptacion.encriptarContraseña(contraseña2);

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test verificación con contraseña correcta")
    void testVerificarContraseñaCorrecta() {
        String contraseña = "admin2024";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }

    @Test
    @DisplayName("Test verificación con contraseña incorrecta")
    void testVerificarContraseñaIncorrecta() {
        String contraseñaOriginal = "admin2024";
        String contraseñaIncorrecta = "admin2025";
        String hash = Encriptacion.encriptarContraseña(contraseñaOriginal);

        assertFalse(Encriptacion.verificarContraseña(contraseñaIncorrecta, hash));
    }

    @Test
    @DisplayName("Test con contraseña vacía")
    void testContraseñaVacia() {
        String contraseñaVacia = "";
        String hash = Encriptacion.encriptarContraseña(contraseñaVacia);

        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertTrue(Encriptacion.verificarContraseña(contraseñaVacia, hash));
    }

    @Test
    @DisplayName("Test con contraseña con caracteres especiales")
    void testContraseñaConCaracteresEspeciales() {
        String contraseña = "P@ssw0rd!#$%&*()";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotNull(hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }

    @Test
    @DisplayName("Test con contraseña muy larga")
    void testContraseñaMuyLarga() {
        String contraseña = "a".repeat(1000);
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotNull(hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }

    @Test
    @DisplayName("Test con contraseña con espacios")
    void testContraseñaConEspacios() {
        String contraseña = "mi contraseña con espacios";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotNull(hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }

    @Test
    @DisplayName("Test con contraseña con acentos y ñ")
    void testContraseñaConAcentos() {
        String contraseña = "contraseñaÁÉÍÓÚ";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotNull(hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }

    @Test
    @DisplayName("Test que el hash tenga formato Base64 válido")
    void testHashFormatoBase64() {
        String contraseña = "testPassword";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotNull(hash);
        // Un hash SHA-256 en Base64 debería tener 44 caracteres
        assertEquals(44, hash.length());
        // Verificar que termine con '=' (padding de Base64)
        assertTrue(hash.matches("^[A-Za-z0-9+/]+=*$"));
    }

    @Test
    @DisplayName("Test sensibilidad a mayúsculas")
    void testSensibilidadMayusculas() {
        String contraseñaMinuscula = "password";
        String contraseñaMayuscula = "PASSWORD";
        
        String hash1 = Encriptacion.encriptarContraseña(contraseñaMinuscula);
        String hash2 = Encriptacion.encriptarContraseña(contraseñaMayuscula);

        assertNotEquals(hash1, hash2);
        assertFalse(Encriptacion.verificarContraseña(contraseñaMayuscula, hash1));
    }

    @Test
    @DisplayName("Test con hash incorrecto en verificación")
    void testVerificacionConHashInvalido() {
        String contraseña = "password123";
        String hashInvalido = "hashInventado123";

        assertFalse(Encriptacion.verificarContraseña(contraseña, hashInvalido));
    }

    @Test
    @DisplayName("Test con contraseña numérica")
    void testContraseñaNumerica() {
        String contraseña = "123456789";
        String hash = Encriptacion.encriptarContraseña(contraseña);

        assertNotNull(hash);
        assertTrue(Encriptacion.verificarContraseña(contraseña, hash));
    }
}
