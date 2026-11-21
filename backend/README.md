# Backend Induztek

Backend Java 21 / Spring Boot 3.5 con servicios independientes, Eureka, un API Gateway, MySQL y Swagger UI centralizada.

## Servicios

- `identity-service`: acceso, usuarios, aceptación de términos, recuperación de contraseña y gestión de fotos de perfil (`fotoPerfil`).
- `roles-service`: catálogo de perfiles y permisos.
- `catalog-service`: equipos e instrumentos.
- `operations-service`: visitas, protocolos, mediciones estructuradas en JSON, pruebas y almacenamiento directo de fotos de evidencia fotográfica.
- `notifications-service`: entrega de correos de recuperación mediante SMTP.
- `service-registry`: registro Eureka.
- `api-gateway`: única entrada HTTP para Android y túneles.

Cada servicio dueño de datos usa su propio esquema MySQL. La app móvil no se conecta a MySQL. Las llamadas internas usan `WebClient` con descubrimiento por Eureka; no se usa `RestTemplate`.

## Ejecución local

1. Copiar `.env.example` a `.env` y reemplazar contraseñas y `JWT_SECRET` por valores aleatorios. Configurar SMTP para que funcionen los correos de recuperación.
2. Desde esta carpeta, ejecutar `docker compose up --build`.
3. La app se expone en `http://localhost:8080`; Eureka queda en `http://localhost:8761`.
4. Swagger: `http://localhost:8080/swagger-ui.html`.

Para Laragon/XAMPP, crear las bases `induztek_identity`, `induztek_roles`, `induztek_catalog` e `induztek_operations`, dar permisos al usuario configurado y establecer `IDENTITY_DATABASE_URL`, `ROLES_DATABASE_URL`, `CATALOG_DATABASE_URL` and `OPERATIONS_DATABASE_URL` en `.env`. Desde contenedores Windows, usa `host.docker.internal` como host de MySQL; no uses `localhost`.

## Android

- Emulador Android: `http://10.0.2.2:8080/`.
- Teléfono físico: IP LAN del equipo para la URL de API y `PUBLIC_BASE_URL`, por ejemplo `http://192.168.x.x:8080/`.
- Túnel: configurar la URL HTTPS pública del Gateway tanto en la app como en `PUBLIC_BASE_URL`; no exponer MySQL ni los puertos de los microservicios.

La URL se configura mediante `INDUZTEK_API_BASE_URL` en `gradle.properties` local o en `local.properties`; no guardar una URL de túnel temporal en el código.

## Seguridad y datos

El registro exige aceptar términos y asigna estado pendiente si `REGISTRATION_REQUIRE_APPROVAL=true`. Solo un administrador bootstrap configurado puede aprobar cuentas. Las contraseñas se guardan con BCrypt, y los tokens son JWT firmados con `JWT_SECRET`.

Las imágenes tienen límite de 10 MB y se almacenan en un volumen Docker persistente gestionado por `identity-service` (fotos de perfil) y `operations-service` (fotos de evidencia de pruebas). SMTP y credenciales de despliegue son configuración externa; nunca deben añadirse al repositorio.
