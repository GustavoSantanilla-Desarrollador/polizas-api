package com.example.polizas.exception;

/** Se lanza cuando una Poliza o Riesgo solicitado no existe. Mapea a HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
