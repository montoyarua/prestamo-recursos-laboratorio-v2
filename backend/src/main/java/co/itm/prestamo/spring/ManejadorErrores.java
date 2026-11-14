package co.itm.prestamo.spring;

import co.itm.prestamo.excepciones.NegocioException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Convierte las excepciones en respuestas JSON {"error": mensaje} con el código HTTP de cada regla de negocio. */
@RestControllerAdvice
public class ManejadorErrores {
    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

    private static ResponseEntity<Map<String, String>> error(int codigo, String mensaje) {
        return ResponseEntity.status(codigo).body(Map.of("error", mensaje));
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, String>> negocio(NegocioException e) { return error(e.getEstadoHttp(), e.getMessage()); }

    /** Restricciones de PostgreSQL: son la última barrera cuando dos peticiones compiten (RN-01, RN-07). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridad(DataIntegrityViolationException e) {
        String detalle = String.valueOf(e.getMostSpecificCause().getMessage());
        if (detalle.contains("sin_cruce_por_recurso")) return error(409, "El recurso no está disponible en ese horario: ya está reservado o prestado");
        if (detalle.contains("ux_usuario_correo")) return error(409, "Ya existe una cuenta con ese correo");
        LOG.warn("Violación de integridad: {}", detalle);
        return error(409, "La operación entra en conflicto con datos ya guardados");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> peticionInvalida(Exception e) { return error(400, "La petición no es válida"); }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperado(Exception e) {
        if (e instanceof ErrorResponse er) return ResponseEntity.status(er.getStatusCode()).body(Map.of("error", "Petición no válida"));
        LOG.error("Error interno", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno");
    }
}
