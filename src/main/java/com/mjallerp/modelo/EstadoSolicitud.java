package com.mjallerp.modelo;

public enum EstadoSolicitud {
  PENDIENTE("Pendiente"),
  PRESTADO("Prestado"),
  RECHAZADA("Rechazada"),
  DEVUELTO("Devuelto"),
  CANCELADA("Cancelada");

  private final String etiqueta;

  EstadoSolicitud(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  public boolean esActiva() {
    return this == PENDIENTE || this == PRESTADO;
  }

  @Override
  public String toString() {
    return etiqueta;
  }
}
