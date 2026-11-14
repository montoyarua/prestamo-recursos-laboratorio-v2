package co.itm.prestamo.inventario;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.excepciones.CamposIncompletosException;
import java.util.*;

public class LaboratorioCreator extends RecursoCreator {
    @Override protected List<String> camposObligatorios() { return List.of("nombre", "ubicacion", "capacidad"); }
    @Override protected Recurso crearRecurso(Map<String, String> d) {
        try { return new Laboratorio(texto(d, "nombre"), texto(d, "ubicacion"), Integer.parseInt(texto(d, "capacidad"))); }
        catch (NumberFormatException e) { throw new CamposIncompletosException("La capacidad debe ser un número"); }
    }
}
