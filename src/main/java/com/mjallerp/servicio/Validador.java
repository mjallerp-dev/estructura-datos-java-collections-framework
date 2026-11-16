package com.mjallerp.servicio;

import com.mjallerp.excepciones.DatoInvalidoException;

import java.util.Locale;

public final class Validador {
  private Validador() {}

  public static String texto(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
      throw new DatoInvalidoException(campo + " es obligatorio");
    }
    return valor.trim().replaceAll("\\s+", " ");
  }

  public static String codigo(String valor, String campo) {
    String codigo = texto(valor, campo).toUpperCase(Locale.ROOT);
    if (!codigo.matches("[A-Z0-9-]{3,15}")) {
      throw new DatoInvalidoException(campo + " debe tener entre 3 y 15 caracteres (letras, números o guiones)");
    }
    return codigo;
  }

  public static String clave(String valor, String campo) {
    return texto(valor, campo).toUpperCase(Locale.ROOT);
  }

  public static String documento(String valor) {
    String documento = texto(valor, "El documento");
    if (!documento.matches("\\d{5,12}")) {
      throw new DatoInvalidoException("El documento debe tener entre 5 y 12 dígitos");
    }
    return documento;
  }

  public static String categoria(String valor) {
    String texto = texto(valor, "La categoría").toLowerCase(Locale.ROOT);
    return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
  }

  public static int rango(int valor, int minimo, int maximo, String campo) {
    if (valor < minimo || valor > maximo) {
      throw new DatoInvalidoException(campo + " debe estar entre " + minimo + " y " + maximo);
    }
    return valor;
  }

  public static <T> T obligatorio(T valor, String campo) {
    if (valor == null) {
      throw new DatoInvalidoException(campo + " es obligatorio");
    }
    return valor;
  }
}
