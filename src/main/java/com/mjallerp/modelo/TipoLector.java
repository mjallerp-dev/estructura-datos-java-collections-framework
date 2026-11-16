package com.mjallerp.modelo;

public enum TipoLector {
  ESTUDIANTE("Estudiante"),
  DOCENTE("Docente"),
  INVESTIGADOR("Investigador"),
  EXTERNO("Externo");

  private final String etiqueta;

  TipoLector(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  @Override
  public String toString() {
    return etiqueta;
  }
}
