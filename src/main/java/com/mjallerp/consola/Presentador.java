package com.mjallerp.consola;

import com.mjallerp.modelo.Autor;
import com.mjallerp.modelo.Lector;
import com.mjallerp.modelo.Libro;
import com.mjallerp.modelo.SolicitudPrestamo;

import java.util.Collection;

public final class Presentador {
  private static final String FORMATO_LIBRO = "%-9s %-36s %-24s %-16s %5s %10s%n";
  private static final String FORMATO_SOLICITUD = "%-8s %-9s %-10s %-18s %-36s %-10s%n";

  private Presentador() {}

  public static void libros(Collection<Libro> libros) {
    if (libros.isEmpty()) {
      System.out.println("  (sin libros)");
      return;
    }
    System.out.printf(FORMATO_LIBRO, "Código", "Título", "Autor", "Categoría", "Año", "Disp/Total");
    for (Libro libro : libros) {
      System.out.printf(FORMATO_LIBRO, libro.getCodigo(), recortar(libro.getTitulo(), 36),
          recortar(libro.getAutor().getNombre(), 24), recortar(libro.getCategoria(), 16),
          libro.getAnioPublicacion(), libro.getEjemplaresDisponibles() + "/" + libro.getEjemplaresTotales());
    }
  }

  public static void libro(Libro libro) {
    System.out.println("  Código:       " + libro.getCodigo());
    System.out.println("  Título:       " + libro.getTitulo());
    System.out.println("  Autor:        " + libro.getAutor());
    System.out.println("  Categoría:    " + libro.getCategoria());
    System.out.println("  Año:          " + libro.getAnioPublicacion());
    System.out.println("  Ejemplares:   " + libro.getEjemplaresDisponibles() + " disponibles de "
        + libro.getEjemplaresTotales() + " (" + libro.getEjemplaresPrestados() + " prestados)");
  }

  public static void solicitudes(Collection<SolicitudPrestamo> solicitudes) {
    if (solicitudes.isEmpty()) {
      System.out.println("  (sin solicitudes)");
      return;
    }
    System.out.printf(FORMATO_SOLICITUD, "Código", "Registro", "Prioridad", "Lector", "Libro", "Estado");
    for (SolicitudPrestamo s : solicitudes) {
      System.out.printf(FORMATO_SOLICITUD, s.getCodigo(), "#" + s.getNumeroRegistro(), s.getPrioridad(),
          recortar(s.getLector().getNombre(), 18), recortar(s.getLibro().toString(), 36), s.getEstado());
    }
  }

  public static void solicitud(SolicitudPrestamo s) {
    System.out.println("  Código:      " + s.getCodigo() + " (registro #" + s.getNumeroRegistro() + ")");
    System.out.println("  Lector:      " + s.getLector());
    System.out.println("  Libro:       " + s.getLibro());
    System.out.println("  Prioridad:   " + s.getPrioridad() + " - " + s.getPrioridad().getDescripcion());
    System.out.println("  Motivo:      " + s.getMotivo());
    System.out.println("  Fecha:       " + s.getFechaSolicitud());
    System.out.println("  Estado:      " + s.getEstado());
  }

  public static void autores(Collection<Autor> autores) {
    for (Autor autor : autores) System.out.println("  " + autor);
  }

  public static void lectores(Collection<Lector> lectores) {
    for (Lector lector : lectores) System.out.println("  " + lector);
  }

  private static String recortar(String texto, int maximo) {
    return texto.length() <= maximo ? texto : texto.substring(0, maximo - 3) + "...";
  }
}
