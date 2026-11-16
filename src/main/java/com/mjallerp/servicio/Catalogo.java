package com.mjallerp.servicio;

import com.mjallerp.excepciones.DatoInvalidoException;
import com.mjallerp.excepciones.RegistroDuplicadoException;
import com.mjallerp.excepciones.RegistroNoEncontradoException;
import com.mjallerp.modelo.Autor;
import com.mjallerp.modelo.Libro;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Catalogo {
  public static final int ANIO_MINIMO = 1450;
  public static final int EJEMPLARES_MAXIMOS = 50;

  private final List<Libro> libros = new ArrayList<>();
  private final Map<String, Libro> librosPorCodigo = new HashMap<>();
  private final Set<String> categorias = new LinkedHashSet<>();
  private final Set<Libro> librosPorTitulo = new TreeSet<>(Libro.POR_TITULO);
  private final Map<String, Set<Libro>> librosPorCategoria = new TreeMap<>();
  private final Map<String, Autor> autoresPorCodigo = new TreeMap<>();

  public Autor registrarAutor(String codigo, String nombre, String nacionalidad) {
    String clave = Validador.codigo(codigo, "El código del autor");
    if (autoresPorCodigo.containsKey(clave)) {
      throw new RegistroDuplicadoException("Ya existe un autor con el código " + clave);
    }
    Autor autor = new Autor(clave, Validador.texto(nombre, "El nombre del autor"),
        Validador.texto(nacionalidad, "La nacionalidad"));
    autoresPorCodigo.put(clave, autor);
    return autor;
  }

  public Autor buscarAutor(String codigo) {
    Autor autor = autoresPorCodigo.get(Validador.clave(codigo, "El código del autor"));
    if (autor == null) {
      throw new RegistroNoEncontradoException("No existe un autor con el código " + codigo.trim());
    }
    return autor;
  }

  public String registrarCategoria(String nombre) {
    String categoria = Validador.categoria(nombre);
    boolean agregada = categorias.add(categoria);
    if (!agregada) {
      throw new RegistroDuplicadoException("La categoría \"" + categoria + "\" ya está registrada");
    }
    return categoria;
  }

  public boolean existeCategoria(String nombre) {
    return categorias.contains(Validador.categoria(nombre));
  }

  public Libro registrarLibro(String codigo, String titulo, String codigoAutor, String categoria,
                              int anioPublicacion, int ejemplares) {
    String clave = Validador.codigo(codigo, "El código del libro");
    if (librosPorCodigo.containsKey(clave)) {
      throw new RegistroDuplicadoException("Ya existe un libro con el código " + clave);
    }
    String tituloValido = Validador.texto(titulo, "El título");
    Autor autor = buscarAutor(codigoAutor);
    String categoriaValida = categoriaRegistrada(categoria);
    Validador.rango(anioPublicacion, ANIO_MINIMO, Year.now().getValue(), "El año de publicación");
    Validador.rango(ejemplares, 1, EJEMPLARES_MAXIMOS, "La cantidad de ejemplares");

    Libro libro = new Libro(clave, tituloValido, autor, categoriaValida, anioPublicacion, ejemplares);
    libros.add(libro);
    librosPorCodigo.put(clave, libro);
    librosPorTitulo.add(libro);
    librosPorCategoria.computeIfAbsent(categoriaValida, k -> new TreeSet<>()).add(libro);
    return libro;
  }

  public Libro buscarLibro(String codigo) {
    Libro libro = librosPorCodigo.get(Validador.clave(codigo, "El código del libro"));
    if (libro == null) {
      throw new RegistroNoEncontradoException("No existe un libro con el código " + codigo.trim());
    }
    return libro;
  }

  public boolean existeLibro(String codigo) {
    return librosPorCodigo.containsKey(Validador.clave(codigo, "El código del libro"));
  }

  public void modificarTitulo(String codigo, String nuevoTitulo) {
    Libro libro = buscarLibro(codigo);
    String titulo = Validador.texto(nuevoTitulo, "El título");
    librosPorTitulo.remove(libro);
    libro.setTitulo(titulo);
    librosPorTitulo.add(libro);
  }

  public void modificarCategoria(String codigo, String nuevaCategoria) {
    Libro libro = buscarLibro(codigo);
    String categoria = categoriaRegistrada(nuevaCategoria);
    if (categoria.equals(libro.getCategoria())) return;
    quitarDeCategoria(libro);
    libro.setCategoria(categoria);
    librosPorCategoria.computeIfAbsent(categoria, k -> new TreeSet<>()).add(libro);
  }

  public void modificarEjemplares(String codigo, int nuevoTotal) {
    Libro libro = buscarLibro(codigo);
    Validador.rango(nuevoTotal, 1, EJEMPLARES_MAXIMOS, "La cantidad de ejemplares");
    libro.setEjemplaresTotales(nuevoTotal);
  }

  void cambiarCodigo(Libro libro, String nuevoCodigo) {
    String clave = Validador.codigo(nuevoCodigo, "El nuevo código");
    if (clave.equals(libro.getCodigo())) {
      throw new DatoInvalidoException("El nuevo código es igual al actual");
    }
    if (librosPorCodigo.containsKey(clave)) {
      throw new RegistroDuplicadoException("Ya existe un libro con el código " + clave);
    }
    librosPorCodigo.remove(libro.getCodigo());
    librosPorTitulo.remove(libro);
    Set<Libro> deCategoria = librosPorCategoria.get(libro.getCategoria());
    deCategoria.remove(libro);

    libro.setCodigo(clave);

    librosPorCodigo.put(clave, libro);
    librosPorTitulo.add(libro);
    deCategoria.add(libro);
  }

  void eliminarLibro(Libro libro) {
    librosPorCodigo.remove(libro.getCodigo());
    libros.remove(libro);
    librosPorTitulo.remove(libro);
    quitarDeCategoria(libro);
  }

  private void quitarDeCategoria(Libro libro) {
    Set<Libro> deCategoria = librosPorCategoria.get(libro.getCategoria());
    deCategoria.remove(libro);
    if (deCategoria.isEmpty()) {
      librosPorCategoria.remove(libro.getCategoria());
    }
  }

  private String categoriaRegistrada(String nombre) {
    String categoria = Validador.categoria(nombre);
    if (!categorias.contains(categoria)) {
      throw new RegistroNoEncontradoException("La categoría \"" + categoria + "\" no está registrada");
    }
    return categoria;
  }

  public List<Libro> getLibros() {
    return Collections.unmodifiableList(libros);
  }

  public Map<String, Libro> getLibrosPorCodigo() {
    return Collections.unmodifiableMap(librosPorCodigo);
  }

  public Set<String> getCategorias() {
    return Collections.unmodifiableSet(categorias);
  }

  public Set<Libro> getLibrosPorTitulo() {
    return Collections.unmodifiableSet(librosPorTitulo);
  }

  public Map<String, Set<Libro>> getLibrosPorCategoria() {
    return Collections.unmodifiableMap(librosPorCategoria);
  }

  public Collection<Autor> getAutores() {
    return Collections.unmodifiableCollection(autoresPorCodigo.values());
  }

  public int cantidadAutores() {
    return autoresPorCodigo.size();
  }
}
