package com.mjallerp.datos;

import com.mjallerp.modelo.PrioridadSolicitud;
import com.mjallerp.modelo.TipoLector;
import com.mjallerp.servicio.Biblioteca;
import com.mjallerp.servicio.Catalogo;
import com.mjallerp.servicio.GestorPrestamos;
import com.mjallerp.servicio.RegistroLectores;

public final class DatosIniciales {
  private DatosIniciales() {}

  public static Biblioteca crearBiblioteca() {
    Biblioteca biblioteca = new Biblioteca();
    cargarAutores(biblioteca.getCatalogo());
    cargarCategorias(biblioteca.getCatalogo());
    cargarLibros(biblioteca.getCatalogo());
    cargarLectores(biblioteca.getLectores());
    cargarSolicitudes(biblioteca.getPrestamos());
    return biblioteca;
  }

  private static void cargarAutores(Catalogo catalogo) {
    catalogo.registrarAutor("AUT-07", "Isaac Asimov", "Estados Unidos");
    catalogo.registrarAutor("AUT-01", "Gabriel García Márquez", "Colombia");
    catalogo.registrarAutor("AUT-03", "Robert C. Martin", "Estados Unidos");
    catalogo.registrarAutor("AUT-05", "Yuval Noah Harari", "Israel");
    catalogo.registrarAutor("AUT-02", "Isabel Allende", "Chile");
    catalogo.registrarAutor("AUT-06", "Stephen Hawking", "Reino Unido");
    catalogo.registrarAutor("AUT-04", "Joshua Bloch", "Estados Unidos");
    catalogo.registrarAutor("AUT-08", "Mario Vargas Llosa", "Perú");
  }

  private static void cargarCategorias(Catalogo catalogo) {
    catalogo.registrarCategoria("Novela");
    catalogo.registrarCategoria("Programación");
    catalogo.registrarCategoria("Ciencia ficción");
    catalogo.registrarCategoria("Historia");
    catalogo.registrarCategoria("Ciencia");
    catalogo.registrarCategoria("Ensayo");
    catalogo.registrarCategoria("Matemáticas");
  }

  private static void cargarLibros(Catalogo catalogo) {
    catalogo.registrarLibro("LIB-010", "Cien años de soledad", "AUT-01", "Novela", 1967, 3);
    catalogo.registrarLibro("LIB-003", "Código limpio", "AUT-03", "Programación", 2008, 2);
    catalogo.registrarLibro("LIB-015", "Fundación", "AUT-07", "Ciencia ficción", 1951, 1);
    catalogo.registrarLibro("LIB-001", "Effective Java", "AUT-04", "Programación", 2018, 2);
    catalogo.registrarLibro("LIB-012", "Sapiens: De animales a dioses", "AUT-05", "Historia", 2011, 4);
    catalogo.registrarLibro("LIB-007", "Fundación", "AUT-07", "Ciencia ficción", 1951, 2);
    catalogo.registrarLibro("LIB-005", "Breve historia del tiempo", "AUT-06", "Ciencia", 1988, 1);
    catalogo.registrarLibro("LIB-018", "La casa de los espíritus", "AUT-02", "Novela", 1982, 2);
    catalogo.registrarLibro("LIB-002", "Arquitectura limpia", "AUT-03", "Programación", 2017, 1);
    catalogo.registrarLibro("LIB-014", "Yo, robot", "AUT-07", "Ciencia ficción", 1950, 2);
    catalogo.registrarLibro("LIB-008", "El amor en los tiempos del cólera", "AUT-01", "Novela", 1985, 1);
    catalogo.registrarLibro("LIB-016", "Homo Deus", "AUT-05", "Historia", 2015, 2);
    catalogo.registrarLibro("LIB-004", "El universo en una cáscara de nuez", "AUT-06", "Ciencia", 2001, 1);
    catalogo.registrarLibro("LIB-011", "La ciudad y los perros", "AUT-08", "Novela", 1963, 2);
    catalogo.registrarLibro("LIB-006", "Crónica de una muerte anunciada", "AUT-01", "Novela", 1981, 3);
    catalogo.registrarLibro("LIB-017", "21 lecciones para el siglo XXI", "AUT-05", "Ensayo", 2018, 1);
    catalogo.registrarLibro("LIB-009", "El fin de la eternidad", "AUT-07", "Ciencia ficción", 1955, 1);
    catalogo.registrarLibro("LIB-013", "Paula", "AUT-02", "Novela", 1994, 1);
  }

  private static void cargarLectores(RegistroLectores lectores) {
    lectores.registrar("1001234567", "Ana María Torres", TipoLector.ESTUDIANTE);
    lectores.registrar("1002345678", "Carlos Pérez", TipoLector.DOCENTE);
    lectores.registrar("1003456789", "Laura Gómez", TipoLector.INVESTIGADOR);
    lectores.registrar("1004567890", "Andrés Ruiz", TipoLector.ESTUDIANTE);
    lectores.registrar("1005678901", "Sofía Martínez", TipoLector.ESTUDIANTE);
    lectores.registrar("1006789012", "Jorge Díaz", TipoLector.EXTERNO);
  }

  private static void cargarSolicitudes(GestorPrestamos prestamos) {
    prestamos.registrarSolicitud("1005678901", "LIB-002", PrioridadSolicitud.NORMAL, "Proyecto de software del semestre");
    prestamos.registrarSolicitud("1002345678", "LIB-008", PrioridadSolicitud.ESPECIAL, "Club de lectura docente");
    prestamos.atenderSiguiente();
    prestamos.atenderSiguiente();

    prestamos.registrarSolicitud("1001234567", "LIB-010", PrioridadSolicitud.NORMAL, "Lectura personal");
    prestamos.registrarSolicitud("1004567890", "LIB-005", PrioridadSolicitud.NORMAL, "Consulta para una exposición");
    prestamos.registrarSolicitud("1003456789", "LIB-005", PrioridadSolicitud.URGENTE, "Sustentación de tesis el viernes");
    prestamos.registrarSolicitud("1002345678", "LIB-003", PrioridadSolicitud.ESPECIAL, "Preparación de clase de ingeniería de software");
    prestamos.registrarSolicitud("1005678901", "LIB-001", PrioridadSolicitud.NORMAL, "Repaso de Java");
    prestamos.registrarSolicitud("1004567890", "LIB-012", PrioridadSolicitud.URGENTE, "Parcial de historia mañana");
  }
}
