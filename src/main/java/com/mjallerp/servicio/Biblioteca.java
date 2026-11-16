package com.mjallerp.servicio;

import com.mjallerp.excepciones.OperacionNoPermitidaException;
import com.mjallerp.modelo.EstadoSolicitud;
import com.mjallerp.modelo.Libro;
import com.mjallerp.modelo.SolicitudPrestamo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Biblioteca {
  private final Catalogo catalogo = new Catalogo();
  private final RegistroLectores lectores = new RegistroLectores();
  private final GestorPrestamos prestamos = new GestorPrestamos(catalogo, lectores);

  public Catalogo getCatalogo() { return catalogo; }
  public RegistroLectores getLectores() { return lectores; }
  public GestorPrestamos getPrestamos() { return prestamos; }

  public String cambiarCodigoLibro(String codigoActual, String nuevoCodigo) {
    Libro libro = catalogo.buscarLibro(codigoActual);
    String codigoAnterior = libro.getCodigo();
    catalogo.cambiarCodigo(libro, nuevoCodigo);
    prestamos.actualizarClavesLibro(libro, codigoAnterior);
    return codigoAnterior;
  }

  public void validarEliminacion(Libro libro) {
    if (prestamos.tieneSolicitudesActivas(libro)) {
      throw new OperacionNoPermitidaException("El libro " + libro.getCodigo()
          + " tiene solicitudes pendientes o ejemplares prestados; atiéndalas, cancélelas o registre la devolución primero");
    }
  }

  public Libro eliminarLibro(String codigo) {
    Libro libro = catalogo.buscarLibro(codigo);
    validarEliminacion(libro);
    catalogo.eliminarLibro(libro);
    return libro;
  }

  public List<String> verificarConsistencia() {
    List<String> problemas = new ArrayList<>();
    List<Libro> libros = catalogo.getLibros();
    Map<String, Libro> porCodigo = catalogo.getLibrosPorCodigo();
    Set<Libro> porTitulo = catalogo.getLibrosPorTitulo();
    Map<String, Set<Libro>> porCategoria = catalogo.getLibrosPorCategoria();

    int enCategorias = 0;
    for (Set<Libro> grupo : porCategoria.values()) enCategorias += grupo.size();
    if (libros.size() != porCodigo.size() || libros.size() != porTitulo.size() || libros.size() != enCategorias) {
      problemas.add("Tamaños distintos: lista=" + libros.size() + ", mapa=" + porCodigo.size()
          + ", por título=" + porTitulo.size() + ", por categoría=" + enCategorias);
    }

    for (Libro libro : libros) {
      if (porCodigo.get(libro.getCodigo()) != libro) {
        problemas.add(libro.getCodigo() + " no está asociado a su código en librosPorCodigo");
      }
      if (!porTitulo.contains(libro)) {
        problemas.add(libro.getCodigo() + " no está en librosPorTitulo");
      }
      Set<Libro> grupo = porCategoria.get(libro.getCategoria());
      if (grupo == null || !grupo.contains(libro)) {
        problemas.add(libro.getCodigo() + " no está en librosPorCategoria[" + libro.getCategoria() + "]");
      }
      if (!catalogo.getCategorias().contains(libro.getCategoria())) {
        problemas.add(libro.getCodigo() + " tiene una categoría no registrada");
      }
      int prestados = 0;
      for (SolicitudPrestamo solicitud : prestamos.getHistorial()) {
        if (solicitud.getLibro() == libro && solicitud.getEstado() == EstadoSolicitud.PRESTADO) prestados++;
      }
      if (prestados != libro.getEjemplaresPrestados()) {
        problemas.add(libro.getCodigo() + " registra " + libro.getEjemplaresPrestados()
            + " prestados pero hay " + prestados + " préstamos activos");
      }
    }

    int activas = 0;
    for (SolicitudPrestamo solicitud : prestamos.getHistorial()) {
      if (prestamos.getSolicitudesPorCodigo().get(solicitud.getCodigo()) != solicitud) {
        problemas.add(solicitud.getCodigo() + " no está asociada en solicitudesPorCodigo");
      }
      boolean pendiente = solicitud.getEstado() == EstadoSolicitud.PENDIENTE;
      if (pendiente != prestamos.estaEnPendientes(solicitud)) {
        problemas.add(solicitud.getCodigo() + " no coincide con la cola de prioridad (estado " + solicitud.getEstado() + ")");
      }
      if (solicitud.getEstado().esActiva()) {
        activas++;
        if (porCodigo.get(solicitud.getLibro().getCodigo()) != solicitud.getLibro()) {
          problemas.add(solicitud.getCodigo() + " está activa pero su libro no está en el catálogo");
        }
        String clave = GestorPrestamos.claveActiva(solicitud.getLector(), solicitud.getLibro().getCodigo());
        if (!prestamos.getSolicitudesActivas().contains(clave)) {
          problemas.add(solicitud.getCodigo() + " está activa pero falta su clave en solicitudesActivas");
        }
      }
    }
    if (activas != prestamos.getSolicitudesActivas().size()) {
      problemas.add("solicitudesActivas tiene " + prestamos.getSolicitudesActivas().size()
          + " claves pero hay " + activas + " solicitudes activas");
    }
    return problemas;
  }
}
