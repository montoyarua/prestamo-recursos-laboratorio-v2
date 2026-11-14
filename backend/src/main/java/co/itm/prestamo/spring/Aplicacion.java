package co.itm.prestamo.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de la aplicación Spring Boot (Backend/API del C4). Sirve la API REST y la interfaz React compilada (carpeta static). */
@SpringBootApplication
public class Aplicacion {
    public static void main(String[] args) { SpringApplication.run(Aplicacion.class, args); }
}
