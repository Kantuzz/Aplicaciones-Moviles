# EvaluacionGastro

Aplicación Android para evaluación gastronómica en tiempo real desarrollada para Duoc UC.

## Descripción

EvaluacionGastro es una aplicación móvil que permite a docentes del área de gastronomía gestionar y recibir evaluaciones anónimas de mesas de trabajo durante las jornadas prácticas. Los estudiantes pueden evaluar mediante códigos QR sin necesidad de autenticación, proporcionando retroalimentación inmediata sobre diferentes criterios.

## Características Principales

### Para Docentes

- **Gestión de Mesas**: Crear, renombrar, activar/desactivar mesas de trabajo
- **Criterios de Evaluación**: Definir y configurar criterios personalizados con ponderaciones
- **Generación de QR**: Crear códigos QR únicos por mesa para evaluación anónima
- **Historial**: Visualizar evaluaciones históricas con filtros por fecha y mesa
- **Exportación**: Compartir códigos QR vía WhatsApp, correo o imprimir
- **API Externa**: Consulta del valor del dólar en tiempo real desde mindicador.cl

### Para Evaluadores (Estudiantes)

- **Evaluación Anónima**: Acceso directo mediante QR sin registro
- **Interfaz Intuitiva**: Sliders 1-5 para cada criterio
- **Comentarios Opcionales**: Campo de texto libre (máx. 280 caracteres)
- **Feedback Inmediato**: Confirmación visual al enviar evaluación

## Arquitectura Técnica

### Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Room (SQLite)
- **Navegación**: Navigation Compose con Deep Links
- **Persistencia**: DataStore Preferences
- **HTTP Client**: Retrofit + Gson
- **Generación QR**: ZXing Core 3.5.3

### Versiones

- **Compile SDK**: 36
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Kotlin**: 2.0.21
- **Gradle**: 8.13

### Dependencias Principales

```gradle
// Jetpack Compose
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

// Persistencia
implementation("androidx.room:room-ktx:2.6.1")
implementation("androidx.datastore:datastore-preferences:1.1.1")

// Red
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// QR
implementation("com.google.zxing:core:3.5.3")
```

## Estructura del Proyecto

```
app/src/main/java/cl/duoc/evalua/
├── core/
│   └── ServiceLocator.kt          # Inyección de dependencias manual
├── data/
│   ├── datastore/
│   │   └── SessionStore.kt        # Gestión de sesión
│   ├── local/
│   │   ├── AppDatabase.kt         # Configuración Room
│   │   ├── Converters.kt          # Conversores de tipo
│   │   ├── dao.kt                 # Data Access Objects
│   │   └── entities.kt            # Entidades de base de datos
│   ├── model/
│   │   └── DolarResponse.kt       # Modelo API externa
│   ├── remote/
│   │   ├── ApiService.kt          # Interface Retrofit
│   │   └── RetrofitClient.kt      # Cliente HTTP
│   └── repo/
│       └── Repositories.kt        # Repositorios de datos
├── ui/
│   ├── navigation/
│   │   ├── AppNavHost.kt          # Grafo de navegación
│   │   └── Routes.kt              # Definición de rutas
│   ├── screens/
│   │   ├── LoginScreen.kt         # Pantalla de autenticación
│   │   ├── HomeDocenteScreen.kt   # Menú principal
│   │   ├── MesasScreen.kt         # Gestión de mesas
│   │   ├── CriteriosScreen.kt     # Configuración de criterios
│   │   ├── QrScreen.kt            # Generación y exportación QR
│   │   ├── HistorialScreen.kt     # Visualización de historial
│   │   └── EvaluacionAnonimaScreen.kt  # Evaluación pública
│   └── theme/
│       ├── Color.kt               # Paleta de colores Duoc
│       ├── Theme.kt               # Configuración Material 3
│       └── Type.kt                # Tipografía
├── viewmodel/
│   ├── AuthViewModel.kt           # ViewModel de autenticación
│   └── DolarViewModel.kt          # ViewModel API externa
└── MainActivity.kt                 # Activity principal
```

## Base de Datos

### Esquema de Entidades

#### Mesas
- id (UUID, PK)
- nombre (String)
- activa (Boolean)
- createdAt (Instant)

#### Criterios
- id (UUID, PK)
- nombre (String)
- peso (Int, opcional)
- orden (Int)
- activo (Boolean)

#### Evaluaciones
- id (UUID, PK)
- mesaId (UUID, FK)
- jornadaId (UUID, FK, nullable)
- timestamp (Instant)
- comentario (String, nullable)

#### EvaluacionDetalle
- evaluacionId (UUID, PK compuesta)
- criterioNombre (String, PK compuesta)
- pesoSnapshot (Int, nullable)
- valor (Int, 1-5)

#### Jornadas
- id (UUID, PK)
- mesaId (UUID, FK)
- fecha (String)
- inicio (Instant)
- fin (Instant, nullable)
- estado (String: ACTIVA/CERRADA)

## Navegación

### Rutas Principales

| Ruta | Descripción | Acceso |
|------|-------------|--------|
| `/login` | Pantalla de autenticación | Pública |
| `/home_docente` | Menú principal docente | Privada |
| `/mesas` | Gestión de mesas | Privada |
| `/generar_qr` | Generación de códigos QR | Privada |
| `/historial` | Visualización de evaluaciones | Privada |
| `/criterios` | Configuración de criterios | Privada |
| `/evaluar/{mesaId}` | Evaluación anónima | Pública (Deep Link) |

### Deep Links

```
app://eval?mesa={mesaId}
```

Este esquema permite abrir directamente la pantalla de evaluación mediante códigos QR.

## Validaciones

### Login
- **Email**: Debe ser del dominio `@profesor.duoc.cl`
- **Contraseña**: Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 dígito

### Criterios
- **Nombre**: Mínimo 2 caracteres
- **Orden**: Autoasignado (máximo actual + 1)

### Evaluaciones
- **Valores**: Escala discreta 1-5 para cada criterio
- **Comentario**: Máximo 280 caracteres

## Instalación y Configuración

### Requisitos Previos

- Android Studio Ladybug | 2024.2.1 o superior
- JDK 21
- Android SDK 36
- Gradle 8.13

### Pasos de Instalación

1. Clonar el repositorio:
```bash
git clone <repository-url>
cd Aplicaciones-Moviles
```

2. Abrir el proyecto en Android Studio

3. Sincronizar dependencias:
```bash
./gradlew sync
```

4. Compilar el proyecto:
```bash
./gradlew build
```

5. Ejecutar en emulador o dispositivo:
```bash
./gradlew installDebug
```

### Configuración de API

La aplicación consume la API de mindicador.cl sin necesidad de configuración adicional:

```kotlin
// RetrofitClient.kt
private const val BASE_URL = "https://mindicador.cl/api/"
```

## Testing

### Tests Unitarios

```bash
./gradlew test
```

Archivos de test:
- `AuthValidatorsTest.kt`: Validaciones de email y contraseña
- `ExampleUnitTest.kt`: Tests básicos

### Tests Instrumentados

```bash
./gradlew connectedAndroidTest
```

Archivos:
- `ExampleInstrumentedTest.kt`: Tests de contexto

## Temas y Estilos

### Paleta de Colores Duoc UC

```kotlin
val DuocYellow   = Color(0xFFFDB913)   // Amarillo institucional
val DuocBlack    = Color(0xFF121212)   // Negro texto
val DuocGray     = Color(0xFF4E4E4E)   // Gris de apoyo
val BgCream      = Color(0xFFFFFCF2)   // Fondo cálido
val SurfaceLight = Color(0xFFFFF7E6)   // Tarjetas
```

## Flujo de Uso

### Docente

1. Iniciar sesión con credenciales institucionales
2. Crear mesas de trabajo desde el módulo "Mesas"
3. Definir criterios de evaluación en "Criterios"
4. Generar código QR para cada mesa
5. Compartir/imprimir QR para que estudiantes evalúen
6. Consultar resultados en "Historial"

### Estudiante/Evaluador

1. Escanear código QR de la mesa
2. Evaluar cada criterio (1-5)
3. Añadir comentario opcional
4. Enviar evaluación
5. Recibir confirmación visual

## Características de Seguridad

- Evaluaciones anónimas (sin identificación de usuario)
- Validación de dominio institucional para docentes
- Snapshot de criterios (preserva configuración histórica)
- Cooldown anti-spam (2 segundos entre envíos)

## Limitaciones Conocidas

- Sin integración con backend remoto (datos locales únicamente)
- Autenticación mock (no valida contra servidor real)
- QR generados son válidos solo en el dispositivo que los creó
- Historial limitado a evaluaciones del dispositivo

## Mejoras Futuras

- Sincronización con servidor central
- Autenticación OAuth2 real
- Exportación de reportes en PDF/Excel
- Dashboard con gráficos estadísticos
- Notificaciones push
- Modo offline con sincronización diferida

## Contribuciones

Este proyecto es parte del curso de Aplicaciones Móviles de Duoc UC. Para contribuir:

1. Fork del repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Licencia

Proyecto académico - Duoc UC 2025

## Contacto

Desarrollado para Duoc UC - Escuela de Gastronomía

---

Versión 1.0 - Diciembre 2025
