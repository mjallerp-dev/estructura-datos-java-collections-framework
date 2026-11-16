package com.mjallerp.servicio;

import com.mjallerp.excepciones.OperacionNoPermitidaException;
import com.mjallerp.excepciones.RegistroDuplicadoException;
import com.mjallerp.excepciones.RegistroNoEncontradoException;
import com.mjallerp.modelo.EstadoSolicitud;
import com.mjallerp.modelo.Lector;
import com.mjallerp.modelo.Libro;
import com.mjallerp.modelo.PrioridadSolicitud;
import com.mjallerp.modelo.SolicitudPrestamo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class GestorPrestamos {
  private final Catalogo catalogo;
  private final RegistroLectores lectores;

  private final List<SolicitudPrestamo> historial = new ArrayList<>();
  private final Map<String, SolicitudPrestamo> solicitudesPorCodigo = new HashMap<>();
  private final Queue<SolicitudPrestamo> solicitudesPendientes = new PriorityQueue<>(SolicitudPrestamo.POR_PRIORIDAD);
  private final Set<String> solicitudesActivas = new HashSet<>();
  private int consecutivo;

  public GestorPrestamos(Catalogo catalogo, RegistroLectores lectores) {
    this.catalogo = catalogo;
    this.lectores = lectores;
  }

  public SolicitudPrestamo registrarSolicitud(String documentoLector, String codigoLibro,
                                              PrioridadSolicitud prioridad, String motivo) {
    Lector lector = lectores.buscar(documentoLector);
    Libro libro = catalogo.buscarLibro(codigoLibro);
    Validador.obligatorio(prioridad, "La prioridad");
    String motivoValido = Validador.texto(motivo, "El motivo de la solicitud");

    String clave = claveActiva(lector, libro.getCodigo());
    if (!solicitudesActivas.add(clave)) {
      throw new RegistroDuplicadoException(lector.getNombre() + " ya tiene una solicitud pendiente o un préstamo activo del libro "
          + libro.getCodigo());
    }

    consecutivo++;
    String codigo = String.format("PRE-%03d", consecutivo);
    SolicitudPrestamo solicitud = new SolicitudPrestamo(codigo, consecutivo, libro, lector, prioridad, motivoValido);
    historial.add(solicitud);
    solicitudesPorCodigo.put(codigo, solicitud);
    solicitudesPendientes.offer(solicitud);
    return solicitud;
  }

  public Optional<SolicitudPrestamo> consultarSiguiente() {
    return Optional.ofNullable(solicitudesPendientes.peek());
  }

  public SolicitudPrestamo atenderSiguiente() {
    SolicitudPrestamo solicitud = solicitudesPendientes.poll();
    if (solicitud == null) {
      throw new OperacionNoPermitidaException("No hay solicitudes pendientes por atender");
    }
    Libro libro = solicitud.getLibro();
    if (libro.getEjemplaresDisponibles() > 0) {
      libro.prestarEjemplar();
      solicitud.setEstado(EstadoSolicitud.PRESTADO);
    } else {
      solicitud.setEstado(EstadoSolicitud.RECHAZADA);
      solicitudesActivas.remove(claveActiva(solicitud.getLector(), libro.getCodigo()));
    }
    return solicitud;
  }

  public void cambiarPrioridad(String codigo, PrioridadSolicitud nuevaPrioridad) {
    SolicitudPrestamo solicitud = buscarSolicitud(codigo);
    Validador.obligatorio(nuevaPrioridad, "La prioridad");
    if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
      throw new OperacionNoPermitidaException("Solo se puede cambiar la prioridad de una solicitud pendiente");
    }
    solicitudesPendientes.remove(solicitud);
    solicitud.setPrioridad(nuevaPrioridad);
    solicitudesPendientes.offer(solicitud);
  }

  public SolicitudPrestamo cancelarSolicitud(String codigo) {
    SolicitudPrestamo solicitud = buscarSolicitud(codigo);
    if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
      throw new OperacionNoPermitidaException("Solo se pueden cancelar solicitudes pendientes; estado actual: "
          + solicitud.getEstado());
    }
    solicitudesPendientes.remove(solicitud);
    solicitudesActivas.remove(claveActiva(solicitud.getLector(), solicitud.getLibro().getCodigo()));
    solicitud.setEstado(EstadoSolicitud.CANCELADA);
    return solicitud;
  }

  public SolicitudPrestamo registrarDevolucion(String codigo) {
    SolicitudPrestamo solicitud = buscarSolicitud(codigo);
    if (solicitud.getEstado() != EstadoSolicitud.PRESTADO) {
      throw new OperacionNoPermitidaException("La solicitud " + solicitud.getCodigo()
          + " no tiene un préstamo activo; estado actual: " + solicitud.getEstado());
    }
    solicitud.getLibro().devolverEjemplar();
    solicitudesActivas.remove(claveActiva(solicitud.getLector(), solicitud.getLibro().getCodigo()));
    solicitud.setEstado(EstadoSolicitud.DEVUELTO);
    return solicitud;
  }

  public SolicitudPrestamo buscarSolicitud(String codigo) {
    SolicitudPrestamo solicitud = solicitudesPorCodigo.get(Validador.clave(codigo, "El código de la solicitud"));
    if (solicitud == null) {
      throw new RegistroNoEncontradoException("No existe una solicitud con el código " + codigo.trim());
    }
    return solicitud;
  }

  public boolean tieneSolicitudesActivas(Libro libro) {
    for (SolicitudPrestamo solicitud : historial) {
      if (solicitud.getLibro().equals(libro) && solicitud.getEstado().esActiva()) return true;
    }
    return false;
  }

  void actualizarClavesLibro(Libro libro, String codigoAnterior) {
    for (SolicitudPrestamo solicitud : historial) {
      if (solicitud.getLibro() == libro && solicitud.getEstado().esActiva()) {
        solicitudesActivas.remove(claveActiva(solicitud.getLector(), codigoAnterior));
        solicitudesActivas.add(claveActiva(solicitud.getLector(), libro.getCodigo()));
      }
    }
  }

  public List<SolicitudPrestamo> pendientesEnOrdenDeAtencion() {
    PriorityQueue<SolicitudPrestamo> copia = new PriorityQueue<>(SolicitudPrestamo.POR_PRIORIDAD);
    copia.addAll(solicitudesPendientes);
    List<SolicitudPrestamo> orden = new ArrayList<>();
    while (!copia.isEmpty()) orden.add(copia.poll());
    return orden;
  }

  public List<SolicitudPrestamo> pendientesOrdenInterno() {
    return new ArrayList<>(solicitudesPendientes);
  }

  public static String claveActiva(Lector lector, String codigoLibro) {
    return lector.getDocumento() + "|" + codigoLibro;
  }

  public List<SolicitudPrestamo> getHistorial() {
    return Collections.unmodifiableList(historial);
  }

  public Map<String, SolicitudPrestamo> getSolicitudesPorCodigo() {
    return Collections.unmodifiableMap(solicitudesPorCodigo);
  }

  public Set<String> getSolicitudesActivas() {
    return Collections.unmodifiableSet(solicitudesActivas);
  }

  public int cantidadPendientes() {
    return solicitudesPendientes.size();
  }

  boolean estaEnPendientes(SolicitudPrestamo solicitud) {
    return solicitudesPendientes.contains(solicitud);
  }
}
