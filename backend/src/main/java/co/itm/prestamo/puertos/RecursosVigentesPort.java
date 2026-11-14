package co.itm.prestamo.puertos;

/** RN-06: ¿el recurso tiene solicitudes pendientes o aprobadas sin devolver, o préstamos activos? */
public interface RecursosVigentesPort { boolean tieneVigentes(long recursoId); }
