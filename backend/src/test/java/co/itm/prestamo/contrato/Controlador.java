package co.itm.prestamo.contrato;

/** Un controlador atiende un recurso REST (/api/{recurso}/...) y devuelve el objeto que se serializa a JSON. */
public interface Controlador { Object manejar(Peticion p); }
