# Rediseño Estético y Estructural basado en Prototipos HTML

Este plan detalla los cambios necesarios para alinear la interfaz de usuario en Jetpack Compose de la aplicación Android con la estética y estructura gráfica definida en los prototipos HTML (`Induztek · Cliente y Administrador.html` y `induztek-uber-roles.html`).

## User Review Required

> [!IMPORTANT]
> Se mantendrán intactas todas la lógica de negocio, ViewModels, base de datos Room y conexiones con los microservicios del backend. El cambio es estrictamente visual y estructural (UI/UX).

## Proposed Changes

### UI Components & Screens

#### [MODIFY] [ListaEquiposScreen.kt](file:///C:/Users/andres/Downloads/Kotlin-induztek/Kotlin-avancebenja/app/src/main/java/com/induztek/protocolos/ui/equipos/ListaEquiposScreen.kt)
- Actualizar el diseño de las tarjetas de equipos (`Card`) para incorporar el indicador de alerta lateral (barra roja o acento según estado), chips de clasificación modernos (*Primarios*, *Secundarios*), y barra de búsqueda estilizada.

#### [MODIFY] [ListaVisitasScreen.kt](file:///C:/Users/andres/Downloads/Kotlin-induztek/Kotlin-avancebenja/app/src/main/java/com/induztek/protocolos/ui/visitas/ListaVisitasScreen.kt)
- Refinar la visualización de visitas técnicas con estados claros (*En Progreso* vs *Cerrada / Acta lista*) y botones flotantes (*FAB*) redondeados.

#### [MODIFY] [PerfilScreen.kt](file:///C:/Users/andres/Downloads/Kotlin-induztek/Kotlin-avancebenja/app/src/main/java/com/induztek/protocolos/ui/perfil/PerfilScreen.kt)
- Estilizar la sección de perfil con avatar circular, información de cuenta, biografía y matriz de permisos del rol (P-01 a P-04) tal como se muestra en el prototipo de roles.

#### [MODIFY] [AdminScreen.kt](file:///C:/Users/andres/Downloads/Kotlin-induztek/Kotlin-avancebenja/app/src/main/java/com/induztek/protocolos/ui/admin/AdminScreen.kt)
- Ajustar las pestañas de administración (*Instrumentos & Calibraciones* y *Gestión de Personal*) con un selector de segmentos limpio y tarjetas de estado vigentes/vencidas.

## Verification Plan

### Automated Tests
- Compilación limpia del proyecto: `gradle_build("app:assembleDebug")`

### Manual Verification
- Desplegar en el emulador o dispositivo y verificar visualmente la consistencia estética con los prototipos HTML.
