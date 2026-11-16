package com.mjallerp.modelo;

public enum PrioridadSolicitud {
  URGENTE("Urgente", "Sustentación, examen o trámite con fecha límite"),
  ESPECIAL("Especial", "Docencia, investigación o lector con condición especial"),
  NORMAL("Normal", "Consulta o lectura general");

  private final String etiqueta;
  private final String descripcion;

  PrioridadSolicitud(String etiqueta, String descripcion) {
    this.etiqueta = etiqueta;
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  @Override
  public String toString() {
    return etiqueta;
  }
}
