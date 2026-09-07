package com.example.polizas.exception;

/**
 * Se lanza cuando una operacion viola una regla de negocio o un conflicto de estado
 * (ej. renovar una poliza cancelada, agregar riesgos a una individual). Mapea a HTTP 409.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
