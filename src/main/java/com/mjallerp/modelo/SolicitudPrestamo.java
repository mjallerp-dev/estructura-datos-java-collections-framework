package com.mjallerp.modelo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class SolicitudPrestamo {
  public static final Comparator<SolicitudPrestamo> POR_PRIORIDAD =
      Comparator.comparing(SolicitudPrestamo::getPrioridad)
                .thenComparingInt(SolicitudPrestamo::getNumeroRegistro);

  private final String codigo;
  private final int numeroRegistro;
  private final Libro libro;
  private final Lector lector;
  private final String motivo;
  private final LocalDate fechaSolicitud;
  private PrioridadSolicitud prioridad;
  private EstadoSolicitud estado;

  public SolicitudPrestamo(String codigo, int numeroRegistro, Libro libro, Lector lector,
                           PrioridadSolicitud prioridad, String motivo) {
    this.codigo = codigo;
    this.numeroRegistro = numeroRegistro;
    this.libro = libro;
    this.lector = lector;
    this.prioridad = prioridad;
    this.motivo = motivo;
    this.fechaSolicitud = LocalDate.now();
    this.estado = EstadoSolicitud.PENDIENTE;
  }

  public String getCodigo() { return codigo; }
  public int getNumeroRegistro() { return numeroRegistro; }
  public Libro getLibro() { return libro; }
  public Lector getLector() { return lector; }
  public String getMotivo() { return motivo; }
  public LocalDate getFechaSolicitud() { return fechaSolicitud; }
  public PrioridadSolicitud getPrioridad() { return prioridad; }
  public EstadoSolicitud getEstado() { return estado; }

  public void setPrioridad(PrioridadSolicitud prioridad) { this.prioridad = prioridad; }
  public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SolicitudPrestamo)) return false;
    SolicitudPrestamo otra = (SolicitudPrestamo) o;
    return codigo.equals(otra.codigo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigo);
  }

  @Override
  public String toString() {
    return codigo + " [" + prioridad + "] " + lector.getNombre() + " -> " + libro.getCodigo() + " (" + estado + ")";
  }
}
