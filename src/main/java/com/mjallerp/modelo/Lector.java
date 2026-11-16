package com.mjallerp.modelo;

import java.util.Objects;

public class Lector {
  private final String documento;
  private final String nombre;
  private final TipoLector tipo;

  public Lector(String documento, String nombre, TipoLector tipo) {
    this.documento = documento;
    this.nombre = nombre;
    this.tipo = tipo;
  }

  public String getDocumento() { return documento; }
  public String getNombre() { return nombre; }
  public TipoLector getTipo() { return tipo; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Lector)) return false;
    Lector otro = (Lector) o;
    return documento.equals(otro.documento);
  }

  @Override
  public int hashCode() {
    return Objects.hash(documento);
  }

  @Override
  public String toString() {
    return documento + " - " + nombre + " (" + tipo + ")";
  }
}
