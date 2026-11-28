package com.induztek.protocolos.domain.errores

import java.util.Locale

/**
 * Catálogo único de errores de la app. Cada error tiene:
 * - [codigo]: identificador estable que ve el usuario entre corchetes, p. ej. `[EQ-002]`.
 * - [mensaje]: texto para el usuario (puede llevar `%s` que se completan con [formatear]).
 * - [razon]: por qué ocurre, para soporte y desarrolladores (no se muestra en pantalla).
 *
 * Prefijos:
 * - AUTH: inicio de sesión, registro, contraseña y recuperación.
 * - PERF: perfil del usuario.
 * - ADM: administración de instrumentos y personal.
 * - EQ: equipos.
 * - VIS: visitas y actas.
 * - PRO: protocolos y edición de pruebas.
 * - PRU: captura de pruebas nuevas.
 * - REG: reglas de negocio de las pruebas.
 * - MED: imágenes y archivos.
 * - HTTP / RED / APP: errores del servidor, de red o inesperados.
 *
 * La tabla completa está en `docs/CodigosError.md`. Los códigos no deben reutilizarse ni cambiarse:
 * soporte y los tests (`CodigoErrorTest`) dependen de ellos.
 */
enum class CodigoError(val codigo: String, val mensaje: String, val razon: String) {
    // ---------- AUTH: autenticación ----------
    AUTH_001("AUTH-001", "Ingresa un correo válido",
        "El correo no cumple el formato usuario@dominio.tld."),
    AUTH_002("AUTH-002", "Ingresa tu contraseña",
        "Se intentó iniciar sesión con la contraseña vacía."),
    AUTH_003("AUTH-003", "El servicio de autenticación no está disponible",
        "El ViewModel se creó sin UsuarioRepository (error de configuración en MainActivity)."),
    AUTH_004("AUTH-004", "Cuenta desactivada. Contacte al Jefe Técnico de Induztek.",
        "El backend autenticó al usuario, pero la cuenta tiene activo = false."),
    AUTH_005("AUTH-005", "Ingresa tu nombre",
        "Registro con el nombre vacío."),
    AUTH_006("AUTH-006", "Debes aceptar los términos y condiciones",
        "Registro sin marcar la aceptación de términos (obligatoria por el backend)."),
    AUTH_010("AUTH-010", "Debe tener al menos 10 caracteres",
        "Contraseña con menos de 10 caracteres."),
    AUTH_011("AUTH-011", "Debe incluir al menos una letra mayúscula",
        "Contraseña sin letras mayúsculas."),
    AUTH_012("AUTH-012", "Debe incluir al menos una letra minúscula",
        "Contraseña sin letras minúsculas."),
    AUTH_013("AUTH-013", "Debe incluir al menos un número",
        "Contraseña sin dígitos."),
    AUTH_014("AUTH-014", "Debe incluir al menos un carácter especial (!@#\$%^&*...)",
        "Contraseña solo con letras y números."),
    AUTH_020("AUTH-020", "Ingresa el código de 6 dígitos",
        "El código de recuperación no tiene exactamente 6 dígitos."),
    AUTH_021("AUTH-021", "Primero verifica el código recibido",
        "Se intentó cambiar la contraseña sin verificar antes el código de recuperación."),
    AUTH_022("AUTH-022", "Las contraseñas no coinciden",
        "La nueva contraseña y su confirmación son distintas."),

    // ---------- PERF: perfil ----------
    PERF_001("PERF-001", "No hay una sesión activa para actualizar el perfil",
        "Se intentó editar el perfil sin usuario activo cargado."),

    // ---------- ADM: administración ----------
    ADM_001("ADM-001", "Marca, modelo y número de serie son obligatorios.",
        "Alta de instrumento con marca, modelo o número de serie vacíos."),
    ADM_002("ADM-002", "La fecha de vencimiento de calibración '%s' no es válida. Usa el formato AAAA-MM-DD.",
        "La fecha de calibración no se pudo leer como yyyy-MM-dd."),
    ADM_003("ADM-003", "Todos los campos son obligatorios y la contraseña debe tener al menos 10 caracteres.",
        "Alta de usuario con nombre o correo vacíos, o contraseña de menos de 10 caracteres."),
    ADM_004("ADM-004", "El correo '%s' no es válido",
        "Alta de usuario con un correo que no cumple usuario@dominio.tld."),

    // ---------- EQ: equipos ----------
    EQ_001("EQ-001", "El código del TAG es obligatorio (ej: TR-500KVA-01, CAB-XLPE-05). Si el equipo no posee TAG, debe ingresar obligatoriamente Marca y N° de Serie para identificarlo.",
        "Equipo sin TAG y sin marca o número de serie: no hay forma de identificarlo."),
    EQ_002("EQ-002", "El cliente es obligatorio",
        "Equipo sin cliente."),
    EQ_003("EQ-003", "La instalación/ubicación es obligatoria",
        "Equipo sin instalación o ubicación."),
    EQ_004("EQ-004", "El año ingresado '%s' no es válido. Debe ser un número de 4 dígitos entre 1900 y 2026.",
        "Año de fabricación vacío o fuera de 1900..2026."),
    EQ_005("EQ-005", "Posible duplicado detectado: Ya existe el equipo '%s' [TAG: %s] en esta instalación (RT-02).",
        "Advertencia: en la misma ubicación ya hay un equipo con igual TAG, serie o código (RT-02)."),
    EQ_006("EQ-006", "No se encontró el equipo '%s'",
        "El código de equipo no existe en el catálogo (o el cliente no tiene acceso)."),

    // ---------- VIS: visitas y actas ----------
    VIS_001("VIS-001", "La Orden de Trabajo (OT) es obligatoria",
        "Visita sin número de OT."),
    VIS_002("VIS-002", "El cliente es obligatorio",
        "Visita sin cliente."),
    VIS_003("VIS-003", "El Ingeniero a cargo es OBLIGATORIO (Aclar. 4 y 7)",
        "Visita sin ingeniero responsable (Aclaraciones 4 y 7)."),
    VIS_004("VIS-004", "Ya existe un protocolo registrado para el equipo %s en esta visita (RN-02).",
        "Regla RN-02: un equipo solo puede tener un protocolo por visita."),
    VIS_005("VIS-005", "No hay una visita activa seleccionada",
        "Se intentó operar sobre una visita sin haberla cargado antes."),
    VIS_006("VIS-006", "Es obligatorio ingresar el nombre de la persona que recibe el acta (RT-08)",
        "Cierre de visita sin el nombre de quien recibe el acta (RT-08)."),
    VIS_007("VIS-007", "No se puede cerrar la visita sin al menos un protocolo registrado.",
        "Cierre de visita sin protocolos."),
    VIS_008("VIS-008", "Error en protocolo %s: %s",
        "Al cerrar la visita, una prueba de un protocolo está incompleta (ver REG-001 / REG-002)."),
    VIS_009("VIS-009", "No se encontró la visita '%s'",
        "El id de visita no existe en el backend."),
    VIS_010("VIS-010", "No se encontró el acta '%s'",
        "El id de acta no existe en el backend."),

    // ---------- PRO: protocolos ----------
    PRO_001("PRO-001", "Si la prueba es NO REALIZADA, debe registrar obligatoriamente el motivo (Aclaración 2).",
        "Prueba marcada como NO REALIZADA sin motivo (Aclaración 2)."),
    PRO_002("PRO-002", "No hay una prueba en edición",
        "Se intentó guardar sin haber iniciado la edición de una prueba."),
    PRO_003("PRO-003", "No hay un protocolo seleccionado",
        "Se intentó guardar, formalizar o agregar fotos sin protocolo cargado."),
    PRO_004("PRO-004", "No se encontró el protocolo '%s'",
        "El id de protocolo no existe en el backend."),
    PRO_005("PRO-005", "No se encontró la prueba '%s'",
        "El id de prueba no existe en el backend."),
    PRO_006("PRO-006", "El protocolo ya está formalizado y no se puede modificar",
        "Los protocolos FORMALIZADOS son inmutables."),

    // ---------- PRU: captura de pruebas ----------
    PRU_001("PRU-001", "Selecciona el equipo de la prueba",
        "Se intentó guardar una prueba sin código de equipo."),
    PRU_002("PRU-002", "La Orden de Trabajo (OT) es obligatoria",
        "Se intentó guardar una prueba sin OT."),
    PRU_003("PRU-003", "Registra al menos una medición",
        "Se intentó guardar una prueba sin mediciones."),
    PRU_004("PRU-004", "La medición necesita nombre y valor",
        "Se intentó agregar una medición con el campo o el valor vacíos."),

    // ---------- REG: reglas de negocio ----------
    REG_001("REG-001", "La prueba '%s' está marcada como NO REALIZADA y requiere un motivo no vacío.",
        "Validación final: prueba NO REALIZADA sin motivo."),
    REG_002("REG-002", "La prueba '%s' debe tener al menos una medición registrada o marcarse como NO REALIZADA con su motivo.",
        "Validación final: prueba REALIZADA sin mediciones."),

    // ---------- MED: imágenes ----------
    MED_001("MED-001", "La imagen seleccionada no está en el almacenamiento privado de la app",
        "Por seguridad solo se suben archivos de filesDir (evita leer archivos arbitrarios del teléfono)."),
    MED_002("MED-002", "La imagen supera el tamaño máximo de 10 MB",
        "Validación local del mismo límite que aplica el backend (413)."),
    MED_003("MED-003", "La imagen se subió, pero no se pudo borrar la copia local temporal",
        "File.delete() devolvió false tras subir la imagen."),

    // ---------- RED / APP ----------
    RED_001("RED-001", "El servidor tardó demasiado en responder. Inténtalo de nuevo.",
        "SocketTimeoutException: no hubo respuesta dentro de RetrofitClient.READ_TIMEOUT_SECONDS."),
    RED_002("RED-002", "No fue posible conectar con el servidor. Verifica tu conexión.",
        "IOException: sin red, túnel caído o DNS que no resuelve."),
    RED_003("RED-003", "El servidor respondió sin datos (HTTP %s)",
        "Respuesta 2xx sin cuerpo en un endpoint que debe devolver datos."),
    APP_001("APP-001", "Ocurrió un error inesperado",
        "Excepción no prevista; revisar Logcat.");

    /** Mensaje para mostrar: `[CODIGO] mensaje`, completando los `%s` con [args]. */
    fun formatear(vararg args: Any?): String {
        val texto = if (args.isEmpty()) mensaje else String.format(Locale.ROOT, mensaje, *args)
        return "[$codigo] $texto"
    }

    companion object {
        /** Busca un error por su código visible (p. ej. "EQ-002"). */
        fun desdeCodigo(codigo: String): CodigoError? = entries.firstOrNull { it.codigo == codigo }
    }
}

/** Excepción de la app que lleva su [CodigoError]; `toUserMessage()` la muestra con su código. */
class AppException(val error: CodigoError, vararg args: Any?) : RuntimeException(error.formatear(*args))
