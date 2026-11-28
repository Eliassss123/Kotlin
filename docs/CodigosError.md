# Códigos de error de la app Android

Todo error que ve el usuario empieza con un código entre corchetes, por ejemplo `[EQ-002] El cliente es obligatorio`. Con el código, soporte sabe exactamente qué pasó sin necesidad de capturas ni logs.

- **Catálogo:** [`domain/errores/CodigoError.kt`](../app/src/main/java/com/induztek/protocolos/domain/errores/CodigoError.kt). Cada error tiene `codigo`, `mensaje` (lo que se muestra) y `razon` (por qué ocurre).
- **Mostrar un error:** `CodigoError.EQ_002.formatear()` → `"[EQ-002] El cliente es obligatorio"`. Los `%s` se completan con parámetros: `CodigoError.EQ_006.formatear("TR-01")`.
- **Lanzar un error desde un repositorio:** `throw AppException(CodigoError.MED_002)`. Los ViewModels lo muestran con `toUserMessage()`.
- **Errores del servidor:** se muestran como `[HTTP-<status>]` (ver [CodigosHTTP.md](CodigosHTTP.md)).
- **Regla:** un código nunca se reutiliza ni se cambia. Para un error nuevo, agrega una entrada con el siguiente número del prefijo; `CodigoErrorTest` verifica que sea único y tenga mensaje y razón.

## Prefijos

| Prefijo | Área | Dónde se usa |
|---------|------|--------------|
| `AUTH` | Inicio de sesión, registro, contraseña, recuperación | `LoginViewModel` |
| `PERF` | Perfil del usuario | `LoginViewModel` |
| `ADM` | Instrumentos y personal | `AdminViewModel` |
| `EQ` | Equipos | `EquipoViewModel` |
| `VIS` | Visitas y actas | `VisitaViewModel` |
| `PRO` | Protocolos y edición de pruebas | `ProtocoloViewModel` |
| `PRU` | Captura de pruebas nuevas | `PruebaViewModel` |
| `REG` | Reglas de negocio de las pruebas | `ReglasNegocio` |
| `MED` | Imágenes | `MediaRepository` |
| `RED` / `APP` | Red, respuestas vacías, errores inesperados | `toUserMessage()`, `requireBody()` |
| `HTTP` | Respuestas de error del backend | `toUserMessage()` |

## Catálogo completo

### Autenticación (AUTH)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `AUTH-001` | Ingresa un correo válido | El correo no cumple el formato usuario@dominio.tld. |
| `AUTH-002` | Ingresa tu contraseña | Se intentó iniciar sesión con la contraseña vacía. |
| `AUTH-003` | El servicio de autenticación no está disponible | El ViewModel se creó sin UsuarioRepository (error de configuración en MainActivity). |
| `AUTH-004` | Cuenta desactivada. Contacte al Jefe Técnico de Induztek. | El backend autenticó al usuario, pero la cuenta tiene activo = false. |
| `AUTH-005` | Ingresa tu nombre | Registro con el nombre vacío. |
| `AUTH-006` | Debes aceptar los términos y condiciones | Registro sin marcar la aceptación de términos (obligatoria por el backend). |
| `AUTH-010` | Debe tener al menos 10 caracteres | Contraseña con menos de 10 caracteres. |
| `AUTH-011` | Debe incluir al menos una letra mayúscula | Contraseña sin letras mayúsculas. |
| `AUTH-012` | Debe incluir al menos una letra minúscula | Contraseña sin letras minúsculas. |
| `AUTH-013` | Debe incluir al menos un número | Contraseña sin dígitos. |
| `AUTH-014` | Debe incluir al menos un carácter especial (!@#$%^&*...) | Contraseña solo con letras y números. |
| `AUTH-020` | Ingresa el código de 6 dígitos | El código de recuperación no tiene exactamente 6 dígitos. |
| `AUTH-021` | Primero verifica el código recibido | Se intentó cambiar la contraseña sin verificar antes el código de recuperación. |
| `AUTH-022` | Las contraseñas no coinciden | La nueva contraseña y su confirmación son distintas. |

### Perfil (PERF)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `PERF-001` | No hay una sesión activa para actualizar el perfil | Se intentó editar el perfil sin usuario activo cargado. |

### Administración (ADM)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `ADM-001` | Marca, modelo y número de serie son obligatorios. | Alta de instrumento con marca, modelo o número de serie vacíos. |
| `ADM-002` | La fecha de vencimiento de calibración '%s' no es válida. Usa el formato AAAA-MM-DD. | La fecha de calibración no se pudo leer como yyyy-MM-dd. |
| `ADM-003` | Todos los campos son obligatorios y la contraseña debe tener al menos 10 caracteres. | Alta de usuario con nombre o correo vacíos, o contraseña de menos de 10 caracteres. |
| `ADM-004` | El correo '%s' no es válido | Alta de usuario con un correo que no cumple usuario@dominio.tld. |

### Equipos (EQ)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `EQ-001` | El código del TAG es obligatorio (ej: TR-500KVA-01, CAB-XLPE-05). Si el equipo no posee TAG, debe ingresar obligatoriamente Marca y N° de Serie para identificarlo. | Equipo sin TAG y sin marca o número de serie: no hay forma de identificarlo. |
| `EQ-002` | El cliente es obligatorio | Equipo sin cliente. |
| `EQ-003` | La instalación/ubicación es obligatoria | Equipo sin instalación o ubicación. |
| `EQ-004` | El año ingresado '%s' no es válido. Debe ser un número de 4 dígitos entre 1900 y 2026. | Año de fabricación vacío o fuera de 1900..2026. |
| `EQ-005` | Posible duplicado detectado: Ya existe el equipo '%s' [TAG: %s] en esta instalación (RT-02). | Advertencia: en la misma ubicación ya hay un equipo con igual TAG, serie o código (RT-02). |
| `EQ-006` | No se encontró el equipo '%s' | El código de equipo no existe en el catálogo (o el cliente no tiene acceso). |

### Visitas y actas (VIS)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `VIS-001` | La Orden de Trabajo (OT) es obligatoria | Visita sin número de OT. |
| `VIS-002` | El cliente es obligatorio | Visita sin cliente. |
| `VIS-003` | El Ingeniero a cargo es OBLIGATORIO (Aclar. 4 y 7) | Visita sin ingeniero responsable (Aclaraciones 4 y 7). |
| `VIS-004` | Ya existe un protocolo registrado para el equipo %s en esta visita (RN-02). | Regla RN-02: un equipo solo puede tener un protocolo por visita. |
| `VIS-005` | No hay una visita activa seleccionada | Se intentó operar sobre una visita sin haberla cargado antes. |
| `VIS-006` | Es obligatorio ingresar el nombre de la persona que recibe el acta (RT-08) | Cierre de visita sin el nombre de quien recibe el acta (RT-08). |
| `VIS-007` | No se puede cerrar la visita sin al menos un protocolo registrado. | Cierre de visita sin protocolos. |
| `VIS-008` | Error en protocolo %s: %s | Al cerrar la visita, una prueba de un protocolo está incompleta (ver REG-001 / REG-002). |
| `VIS-009` | No se encontró la visita '%s' | El id de visita no existe en el backend. |
| `VIS-010` | No se encontró el acta '%s' | El id de acta no existe en el backend. |

### Protocolos (PRO)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `PRO-001` | Si la prueba es NO REALIZADA, debe registrar obligatoriamente el motivo (Aclaración 2). | Prueba marcada como NO REALIZADA sin motivo (Aclaración 2). |
| `PRO-002` | No hay una prueba en edición | Se intentó guardar sin haber iniciado la edición de una prueba. |
| `PRO-003` | No hay un protocolo seleccionado | Se intentó guardar, formalizar o agregar fotos sin protocolo cargado. |
| `PRO-004` | No se encontró el protocolo '%s' | El id de protocolo no existe en el backend. |
| `PRO-005` | No se encontró la prueba '%s' | El id de prueba no existe en el backend. |
| `PRO-006` | El protocolo ya está formalizado y no se puede modificar | Los protocolos FORMALIZADOS son inmutables. |

### Captura de pruebas (PRU)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `PRU-001` | Selecciona el equipo de la prueba | Se intentó guardar una prueba sin código de equipo. |
| `PRU-002` | La Orden de Trabajo (OT) es obligatoria | Se intentó guardar una prueba sin OT. |
| `PRU-003` | Registra al menos una medición | Se intentó guardar una prueba sin mediciones. |
| `PRU-004` | La medición necesita nombre y valor | Se intentó agregar una medición con el campo o el valor vacíos. |

### Reglas de negocio (REG)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `REG-001` | La prueba '%s' está marcada como NO REALIZADA y requiere un motivo no vacío. | Validación final: prueba NO REALIZADA sin motivo. |
| `REG-002` | La prueba '%s' debe tener al menos una medición registrada o marcarse como NO REALIZADA con su motivo. | Validación final: prueba REALIZADA sin mediciones. |

### Imágenes (MED)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `MED-001` | La imagen seleccionada no está en el almacenamiento privado de la app | Por seguridad solo se suben archivos de filesDir (evita leer archivos arbitrarios del teléfono). |
| `MED-002` | La imagen supera el tamaño máximo de 10 MB | Validación local del mismo límite que aplica el backend (413). |
| `MED-003` | La imagen se subió, pero no se pudo borrar la copia local temporal | File.delete() devolvió false tras subir la imagen. |

### Red y app (RED / APP)

| Código | Mensaje al usuario | Razón |
|--------|--------------------|-------|
| `RED-001` | El servidor tardó demasiado en responder. Inténtalo de nuevo. | SocketTimeoutException: no hubo respuesta dentro de RetrofitClient.READ_TIMEOUT_SECONDS. |
| `RED-002` | No fue posible conectar con el servidor. Verifica tu conexión. | IOException: sin red, túnel caído o DNS que no resuelve. |
| `RED-003` | El servidor respondió sin datos (HTTP %s) | Respuesta 2xx sin cuerpo en un endpoint que debe devolver datos. |
| `APP-001` | Ocurrió un error inesperado | Excepción no prevista; revisar Logcat. |

### Servidor (HTTP)

`[HTTP-400]` a `[HTTP-504]`: el detalle de cada código y su mensaje está en [CodigosHTTP.md](CodigosHTTP.md).

## Pruebas unitarias y criterios de aceptación

Cada clase de test empieza con un comentario que lista sus **criterios de aceptación** (`CA-XXX-NN`) y los códigos de error que cubre. Cada test lleva encima una línea que dice qué criterio verifica, por ejemplo:

```kotlin
// CA-EQ-02 [EQ-002]: el cliente es obligatorio.
@Test
fun sinCliente() { ... }
```

| Clase de test | Qué cubre | Criterios |
|---------------|-----------|-----------|
| `domain/CodigoErrorTest` | Catálogo de errores | CA-ERR-01..05 |
| `domain/ReglasNegocioTest` | IP, TTR, contactos, relé, variación, resultado global, validación de pruebas | CA-REG-01..10 |
| `viewmodel/LoginViewModelTest` | Login, registro, contraseña, perfil, cierre de sesión | CA-LOG, CA-REGI, CA-PASS, CA-PERF, CA-SES |
| `viewmodel/LoginViewModelRecuperacionTest` | Recuperación de contraseña | CA-REC-01..07 |
| `viewmodel/AdminViewModelTest` | Instrumentos y personal | CA-ADM-01..07 |
| `viewmodel/EquipoViewModelTest` | Alta, duplicados, detalle y búsqueda de equipos | CA-EQ-01..08 |
| `viewmodel/VisitaViewModelTest` | Visitas, protocolos en terreno y cierre con acta | CA-VIS-01..10 |
| `viewmodel/ProtocoloViewModelTest` | Edición de pruebas, recálculo y formalización | CA-PRO-01..09 |
| `viewmodel/PruebaViewModelTest` | Captura de pruebas nuevas | CA-PRU-01..07 |
| `data/repository/MediaRepositoryTest` | Subida de imágenes | CA-MED-01..05 |
| `ApiErrorsTest` | Traducción de errores HTTP y de red | CA-API-01..05 |
| `data/remote/AuthApiRecoveryTest` | Contrato HTTP de la recuperación (MockWebServer) | — |

```powershell
.\gradlew.bat :app:testDebugUnitTest
```
