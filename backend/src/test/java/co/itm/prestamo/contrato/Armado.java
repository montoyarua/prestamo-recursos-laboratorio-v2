package co.itm.prestamo.contrato;

import co.itm.prestamo.Main;
import co.itm.prestamo.Main.Sistema;
import co.itm.prestamo.persistencia.TablasMemoria;
import java.time.Clock;

/** SOLO PARA PRUEBAS: sistema con tablas en memoria y cuentas de demostración. */
public final class Armado {
    private Armado() { }
    public static Sistema enMemoria(Clock reloj) {
        Sistema s = Main.armar(reloj, TablasMemoria.crear());
        Main.cargarDatosDeDemostracion(s.usuarios(), s.recursos());
        return s;
    }
}
