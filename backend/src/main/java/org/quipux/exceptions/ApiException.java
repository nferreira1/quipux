package org.quipux.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ApiException extends WebApplicationException {

    public ApiException(Response.Status status, String message) {
        super(message, status);
    }

    public static ApiException notFound(String message) {
        return new ApiException(Response.Status.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(Response.Status.CONFLICT, message);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(Response.Status.BAD_REQUEST, message);
    }

    public static ApiException unavailable(String message) {
        return new ApiException(Response.Status.SERVICE_UNAVAILABLE, message);
    }
}
