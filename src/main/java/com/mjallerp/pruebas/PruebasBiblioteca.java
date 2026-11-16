package com.mjallerp.pruebas;

import com.mjallerp.datos.DatosIniciales;
import com.mjallerp.excepciones.BibliotecaException;
import com.mjallerp.excepciones.DatoInvalidoException;
import com.mjallerp.excepciones.OperacionNoPermitidaException;
import com.mjallerp.excepciones.RegistroDuplicadoException;
import com.mjallerp.excepciones.RegistroNoEncontradoException;
import com.mjallerp.modelo.EstadoSolicitud;
import com.mjallerp.modelo.Libro;
import com.mjallerp.modelo.PrioridadSolicitud;
import com.mjallerp.modelo.SolicitudPrestamo;
import com.mjallerp.servicio.Biblioteca;
import com.mjallerp.servicio.Catalogo;
import com.mjallerp.servicio.GestorPrestamos;

import java.util.ArrayList;
import java.util.List;

public final class PruebasBiblioteca {
  private static int exitosas;
  private static int fallidas;

  private PruebasBiblioteca() {}

  public static void ejecutar() {
    exitosas = 0;
    fallidas = 0;
    System.out.println("Las pruebas usan una biblioteca nueva con los datos iniciales; no modifican la que está en uso.\n");

    Biblioteca biblioteca = DatosIniciales.crearBiblioteca();
    Catalogo catalogo = biblioteca.getCatalogo();
    GestorPrestamos prestamos = biblioteca.getPrestamos();

    verificar("Datos iniciales: 18 libros y colecciones consistentes",
        catalogo.getLibros().size() == 18 && biblioteca.verificarConsistencia().isEmpty(),
        catalogo.getLibros().size() + " libros");

    Libro nuevo = catalogo.registrarLibro("LIB-020", "Patrones de diseño", "AUT-03", "programación", 1994, 2);
    verificar("Registro válido queda en lista, mapa, TreeSet y TreeMap",
        catalogo.getLibros().contains(nuevo) && catalogo.getLibrosPorCodigo().get("LIB-020") == nuevo
            && catalogo.getLibrosPorTitulo().contains(nuevo)
            && catalogo.getLibrosPorCategoria().get("Programación").contains(nuevo),
        "categoría normalizada: " + nuevo.getCategoria());

    esperarExcepcion("Código de libro duplicado (LIB-001)", RegistroDuplicadoException.class,
        () -> catalogo.registrarLibro("lib-001", "Otro libro", "AUT-04", "Programación", 2020, 1));
    verificar("El intento duplicado no cambió el tamaño de la lista", catalogo.getLibros().size() == 19,
        catalogo.getLibros().size() + " libros");

    int categoriasAntes = catalogo.getCategorias().size();
    esperarExcepcion("Categoría duplicada (\"  novela \")", RegistroDuplicadoException.class,
        () -> catalogo.registrarCategoria("  novela "));
    verificar("El Set de categorías no cambió", catalogo.getCategorias().size() == categoriasAntes,
        catalogo.getCategorias().size() + " categorías");
    catalogo.registrarCategoria("Poesía");
    verificar("Categoría nueva (Poesía) se agrega al Set", catalogo.existeCategoria("poesía"),
        catalogo.getCategorias().toString());

    verificar("Búsqueda existente por Map (LIB-003)", catalogo.buscarLibro("lib-003").getTitulo().equals("Código limpio"),
        catalogo.buscarLibro("LIB-003").getTitulo());
    verificar("containsKey de un código inexistente (LIB-999)", !catalogo.existeLibro("LIB-999"), "false");
    esperarExcepcion("Búsqueda inexistente (LIB-999)", RegistroNoEncontradoException.class,
        () -> catalogo.buscarLibro("LIB-999"));
    esperarExcepcion("Referencia a autor inexistente (AUT-99)", RegistroNoEncontradoException.class,
        () -> catalogo.registrarLibro("LIB-021", "Libro sin autor", "AUT-99", "Novela", 2000, 1));
    esperarExcepcion("Categoría no registrada (Arte)", RegistroNoEncontradoException.class,
        () -> catalogo.registrarLibro("LIB-021", "Libro de arte", "AUT-01", "Arte", 2000, 1));
    esperarExcepcion("Ejemplares fuera de rango (0)", DatoInvalidoException.class,
        () -> catalogo.registrarLibro("LIB-021", "Libro", "AUT-01", "Novela", 2000, 0));
    esperarExcepcion("Año fuera de rango (3000)", DatoInvalidoException.class,
        () -> catalogo.registrarLibro("LIB-021", "Libro", "AUT-01", "Novela", 3000, 1));
    esperarExcepcion("Código vacío", DatoInvalidoException.class,
        () -> catalogo.registrarLibro("   ", "Libro", "AUT-01", "Novela", 2000, 1));

    Libro primero = catalogo.getLibrosPorTitulo().iterator().next();
    verificar("TreeSet por título: el primero es \"21 lecciones para el siglo XXI\"",
        primero.getCodigo().equals("LIB-017"), primero.toString());
    int fundaciones = 0;
    for (Libro libro : catalogo.getLibrosPorTitulo()) if (libro.getTitulo().equals("Fundación")) fundaciones++;
    verificar("Dos libros con el mismo título no se pierden en el TreeSet (desempate por código)",
        fundaciones == 2, fundaciones + " libros \"Fundación\"");
    verificar("TreeMap de categorías en orden alfabético (primera: Ciencia)",
        catalogo.getLibrosPorCategoria().keySet().iterator().next().equals("Ciencia"),
        catalogo.getLibrosPorCategoria().keySet().toString());

    verificar("PriorityQueue: peek devuelve la urgente más antigua (PRE-005)",
        prestamos.consultarSiguiente().get().getCodigo().equals("PRE-005"), codigos(prestamos.pendientesEnOrdenDeAtencion()));
    verificar("Orden de atención por prioridad y no por llegada",
        codigos(prestamos.pendientesEnOrdenDeAtencion()).equals("[PRE-005, PRE-008, PRE-006, PRE-003, PRE-004, PRE-007]"),
        codigos(prestamos.pendientesEnOrdenDeAtencion()));

    prestamos.cambiarPrioridad("PRE-007", PrioridadSolicitud.URGENTE);
    verificar("Cambiar PRE-007 a Urgente la reubica (desempate por número de registro)",
        codigos(prestamos.pendientesEnOrdenDeAtencion()).equals("[PRE-005, PRE-007, PRE-008, PRE-006, PRE-003, PRE-004]"),
        codigos(prestamos.pendientesEnOrdenDeAtencion()));

    esperarExcepcion("Solicitud duplicada del mismo lector y libro (Ana, LIB-010)", RegistroDuplicadoException.class,
        () -> prestamos.registrarSolicitud("1001234567", "LIB-010", PrioridadSolicitud.NORMAL, "Otra vez"));
    esperarExcepcion("Solicitud con lector inexistente", RegistroNoEncontradoException.class,
        () -> prestamos.registrarSolicitud("9999999", "LIB-010", PrioridadSolicitud.NORMAL, "Prueba"));

    SolicitudPrestamo atendida = prestamos.atenderSiguiente();
    verificar("Atender: sale PRE-005 y se presta el único ejemplar de LIB-005",
        atendida.getCodigo().equals("PRE-005") && atendida.getEstado() == EstadoSolicitud.PRESTADO
            && atendida.getLibro().getEjemplaresDisponibles() == 0,
        atendida.toString());

    String anterior = biblioteca.cambiarCodigoLibro("LIB-003", "LIB-030");
    Libro renombrado = catalogo.buscarLibro("LIB-030");
    verificar("Cambio de código: Map con la clave nueva y sin la anterior",
        !catalogo.existeLibro(anterior) && catalogo.getLibros().contains(renombrado)
            && catalogo.getLibrosPorTitulo().contains(renombrado) && biblioteca.verificarConsistencia().isEmpty(),
        anterior + " -> " + renombrado.getCodigo());
    esperarExcepcion("La solicitud activa sigue protegida con el código nuevo (Carlos, LIB-030)",
        RegistroDuplicadoException.class,
        () -> prestamos.registrarSolicitud("1002345678", "LIB-030", PrioridadSolicitud.NORMAL, "Duplicado"));
    esperarExcepcion("No se elimina un libro con solicitud pendiente (LIB-030)", OperacionNoPermitidaException.class,
        () -> biblioteca.eliminarLibro("LIB-030"));

    Libro eliminado = biblioteca.eliminarLibro("LIB-016");
    verificar("Eliminar LIB-016 lo retira de todas las colecciones",
        !catalogo.existeLibro("LIB-016") && !catalogo.getLibros().contains(eliminado)
            && !catalogo.getLibrosPorTitulo().contains(eliminado)
            && !catalogo.getLibrosPorCategoria().get("Historia").contains(eliminado),
        catalogo.getLibros().size() + " libros");

    SolicitudPrestamo devuelta = prestamos.registrarDevolucion("PRE-001");
    verificar("Devolución de PRE-001 libera el ejemplar de LIB-002",
        devuelta.getEstado() == EstadoSolicitud.DEVUELTO && devuelta.getLibro().getEjemplaresDisponibles() == 1,
        devuelta.toString());
    esperarExcepcion("Devolver dos veces el mismo préstamo", OperacionNoPermitidaException.class,
        () -> prestamos.registrarDevolucion("PRE-001"));

    SolicitudPrestamo cancelada = prestamos.cancelarSolicitud("PRE-003");
    verificar("Cancelar PRE-003 la retira de la cola de pendientes",
        cancelada.getEstado() == EstadoSolicitud.CANCELADA
            && !codigos(prestamos.pendientesEnOrdenDeAtencion()).contains("PRE-003"),
        codigos(prestamos.pendientesEnOrdenDeAtencion()));

    while (prestamos.consultarSiguiente().isPresent()) prestamos.atenderSiguiente();
    verificar("PRE-004 se rechaza porque LIB-005 no tiene ejemplares",
        prestamos.buscarSolicitud("PRE-004").getEstado() == EstadoSolicitud.RECHAZADA,
        prestamos.buscarSolicitud("PRE-004").toString());
    esperarExcepcion("Atender con la cola vacía", OperacionNoPermitidaException.class, prestamos::atenderSiguiente);

    List<String> problemas = biblioteca.verificarConsistencia();
    verificar("Consistencia final entre todas las colecciones", problemas.isEmpty(),
        problemas.isEmpty() ? "sin problemas" : problemas.toString());

    System.out.println("\nResultado: " + exitosas + " exitosas, " + fallidas + " fallidas");
  }

  private static void verificar(String nombre, boolean ok, String detalle) {
    if (ok) exitosas++; else fallidas++;
    System.out.printf("[%s] %s -> %s%n", ok ? "OK" : "FALLO", nombre, detalle);
  }

  private static void esperarExcepcion(String nombre, Class<? extends BibliotecaException> esperada, Runnable accion) {
    try {
      accion.run();
      verificar(nombre, false, "no lanzó " + esperada.getSimpleName());
    } catch (BibliotecaException e) {
      verificar(nombre, esperada.isInstance(e), e.getClass().getSimpleName() + ": " + e.getMessage());
    }
  }

  private static String codigos(List<SolicitudPrestamo> solicitudes) {
    List<String> codigos = new ArrayList<>();
    for (SolicitudPrestamo s : solicitudes) codigos.add(s.getCodigo());
    return codigos.toString();
  }
}
