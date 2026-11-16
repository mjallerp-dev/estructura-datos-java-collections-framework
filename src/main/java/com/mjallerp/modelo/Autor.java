package com.mjallerp.modelo;

import java.util.Objects;

public class Autor {
  private final String codigo;
  private final String nombre;
  private final String nacionalidad;

  public Autor(String codigo, String nombre, String nacionalidad) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.nacionalidad = nacionalidad;
  }

  public String getCodigo() { return codigo; }
  public String getNombre() { return nombre; }
  public String getNacionalidad() { return nacionalidad; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Autor)) return false;
    Autor otro = (Autor) o;
    return codigo.equals(otro.codigo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigo);
  }

  @Override
  public String toString() {
    return codigo + " - " + nombre + " (" + nacionalidad + ")";
  }
}
