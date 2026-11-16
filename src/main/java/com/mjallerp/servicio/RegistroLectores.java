package com.mjallerp.servicio;

import com.mjallerp.excepciones.RegistroDuplicadoException;
import com.mjallerp.excepciones.RegistroNoEncontradoException;
import com.mjallerp.modelo.Lector;
import com.mjallerp.modelo.TipoLector;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroLectores {
  private final Map<String, Lector> lectoresPorDocumento = new LinkedHashMap<>();

  public Lector registrar(String documento, String nombre, TipoLector tipo) {
    String clave = Validador.documento(documento);
    if (lectoresPorDocumento.containsKey(clave)) {
      throw new RegistroDuplicadoException("Ya existe un lector con el documento " + clave);
    }
    Lector lector = new Lector(clave, Validador.texto(nombre, "El nombre del lector"),
        Validador.obligatorio(tipo, "El tipo de lector"));
    lectoresPorDocumento.put(clave, lector);
    return lector;
  }

  public Lector buscar(String documento) {
    Lector lector = lectoresPorDocumento.get(Validador.texto(documento, "El documento"));
    if (lector == null) {
      throw new RegistroNoEncontradoException("No existe un lector con el documento " + documento.trim());
    }
    return lector;
  }

  public boolean existe(String documento) {
    return lectoresPorDocumento.containsKey(Validador.texto(documento, "El documento"));
  }

  public Collection<Lector> getLectores() {
    return Collections.unmodifiableCollection(lectoresPorDocumento.values());
  }

  public int cantidad() {
    return lectoresPorDocumento.size();
  }
}
