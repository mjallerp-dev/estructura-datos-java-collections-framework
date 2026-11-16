package com.mjallerp.modelo;

import com.mjallerp.excepciones.OperacionNoPermitidaException;

import java.util.Comparator;
import java.util.Objects;

public class Libro implements Comparable<Libro> {
  public static final Comparator<Libro> POR_TITULO =
      Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Libro::getCodigo);

  private String codigo;
  private String titulo;
  private Autor autor;
  private String categoria;
  private int anioPublicacion;
  private int ejemplaresTotales;
  private int ejemplaresDisponibles;

  public Libro(String codigo, String titulo, Autor autor, String categoria, int anioPublicacion, int ejemplares) {
    this.codigo = codigo;
    this.titulo = titulo;
    this.autor = autor;
    this.categoria = categoria;
    this.anioPublicacion = anioPublicacion;
    this.ejemplaresTotales = ejemplares;
    this.ejemplaresDisponibles = ejemplares;
  }

  public String getCodigo() { return codigo; }
  public String getTitulo() { return titulo; }
  public Autor getAutor() { return autor; }
  public String getCategoria() { return categoria; }
  public int getAnioPublicacion() { return anioPublicacion; }
  public int getEjemplaresTotales() { return ejemplaresTotales; }
  public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }

  public int getEjemplaresPrestados() {
    return ejemplaresTotales - ejemplaresDisponibles;
  }

  public void setCodigo(String codigo) { this.codigo = codigo; }
  public void setTitulo(String titulo) { this.titulo = titulo; }
  public void setCategoria(String categoria) { this.categoria = categoria; }

  public void setEjemplaresTotales(int nuevoTotal) {
    int prestados = getEjemplaresPrestados();
    if (nuevoTotal < prestados) {
      throw new OperacionNoPermitidaException("El libro tiene " + prestados
          + " ejemplares prestados; el total no puede ser menor a ese valor");
    }
    ejemplaresTotales = nuevoTotal;
    ejemplaresDisponibles = nuevoTotal - prestados;
  }

  public void prestarEjemplar() {
    if (ejemplaresDisponibles == 0) {
      throw new OperacionNoPermitidaException("No hay ejemplares disponibles de " + codigo);
    }
    ejemplaresDisponibles--;
  }

  public void devolverEjemplar() {
    if (ejemplaresDisponibles == ejemplaresTotales) {
      throw new OperacionNoPermitidaException("Todos los ejemplares de " + codigo + " ya están en la biblioteca");
    }
    ejemplaresDisponibles++;
  }

  @Override
  public int compareTo(Libro otro) {
    return codigo.compareTo(otro.codigo);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Libro)) return false;
    Libro otro = (Libro) o;
    return codigo.equals(otro.codigo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigo);
  }

  @Override
  public String toString() {
    return codigo + " - " + titulo + " (" + autor.getNombre() + ")";
  }
}
