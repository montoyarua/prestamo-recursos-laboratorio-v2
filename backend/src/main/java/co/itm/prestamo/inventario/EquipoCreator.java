package co.itm.prestamo.inventario;

import co.itm.prestamo.dominio.*;
import java.util.*;

public class EquipoCreator extends RecursoCreator {
    @Override protected List<String> camposObligatorios() { return List.of("nombre", "marca", "modelo", "numeroSerie"); }
    @Override protected Recurso crearRecurso(Map<String, String> d) {
        return new Equipo(texto(d, "nombre"), texto(d, "marca"), texto(d, "modelo"), texto(d, "numeroSerie"));
    }
}
