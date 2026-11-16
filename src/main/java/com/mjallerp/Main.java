package com.mjallerp;

import com.mjallerp.consola.LectorConsola;
import com.mjallerp.consola.MenuBiblioteca;
import com.mjallerp.datos.DatosIniciales;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    new MenuBiblioteca(new LectorConsola(scanner), DatosIniciales.crearBiblioteca()).mostrar();
  }
}
