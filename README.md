# Induztek — App Móvil Android (DSY1105)

Aplicación Android para registro digital de pruebas técnicas eléctricas en terreno.

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM (ViewModel sin Context) |
| Persistencia local | Room (SQLite) |
| Backend | Spring Boot, API REST *(Eval 3)* |
| Comunicación | Retrofit + multipart/form-data |
| DI | Hilt |
| Imágenes | Coil |

## Cómo abrir en Android Studio

### Requisitos previos
- **Android Studio Ladybug** (2024.2.1) o superior
- **JDK 17** (incluido con Android Studio)
- **Android SDK** con API 26–35 instalado

### Pasos

1. Abre Android Studio
2. `File → Open` → selecciona la carpeta `kotlin/` (la raíz del proyecto)
3. Espera a que Android Studio sincronice Gradle (descargará ~500 MB la primera vez)
4. En la barra superior, selecciona el emulador o dispositivo físico
5. Presiona ▶ **Run**

### Si Gradle falla al sincronizar

Esto puede ocurrir si hay problemas de red. Intenta:
- `File → Sync Project with Gradle Files`
- Si el error menciona versiones de KSP o AGP, verifica `gradle/libs.versions.toml`

## Estructura del proyecto

```
app/src/main/java/com/induztek/app/
├── data/          ← Room entities, DAOs, Repositories, Mappers
├── domain/        ← Modelos puros, interfaces de Repository
├── presentation/  ← ViewModels, Composables, Navigation
│   ├── screen/
│   │   ├── equipos/     ← ListaEquiposScreen (pantalla inicio)
│   │   ├── nuevaprueba/ ← NuevaPruebaScreen + CapturaFoto
│   │   └── detalle/     ← DetallePruebaScreen
│   ├── navigation/      ← NavGraph + rutas
│   └── theme/           ← Material 3 theme
└── di/            ← Módulos Hilt
```

## Flujo principal

```
Abrir app → Lista de equipos → Tap en equipo
         → Formulario nueva prueba (con foto)
         → Guardar offline (Room)
         → Detalle de la prueba guardada
```

## Datos sintéticos

Al primer arranque se insertan 8 equipos ficticios:
- TRF-001, TRF-002 (Transformadores)
- REL-042, REL-055 (Relés)
- CEL-010, CEL-011 (Celdas)
- CAB-201, CAB-202 (Cables)

Ningún dato real de clientes.

## Permisos requeridos

| Permiso | Para qué |
|---------|----------|
| `CAMERA` | Captura de foto de evidencia |
| `INTERNET` | Sincronización con backend (Eval 3) |
| `ACCESS_NETWORK_STATE` | Detectar conectividad para sync |

## Tests unitarios

```bash
# Ejecutar desde Android Studio:
# Run → Edit Configurations → + → JUnit → NuevaPruebaViewModelTest

# O desde terminal (requiere SDK):
./gradlew test
```
