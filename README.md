# Sistema de biblioteca - Java Collections Framework

**Actividad:** Estructuras de datos - Unidad III - Protocolo individual: *Diseño de un sistema de gestión
utilizando Java Collections Framework*. Caso asignado: **10. Sistema de biblioteca**.

## Descripción del problema

Aplicación de consola para gestionar los libros, autores, categorías, lectores y solicitudes de préstamo de una
biblioteca. Cada libro tiene un código único y cada solicitud de préstamo un código propio (`PRE-001`, `PRE-002`...).
El sistema:

- mantiene el catálogo general de libros en orden de registro;
- evita categorías duplicadas;
- busca libros, solicitudes, lectores y autores por su clave;
- mantiene libros ordenados por título y agrupados por categoría;
- atiende las solicitudes de préstamo según su prioridad (Urgente, Especial, Normal) y no por orden de llegada;
- permite modificar libros (incluido su código) y prioridades, eliminar libros, cancelar solicitudes y registrar
  devoluciones manteniendo sincronizadas todas las colecciones.

## Versión de Java

Java 17 (proyecto Maven). Probado con JDK 17 o superior.

## Compilar y ejecutar

Desde IntelliJ IDEA: abrir el proyecto y ejecutar `com.mjallerp.Main`.

Con Maven:

```bash
mvn compile
java -cp target/classes com.mjallerp.Main
```

Sin Maven:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out com.mjallerp.Main
```

La aplicación inicia con datos de prueba: 18 libros, 8 autores, 7 categorías, 6 lectores y 8 solicitudes (2 ya
prestadas y 6 pendientes registradas en un orden distinto a su prioridad). La opción 15 del menú ejecuta 34 casos
de prueba automáticos sobre una copia nueva de esos datos.

## Colecciones principales

| Necesidad del sistema | Interfaz | Implementación | Clase |
|-----------------------|----------|----------------|-------|
| Catálogo general de libros en orden de registro | `List<Libro>` | `ArrayList` | `Catalogo` |
| Buscar un libro por su código | `Map<String, Libro>` | `HashMap` | `Catalogo` |
| Evitar categorías duplicadas | `Set<String>` | `LinkedHashSet` | `Catalogo` |
| Libros ordenados por título | `Set<Libro>` | `TreeSet` con `Comparator` | `Catalogo` |
| Libros agrupados por categoría | `Map<String, Set<Libro>>` | `TreeMap` + `TreeSet` (`Comparable`) | `Catalogo` |
| Autores por código, ordenados | `Map<String, Autor>` | `TreeMap` | `Catalogo` |
| Lectores por documento en orden de registro | `Map<String, Lector>` | `LinkedHashMap` | `RegistroLectores` |
| Historial de solicitudes | `List<SolicitudPrestamo>` | `ArrayList` | `GestorPrestamos` |
| Buscar solicitud por código | `Map<String, SolicitudPrestamo>` | `HashMap` | `GestorPrestamos` |
| Atender solicitudes por prioridad | `Queue<SolicitudPrestamo>` | `PriorityQueue` con `Comparator` | `GestorPrestamos` |
| Evitar dos solicitudes activas del mismo lector y libro | `Set<String>` | `HashSet` | `GestorPrestamos` |

No se implementó manualmente ninguna estructura de datos: todas pertenecen al Java Collections Framework.

## Estructura del proyecto

```
src/main/java/com/mjallerp
├── Main.java                 Punto de entrada
├── modelo                    Libro, Autor, Lector, SolicitudPrestamo y enums
├── excepciones               BibliotecaException y sus subclases
├── servicio                  Catalogo, RegistroLectores, GestorPrestamos, Biblioteca, Validador
├── datos                     DatosIniciales (datos de prueba)
├── consola                   MenuBiblioteca, LectorConsola, Presentador
└── pruebas                   PruebasBiblioteca (34 casos)
```

## Documentación

El informe en PDF y la guía de sustentación están en la carpeta [`docs`](docs).
