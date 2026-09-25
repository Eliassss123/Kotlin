package com.induztek.app.domain.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias de las 5 Reglas de Negocio en PasswordPolicy (Capa Domain).
 */
class PasswordPolicyTest {

    private lateinit var policy: PasswordPolicy

    @Before
    fun setUp() {
        policy = PasswordPolicy()
    }

    @Test
    fun `contrasena valida con todas las reglas retorna Valid`() {
        val resultado = policy.validate("Induztek2026!")
        assertTrue(resultado is PasswordPolicy.ValidationResult.Valid)
    }

    @Test
    fun `contrasena corta menor a 8 caracteres retorna Invalid`() {
        val resultado = policy.validate("Ind1!")
        assertTrue(resultado is PasswordPolicy.ValidationResult.Invalid)
        val errors = (resultado as PasswordPolicy.ValidationResult.Invalid).errors
        assertTrue(errors.any { it.contains("8 caracteres") })
    }

    @Test
    fun `contrasena sin mayuscula retorna Invalid`() {
        val resultado = policy.validate("induztek2026!")
        assertTrue(resultado is PasswordPolicy.ValidationResult.Invalid)
        val errors = (resultado as PasswordPolicy.ValidationResult.Invalid).errors
        assertTrue(errors.any { it.contains("mayúscula") })
    }

    @Test
    fun `contrasena sin digito numerico retorna Invalid`() {
        val resultado = policy.validate("InduztekPass!")
        assertTrue(resultado is PasswordPolicy.ValidationResult.Invalid)
        val errors = (resultado as PasswordPolicy.ValidationResult.Invalid).errors
        assertTrue(errors.any { it.contains("número") })
    }

    @Test
    fun `contrasena sin caracter especial retorna Invalid`() {
        val resultado = policy.validate("Induztek2026")
        assertTrue(resultado is PasswordPolicy.ValidationResult.Invalid)
        val errors = (resultado as PasswordPolicy.ValidationResult.Invalid).errors
        assertTrue(errors.any { it.contains("carácter especial") })
    }
}
