package com.induztek.app.domain.policy

/**
 * Reglas de negocio explícitas para validación de contraseñas.
 * Requisito de arquitectura Duoc UC (mínimo 5 reglas en domain/policy).
 */
class PasswordPolicy {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val errors: List<String>) : ValidationResult()
    }

    /**
     * Evalúa las 5 reglas de seguridad corporativa para contraseñas de Induztek.
     */
    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        // Regla 1: Longitud mínima de 8 caracteres
        if (password.length < 8) {
            errors.add("La contraseña debe tener al menos 8 caracteres")
        }

        // Regla 2: Al menos una letra mayúscula
        if (!password.any { it.isUpperCase() }) {
            errors.add("Debe incluir al menos una letra mayúscula")
        }

        // Regla 3: Al menos una letra minúscula
        if (!password.any { it.isLowerCase() }) {
            errors.add("Debe incluir al menos una letra minúscula")
        }

        // Regla 4: Al menos un dígito numérico
        if (!password.any { it.isDigit() }) {
            errors.add("Debe incluir al menos un número")
        }

        // Regla 5: Al menos un carácter especial (@#$%&*!-_)
        val specialChars = setOf('@', '#', '$', '%', '&', '*', '!', '-', '_', '.', '?')
        if (!password.any { it in specialChars }) {
            errors.add("Debe incluir al menos un carácter especial (@, #, $, %, &, *, !, -, _)")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
