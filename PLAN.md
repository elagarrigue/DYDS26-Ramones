# Plan de Implementación: Integración TMDB + OMDB

## Contexto

Se reemplaza la búsqueda de detalle de película por id (TMDB) por búsqueda por título.
Ambos servicios (TMDB y OMDB) soportan búsqueda por título. El id de TMDB no tiene
correlación en OMDB, por lo que el título es el identificador común entre ambas APIs.

### Lógica de combinación del Broker

| TMDB | OMDB | Resultado |
|------|------|-----------|
| ✅ | ✅ | `Movie` con campos combinados de ambos servicios |
| ✅ | ❌ | `Movie` de TMDB con overview prefijado con `"TMDB: "` |
| ❌ | ✅ | `Movie` de OMDB con overview prefijado con `"OMDB: "` |
| ❌ | ❌ | `null` |

---

## Etapa 1 — Reorganizar data/external y renombrar

**Objetivo:** Mover los archivos de TMDB a su propio subdirectorio y renombrar la implementación.

### Tareas:
1. Crear directorio `data/external/tmdb/`
2. Mover `RemoteMovie.kt`, `RemoteResult.kt` y `RemoteMoviesDataSourceImpl.kt` a `data/external/tmdb/`
3. Renombrar `RemoteMoviesDataSourceImpl` → `TMDBMoviesExternalSource`
4. Actualizar packages e imports en todos los archivos afectados

---

## Etapa 2 — Separar la interfaz (Interface Segregation)

**Objetivo:** `RemoteMoviesDataSource` mezcla responsabilidades de lista y detalle. OMDB solo
se usará para detalle, no para listas. Separar en dos interfaces.

### Tareas:
1. Reemplazar `RemoteMoviesDataSource` por dos interfaces:
    - `MoviesListExternalSource` → `getPopularMovies(): List<RemoteMovie>`
    - `MovieDetailExternalSource` → `getMovieDetail(title: String): Movie?`
2. `TMDBMoviesExternalSource` implementa `MoviesListExternalSource`
3. Actualizar `MoviesRepositoryImpl` para depender de las interfaces correctas
4. Actualizar `MoviesDependencyInjector`
5. Actualizar tests afectados

---

## Etapa 3 — Reemplazar getMovieDetails(id) por búsqueda por título en TMDB

**Objetivo:** Cambiar el mecanismo de obtención de detalle de película de id a título.

### Tareas:
1. Cambiar firma en `MoviesRepository` (domain): `getMovieDetails(id: Int)` → `getMovieDetail(title: String)`
2. Actualizar `GetMovieDetailUseCase` y `GetMovieDetailUseCaseImpl`
3. Actualizar `DetailViewModel` para pasar título en vez de id
4. Actualizar `DetailScreen` y `Navigation` para propagar el título
5. Actualizar `TMDBMoviesExternalSource`: implementar búsqueda por título via TMDB search API
6. Actualizar tests afectados (`GetMovieDetailUseCaseImplTest`, `MoviesRepositoryImplTest`, `DetailViewModelTest`)

---

## Etapa 4 — Agregar servicio OMDB

**Objetivo:** Incorporar OMDB como fuente de datos para el detalle de películas.

### Tareas:
1. Crear `data/external/omdb/OMDBMovie.kt` — modelo de respuesta de OMDB (`@Serializable`)
2. Crear `data/external/omdb/OMDBMoviesExternalSource.kt` — implementa `MovieDetailExternalSource`, busca por título en OMDB API
3. Agregar `HttpClient` configurado para OMDB en `MoviesDependencyInjector`

---

## Etapa 5 — Agregar el Broker

**Objetivo:** Implementar la lógica de combinación de resultados de TMDB y OMDB.

### Tareas:
1. Crear `data/external/MovieDetailExternalSourceBroker.kt` que implementa `MovieDetailExternalSource`
2. Implementar la lógica de combinación según la tabla del contexto
3. Registrar el Broker en `MoviesDependencyInjector` reemplazando el uso directo de `TMDBMoviesExternalSource` para detalle

---

## Etapa 6 — Tests del Broker

**Objetivo:** Cubrir todos los casos de combinación del Broker con tests unitarios.

### Casos a cubrir:
1. Ambos servicios retornan resultado → `Movie` combinado
2. Solo TMDB retorna resultado → overview con `"TMDB: "`
3. Solo OMDB retorna resultado → overview con `"OMDB: "`
4. Ninguno retorna resultado → `null`

