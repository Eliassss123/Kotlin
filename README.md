# Induztek Protocolos de Prueba en Terreno

## Repositorios

- Aplicación Android (Kotlin): [Eliassss123/Kotlin](https://github.com/Eliassss123/Kotlin/tree/kotlin)
- Backend de microservicios: [Benjamin-Negrete/Kotlin-Microservicios](https://github.com/Benjamin-Negrete/Kotlin-Microservicios)

Sistema integral de gestión, ejecución y control de protocolos de prueba y mantenimiento para sistemas eléctricos de alta y media tensión en terreno, compuesto por una aplicación móvil **Android (Jetpack Compose / Kotlin)** y una arquitectura backend basada en **Microservicios Spring Boot (Cloud & Netflix Eureka)**.

---

## Índice de Contenidos
1. [Descripción General y Arquitectura](#1-descripción-general-y-arquitectura)
2. [Manual de Usuario](#2-manual-de-usuario)
3. [Documentación de Endpoints de los Microservicios](#3-documentación-de-endpoints-de-los-microservicios)
4. [Documentación de Código Kotlin (Comentarios y KDoc)](#4-documentación-de-código-kotlin-comentarios-y-kdoc)

---

## 1. Descripción General y Arquitectura

### Stack Tecnológico
* **Frontend Móvil:** Android nativo en Kotlin, Jetpack Compose (Material 3), Navigation, Coroutines / Flow, Retrofit / Gson, Room Database para persistencia offline y sincronización.
* **Backend:** Arquitectura de Microservicios con Spring Boot (Java 21), Spring Cloud Gateway, Netflix Eureka Server (Service Discovery), Spring Security (OAuth2 / JWT Resource Server), Spring Data JPA / Hibernate y base de datos relacional MySQL.

### Estructura de Microservicios
* **`api-gateway` (Puerto 8080):** Punto único de entrada al sistema. Enruta peticiones y gestiona la seguridad perimetral.
* **`identity-service` (Puerto 8081):** Autenticación, registro, gestión de usuarios, control de roles (P-01, P-03, P-04) y recuperación de contraseñas mediante códigos de 6 dígitos enviados por correo SMTP.
* **`roles-service` (Puerto 8082):** Definición y catálogos de permisos y perfiles técnicos de Induztek.
* **`catalog-service` (Puerto 8083):** Gestión y registro de equipos eléctricos (Transformadores, Interruptores, Cables, Relés) y calibración de instrumentos de prueba.
* **`operations-service` (Puerto 8084):** Ejecución de visitas técnicas en terreno (RT-01), protocolos de prueba (RT-03), mediciones de campo (RT-04), registro fotográfico de evidencias y emisión de Actas Oficiales de Entrega (RT-08).
* **`notifications-service` (Puerto 8086):** Envío de correos electrónicos transaccionales y notificaciones del sistema.

---

## 2. Manual de Usuario

### Roles del Sistema (Simplificados a 3 Tipos)
1. **Técnico de Terreno (`P-01` / `FIELD_TECHNICIAN`):**
   * Registro de equipos eléctricos en terreno (RT-02).
   * Creación de visitas técnicas (RT-01) y asociación de protocolos.
   * Ejecución de pruebas y registro de mediciones técnicas (RT-04).
   * Adjuntar fotografías de evidencia fotográfica.
2. **Administrador / Jefe Técnico (`P-03` / `ADMIN`):**
   * Control maestro y gestión integral del sistema.
   * Gestión de personal: crear cuentas nuevas asignando cargo y planta/empresa, activar/desactivar cuentas o eliminarlas permanentemente.
   * Gestión de instrumentos y control de vigencia de calibración (P-03 / RT-11).
   * Revisión, aprobación, observación y formalización de protocolos (inmutabilidad técnica RN-16).
   * Cierre de visitas técnicas y emisión de Actas Oficiales de Entrega (RT-08).
3. **Usuario Cliente (`P-04` / `CLIENT`):**
   * Acceso exclusivo de **solo consulta** (*Read-Only*).
   * Visualización del estado de visitas, equipos, protocolos formalizados y actas oficiales correspondientes únicamente a su propia empresa/planta asignada.

### Flujo de Operación en Terreno
1. **Inicio de Sesión:** Ingrese su correo y contraseña. Al autenticarse con éxito, el sistema emite una notificación de bienvenida (*"Bienvenido (Correo)"*) y genera vibración háptica de confirmación.
2. **Creación de Visita (RT-01):** Indique la Orden de Trabajo (OT), empresa cliente, instalación/subestación y modalidad de prueba.
3. **Registro de Equipos (RT-02):** Añada los equipos intervenidos especificando su placa de características (potencia, tensiones, grupo de conexión y posición de tap).
4. **Ejecución de Protocolos y Pruebas (RT-03 / RT-04):** Seleccione un equipo dentro de la visita, registre las mediciones eléctricas y adjunte evidencia fotográfica (cámara o galería).
5. **Cierre y Acta (RT-08):** Ingrese el nombre del receptor por parte del cliente y emita el acta de entrega oficial.

---

## 3. Documentación de Endpoints de los Microservicios

### A. Identidad y Usuarios (`identity-service` via API Gateway `/api`)
* `POST /api/auth/register`: Registra un nuevo usuario en el sistema con su respectivo rol y empresa/planta.
* `POST /api/auth/login`: Autentica credenciales y retorna el token JWT y el perfil del usuario.
* `POST /api/auth/password/recovery`: Solicita la generación de un código de 6 dígitos para recuperación de contraseña.
* `POST /api/auth/password/verify`: Valida el código de 6 dígitos ingresado por el usuario.
* `POST /api/auth/password/reset`: Actualiza la contraseña del usuario tras validar el código.
* `GET /api/usuarios/me`: Obtiene los datos del perfil del usuario autenticado actual.
* `PATCH /api/usuarios/me/perfil`: Actualiza la biografía, nombre, correo o foto de perfil del usuario.
* `GET /api/usuarios`: *(Solo Administrador)* Lista todos los usuarios registrados en el sistema.
* `PATCH /api/usuarios/{id}/estado`: *(Solo Administrador)* Activa o desactiva el acceso de una cuenta de usuario.
* `PATCH /api/usuarios/{id}/rol`: *(Solo Administrador)* Modifica el cargo/rol asignado a una cuenta de usuario.
* `DELETE /api/usuarios/{id}`: *(Solo Administrador)* Elimina permanentemente una cuenta de usuario del sistema.

### B. Catálogo e Instrumentos (`catalog-service` via API Gateway `/api`)
* `GET /api/equipos`: Lista los equipos eléctricos registrados en terreno (filtrables por cliente).
* `POST /api/equipos`: Registra o actualiza un equipo eléctrico en placa de características.
* `GET /api/equipos/internal/{code}`: Validación interna de existencia de equipo requerida para la creación de protocolos.
* `GET /api/instrumentos`: Lista los instrumentos de prueba y medición y su estado de calibración.
* `POST /api/instrumentos`: Registra un nuevo instrumento de prueba en el sistema.
* `PATCH /api/instrumentos/{id}/calibracion`: Actualiza la fecha de vencimiento de calibración de un instrumento.

### C. Operaciones y Terreno (`operations-service` via API Gateway `/api`)
* `GET /api/visitas`: Lista las visitas técnicas (con filtrado por cliente para usuarios externos).
* `POST /api/visitas`: Registra una nueva visita técnica en terreno (RT-01).
* `PATCH /api/visitas/{id}/cerrar`: Cierra una visita técnica y genera el acta de entrega formal.
* `GET /api/protocolos`: Lista los protocolos de prueba (filtrables por visita o código de equipo).
* `POST /api/protocolos`: Crea un nuevo protocolo de prueba técnica en terreno vinculado a un equipo (RN-02).
* `PATCH /api/protocolos/{id}/formalizar`: Formaliza e inmutabiliza un protocolo de prueba (RN-16).
* `GET /api/actas/{id}`: Obtiene el detalle de un acta oficial de entrega (RT-08).

---

## 4. Documentación de Código Kotlin (Comentarios y KDoc)

El código fuente en Kotlin de la aplicación Android está estructurado bajo principios de arquitectura limpia (MVVM) y debidamente documentado mediante KDoc y comentarios descriptivos en los bloques principales:
* **`LoginViewModel`**: Gestiona el estado de autenticación, almacenamiento seguro de sesión, notificaciones locales de bienvenida y vibración háptica.
* **`AdminViewModel`**: Administra la lógica de negocio del panel de administración (gestión de personal, creación/eliminación de cuentas, asignación de roles y control de instrumentos).
* **`VisitaViewModel` & `EquipoViewModel`**: Controlan la persistencia offline y sincronización remota de visitas técnicas, equipos en terreno y protocolos de ensayo.
* **`NotificationHelper`**: Utilidad centralizada para la gestión de canales de notificación en Android y disparo de vibraciones de hardware (*Haptic Feedback*).
