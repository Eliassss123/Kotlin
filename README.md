# Readme con instrucciones

Aplicacion movil desarrollada en Jetpack Compose (Kotlin) para la gestion y ejecucion de protocolos de prueba en terreno, inspeccion de equipos electricos, registro de visitas tecnicas y emision de actas de conformidad.

---

## Cuentas Disponibles para Iniciar Sesion

Puedes utilizar cualquiera de las siguientes credenciales de prueba segun el rol que desees simular:

| Rol / Perfil | Correo Electronico | Contrasena | Nombre / Descripcion |
| :--- | :--- | :--- | :--- |
| Tecnico | `tecnico@induztek.cl` | `Tecnico2026!` | Juan Perez (P-01 Tecnico) |
| Ingeniero | `ingeniero@induztek.cl` | `Ingeniero2026!` | Carlos Mendoza (P-02 Ingeniero) |
| Jefe Tecnico / Supervisor | `jefetecnico@induztek.cl` | `Jefe2026!` | Roberto Silva (P-03 Jefe Tecnico) |
| Cliente Externo | `cliente@plantalampa.cl` | `Cliente2026!` | Mario Gomez (P-04 Cliente Planta Lampa) |

---

## Pasos para Usar la Aplicacion

### 1. Inicio de Sesion
1. Al abrir la aplicacion, veras la pantalla de bienvenida INDUZTEK en modo oscuro.
2. Ingresa manualmente el Correo Electronico y la Contrasena de una de las cuentas listadas arriba.
3. Presiona el boton INICIAR SESION.

### 2. Navegacion y Modulos Principales
Una vez dentro de la app, podras acceder a las siguientes secciones desde el menu o panel principal:
* Catalogo de Equipos: Consulta, registra o visualiza la ficha tecnica de los equipos electricos a inspeccionar.
* Gestion de Visitas: Organiza y programa visitas tecnicas a plantas o instalaciones.
* Protocolos y Pruebas en Terreno: Ejecuta mediciones, registra datos y adjunta evidencia fotografica (usando camara o galeria) durante las pruebas.
* Emision de Actas: Genera y visualiza actas de conformidad y recepcion para los clientes.
* Perfil de Usuario: Visualiza los datos del usuario actual y gestiona la foto de perfil en la seccion de perfil.

---

## Aspectos Tecnicos Destacados
* Interfaz Moderna: Desarrollada 100% con Jetpack Compose y Material 3.
* Modo Oscuro Permanente: Tonos oscuros profesionales de alto contraste (fondo Slate 900 y tarjetas Slate 800) optimizados para visibilidad en terreno y pantallas moviles (como Redmi 10C).
* Almacenamiento Local: Base de datos Room para funcionamiento offline en terreno.
* Gestion de Archivos: Soporte para captura y almacenamiento local de imagenes de evidencia y perfiles con ImageStorageHelper.
