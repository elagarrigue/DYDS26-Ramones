# Plan de Reestructuración del Proyecto DYDS26-Ramones

## Objetivo
Restructurar el proyecto para seguir los principios de arquitectura limpia (Clean Architecture) con capas bien definidas, respetando SOLID y Clean Code. Separar las responsabilidades en: Presentation, Domain, Data e Inyección de Dependencias (DI).

## Estructura de Directorios Propuesta

```
desktopMain/
└── kotlin/
    └── edu/dyds/movies/
        ├── data/
        │   ├── external/          # Fuentes de datos externas (API, servicios)
        │   ├── local/             # Fuentes de datos locales (BD, caché)
        │   └── MoviesRepositoryImpl.kt
        ├── di/                    # Inyección de Dependencias
        │   └── MoviesDependencyInjector.kt
        ├── domain/
        │   ├── entity/            # Entidades de dominio
        │   │   └── Movie.kt
        │   ├── repository/        # Interfaces de repositorios
        │   └── usecase/           # Casos de uso
        ├── presentation/
        │   ├── home/              # Pantalla de inicio
        │   │   └── HomeScreen.kt
        │   ├── detail/            # Pantalla de detalle
        │   │   └── DetailScreen.kt
        │   ├── utils/             # Componentes comunes
        │   │   ├── App.kt
        │   │   ├── Navigation.kt
        │   │   └── CommonComposables.kt
        │   └── MoviesViewModel.kt
        └── main.kt
```

## Etapas de Implementación

### [COMPLETADA] Etapa 1: Crear Estructura de Directorios y Mover Archivos
**Objetivo:** Organizar el proyecto en capas sin modificar código.

#### Tareas:
1. Crear directorios base:
   - `/domain/entity/`
   - `/domain/repository/`
   - `/domain/usecase/`
   - `/data/external/`
   - `/data/local/`
   - `/di/`
   - `/presentation/home/`
   - `/presentation/detail/`
   - `/presentation/utils/`

2. Mover archivos sin modificar:
   - `Movie.kt` → `/domain/entity/Movie.kt`
   - `MoviesDependencyInjector.kt` → `/di/MoviesDependencyInjector.kt`
   - `HomeScreen.kt` → `/presentation/home/HomeScreen.kt`
   - `DetailScreen.kt` → `/presentation/detail/DetailScreen.kt`
   - `App.kt` → `/presentation/utils/App.kt`
   - `Navigation.kt` → `/presentation/utils/Navigation.kt`
   - `CommonComposables.kt` → `/presentation/utils/CommonComposables.kt`
   - `MoviesViewModel.kt` → `/presentation/MoviesViewModel.kt`
   - `main.kt` → `/main.kt` (mantener en raíz)

3. Crear archivo placeholder:
   - `/data/MoviesRepositoryImpl.kt`

### [COMPLETADA] Etapa 2: Separar Responsabilidades en Domain
**Objetivo:** Definir las interfaces y entidades de dominio.

#### Tareas:
1. En `/domain/entity/Movie.kt`:
   - Mantener `Movie` (entidad de dominio)
   - Mantener `QualifiedMovie` (entidad de dominio)
   - Eliminar modelos remotos (`RemoteMovie`, `RemoteResult`)

2. En `/domain/repository/MoviesRepository.kt`:
   - Crear interfaz `MoviesRepository` con métodos:
     - `suspend fun getPopularMovies(): List<QualifiedMovie>`
     - `suspend fun getMovieDetails(id: Int): Movie?`

3. En `/domain/usecase/`:
   - `GetPopularMoviesUseCase.kt`: Orquestar la obtención y filtrado de películas
   - `GetMovieDetailUseCase.kt`: Orquestar la obtención de detalles de una película

### [COMPLETADA] Etapa 3: Implementar la Capa Data
**Objetivo:** Abstraer la lógica de acceso a datos.

#### Tareas:
1. En `/data/external/`:
   - `RemoteMoviesDataSource.kt`: Interface para acceso a datos remotos
   - `RemoteMoviesDataSourceImpl.kt`: Implementación con Ktor
   - Mover modelos remotos (`RemoteMovie`, `RemoteResult`)

2. En `/data/local/`:
   - `LocalMoviesCache.kt`: Interface para caché local
   - `LocalMoviesCacheImpl.kt`: Implementación de caché
   - Mover lógica de caché del ViewModel

3. En `/data/MoviesRepositoryImpl.kt`:
   - Implementar `MoviesRepository` del dominio
   - Orquestar llamadas a `RemoteMoviesDataSource` y `LocalMoviesCache`
   - Manejar lógica de sorting y mapping

### [COMPLETADA] Etapa 4: Refactorizar Presentation Layer
**Objetivo:** ViewModels deben usar casos de uso, no datos directamente.

#### Tareas:
1. En `/presentation/MoviesViewModel.kt`:
   - Refactorizar para depender de casos de uso
   - Eliminar acceso directo a API o base de datos
   - Mantener la lógica de estados UI

2. En `/presentation/home/HomeScreen.kt`:
   - Actualizar imports si es necesario
   - Mantener estructura de composables

3. En `/presentation/detail/DetailScreen.kt`:
   - Actualizar imports si es necesario
   - Mantener estructura de composables

4. En `/presentation/utils/`:
   - `App.kt`: Punto de entrada de la presentación
   - `Navigation.kt`: Enrutamiento entre pantallas
   - `CommonComposables.kt`: Componentes reutilizables

### Etapa 5: Actualizar Inyección de Dependencias
**Objetivo:** Proporcionar instancias correctas a todas las capas.

#### Tareas:
1. En `/di/MoviesDependencyInjector.kt`:
   - Instanciar `HttpClient` (para data)
   - Instanciar `RemoteMoviesDataSourceImpl`
   - Instanciar `LocalMoviesCacheImpl`
   - Instanciar `MoviesRepositoryImpl`
   - Instanciar casos de uso (`GetPopularMoviesUseCase`, `GetMovieDetailUseCase`)
   - Proporcionar `MoviesViewModel` con casos de uso

### Etapa 6: Testing y Validación
**Objetivo:** Asegurar que la aplicación siga funcionando correctamente.

#### Tareas:
1. Compilar el proyecto
2. Ejecutar tests
3. Verificar que la navegación funciona
4. Verificar que se cargan las películas
5. Verificar que se muestran los detalles de películas
6. Validar que los archivos compilados tengan la estructura correcta

## Principios SOLID Aplicados

- **S**ingle Responsibility: Cada capa tiene una responsabilidad clara
  - Domain: Lógica de negocio
  - Data: Acceso a datos
  - Presentation: UI e interacción

- **O**pen/Closed: Abierto para extensión (nuevas fuentes de datos), cerrado para modificación

- **L**iskov Substitution: Las implementaciones de repositorios y data sources son intercambiables

- **I**nterface Segregation: Interfaces específicas por responsabilidad (MoviesRepository, RemoteDataSource, LocalCache)

- **D**ependency Inversion: Presentation depende de Domain, Data depende de Domain, no viceversa

## Dependencias

- Presentation → Domain (depende)
- Domain → (nada)
- Data → Domain (depende para interfaces)
- DI → Todas (proporciona instancias)

**Prohibido:**
- Presentation → Data (nunca debe depender directamente)
- Domain → Data (nunca debe depender directamente)
- Domain → Presentation (nunca debe depender)
