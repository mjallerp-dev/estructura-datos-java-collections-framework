package com.mjallerp.consola;

import com.mjallerp.excepciones.BibliotecaException;
import com.mjallerp.excepciones.DatoInvalidoException;
import com.mjallerp.modelo.EstadoSolicitud;
import com.mjallerp.modelo.Libro;
import com.mjallerp.modelo.PrioridadSolicitud;
import com.mjallerp.modelo.SolicitudPrestamo;
import com.mjallerp.modelo.TipoLector;
import com.mjallerp.pruebas.PruebasBiblioteca;
import com.mjallerp.servicio.Biblioteca;
import com.mjallerp.servicio.Catalogo;
import com.mjallerp.servicio.GestorPrestamos;
import com.mjallerp.servicio.RegistroLectores;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MenuBiblioteca {
  private final LectorConsola lector;
  private final Biblioteca biblioteca;
  private final Catalogo catalogo;
  private final RegistroLectores lectores;
  private final GestorPrestamos prestamos;

  public MenuBiblioteca(LectorConsola lector, Biblioteca biblioteca) {
    this.lector = lector;
    this.biblioteca = biblioteca;
    this.catalogo = biblioteca.getCatalogo();
    this.lectores = biblioteca.getLectores();
    this.prestamos = biblioteca.getPrestamos();
  }

  public void mostrar() {
    int opcion;
    do {
      System.out.println("\n========== SISTEMA DE BIBLIOTECA ==========");
      System.out.println("1. Registrar libro");
      System.out.println("2. Ver catálogo general");
      System.out.println("3. Buscar libro por código");
      System.out.println("4. Categorías (registrar / consultar)");
      System.out.println("5. Ver información ordenada");
      System.out.println("6. Registrar solicitud de préstamo");
      System.out.println("7. Consultar próxima solicitud a atender");
      System.out.println("8. Atender siguiente solicitud");
      System.out.println("9. Modificar libro o solicitud");
      System.out.println("10. Eliminar libro o cancelar solicitud");
      System.out.println("11. Registrar devolución");
      System.out.println("12. Consultar solicitudes");
      System.out.println("13. Lectores y autores");
      System.out.println("14. Mostrar información de las colecciones");
      System.out.println("15. Ejecutar casos de prueba");
      System.out.println("0. Salir");
      opcion = lector.leerEntero("Seleccione una opción: ");
      System.out.println();
      try {
        switch (opcion) {
          case 1: registrarLibro(); break;
          case 2: verCatalogo(); break;
          case 3: buscarLibro(); break;
          case 4: menuCategorias(); break;
          case 5: menuOrdenado(); break;
          case 6: registrarSolicitud(); break;
          case 7: consultarSiguiente(); break;
          case 8: atenderSiguiente(); break;
          case 9: menuModificar(); break;
          case 10: menuEliminar(); break;
          case 11: registrarDevolucion(); break;
          case 12: menuSolicitudes(); break;
          case 13: menuLectoresAutores(); break;
          case 14: mostrarColecciones(); break;
          case 15: PruebasBiblioteca.ejecutar(); break;
          case 0: System.out.println("Hasta luego."); break;
          default: System.out.println("Opción inválida.");
        }
      } catch (BibliotecaException e) {
        System.out.println("No se pudo completar la operación: " + e.getMessage());
      }
      if (opcion != 0) lector.pausar();
    } while (opcion != 0);
  }

  private void registrarLibro() {
    System.out.println("--- Registrar libro ---");
    String codigo = lector.leerTexto("Código del libro (ej. LIB-020): ");
    String titulo = lector.leerTexto("Título: ");
    System.out.println("Autores registrados:");
    Presentador.autores(catalogo.getAutores());
    String autor = lector.leerTexto("Código del autor: ");
    System.out.println("Categorías registradas: " + catalogo.getCategorias());
    String categoria = lector.leerTexto("Categoría: ");
    int anio = lector.leerEntero("Año de publicación: ");
    int ejemplares = lector.leerEntero("Cantidad de ejemplares (1 - " + Catalogo.EJEMPLARES_MAXIMOS + "): ");

    Libro libro = catalogo.registrarLibro(codigo, titulo, autor, categoria, anio, ejemplares);
    System.out.println("\nLibro registrado:");
    Presentador.libro(libro);
    System.out.println("\nQuedó almacenado en:");
    System.out.println("  List<Libro> libros (ArrayList)                 posición " + catalogo.getLibros().indexOf(libro));
    System.out.println("  Map<String, Libro> librosPorCodigo (HashMap)   containsKey(\"" + libro.getCodigo() + "\") = "
        + catalogo.getLibrosPorCodigo().containsKey(libro.getCodigo()));
    System.out.println("  Set<Libro> librosPorTitulo (TreeSet)           contains = " + catalogo.getLibrosPorTitulo().contains(libro));
    System.out.println("  Map<String, Set<Libro>> librosPorCategoria     [" + libro.getCategoria() + "] = "
        + catalogo.getLibrosPorCategoria().get(libro.getCategoria()).size() + " libros");
  }

  private void verCatalogo() {
    System.out.println("--- Catálogo general: List<Libro> (ArrayList) en orden de registro ---");
    Presentador.libros(catalogo.getLibros());
    System.out.println("Total: " + catalogo.getLibros().size() + " libros");
  }

  private void buscarLibro() {
    String codigo = lector.leerTexto("Código del libro a buscar: ");
    String clave = codigo.trim().toUpperCase();
    boolean existe = catalogo.existeLibro(codigo);
    System.out.println("librosPorCodigo.containsKey(\"" + clave + "\") = " + existe);
    if (!existe) {
      System.out.println("librosPorCodigo.get(\"" + clave + "\") = null -> el libro no existe en el catálogo.");
      return;
    }
    System.out.println("librosPorCodigo.get(\"" + clave + "\") encontró el libro directamente, sin recorrer la lista:");
    Presentador.libro(catalogo.buscarLibro(codigo));
  }

  private void menuCategorias() {
    System.out.println("--- Categorías: Set<String> (LinkedHashSet) ---");
    System.out.println("1. Registrar categoría");
    System.out.println("2. Consultar categorías");
    System.out.println("0. Volver");
    int opcion = lector.leerEntero("Seleccione una opción: ");
    if (opcion == 1) {
      String nombre = lector.leerTexto("Nombre de la categoría: ");
      String categoria = catalogo.registrarCategoria(nombre);
      System.out.println("categorias.add(\"" + categoria + "\") = true -> categoría registrada.");
      System.out.println("Categorías: " + catalogo.getCategorias());
    } else if (opcion == 2) {
      System.out.println("Categorías en orden de registro (" + catalogo.getCategorias().size() + "):");
      for (String categoria : catalogo.getCategorias()) {
        Set<Libro> libros = catalogo.getLibrosPorCategoria().get(categoria);
        System.out.println("  " + categoria + " - " + (libros == null ? 0 : libros.size()) + " libros");
      }
    }
  }

  private void menuOrdenado() {
    System.out.println("--- Información ordenada ---");
    System.out.println("1. Libros por título (TreeSet con Comparator)");
    System.out.println("2. Libros agrupados por categoría (TreeMap + TreeSet por código)");
    System.out.println("3. Autores por código (TreeMap)");
    System.out.println("0. Volver");
    int opcion = lector.leerEntero("Seleccione una opción: ");
    System.out.println();
    if (opcion == 1) {
      System.out.println("Criterio: Libro.POR_TITULO -> título sin distinguir mayúsculas y, si empata, código.");
      Presentador.libros(catalogo.getLibrosPorTitulo());
    } else if (opcion == 2) {
      System.out.println("Claves del TreeMap en orden alfabético; cada grupo es un TreeSet ordenado por compareTo (código).");
      for (Map.Entry<String, Set<Libro>> grupo : catalogo.getLibrosPorCategoria().entrySet()) {
        System.out.println("\n[" + grupo.getKey() + "] " + grupo.getValue().size() + " libros");
        Presentador.libros(grupo.getValue());
      }
    } else if (opcion == 3) {
      System.out.println("Autores ordenados por la clave del TreeMap (código):");
      Presentador.autores(catalogo.getAutores());
    }
  }

  private void registrarSolicitud() {
    System.out.println("--- Registrar solicitud de préstamo ---");
    System.out.println("Lectores registrados:");
    Presentador.lectores(lectores.getLectores());
    String documento = lector.leerTexto("Documento del lector: ");
    String codigoLibro = lector.leerTexto("Código del libro: ");
    PrioridadSolicitud prioridad = leerPrioridad();
    String motivo = lector.leerTexto("Motivo de la solicitud: ");

    SolicitudPrestamo solicitud = prestamos.registrarSolicitud(documento, codigoLibro, prioridad, motivo);
    System.out.println("\nSolicitud registrada:");
    Presentador.solicitud(solicitud);
    System.out.println("\nQuedó almacenada en: historial (ArrayList), solicitudesPorCodigo (HashMap),");
    System.out.println("solicitudesPendientes (PriorityQueue) y su clave en solicitudesActivas (HashSet).");
    List<SolicitudPrestamo> orden = prestamos.pendientesEnOrdenDeAtencion();
    System.out.println("Posición en el orden de atención: " + (orden.indexOf(solicitud) + 1) + " de " + orden.size());
  }

  private void consultarSiguiente() {
    Optional<SolicitudPrestamo> siguiente = prestamos.consultarSiguiente();
    if (siguiente.isEmpty()) {
      System.out.println("No hay solicitudes pendientes.");
      return;
    }
    System.out.println("solicitudesPendientes.peek() -> próxima solicitud (no se retira de la cola):");
    Presentador.solicitud(siguiente.get());
    System.out.println("\nOrden completo de atención:");
    Presentador.solicitudes(prestamos.pendientesEnOrdenDeAtencion());
  }

  private void atenderSiguiente() {
    SolicitudPrestamo solicitud = prestamos.atenderSiguiente();
    System.out.println("solicitudesPendientes.poll() retiró la solicitud de mayor prioridad:");
    Presentador.solicitud(solicitud);
    if (solicitud.getEstado().esActiva()) {
      System.out.println("Préstamo aprobado. Ejemplares disponibles de " + solicitud.getLibro().getCodigo()
          + ": " + solicitud.getLibro().getEjemplaresDisponibles());
    } else {
      System.out.println("Solicitud rechazada: no hay ejemplares disponibles de " + solicitud.getLibro().getCodigo() + ".");
    }
    System.out.println("Solicitudes pendientes: " + prestamos.cantidadPendientes());
  }

  private void menuModificar() {
    System.out.println("--- Modificar ---");
    System.out.println("1. Título de un libro");
    System.out.println("2. Categoría de un libro");
    System.out.println("3. Cantidad de ejemplares de un libro");
    System.out.println("4. Código de un libro");
    System.out.println("5. Prioridad de una solicitud pendiente");
    System.out.println("0. Volver");
    int opcion = lector.leerEntero("Seleccione una opción: ");
    if (opcion < 1 || opcion > 5) return;

    if (opcion == 5) {
      String codigo = lector.leerTexto("Código de la solicitud: ");
      SolicitudPrestamo solicitud = prestamos.buscarSolicitud(codigo);
      System.out.println("Prioridad actual: " + solicitud.getPrioridad());
      PrioridadSolicitud nueva = leerPrioridad();
      prestamos.cambiarPrioridad(codigo, nueva);
      System.out.println("Se retiró de la PriorityQueue, se cambió la prioridad y se volvió a insertar.");
      System.out.println("Nuevo orden de atención:");
      Presentador.solicitudes(prestamos.pendientesEnOrdenDeAtencion());
      return;
    }

    String codigo = lector.leerTexto("Código del libro: ");
    Libro libro = catalogo.buscarLibro(codigo);
    Presentador.libro(libro);
    switch (opcion) {
      case 1:
        catalogo.modificarTitulo(codigo, lector.leerTexto("Nuevo título: "));
        System.out.println("Título actualizado; el libro se reubicó en librosPorTitulo.");
        break;
      case 2:
        System.out.println("Categorías registradas: " + catalogo.getCategorias());
        catalogo.modificarCategoria(codigo, lector.leerTexto("Nueva categoría: "));
        System.out.println("Categoría actualizada; el libro se movió de grupo en librosPorCategoria.");
        break;
      case 3:
        catalogo.modificarEjemplares(codigo, lector.leerEntero("Nueva cantidad total de ejemplares: "));
        System.out.println("Ejemplares actualizados.");
        break;
      case 4:
        String anterior = biblioteca.cambiarCodigoLibro(codigo, lector.leerTexto("Nuevo código: "));
        System.out.println("Código cambiado de " + anterior + " a " + libro.getCodigo() + ".");
        System.out.println("librosPorCodigo.containsKey(\"" + anterior + "\") = "
            + catalogo.getLibrosPorCodigo().containsKey(anterior));
        System.out.println("librosPorCodigo.containsKey(\"" + libro.getCodigo() + "\") = "
            + catalogo.getLibrosPorCodigo().containsKey(libro.getCodigo()));
        break;
      default:
        break;
    }
    Presentador.libro(libro);
    mostrarResultadoConsistencia();
  }

  private void menuEliminar() {
    System.out.println("--- Eliminar o cancelar ---");
    System.out.println("1. Eliminar libro del catálogo");
    System.out.println("2. Cancelar solicitud pendiente");
    System.out.println("0. Volver");
    int opcion = lector.leerEntero("Seleccione una opción: ");
    if (opcion == 1) {
      String codigo = lector.leerTexto("Código del libro a eliminar: ");
      Libro libro = catalogo.buscarLibro(codigo);
      Presentador.libro(libro);
      biblioteca.validarEliminacion(libro);
      if (!lector.confirmar("¿Confirma la eliminación?")) {
        System.out.println("Eliminación cancelada.");
        return;
      }
      biblioteca.eliminarLibro(codigo);
      System.out.println("Libro eliminado de: libros, librosPorCodigo, librosPorTitulo y librosPorCategoria.");
      System.out.println("librosPorCodigo.containsKey(\"" + libro.getCodigo() + "\") = "
          + catalogo.getLibrosPorCodigo().containsKey(libro.getCodigo()));
      System.out.println("libros.contains(libro) = " + catalogo.getLibros().contains(libro));
      mostrarResultadoConsistencia();
    } else if (opcion == 2) {
      String codigo = lector.leerTexto("Código de la solicitud a cancelar: ");
      SolicitudPrestamo solicitud = prestamos.cancelarSolicitud(codigo);
      System.out.println("Solicitud " + solicitud.getCodigo() + " cancelada: se retiró de la PriorityQueue y de solicitudesActivas.");
      System.out.println("Se conserva en el historial con estado " + solicitud.getEstado() + ".");
      mostrarResultadoConsistencia();
    }
  }

  private void registrarDevolucion() {
    System.out.println("Préstamos activos:");
    boolean hay = false;
    for (SolicitudPrestamo s : prestamos.getHistorial()) {
      if (s.getEstado() == EstadoSolicitud.PRESTADO) {
        System.out.println("  " + s);
        hay = true;
      }
    }
    if (!hay) {
      System.out.println("  (no hay préstamos activos)");
      return;
    }
    String codigo = lector.leerTexto("Código de la solicitud a devolver: ");
    SolicitudPrestamo solicitud = prestamos.registrarDevolucion(codigo);
    System.out.println("Devolución registrada. " + solicitud.getLibro().getCodigo() + " tiene ahora "
        + solicitud.getLibro().getEjemplaresDisponibles() + " ejemplares disponibles.");
  }

  private void menuSolicitudes() {
    System.out.println("--- Solicitudes de préstamo ---");
    System.out.println("1. Pendientes en orden de atención (PriorityQueue)");
    System.out.println("2. Historial completo en orden de registro (ArrayList)");
    System.out.println("3. Buscar solicitud por código (HashMap)");
    System.out.println("0. Volver");
    int opcion = lector.leerEntero("Seleccione una opción: ");
    System.out.println();
    if (opcion == 1) {
      System.out.println("Criterio: prioridad (Urgente > Especial > Normal) y, si empata, número de registro.");
      Presentador.solicitudes(prestamos.pendientesEnOrdenDeAtencion());
    } else if (opcion == 2) {
      Presentador.solicitudes(prestamos.getHistorial());
    } else if (opcion == 3) {
      Presentador.solicitud(prestamos.buscarSolicitud(lector.leerTexto("Código de la solicitud: ")));
    }
  }

  private void menuLectoresAutores() {
    System.out.println("--- Lectores y autores ---");
    System.out.println("1. Registrar lector");
    System.out.println("2. Consultar lectores (LinkedHashMap, orden de registro)");
    System.out.println("3. Buscar lector por documento");
    System.out.println("4. Registrar autor");
    System.out.println("5. Consultar autores (TreeMap, orden por código)");
    System.out.println("0. Volver");
    int opcion = lector.leerEntero("Seleccione una opción: ");
    System.out.println();
    switch (opcion) {
      case 1:
        String documento = lector.leerTexto("Documento: ");
        String nombre = lector.leerTexto("Nombre completo: ");
        TipoLector tipo = leerTipoLector();
        System.out.println("Lector registrado: " + lectores.registrar(documento, nombre, tipo));
        break;
      case 2:
        Presentador.lectores(lectores.getLectores());
        break;
      case 3:
        System.out.println(lectores.buscar(lector.leerTexto("Documento: ")));
        break;
      case 4:
        String codigo = lector.leerTexto("Código del autor (ej. AUT-09): ");
        String nombreAutor = lector.leerTexto("Nombre: ");
        String nacionalidad = lector.leerTexto("Nacionalidad: ");
        System.out.println("Autor registrado: " + catalogo.registrarAutor(codigo, nombreAutor, nacionalidad));
        break;
      case 5:
        Presentador.autores(catalogo.getAutores());
        break;
      default:
        break;
    }
  }

  private void mostrarColecciones() {
    System.out.println("--- Colecciones del sistema ---");
    System.out.printf("%-64s %s%n", "Colección", "Elementos");
    System.out.printf("%-64s %d%n", "List<Libro> libros = ArrayList", catalogo.getLibros().size());
    System.out.printf("%-64s %d%n", "Map<String, Libro> librosPorCodigo = HashMap", catalogo.getLibrosPorCodigo().size());
    System.out.printf("%-64s %d%n", "Set<String> categorias = LinkedHashSet", catalogo.getCategorias().size());
    System.out.printf("%-64s %d%n", "Set<Libro> librosPorTitulo = TreeSet(POR_TITULO)", catalogo.getLibrosPorTitulo().size());
    System.out.printf("%-64s %d claves%n", "Map<String, Set<Libro>> librosPorCategoria = TreeMap", catalogo.getLibrosPorCategoria().size());
    System.out.printf("%-64s %d%n", "Map<String, Autor> autoresPorCodigo = TreeMap", catalogo.cantidadAutores());
    System.out.printf("%-64s %d%n", "Map<String, Lector> lectoresPorDocumento = LinkedHashMap", lectores.cantidad());
    System.out.printf("%-64s %d%n", "List<SolicitudPrestamo> historial = ArrayList", prestamos.getHistorial().size());
    System.out.printf("%-64s %d%n", "Map<String, SolicitudPrestamo> solicitudesPorCodigo = HashMap", prestamos.getSolicitudesPorCodigo().size());
    System.out.printf("%-64s %d%n", "Queue<SolicitudPrestamo> solicitudesPendientes = PriorityQueue", prestamos.cantidadPendientes());
    System.out.printf("%-64s %d%n", "Set<String> solicitudesActivas = HashSet", prestamos.getSolicitudesActivas().size());

    System.out.println("\nCódigos en libros (orden de registro):        " + codigos(catalogo.getLibros()));
    System.out.println("Claves de librosPorCodigo (HashMap, sin orden): " + catalogo.getLibrosPorCodigo().keySet());
    System.out.println("Categorías (LinkedHashSet, orden de registro): " + catalogo.getCategorias());
    System.out.println("Claves de librosPorCategoria (TreeMap, alfabético): " + catalogo.getLibrosPorCategoria().keySet());
    System.out.println("solicitudesActivas (documento|libro): " + prestamos.getSolicitudesActivas());

    System.out.println("\nPriorityQueue - orden interno del arreglo (heap), NO es el orden de atención:");
    System.out.println("  " + codigosSolicitudes(prestamos.pendientesOrdenInterno()));
    System.out.println("PriorityQueue - orden real de atención (sacando con poll sobre una copia):");
    System.out.println("  " + codigosSolicitudes(prestamos.pendientesEnOrdenDeAtencion()));

    System.out.println();
    mostrarResultadoConsistencia();
  }

  private void mostrarResultadoConsistencia() {
    List<String> problemas = biblioteca.verificarConsistencia();
    if (problemas.isEmpty()) {
      System.out.println("Verificación de consistencia: todas las colecciones están sincronizadas.");
    } else {
      System.out.println("Verificación de consistencia: se encontraron problemas:");
      for (String problema : problemas) System.out.println("  - " + problema);
    }
  }

  private PrioridadSolicitud leerPrioridad() {
    PrioridadSolicitud[] valores = PrioridadSolicitud.values();
    for (int i = 0; i < valores.length; i++) {
      System.out.println("  " + (i + 1) + ". " + valores[i] + " - " + valores[i].getDescripcion());
    }
    int opcion = lector.leerEntero("Prioridad: ");
    if (opcion < 1 || opcion > valores.length) {
      throw new DatoInvalidoException("La prioridad debe ser un número entre 1 y " + valores.length);
    }
    return valores[opcion - 1];
  }

  private TipoLector leerTipoLector() {
    TipoLector[] valores = TipoLector.values();
    for (int i = 0; i < valores.length; i++) System.out.println("  " + (i + 1) + ". " + valores[i]);
    int opcion = lector.leerEntero("Tipo de lector: ");
    if (opcion < 1 || opcion > valores.length) {
      throw new DatoInvalidoException("El tipo de lector debe ser un número entre 1 y " + valores.length);
    }
    return valores[opcion - 1];
  }

  private static String codigos(List<Libro> libros) {
    StringBuilder sb = new StringBuilder("[");
    for (Libro libro : libros) {
      if (sb.length() > 1) sb.append(", ");
      sb.append(libro.getCodigo());
    }
    return sb.append("]").toString();
  }

  private static String codigosSolicitudes(List<SolicitudPrestamo> solicitudes) {
    StringBuilder sb = new StringBuilder("[");
    for (SolicitudPrestamo s : solicitudes) {
      if (sb.length() > 1) sb.append(", ");
      sb.append(s.getCodigo()).append(" ").append(s.getPrioridad());
    }
    return sb.append("]").toString();
  }
}
