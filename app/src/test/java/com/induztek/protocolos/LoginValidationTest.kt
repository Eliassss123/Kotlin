// ============================================================================
// ARCHIVO : test/.../LoginValidationTest.kt
// CAPA    : Pruebas unitarias (corren en tu PC, sin emulador)
// RESUMEN : verifica automáticamente que las reglas de validación de correo y contraseña funcionen.
//           Ejecutar: clic derecho en el archivo -> Run 'LoginValidationTest'.
// ============================================================================

package com.induztek.protocolos

import com.induztek.protocolos.viewmodel.LoginViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginValidationTest {

    // Se crea el ViewModel SIN repositorio (por eso el parámetro es nullable) para probar solo las validaciones.
    private val viewModel = LoginViewModel()

    // @Test: marca una función como prueba. El nombre sigue el patrón cosaProbada_situación_resultadoEsperado.
    @Test
    fun emailValidation_validEmails_returnsTrue() {
        // assertTrue(x) = la prueba pasa solo si x es verdadero.
        assertTrue(viewModel.validarCorreo("usuario@dominio.com"))
        assertTrue(viewModel.validarCorreo("usuario.apellido@empresa.cl"))
        assertTrue(viewModel.validarCorreo("soporte+tag@induztek.org"))
        assertTrue(viewModel.validarCorreo("contacto@sub.dominio.edu.co"))
    }

    @Test
    fun emailValidation_invalidEmails_returnsFalse() {
        // assertFalse(x) = pasa solo si x es falso (correos inválidos deben ser rechazados).
        assertFalse(viewModel.validarCorreo(""))
        assertFalse(viewModel.validarCorreo("correo"))
        assertFalse(viewModel.validarCorreo("correo@"))
        assertFalse(viewModel.validarCorreo("correo@dominio"))
        assertFalse(viewModel.validarCorreo("@dominio.com"))
    }

    @Test
    // Casos que DEBEN fallar: menos de 8 caracteres.
    fun passwordValidation_tooShort_returnsFalse() {
        assertFalse(viewModel.validarContrasena("Ab1"))
        assertFalse(viewModel.validarContrasena("Ab12345")) // 7 chars
    }

    @Test
    fun passwordValidation_missingUppercase_returnsFalse() {
        assertFalse(viewModel.validarContrasena("admin2026"))
    }

    @Test
    fun passwordValidation_missingLowercase_returnsFalse() {
        assertFalse(viewModel.validarContrasena("ADMIN2026"))
    }

    @Test
    fun passwordValidation_missingNumber_returnsFalse() {
        assertFalse(viewModel.validarContrasena("AdminPass"))
    }

    @Test
    fun passwordValidation_missingSpecialChar_returnsFalse() {
        assertFalse(viewModel.validarContrasena("Admin2026"))
        assertFalse(viewModel.validarContrasena("Tecnico2026"))
    }

    @Test
    // Casos que DEBEN pasar: cumplen todas las reglas.
    fun passwordValidation_validPasswords_returnsTrue() {
        assertTrue(viewModel.validarContrasena("Admin2026!"))
        assertTrue(viewModel.validarContrasena("Tecnico2026!"))
        assertTrue(viewModel.validarContrasena("MiClave1@"))
    }
}
